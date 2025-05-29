package aislayer.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class AIUsePotionAction extends AbstractGameAction {

    public AbstractPotion potion;
    public AbstractCreature potionTarget;

    public AIUsePotionAction(AbstractPotion potion, AbstractCreature potionTarget) {
        this.potion = potion;
        this.potionTarget = potionTarget;
    }

    @Override
    public void update() {
        AbstractDungeon.player.removePotion(potion);
        potion.use(potionTarget);
        this.isDone = true;
    }
}
