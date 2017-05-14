package tictactoe.game;


public class Point {

    public final int row;
    public final int col;

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String toString() {
        return "("+row+", "+col+")";
    }

}
