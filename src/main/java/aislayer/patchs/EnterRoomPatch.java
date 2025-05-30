package aislayer.patchs;

import aislayer.AISlayer;
import aislayer.panels.ConfigPanel;
import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import static aislayer.AISlayer.*;

public class EnterRoomPatch {

    public static final Logger logger = LogManager.getLogger(EnterRoomPatch.class.getName());

    private static void preEnter() {
        if (isAIStart()) {
            AISlayer.knownCards.clear();
            AISlayer.knownPotions.clear();
            AISlayer.knownRelics.clear();
            AISlayer.knownKeywords.clear();
            AISlayer.allDescriptions.clear();
            AISlayer.allCards.clear();
            AISlayer.allPotions.clear();
            AISlayer.allRelics.clear();

            String systemMessage = "你是一个专业的杀戮尖塔高手玩家，请根据游戏信息和信息里面当前你需要做的事直接执行你的下一步操作，任何操作都只能调用函数工具，禁止输出任何其他内容，如果要回复文字内容就使用" + ConfigPanel.language + "语言，比如行动理由，注意遵守游戏规则，有策略有头脑地完成这局游戏。如果要击杀最终BOSS心脏需要在第三章结束前拿到红绿蓝三种钥匙，红宝石在火堆，蓝宝石在非BOSS宝箱，绿宝石在每一层的加强精英战利品。";

            AIUtils.messagesArray = new JSONArray();
            JSONObject msg = new JSONObject();
            msg.put("role", "system");
            msg.put("content", systemMessage);
            AIUtils.messagesArray.put(msg);

            logger.info("[进入新房间,AI记忆已清空]{}", systemMessage);

        }
    }

    private  static void postEnter(MapRoomNode roomNode) {
        AbstractRoom room = roomNode.getRoom();
        String roomName = room.getClass().getSimpleName();
        String todo = "";
        switch (roomName) {
            case "TreasureRoom":
                todo = "是否打开宝箱(可能有遗物、蓝宝石)";
                break;
            case "TreasureRoomBoss":
                todo = "是否打开BOSS宝箱(BOSS遗物三选一)";
                break;
            case "NeowRoom":
            case "EventRoom":
            case "ShopRoom":
            case "RestRoom":
            default:
                logger.info("当前房间: {}({})", roomName, roomNode);
                break;
        }
        if (!todo.isEmpty()) {
            AIUtils.action(getInfo(todo));
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "populatePathTaken"
    )
    public static class EnterRoomLoad {
        @SpirePrefixPatch
        public static void Prefix() {
            preEnter();
        }
        @SpirePostfixPatch
        public static void Postfix() {
            postEnter(AbstractDungeon.getCurrMapNode());
        }
    }

    @SpirePatch(
            clz = SaveFile.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    SaveFile.SaveType.class
            }
    )
    public static class EnterRoomSave {
        @SpirePrefixPatch
        public static void Prefix() {
            preEnter();
        }
        @SpirePostfixPatch
        public static void Postfix() {
            if (AbstractDungeon.nextRoom != null && !(AbstractDungeon.getCurrRoom() instanceof NeowRoom)) {
                postEnter(AbstractDungeon.nextRoom);
            } else {
                postEnter(AbstractDungeon.getCurrMapNode());
            }
        }
    }

}