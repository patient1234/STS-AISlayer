package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;

import static aislayer.AISlayer.*;

@SpirePatch(
        clz = BossRelicSelectScreen.class,
        method = "open"
)
public class SelectBossRelicPatch {
    @SpirePostfixPatch
    public static void Postfix() {
        if (isAIStart()) {
            String todo = "选择一个遗物(可以不选): " + AbstractDungeon.bossRelicScreen.relics;
            allRelics.addAll(AbstractDungeon.bossRelicScreen.relics);
            AIUtils.action(getInfo(todo));
        }
    }
}
