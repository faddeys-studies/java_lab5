package tictactoe.player;

import tictactoe.game.GameView;
import tictactoe.game.Turn;

public interface Agent {

    Turn decideTurn(GameView game);

    boolean isAI();

    void stop();

}
