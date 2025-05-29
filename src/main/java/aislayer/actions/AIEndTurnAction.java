package aislayer.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;

public class AIEndTurnAction extends AbstractGameAction {

    public AIEndTurnAction() {
        this.actionType = ActionType.WAIT;
    }

    @Override
    public void update() {
        this.addToBot(new PressEndTurnButtonAction());
        this.isDone = true;
    }
}
