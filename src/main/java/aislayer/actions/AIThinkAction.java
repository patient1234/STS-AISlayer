package aislayer.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class AIThinkAction extends AbstractGameAction {

    public AIThinkAction() {

    }

    @Override
    public void update() {
        if (!AbstractDungeon.actionManager.actions.isEmpty()) {
            this.isDone = true;
        }
    }
}
