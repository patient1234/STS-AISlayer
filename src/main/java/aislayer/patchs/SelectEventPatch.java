package aislayer.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;

import java.util.ArrayList;

import static aislayer.AISlayer.logger;

@SpirePatch(
        clz = RoomEventDialog.class,
        method = "update"
)
public class SelectEventPatch {
    @SpireInsertPatch(
            rloc = 4
    )
    public static void Insert(RoomEventDialog __instance) {
        ArrayList<LargeDialogOptionButton> optionList = RoomEventDialog.optionList;
        StringBuilder events = new StringBuilder();
        for (LargeDialogOptionButton optionButton : optionList) {
            events.append(optionButton.msg).append("\n");
        }
        logger.info("[事件选项]{}", events.toString());
    }
}
