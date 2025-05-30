package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static aislayer.AISlayer.*;

@SpirePatch(
        clz = DungeonMapScreen.class,
        method = "open"
)
public class SelectRoomPatch {

    public static final Logger logger = LogManager.getLogger(SelectRoomPatch.class.getName());
    @SpirePostfixPatch
    public static void Postfix() {
        if (
                AbstractDungeon.getCurrRoom().rewardTime
                        || AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE
        ) {
            if (isAIStart()) {
                ArrayList<ArrayList<MapRoomNode>> mapPaths = getMapPaths(AbstractDungeon.getCurrMapNode());

                logger.info("当前房间: {}", AbstractDungeon.getCurrMapNode());
                String todo = "从以下给出的若干路线中选择一条接下来你要走的路线(如果只有一条那就选那唯一的一条): " + mapPathsToString(mapPaths);
                AIUtils.action(getInfo(todo));
            }
        }
    }
}
