package aislayer.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SpirePatch(
        clz = RoomEventDialog.class,
        method = "update"
)
public class SelectEventPatch {

    public static final Logger logger = LogManager.getLogger(SelectEventPatch.class.getName());
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
