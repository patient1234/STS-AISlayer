package aislayer.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class AIUseCardAction extends AbstractGameAction {

    public AbstractCard card;
    public AbstractCreature cardTarget;

    public AIUseCardAction(AbstractCard card, AbstractCreature target) {
        this.card = card;
        this.cardTarget = target;
    }

    @Override
    public void update() {
        int cost;
        if (card.costForTurn >= 0) {
            cost = card.costForTurn;
        } else {
            cost = EnergyPanel.getCurrentEnergy() + 1;
        }
        AbstractDungeon.player.useCard(card, (AbstractMonster) cardTarget, cost);
        this.isDone = true;
    }
}
