package aislayer.patchs;

import aislayer.AISlayer;
import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import static aislayer.AISlayer.getInfo;
import static aislayer.AISlayer.isAIStart;

@SpirePatch(
        clz = AbstractMonster.class,
        method = "update"
)
public class PlayFirstCardPatch {

    @SpirePostfixPatch
    public static void Postfix(AbstractMonster __instance) {
        if (
                __instance.intent != AbstractMonster.Intent.DEBUG
                        && AbstractDungeon.actionManager.phase == GameActionManager.Phase.WAITING_ON_USER
                        && AbstractDungeon.overlayMenu.endTurnButton.enabled
                        && !AISlayer.intentUpdated
                        && isAIStart()
        ) {
            AISlayer.intentUpdated = true;

            String langPackDir = "aislayerResources" + File.separator + "localization" + File.separator + Settings.language.toString().toLowerCase();
            String textPath = langPackDir + File.separator + "text.json";
            JSONArray text = (new JSONObject(AISlayer.loadJson(textPath))).getJSONArray("thinking");
            String thinking = text.getString((int) (Math.random() * text.length()));
            AbstractDungeon.actionManager.addToBottom(new TalkAction(true, thinking, 4.0F, 4.0F));

            String todo = "现在你可以选择使用药水、打出手牌、结束回合";
            AIUtils.action(getInfo(todo));

        }
    }

}
