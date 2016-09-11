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
import java.util.concurrent.ExecutionException;

public class GameInfoFactory {
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

    private static GameTypeInfo _loadGameTypeInfo(GameDescriptor descriptor) throws GameInfoFactoryException {
        GameTypeInfo gameTypeInfo = new GameTypeInfo();
        if (descriptor.getGameType().equalsIgnoreCase("SinglePlayer")) {
            gameTypeInfo.getPlayersInformation().add(new PlayerInfo(1, "Player", PlayerType.Human));
            gameTypeInfo.setMaxMoves(-1);
            gameTypeInfo.setTitle("Single Player Game");
        } else if (descriptor.getGameType().equalsIgnoreCase("MultiPlayer")) {
            gameTypeInfo.setMaxMoves(Integer.valueOf(descriptor.getMultiPlayers().getMoves()));
            for(Player player : descriptor.getMultiPlayers().getPlayers().getPlayer()) {
                PlayerType playerType;
                if (player.getPlayerType().equalsIgnoreCase("human")) {
                    playerType = PlayerType.Human;
                } else if (player.getPlayerType().equalsIgnoreCase("computer")) {
                    playerType = PlayerType.Computer;
                } else {
                    return null;
                }

                gameTypeInfo.getPlayersInformation().add(
                        new PlayerInfo(player.getId().intValue(),
                        player.getName(),
                        playerType));
            }

            gameTypeInfo.setTitle("Multi Player Game");
        } else if (descriptor.getGameType().equalsIgnoreCase("DynamicMultiPlayer")) {
            //TODO
        }
        else {
            throw new GameInfoFactoryException("Invalid or missing Game Type");
        }

        return gameTypeInfo;
    }

    private static void _validateNonogram(Nonogram nonogram) throws GameInfoFactoryException {
        _validateConstraints(nonogram);
        _validatesolution(nonogram);
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
        for (NonogramConstraint constraint : nonogram.getColumnConstraints()) {
            if (constraint.count() == 0)
            {
                throw new GameInfoFactoryException("Missing constraints");
            }
        }

        for (NonogramConstraint constraint : nonogram.getRowConstraints()) {
            if (constraint == null || constraint.count() == 0)
            {
                throw new GameInfoFactoryException("Missing constraints");
            }
        }
    }

    private static void _loadSolution(GameDescriptor descriptor, NonogramBuilder nonogramBuilder) {
        for (Square square: descriptor.getBoard().getSolution().getSquare()) {
            nonogramBuilder.addSolutionBlock(square.getColumn().intValue() - 1, square.getRow().intValue() - 1);
        }
    }

    private static boolean _loadSlices(GameDescriptor descriptor, NonogramBuilder nonogramBuilder) throws GameInfoFactoryException {
        for(Slice slice : descriptor.getBoard().getDefinition().getSlices().getSlice()) {
            SliceOrientation orientation;
            if (slice.getOrientation().equalsIgnoreCase("row")) {
                orientation = SliceOrientation.Row;
            }
            else if (slice.getOrientation().equalsIgnoreCase("column")) {
                orientation = SliceOrientation.Column;
            } else {
                throw new GameInfoFactoryException("Invalid orientation");
            }

            ArrayList<Integer> blocks = new ArrayList<>();
            for(String block: slice.getBlocks().split(",")) {
                blocks.add(Integer.valueOf(block.trim()));
            }

            nonogramBuilder.addSlice(orientation, slice.getId().intValue() - 1, blocks);
        }

        return true;
    }
}
