package tictactoe.game;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameUtils {

    public static Iterable<List<Cell>> getRows(GameView game) {
        List<List<Cell>> result = new LinkedList<>();
        for (int row = 0; row < game.getFieldHeight(); row++) {
            List<Cell> cells = new ArrayList<>(game.getFieldWidth());
            for (int col = 0; col < game.getFieldWidth(); col++) {
                cells.add(new Cell(game.getCellState(row, col), row, col));
            }
            result.add(cells);
        }
        return result;
    }

    public static Iterable<List<Cell>> getCols(GameView game) {
        List<List<Cell>> result = new LinkedList<>();
        for (int col = 0; col < game.getFieldWidth(); col++) {
            List<Cell> cells = new ArrayList<>(game.getFieldHeight());
            for (int row = 0; row < game.getFieldHeight(); row++) {
                cells.add(new Cell(game.getCellState(row, col), row, col));
            }
            result.add(cells);
        }
        return result;
    }

    public static Iterable<List<Cell>> getMainDiagonals(GameView game) {
        List<List<Cell>> result = new LinkedList<>();
        for (int i = 0; i < game.getFieldHeight(); i++) {
            int diagLen = Math.min(game.getFieldWidth(), game.getFieldHeight()-i);
            List<Cell> cells = new ArrayList<>(diagLen);
            for (int j = 0; j < diagLen; j++) {
                cells.add(new Cell(game.getCellState(i+j, j), i+j, j));
            }
            result.add(cells);
        }
        for (int i = 1; i < game.getFieldWidth(); i++) {
            int diagLen = Math.min(game.getFieldWidth()-i, game.getFieldHeight());
            List<Cell> cells = new ArrayList<>(diagLen);
            for (int j = 0; j < diagLen; j++) {
                cells.add(new Cell(game.getCellState(j, i+j), j, i+j));
            }
            result.add(cells);
        }
        return result;
    }

    public static Iterable<List<Cell>> getCollateDiagonals(GameView game) {
        List<List<Cell>> result = new LinkedList<>();
        for (int i = 0; i < game.getFieldHeight(); i++) {
            int diagLen = Math.min(i+1, game.getFieldWidth());
            List<Cell> cells = new ArrayList<>(diagLen);
            for (int j = 0; j < diagLen; j++) {
                cells.add(new Cell(game.getCellState(i-j, j), i-j, j));
            }
            result.add(cells);
        }
        for (int i = 1; i < game.getFieldWidth(); i++) {
            int diagLen = Math.min(game.getFieldWidth()-i, game.getFieldHeight());
            List<Cell> cells = new ArrayList<>(diagLen);
            for (int j = 1; j < diagLen+1; j++) {
                cells.add(new Cell(
                        game.getCellState(game.getFieldHeight()-j, i+j-1),
                        game.getFieldHeight()-j, i+j-1)
                );
            }
            result.add(cells);
        }
        return result;
    }

}
