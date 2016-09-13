package org.bs.jnonogram.core;

import javafx.concurrent.Task;
import org.bs.jnonogram.core.jax.GameDescriptor;
import org.bs.jnonogram.core.jax.Player;
import org.bs.jnonogram.core.jax.Slice;
import org.bs.jnonogram.core.jax.Square;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class GameInfoFactory {
    private static final int _max_dimensions = 99;
    private static final int _min_dimensions = 10;

    public static GameInfo loadFromXml(String path) throws GameInfoFactoryException {
        return loadFromXml(new File(path));
    }

    private static GameDescriptor loadDescriptorFromFile(File file) throws GameInfoFactoryException {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(GameDescriptor.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (GameDescriptor) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new GameInfoFactoryException("Invalid XML", e);
        }
    }

    public static GameInfo loadFromXml(File file) throws GameInfoFactoryException {
        Task<GameInfo> task = loadFromXmlAsync(file);
        task.run();
        try {
            return task.get();
        } catch (InterruptedException e) {
            // Should never happen
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw (GameInfoFactoryException) e.getCause();
        }
    }

    public static Task<GameInfo> loadFromXmlAsync(File file) {
        return new Task<GameInfo>() {
            @Override
            protected GameInfo call() throws Exception {
                final long fakeDelay = 100; // Used so we actually see the stages in the UI
                updateProgress(0, 4);
                updateTitle("Load Nonogram Game File");
                updateMessage("Loading xml");
                Thread.sleep(fakeDelay);
                GameDescriptor descriptor = loadDescriptorFromFile(file);
                _ValidateDescriptor(descriptor);
                updateMessage("Initializing data");
                updateProgress(1, 4);
                Thread.sleep(fakeDelay);
                NonogramBuilder nonogramBuilder = new NonogramBuilder(
                        descriptor.getBoard().getDefinition().getRows().intValue(),
                        descriptor.getBoard().getDefinition().getColumns().intValue()
                );
                updateMessage("Loading nonogram");
                updateProgress(2, 4);
                Thread.sleep(fakeDelay);
                _loadSlices(descriptor, nonogramBuilder);
                _loadSolution(descriptor, nonogramBuilder);
                Nonogram nonogram = nonogramBuilder.build();
                updateMessage("Validating nonogram");
                updateProgress(3, 4);
                Thread.sleep(fakeDelay);
                _validateNonogram(nonogram);
                GameTypeInfo gameTypeInfo = _loadGameTypeInfo(descriptor);

                updateMessage("Finished Loading nonogram");
                updateProgress(4, 4);
                return new GameInfo(
                        nonogram,
                        gameTypeInfo);
            }
        };
    }

    private static void _ValidateDescriptor(GameDescriptor descriptor) throws GameInfoFactoryException  {
        if(descriptor.getBoard() == null)
        {
            throw new GameInfoFactoryException("Board tag missing");
        }
        if(descriptor.getBoard().getSolution() == null)
        {
            throw new GameInfoFactoryException("Solution missing");
        }
        if(descriptor.getBoard().getDefinition() == null)
        {
            throw new GameInfoFactoryException("Board Definition missing");
        }
        if(descriptor.getBoard().getDefinition().getRows() == null)
        {
            throw new GameInfoFactoryException("Row tag is missing");
        }
        if(descriptor.getBoard().getDefinition().getColumns() == null)
        {
            throw new GameInfoFactoryException("Column tag is missing");
        }
        if(descriptor.getBoard().getDefinition().getRows().intValue() > _max_dimensions || descriptor.getBoard().getDefinition().getColumns().intValue() > _max_dimensions)
        {
            throw new GameInfoFactoryException("Rows and Columns must be less or equal to " + _max_dimensions);
        }
        if(descriptor.getBoard().getDefinition().getRows().intValue() < _min_dimensions || descriptor.getBoard().getDefinition().getColumns().intValue() < _min_dimensions)
        {
            throw new GameInfoFactoryException("Rows and Columns must be greater or equal to " + _min_dimensions);
        }
        if(descriptor.getBoard().getDefinition().getSlices() == null)
        {
            throw new GameInfoFactoryException("Slices tag missing");
        }
        if(descriptor.getGameType() == null)
        {
            throw new GameInfoFactoryException("Game Type missing");
        }
        if(descriptor.getBoard().getDefinition().getSlices().getSlice().size() >
                descriptor.getBoard().getDefinition().getColumns().intValue() +
                        descriptor.getBoard().getDefinition().getRows().intValue())
        {
            throw new GameInfoFactoryException("Number of slices exceed Columns and rows size definition");
        }
    }
    private static void validateMultiPlayerDescriptor(GameDescriptor descriptor) throws GameInfoFactoryException {
        if (descriptor.getMultiPlayers() == null)
        {
            throw new GameInfoFactoryException("Missing Multi Player Data");
        }
        try {
            int moves = Integer.parseInt(descriptor.getMultiPlayers().getMoves());
            if (moves < 1)
            {
                throw new GameInfoFactoryException("Illegal number of moves entered");
            }
        } catch(Exception e) {
            throw new GameInfoFactoryException("Number of moves is not Legal. Enter a positive int");
        }

        if (descriptor.getMultiPlayers().getPlayers() == null ||
                descriptor.getMultiPlayers().getPlayers().getPlayer() == null ||
                descriptor.getMultiPlayers().getPlayers().getPlayer().size() < 1)
        {
            throw new GameInfoFactoryException("Missing Players");
        }
        Set<Integer> ids = new HashSet<Integer>();
        for (Player player : descriptor.getMultiPlayers().getPlayers().getPlayer()){
            if(player.getName() == null)
            {
                throw new GameInfoFactoryException("Missing player name");
            }
            if(player.getId() == null)
            {
                throw new GameInfoFactoryException("Missing player Id");
            }
            if(ids.contains(player.getId().intValue())){
                throw new GameInfoFactoryException("Ids are not unique");
            }
            ids.add(player.getId().intValue());
        }
    }

    private static GameTypeInfo _loadGameTypeInfo(GameDescriptor descriptor) throws GameInfoFactoryException {
        GameTypeInfo gameTypeInfo = new GameTypeInfo();
        if (descriptor.getGameType().equalsIgnoreCase("SinglePlayer")) {
            gameTypeInfo.getPlayersInformation().add(new PlayerInfo(1, "Player", PlayerType.Human));
            gameTypeInfo.setMaxMoves(-1);
            gameTypeInfo.setTitle("Single Player Game");
        } else if (descriptor.getGameType().equalsIgnoreCase("MultiPlayer")) {
            validateMultiPlayerDescriptor(descriptor);
            gameTypeInfo.setMaxMoves(Integer.valueOf(descriptor.getMultiPlayers().getMoves()));
            for(Player player : descriptor.getMultiPlayers().getPlayers().getPlayer()) {
                PlayerType playerType;
                if (player.getPlayerType().equalsIgnoreCase("human")) {
                    playerType = PlayerType.Human;
                } else if (player.getPlayerType().equalsIgnoreCase("computer")) {
                    playerType = PlayerType.Computer;
                } else {
                    throw new GameInfoFactoryException("Invalid Player Type");
                }

                gameTypeInfo.getPlayersInformation().add(
                        new PlayerInfo(player.getId().intValue(),
                        player.getName(),
                        playerType));
            }

            gameTypeInfo.setTitle("Multi Player Game");
        } else if (descriptor.getGameType().equalsIgnoreCase("DynamicMultiPlayer")) {
            throw new GameInfoFactoryException("DynamicMultiPlayer not yet supported");
        }
        else {
            throw new GameInfoFactoryException("Invalid or missing Game Type");
        }

        return gameTypeInfo;
    }

    private static void _validateNonogram(Nonogram nonogram) throws GameInfoFactoryException {
        _validateConstraints(nonogram);
        //_validatesolution(nonogram);
    }

    private static void _validatesolution(Nonogram nonogram)throws GameInfoFactoryException
    {
        NonogramConstraint[] rowConstraints = NonogramConstraint.clone(nonogram.getRowConstraints());
        NonogramConstraint[] columnConstraints = NonogramConstraint.clone(nonogram.getColumnConstraints());

        nonogram.updateSatisfiedConstraints(rowConstraints, columnConstraints, nonogram.getSolution());

        for (NonogramConstraint constraint : rowConstraints) {
            for (int i = 0 ; i < constraint.count(); i++)
            {
                if(!constraint.getSlice(i).isSatisfied())
                {
                    throw new GameInfoFactoryException("Solution doesn't Match constraints");
                }
            }
        }

        for (NonogramConstraint constraint : columnConstraints) {
            for (int i = 0 ; i < constraint.count(); i++)
            {
                if(!constraint.getSlice(i).isSatisfied()) {
                    throw new GameInfoFactoryException("Solution doesn't Match constraints");
                }
            }
        }
    }

    private static void _validateConstraints(Nonogram nonogram) throws GameInfoFactoryException {
        int constraintIndex = 0;
        for (NonogramConstraint constraint : nonogram.getColumnConstraints()) {
            constraintIndex++;
            if (constraint == null || constraint.count() == 0)
            {
                throw new GameInfoFactoryException("Missing constraints: column constraint number " + constraintIndex);
            }
        }

        constraintIndex = 0;
        for (NonogramConstraint constraint : nonogram.getRowConstraints()) {
            constraintIndex++;
            if (constraint == null || constraint.count() == 0)
            {
                throw new GameInfoFactoryException("Missing constraints: row constraint number " + constraintIndex);
            }
        }
    }

    private static void _loadSolution(GameDescriptor descriptor, NonogramBuilder nonogramBuilder) throws GameInfoFactoryException {
        for (Square square: descriptor.getBoard().getSolution().getSquare()) {
            if(square.getColumn().intValue() > descriptor.getBoard().getDefinition().getColumns().intValue())
            {
                throw new GameInfoFactoryException("Solution block exceed columns size definition, illegal value : " + square.getColumn().intValue());
            }
            if(square.getRow().intValue() > descriptor.getBoard().getDefinition().getRows().intValue())
            {
                throw new GameInfoFactoryException("Solution block exceed rows size definition, illegal value : " + square.getRow().intValue());
            }
            nonogramBuilder.addSolutionBlock(square.getColumn().intValue() - 1, square.getRow().intValue() - 1);

        }
    }

    private static boolean _loadSlices(GameDescriptor descriptor, NonogramBuilder nonogramBuilder) throws GameInfoFactoryException {
        int rowsSize = descriptor.getBoard().getDefinition().getRows().intValue();
        int columnsSize = descriptor.getBoard().getDefinition().getColumns().intValue();

        for(Slice slice : descriptor.getBoard().getDefinition().getSlices().getSlice()) {
            SliceOrientation orientation;
            if (slice.getOrientation().equalsIgnoreCase("row")) {
                orientation = SliceOrientation.Row;
                if(slice.getId().intValue() > rowsSize)
                {
                    throw new GameInfoFactoryException("A slice is defined in a row number that is larger than number of rows, , illegal value :  "+ slice.getId().intValue());
                }
            }
            else if (slice.getOrientation().equalsIgnoreCase("column")) {
                orientation = SliceOrientation.Column;
                if(slice.getId().intValue() > columnsSize)
                {
                    throw new GameInfoFactoryException("A slice is defined in a column number that is larger than number of columns, , illegal value : "+ slice.getId().intValue());
                }
            } else {
                throw new GameInfoFactoryException("Invalid orientation: orientation value must be either 'column' or 'row'");
            }
            if(slice.getBlocks() == null)
            {
                throw new GameInfoFactoryException("Missing Block definition for Slice");
            }
            ArrayList<Integer> blocks = new ArrayList<>();
            int blockSum = 0;
            for(String block: slice.getBlocks().split(",")) {
                blocks.add(Integer.valueOf(block.trim()));
                blockSum += Integer.valueOf(block.trim());
            }

            if(orientation == SliceOrientation.Column && blockSum > columnsSize)
            {
                throw new GameInfoFactoryException("Column blocks sum cannot be larger than column size itself, illegal Block Sum: " + blockSum);
            }
            if(orientation == SliceOrientation.Row && blockSum > rowsSize)
            {
                throw new GameInfoFactoryException("Column blocks sum cannot be larger than column size itself, illegal Block Sum: " + blockSum);

            }

            nonogramBuilder.addSlice(orientation, slice.getId().intValue() - 1, blocks);
        }

        return true;
    }
}
