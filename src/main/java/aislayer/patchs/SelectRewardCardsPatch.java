package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import static aislayer.AISlayer.*;

@SpirePatch(
        clz = CardRewardScreen.class,
        method = "open"
)
public class SelectRewardCardsPatch {
    @SpireInsertPatch(
            rloc = 0,
            localvars = {"rItem"}
    )
    public static void Insert(RewardItem rItem) {
        if (isAIStart()) {
            String todo = rItem.text+ "(可以不选)" + ": " + rItem.cards;
            allCards.addAll(rItem.cards);
            AIUtils.action(getInfo(todo));
        }
    }
}
