package aislayer.patchs;

import aislayer.AISlayer;
import aislayer.utils.AIUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz = CampfireUI.class,
        method = SpirePatch.CONSTRUCTOR
)
public class SelectCampfirePatch {

    public static final Logger logger = LogManager.getLogger(SelectCampfirePatch.class.getName());
    public static ArrayList<AbstractCampfireOption> buttons = new ArrayList<>();

    private static ArrayList<String> getCampfire(CampfireUI campfireUI) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<String> Campfire = new ArrayList<>();
        Field buttonsField = CampfireUI.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        buttons = (ArrayList<AbstractCampfireOption>) buttonsField.get(campfireUI);
        for (AbstractCampfireOption button : buttons) {
            Field labelField = AbstractCampfireOption.class.getDeclaredField("label");
            labelField.setAccessible(true);
            String label = (String) labelField.get(button);
            Field descriptionField = AbstractCampfireOption.class.getDeclaredField("description");
            descriptionField.setAccessible(true);
            String description;
            if (button.usable) {
                description = (String) descriptionField.get(button);
            } else {
                description = "不可用";
            }
            Campfire.add(label + "(" + description + ")");
        }
        return Campfire;
    }

    @SpirePostfixPatch
    public static void Postfix(CampfireUI __instance) {
        if (AISlayer.isAIStart()) {
            try {
                ArrayList<String> Campfire = getCampfire(__instance);
                String todo = "用select选择一个火堆行为: " + Campfire;
                AIUtils.action(AISlayer.getInfo(todo));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("获取CampfireUI按钮失败", e);
            }
        }
    }
}
