package aislayer;

import aislayer.panels.ConfigPanel;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AISlayer {

    public static boolean intentUpdated = false;
    public static ArrayList<String> knownPotions = new ArrayList<>();
    public static ArrayList<String> knownRelics = new ArrayList<>();
    public static ArrayList<String> knownCards = new ArrayList<>();
    public static ArrayList<String> knownKeywords = new ArrayList<>();
    public static ArrayList<String> allDescriptions = new ArrayList<>();
    public static ArrayList<AbstractCard> allCards = new ArrayList<>();
    public static ArrayList<AbstractRelic> allRelics = new ArrayList<>();
    public static ArrayList<AbstractPotion> allPotions = new ArrayList<>();

    public static String apiKey;
    public static String apiUrl;
    public static String model;

    public static final Logger logger = LogManager.getLogger(AISlayer.class.getName());

    public AISlayer () {

    }

    public static String handleDescription (AbstractCard card) {
        return handleDescription(card.rawDescription, card);
    }

    public static String handleDescription (String description) {
        return handleDescription(description, null);
    }

    public static String handleDescription (String description, AbstractCard card) {
        description = description.replace("NL", "");
        if (card != null) {
            description = description.replace("!B!", card.baseBlock + "(基础)");
            description = description.replace("!M!", card.baseMagicNumber + "(基础)");
            description = description.replace("!D!", card.baseDamage + "(基础)");
        }
        description = description.replace("[E]", "能量");
        description = description.replace("[W]", "能量");
        description = description.replace("[R]", "能量");
        description = description.replace("[B]", "能量");
        description = description.replace("[P]", "能量");
        description = description.replace("[G]", "能量");
        description = description.replace("#g", "");
        description = description.replace("#r", "");
        description = description.replace("#b", "");
        description = description.replace("#y", "");
        description = description.replace("#p", "");
        description = description.replace(" ", "");
        allDescriptions.add(description);
        return description;
    }

    public static JSONObject getInfo (String todo) {

        JSONObject infoJson = new JSONObject();

        infoJson.put("当前回合", GameActionManager.turn);

        infoJson.put("现在你可以做的事", todo);

        JSONArray creaturesJson = new JSONArray();

        JSONObject playerJson = new JSONObject();

        //角色基本信息
        playerJson.put("角色", AbstractDungeon.player.getCharacterString().NAMES[0]);
        playerJson.put("序号", 0);
        playerJson.put("金币", AbstractDungeon.player.gold);
        playerJson.put("血量", AbstractDungeon.player.currentHealth + "/" + AbstractDungeon.player.maxHealth);
        if (AbstractDungeon.player.currentBlock > 0) {
            playerJson.put("格挡", AbstractDungeon.player.currentBlock);
        }
        playerJson.put("能量", EnergyPanel.getCurrentEnergy() + "/" + AbstractDungeon.player.energy.energyMaster);
        if (AbstractDungeon.player.stance.name != null) {
            playerJson.put("姿态", AbstractDungeon.player.stance.name);
        }

        //钥匙
        ArrayList<String> keys = new ArrayList<>();
        if (Settings.hasRubyKey) {
            keys.add("红宝石钥匙");
        }
        if (Settings.hasEmeraldKey) {
            keys.add("绿宝石钥匙");
        }
        if (Settings.hasSapphireKey) {
            keys.add("蓝宝石钥匙");
        }
        if (!keys.isEmpty()) {
            playerJson.put("钥匙", keys);
        }

        //药水
        ArrayList<String> potions = new ArrayList<>();
        for (AbstractPotion potion : AbstractDungeon.player.potions) {
            potions.add(potion.name);
        }
        playerJson.put("药水", potions);
        allPotions.addAll(AbstractDungeon.player.potions);

        //充能球
        ArrayList<JSONObject> orbsJson = new ArrayList<>();
        for (AbstractOrb orb : AbstractDungeon.player.orbs) {
            JSONObject orbJson = new JSONObject();
            orbJson.put("名称", orb.name);
            if (!Objects.equals(orb.name, "充能球栏位")) {
                orbJson.put("描述", handleDescription(orb.description));
            } else {
                orbJson.put("描述", "空栏位");
            }
            orbsJson.add(orbJson);
        }
        if (!orbsJson.isEmpty()) {
            Collections.reverse(orbsJson);
            playerJson.put("充能球", orbsJson);
        }

        //遗物
        ArrayList<String> relics = new ArrayList<>();
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            String status = "";
            if (r.usedUp) {
                status = "(已用完)";
            }
            else if(r.counter > 0) {
                status = "(层数: " + r.counter + ")";
            }
            relics.add(r.name + status);
        }
        playerJson.put("遗物", relics);
        allRelics.addAll(AbstractDungeon.player.relics);

        //总牌组
        ArrayList<String> masterDeck = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
            masterDeck.add(card.name);
        }
        playerJson.put("总牌组", masterDeck);
        allCards.addAll(AbstractDungeon.player.masterDeck.group);

        //手牌
        ArrayList<String> hand = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            String status;
            if (card.type == AbstractCard.CardType.STATUS) {
                if (card.costForTurn >= 0) {
                    status = "(需要" + card.costForTurn + "能量)";
                } else if (AbstractDungeon.player.hasRelic("Medical Kit")) {
                    status = "(可以直接打出)";
                } else {
                    status = "(不能打出)";
                }
            } else if (card.type == AbstractCard.CardType.CURSE) {
                if (card.costForTurn >= 0) {
                    status = "(需要" + card.costForTurn + "能量)";
                } else if (AbstractDungeon.player.hasRelic("Blue Candle")) {
                    status = "(可以耗1血打出)";
                } else {
                    status = "(不能打出)";
                }
            } else {
                if ( ( !card.hasEnoughEnergy() && !card.ignoreEnergyOnUse ) || card.costForTurn == -2 ) {
                    status = "(不能打出)";
                } else if (card.costForTurn == -1) {
                    status = "(需要剩下全部能量X)";
                } else {
                    status = "(需要" + card.costForTurn + "能量)";
                }
            }
            hand.add("(序号: " + AbstractDungeon.player.hand.group.indexOf(card) + ")" + card.name + status);
        }
        playerJson.put("手牌", hand);
        allCards.addAll(AbstractDungeon.player.hand.group);

        //抽牌堆
        ArrayList<String> drawPile = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.drawPile.group) {
            drawPile.add(card.name);
        }
        playerJson.put("抽牌堆", drawPile);
        allCards.addAll(AbstractDungeon.player.drawPile.group);

        //弃牌堆
        ArrayList<String> discardPile = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.discardPile.group) {
            discardPile.add(card.name);
        }
        playerJson.put("弃牌堆", discardPile);
        allCards.addAll(AbstractDungeon.player.discardPile.group);

        //消耗牌堆
        ArrayList<String> exhaustPile = new ArrayList<>();
        for (AbstractCard card : AbstractDungeon.player.exhaustPile.group) {
            exhaustPile.add(card.name);
        }
        playerJson.put("消耗牌堆", exhaustPile);
        allCards.addAll(AbstractDungeon.player.exhaustPile.group);

        //效果
        JSONArray powersJson = new JSONArray();
        for (AbstractPower power : AbstractDungeon.player.powers) {
            JSONObject powerJson = getPowerInfo(power);
            powersJson.put(powerJson);
        }
        playerJson.put("效果", powersJson);

        creaturesJson.put(playerJson);

        //怪物
        if (AbstractDungeon.getCurrRoom().monsters != null) {
            int indexMonster = 0;
            for (AbstractMonster monster : AbstractDungeon.getCurrRoom().monsters.monsters) {
                indexMonster++;
                JSONObject monsterJson = new JSONObject();
                monsterJson.put("怪物", monster.name);
                if (monster.currentHealth == 0) {
                    monsterJson.put("状态", "不可选中");
                } else {
                    monsterJson.put("序号", indexMonster);
                    monsterJson.put("血量", monster.currentHealth + "/" + monster.maxHealth);
                    if (monster.currentBlock > 0) {
                        monsterJson.put("格挡", monster.currentBlock);
                    }
                }

                if (monster.currentHealth > 0 || monster.halfDead) {
                    ArrayList<String> monsterIntentDesc = getMonsterIntentDesc(monster);
                    monsterJson.put("意图", monsterIntentDesc);

                    JSONArray monsterPowersJson = new JSONArray();
                    for (AbstractPower power : monster.powers) {
                        JSONObject monsterPowerJson = getPowerInfo(power);
                        monsterPowersJson.put(monsterPowerJson);
                    }
                    monsterJson.put("效果", monsterPowersJson);
                }
                creaturesJson.put(monsterJson);
            }
        }

        infoJson.put("生物", creaturesJson);

        return infoJson;
    }

    public static JSONObject getPowerInfo (AbstractPower power) {
        JSONObject monsterPowerJson = new JSONObject();
        monsterPowerJson.put("名称", power.name);
        monsterPowerJson.put("层数", power.amount);
        monsterPowerJson.put("描述", handleDescription(power.description));
        return monsterPowerJson;
    }

    public static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    private static ArrayList<String> getMonsterIntentDesc (AbstractMonster monster) {
        String monsterIntent = monster.intent.toString();
        ArrayList<String> monsterIntentDesc = new ArrayList<>();
        if (monsterIntent.contains("ATTACK")) {
            monsterIntentDesc.add("造成" + monster.getIntentDmg() + "伤害");
        }
        if (monsterIntent.contains("DEFEND")) {
            monsterIntentDesc.add("获得格挡");
        }
        if (monsterIntent.startsWith("BUFF") || monsterIntent.endsWith("_BUFF")) {
            monsterIntentDesc.add("获得强化效果");
        }
        if (monsterIntent.contains("DEBUFF")) {
            monsterIntentDesc.add("施加负面效果");
        }
        if (monsterIntent.contains("ESCAPE")) {
            monsterIntentDesc.add("马上逃跑");
        }
        if (monsterIntent.contains("SLEEP")) {
            monsterIntentDesc.add("正常沉睡");
        }
        if (monsterIntent.contains("UNKNOWN")) {
            monsterIntentDesc.add("未知,但不是攻击");
        }
        if (monsterIntent.contains("STUN")) {
            monsterIntentDesc.add("正在昏迷");
        }
        if (monsterIntent.contains("MAGIC")) {
            monsterIntentDesc.add("使用魔法");
        }
        return monsterIntentDesc;
    }

    public static JSONObject getCardInfo (AbstractCard card) {
        JSONObject infoJson = new JSONObject();

        infoJson.put("名称", card.name);
        infoJson.put("类型", getType(card.type));
        infoJson.put("稀有度", getRarity(card.rarity));
        infoJson.put("颜色", getColor(card.color));
        if (card.cost >= 0) {
            infoJson.put("能耗", card.cost + "(基础)");
        } else if (card.cost == -1) {
            infoJson.put("能耗", "全部能量X");
        }
        infoJson.put("描述", handleDescription(card));

        return infoJson;
    }

    private static String getType(AbstractCard.CardType type) {
        switch (type) {
            case ATTACK:
                return "攻击";
            case SKILL:
                return "技能";
            case POWER:
                return "效果";
            case STATUS:
                return "状态";
            case CURSE:
                return "诅咒";
            default:
                return "未知";
        }
    }

    private static String getColor(AbstractCard.CardColor color) {
        switch (color) {
            case RED:
                return "红色";
            case GREEN:
                return "绿色";
            case BLUE:
                return "蓝色";
            case PURPLE:
                return "紫色";
            case CURSE:
                return "诅咒";
            case COLORLESS:
                return "无色";
            default:
                return "未知";
        }
    }

    private static String getRarity(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case BASIC:
                return "基础";
            case COMMON:
                return "普通";
            case UNCOMMON:
                return "罕见";
            case RARE:
                return "稀有";
            case CURSE:
                return "诅咒";
            default:
                return "未知";
        }
    }

    public static boolean isAIStart () {

        if (ConfigPanel.selectedPlatform_1) {
            AISlayer.apiKey = ConfigPanel.apiKey_1;
            AISlayer.apiUrl = handleApiUrl(ConfigPanel.apiUrl_1);
            if (ConfigPanel.selectedModel_1_1) {
                AISlayer.model = ConfigPanel.model_1_1;
            } else if (ConfigPanel.selectedModel_1_2) {
                AISlayer.model = ConfigPanel.model_1_2;
            } else {
                return false;
            }
        } else if (ConfigPanel.selectedPlatform_2) {
            AISlayer.apiKey = ConfigPanel.apiKey_2;
            AISlayer.apiUrl = handleApiUrl(ConfigPanel.apiUrl_2);
            if (ConfigPanel.selectedModel_2_1) {
                AISlayer.model = ConfigPanel.model_2_1;
            } else if (ConfigPanel.selectedModel_2_2) {
                AISlayer.model = ConfigPanel.model_2_2;
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private static String handleApiUrl(String apiUrl) {
        if (apiUrl.endsWith("/")) {
            apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
        }
        if (!apiUrl.endsWith("/chat/completions")) {
            if (apiUrl.endsWith("/chat")) {
                apiUrl = apiUrl + "/completions";
            } else {
                apiUrl = apiUrl + "/chat/completions";
            }
        }
        if (!apiUrl.startsWith("https://") && !apiUrl.startsWith("http://")) {
            apiUrl = "https://" + apiUrl;
        }
        return apiUrl;
    }

    public static JSONObject mapPathsToString(ArrayList<ArrayList<MapRoomNode>> mapPaths) {
        JSONObject mapPathsJson = new JSONObject();
        for (ArrayList<MapRoomNode> path : mapPaths) {
            ArrayList<String> pathString = new ArrayList<>();
            for (MapRoomNode node : path) {
                String nodeSymbol = node.room.getMapSymbol();
                switch (nodeSymbol) {
                    case "M":
                        if (node.hasEmeraldKey) {
                            pathString.add("怪物(绿宝石钥匙)");
                        } else {
                            pathString.add("怪物");
                        }
                        break;
                    case "B":
                        pathString.add("Boss");
                        break;
                    case "E":
                        if (node.hasEmeraldKey) {
                            pathString.add("精英(绿宝石钥匙)");
                        } else {
                            pathString.add("精英");
                        }
                        break;
                    case "T":
                        pathString.add("宝箱");
                        break;
                    case "$":
                        pathString.add("商店");
                        break;
                    case "?":
                        pathString.add("事件");
                        break;
                    case "R":
                        pathString.add("火堆");
                        break;
                    default:
                        pathString.add(nodeSymbol);
                        break;
                }
            }
            mapPathsJson.put("路线" + mapPaths.indexOf(path), pathString);
        }
        return mapPathsJson;
    }

    public static ArrayList<ArrayList<MapRoomNode>> getMapPaths(MapRoomNode root) {

        ArrayList<ArrayList<MapRoomNode>> MapPaths = new ArrayList<>();

        if (root.x == -1 || root.y == -1) {
            ArrayList<MapRoomNode> firstMapPaths = getFirstMapPaths();
            for (MapRoomNode node : firstMapPaths) {
                root.addEdge(getEdge(node));
            }
        }

        for (MapEdge edge : root.getEdges()) {
            MapRoomNode mapNode = getMapNode(edge);
            if (mapNode != null) {
                ArrayList<ArrayList<MapRoomNode>> subPaths = getMapPaths(mapNode);
                for (ArrayList<MapRoomNode> subPath : subPaths) {
                    if (root != AbstractDungeon.getCurrMapNode()) {
                        subPath.add(0, root);
                    }
                    MapPaths.add(subPath);
                }
            } else {
                ArrayList<MapRoomNode> path = new ArrayList<>();
                if (root != AbstractDungeon.getCurrMapNode()) {
                    path.add(0, root);
                }
                MapRoomNode bossMapNode = new MapRoomNode(-1, 15);
                bossMapNode.room = new MonsterRoomBoss();
                bossMapNode.hb = AbstractDungeon.dungeonMapScreen.map.bossHb;
                path.add(bossMapNode);
                MapPaths.add(path);
            }
        }

        return MapPaths;
    }

    private static ArrayList<MapRoomNode> getFirstMapPaths() {
        ArrayList<MapRoomNode> firstMapPaths = new ArrayList<>();
        ArrayList<MapRoomNode> allMapNodes = getAllMapNodes();
        for (MapRoomNode node : allMapNodes) {
            if (node.y == 0) {
                firstMapPaths.add(node);
            }
        }
        return firstMapPaths;
    }

    private static MapRoomNode getMapNode(MapEdge edge) {
        ArrayList<MapRoomNode> allMapNodes = getAllMapNodes();
        for (MapRoomNode node : allMapNodes) {
            if (node.x == edge.dstX && node.y == edge.dstY) {
                return node;
            }
        }
        return null;
    }

    private static MapEdge getEdge(MapRoomNode node) {
        return new MapEdge(0, 0, node.x, node.y);
    }

    private static ArrayList<MapRoomNode> getAllMapNodes() {
        ArrayList<MapRoomNode> allMapNodes = new ArrayList<>();
        for (ArrayList<MapRoomNode> raws : AbstractDungeon.map) {
            allMapNodes.addAll(raws);
        }
        return allMapNodes;
    }

}