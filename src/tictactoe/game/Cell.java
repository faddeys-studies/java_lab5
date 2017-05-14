package tictactoe.game;


public class Cell {
    public final int row, col;
    public final Player player;

    public Cell(Player player, int row, int col) {
        this.player = player;
        this.row = row;
        this.col = col;
    }
}
