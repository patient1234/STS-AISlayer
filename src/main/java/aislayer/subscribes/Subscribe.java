package aislayer.subscribes;

import aislayer.AISlayer;
import aislayer.panels.ConfigPanel;
import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpireInitializer
public class Subscribe implements
        OnStartBattleSubscriber,
        PostInitializeSubscriber {

    public Subscribe() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new Subscribe();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        AISlayer.intentUpdated = false;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = ImageMaster.loadImage("aislayerResources/images/badge.png");
        BaseMod.registerModBadge(badgeTexture, "AI爬塔", "河童", "让AI来爬塔!", new ConfigPanel());
    }
}
