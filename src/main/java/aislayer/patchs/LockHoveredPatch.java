package aislayer.patchs;

import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import static aislayer.AISlayer.logger;

@SpirePatch(
        clz = InputHelper.class,
        method = "updateFirst"
)
public class LockHoveredPatch {

    private static int lockedTime = 0;

    @SpirePostfixPatch
    public static void Postfix() {
        Hitbox hb = AIUtils.lockedHoveredHitbox;
        if (hb != null) {
            if (hb.clicked && lockedTime++ < 5) {
                logger.info("鼠标锁定在{}", hb);
                InputHelper.mX = (int) (hb.x + hb.width / 2);
                InputHelper.mY = (int) (hb.y + hb.height / 2);
            } else {
                AIUtils.lockedHoveredHitbox = null;
                lockedTime = 0;
            }
        }
    }
}
