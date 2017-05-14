package tictactoe.game;


public interface GameView {

    Result getResult();

    int getWinLength();

    int getFieldWidth();

    int getFieldHeight();

    Player getCellState(int row, int col);

    Player getCurrentPlayer();

    default boolean isFinished() {
        return getResult() != null;
    }

    interface Result {

        Player getWinner();

        int[] getWinRowIndices();

        int[] getWinColIndices();
    }

}
