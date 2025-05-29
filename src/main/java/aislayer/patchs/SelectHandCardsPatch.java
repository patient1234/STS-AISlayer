package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;

import static aislayer.AISlayer.getInfo;
import static aislayer.AISlayer.isAIStart;

public class SelectHandCardsPatch {

    private static String getMessage(int numCardsToSelect, boolean anyNumber, String selectionReason) {
        String message;
        if ( !anyNumber ) {
            message = "选择 " + numCardsToSelect + " 张牌来" + selectionReason;
        } else {
            message = "选择任意张牌来" + selectionReason + "(可以不选)";
        }
        return message;
    }

    private static void patch(int numCardsToSelect, boolean anyNumber, String selectionReason) {
        if (isAIStart()) {
            String todo = getMessage(numCardsToSelect, anyNumber, selectionReason) + ": " + AbstractDungeon.player.hand.group;
            AIUtils.action(getInfo(todo));
        }
    }

    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "open",
            paramtypez = {
                    String.class,
                    int.class,
                    boolean.class,
                    boolean.class
            }
    )
    public static class SelectHandCardsNotForChange {
        @SpireInsertPatch(
                rloc = 0,
                localvars = {"amount", "anyNumber", "msg"}
        )
        public static void Insert(int amount, boolean anyNumber, String msg) {
            patch(amount, anyNumber, msg);
        }
    }

    @SpirePatch(
            clz = HandCardSelectScreen.class,
            method = "open",
            paramtypez = {
                    String.class,
                    int.class,
                    boolean.class,
                    boolean.class,
                    boolean.class,
                    boolean.class,
                    boolean.class
            }
    )
    public static class SelectHandCardsForChange {
        @SpireInsertPatch(
                rloc = 0,
                localvars = {"amount", "anyNumber", "msg"}
        )
        public static void Insert(int amount, boolean anyNumber, String msg) {
            patch(amount, anyNumber, msg);
        }
    }

}