package org.bs.jnonogram.core;

import org.bs.jnonogram.core.jax.GameDescriptor;
import org.bs.jnonogram.core.jax.Player;
import org.bs.jnonogram.core.jax.Slice;
import org.bs.jnonogram.core.jax.Square;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameInfoFactory {
    public static GameInfo loadFromXml(String path) throws IOException, SAXException {
        return loadFromXml(new File(path));
    }

    public static GameInfo loadFromXml(File file) throws IOException, SAXException {
        GameDescriptor descriptor;
        try {
            JAXBContext context = JAXBContext.newInstance(GameDescriptor.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            descriptor = (GameDescriptor) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            return null;
        }

        NonogramBuilder nonogramBuilder = new NonogramBuilder(
                descriptor.getBoard().getDefinition().getRows().intValue(),
                descriptor.getBoard().getDefinition().getColumns().intValue()
        );
        for(Slice slice : descriptor.getBoard().getDefinition().getSlices().getSlice()) {
            SliceOrientation orientation;
            if (slice.getOrientation().equalsIgnoreCase("row")) {
                orientation = SliceOrientation.Row;
            }
            else if (slice.getOrientation().equalsIgnoreCase("column")) {
                orientation = SliceOrientation.Column;
            } else {
                return null;
            }

            ArrayList<Integer> blocks = new ArrayList<>();
            for(String block: slice.getBlocks().split(",")) {
                blocks.add(Integer.valueOf(block.trim()));
            }

            nonogramBuilder.addSlice(orientation, slice.getId().intValue() - 1, blocks);
        }

        for (Square square: descriptor.getBoard().getSolution().getSquare()) {
            nonogramBuilder.addSolutionBlock(square.getColumn().intValue() - 1, square.getRow().intValue() - 1);
        }

        ArrayList<PlayerInfo> playerInfos = new ArrayList<>();
        int maxMoves;
        String title;
        if (descriptor.getGameType().equalsIgnoreCase("SinglePlayer")) {
            playerInfos.add(new PlayerInfo(1, "Player", PlayerType.Human));
            maxMoves = -1;
            title = "Single Player Game";
        } else if (descriptor.getGameType().equalsIgnoreCase("MultiPlayer")) {
            maxMoves = Integer.valueOf(descriptor.getMultiPlayers().getMoves());
            for(Player player : descriptor.getMultiPlayers().getPlayers().getPlayer()) {
                PlayerType playerType;
                if (player.getPlayerType().equalsIgnoreCase("human")) {
                    playerType = PlayerType.Human;
                } else if (player.getPlayerType().equalsIgnoreCase("computer")) {
                    playerType = PlayerType.Computer;
                } else {
                    return null;
                }

                playerInfos.add(new PlayerInfo(player.getId().intValue(),
                        player.getName(),
                        playerType));
            }

            title = "Multi Player Game";
        } else if (descriptor.getGameType().equalsIgnoreCase("DynamicMultiPlayer")) {
            //TODO
            maxMoves = -1;
            title = "No Title";
        }
        else {
            return null;
        }

        return new GameInfo(
                nonogramBuilder.build(),
                maxMoves,
                title,
                playerInfos);
    }
}
