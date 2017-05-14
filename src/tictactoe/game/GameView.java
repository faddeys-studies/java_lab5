package tictactoe.game;


public interface GameView {

    Result getResult();

    int getWinLength();

    int getFieldWidth();

    int getFieldHeight();

    Player getCellState(int row, int col);

    Player getCurrentPlayer();

    interface Result {

        Player getWinner();

        int[] getWinRowIndices();

        int[] getWinColIndices();
    }

}
