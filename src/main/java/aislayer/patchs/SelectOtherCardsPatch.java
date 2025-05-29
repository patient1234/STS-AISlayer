package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

import static aislayer.AISlayer.*;

@SpirePatch(
        clz = GridCardSelectScreen.class,
        method = "open",
        paramtypez = {
                CardGroup.class,
                int.class,
                String.class,
                boolean.class,
                boolean.class,
                boolean.class,
                boolean.class
        }
)
public class SelectOtherCardsPatch {

    @SpireInsertPatch(
            rloc = 0,
            localvars = {"group", "tipMsg"}
    )
    public static void Insert(CardGroup group ,String tipMsg) {
        if (isAIStart()) {
            String todo = tipMsg.replace("。", "") + ": " + group.group;
            allCards.addAll(group.group);
            AIUtils.action(getInfo(todo));
        }
    }
}
