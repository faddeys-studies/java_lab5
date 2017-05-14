package tictactoe.player;

import tictactoe.game.GameView;
import tictactoe.game.Point;

public interface Agent {

    Point decideTurn(GameView game);

    boolean isAI();

    void stop();

}
