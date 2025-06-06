package aislayer.panels;

import basemod.EasyConfigPanel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import static aislayer.AISlayer.loadJson;

public class ConfigPanel extends EasyConfigPanel {

    public static boolean selectedPlatform_1 = true;
    public static String apiKey_1 = "sk-...";
    public static String apiUrl_1 = "api.deepseek.com/v1";
    public static boolean selectedModel_1_1 = true;
    public static String model_1_1 = "deepseek-chat";
    public static boolean selectedModel_1_2 = false;
    public static String model_1_2 = "deepseek-chat";

    public static boolean selectedPlatform_2 = false;
    public static String apiKey_2 = "sk-...";
    public static String apiUrl_2 = "api.openai.com/v1";
    public static boolean selectedModel_2_1 = false;
    public static String model_2_1 = "gpt-3.5-turbo";
    public static boolean selectedModel_2_2 = false;
    public static String model_2_2 = "gpt-4";

    public static String language = "中文";
    public static boolean handleApiUrl = true;

    public ConfigPanel() {
        super("aislayer", getUIStrings(), "config");
        setupTextField("apiUrl_1", 500, 100);
        setupTextField("apiKey_1", 750, 100);
        setupTextField("apiUrl_2", 500, 100);
        setupTextField("apiKey_2", 750, 100);
        setPadding(30.0F);
    }

    private static UIStrings getUIStrings() {
        String langPackDir = "aislayerResources" + File.separator + "localization" + File.separator + Settings.language.toString().toLowerCase();
        String uiPath = langPackDir + File.separator + "ui.json";
        Gson gson = new Gson();
        Type uiType = (new TypeToken<Map<String, UIStrings>>() {
        }).getType();
        Map<String, UIStrings> ui = gson.fromJson(loadJson(uiPath), uiType);
        return ui.get("aislayer:Config");
    }

}
