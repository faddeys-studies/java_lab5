package tictactoe.game;


public class Turn {

    public final int row;
    public final int col;

    public Turn(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String toString() {
        return "("+row+", "+col+")";
    }

}
