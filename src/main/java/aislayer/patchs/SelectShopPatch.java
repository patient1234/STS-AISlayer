package aislayer.patchs;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static aislayer.AISlayer.isAIStart;
import static aislayer.utils.AIUtils.lockedHoveredHitbox;

@SpirePatch(
        clz = ShopScreen.class,
        method = "open"
)
public class SelectShopPatch {

    public static final Logger logger = LogManager.getLogger(SelectShopPatch.class.getName());
    public static ArrayList<ShopItem> shopItems = new ArrayList<>();

    public static class ShopItem {
        public String name;
        public int price;
        public Hitbox hb;
    }

    public static class CardShopItem extends ShopItem {
        public AbstractCard card;

        public CardShopItem(AbstractCard card) {
            this.name = card.name;
            this.price = card.price;
            this.card = card;
            this.hb = card.hb;
        }

        @Override
        public String toString() {
            return "卡牌:" + name + "(" + price + "金币)";
        }
    }

    public static class PotionShopItem extends ShopItem {
        public AbstractPotion potion;

        public PotionShopItem(StorePotion potion) {
            this.name = potion.potion.name;
            this.price = potion.price;
            this.potion = potion.potion;
            this.hb = potion.potion.hb;
        }

        @Override
        public String toString() {
            return "药水:" + name + "(" + price + "金币)";
        }
    }

    public static class RelicShopItem extends ShopItem {
        public AbstractRelic relic;

        public RelicShopItem(StoreRelic relic) {
            this.name = relic.relic.name;
            this.price = relic.price;
            this.relic = relic.relic;
            this.hb = relic.relic.hb;
        }

        @Override
        public String toString() {
            return "遗物:" + name + "(" + price + "金币)";
        }
    }

    public static class PurgeShopItem extends ShopItem {
        public PurgeShopItem() {
            this.name = "移除卡牌";
            this.price = ShopScreen.actualPurgeCost;
            float rugY = (float)Settings.HEIGHT;
            float BOTTOM_ROW_Y = 337.0F * Settings.yScale;
            float purgeCardX = 1554.0F * Settings.xScale;
            float purgeCardY = rugY + BOTTOM_ROW_Y;
            float CARD_W = 110.0F * Settings.scale;
            float CARD_H = 150.0F * Settings.scale;
            this.hb = new Hitbox(purgeCardX, purgeCardY, CARD_W, CARD_H);
        }

        @Override
        public String toString() {
            return "移除卡牌(" + price + "金币)";
        }
    }

    @SpirePostfixPatch
    public static void Postfix(ShopScreen __instance) {
        if (isAIStart()) {
            try {
                for (AbstractCard coloredCard : __instance.coloredCards) {
                    shopItems.add(new CardShopItem(coloredCard));
                }
                for (AbstractCard colorlessCard : __instance.colorlessCards) {
                    shopItems.add(new CardShopItem(colorlessCard));
                }

                Field potionsField = ShopScreen.class.getDeclaredField("potions");
                potionsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                ArrayList<StorePotion> potions = (ArrayList<StorePotion>) potionsField.get(__instance);
                for (StorePotion potion : potions) {
                    shopItems.add(new PotionShopItem(potion));
                }

                Field relicsField = ShopScreen.class.getDeclaredField("relics");
                relicsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) relicsField.get(__instance);
                for (StoreRelic relic : relics) {
                    shopItems.add(new RelicShopItem(relic));
                }

                PurgeShopItem purgeShopItem = new PurgeShopItem();
                shopItems.add(purgeShopItem);
                lockedHoveredHitbox = purgeShopItem.hb;

                logger.info("商店物品: {}", shopItems);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("获取ShopScreen按钮失败", e);
            }
        }
    }
}
