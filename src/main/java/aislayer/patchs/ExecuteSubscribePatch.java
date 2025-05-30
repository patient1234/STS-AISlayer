package aislayer.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import static aislayer.AISlayer.logger;

@SpirePatch(
        clz = AbstractPlayer.class,
        method = "update"
)
public class ExecuteSubscribePatch {

    private static AbstractGameAction action_pre = null;

    @SpirePostfixPatch
    public static void Postfix() {

        GameActionManager am = AbstractDungeon.actionManager;
        GameActionManager.Phase phase = am.phase;
        GameActionManager.Phase executing = GameActionManager.Phase.EXECUTING_ACTIONS;
        AbstractGameAction action_current = am.currentAction;

        if (phase == executing) {

            if(action_current != null) {

                if (action_current != action_pre) {

                    action_pre = action_current;

                    String actionName = action_current.getClass().getSimpleName();
                    String infoAction = "[执行]: " + actionName;
                    logger.info(infoAction);

                }
            } else {
                logger.info("为啥没有行动?");
            }
        }
    }
}
