package tictactoe.player;


import tictactoe.game.GameView;
import tictactoe.game.Turn;

public class MinimaxAIAgent implements Agent {

    @Override
    public Turn decideTurn(GameView game) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isAI() {
        return true;
    }

    @Override
    public void stop() {

    }
}
