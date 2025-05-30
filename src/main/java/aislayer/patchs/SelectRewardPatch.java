package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.ArrayList;

import static aislayer.AISlayer.*;

public class SelectRewardPatch {

    private static void patch() {
        if (isAIStart()) {
            ArrayList<String> todoArray = new ArrayList<>();
            ArrayList<RewardItem> rewards = AbstractDungeon.combatRewardScreen.rewards;
            for (RewardItem reward : rewards) {
                if (reward.type == RewardItem.RewardType.CARD) {
                    allCards.addAll(reward.cards);
                    todoArray.add(reward.text + reward.cards + "(现在不能直接选牌，必须选择此项后进入选牌界面)");
                } else if (reward.relicLink != null) {
                    todoArray.add(reward.text + "(选择这个后，不能选择" + reward.relicLink.text + ")");
                } else {
                    todoArray.add(reward.text);
                }
                switch (reward.type) {
                    case RELIC:
                        allRelics.add(reward.relic);
                        break;
                    case POTION:
                        allPotions.add(reward.potion);
                        break;
                    default:
                        break;
                }
            }
            String todo = "选择任意奖励(可以全选或者不选，建议都选上): " + todoArray;
            AIUtils.action(getInfo(todo));
        }
    }

    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "open",
            paramtypez = {
                    String.class
            }
    )
    public static class SelectRewardSetLabel {
        @SpirePostfixPatch
        public static void Postfix() {
            patch();
        }
    }

    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "open",
            paramtypez = {}
    )
    public static class SelectReward {
        @SpirePostfixPatch
        public static void Postfix() {
            patch();
        }
    }
}