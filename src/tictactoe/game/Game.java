package tictactoe.game;


import javenue.csv.Csv;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Game implements GameView {

    private static final Logger log = Logger.getLogger(Game.class.getName());

    private Player field[][];
    private Player whosTurn;
    private int winLength;
    private Result lastResult = null;
    private boolean resultIsValid = false;

    public Game(int winLength, int fieldHeight, int fieldWidth) {
        _init(Player.CROSSES, winLength, fieldHeight, fieldWidth);
    }

    private void _init(Player player, int winLength, int fieldHeight, int fieldWidth) {
        field = new Player[fieldHeight][];
        for (int i = 0; i < fieldHeight; i++) {
            field[i] = new Player[fieldWidth];
        }
        whosTurn = player;
        this.winLength = winLength;
    }

    public Game(Csv.Reader csv) throws TicTacToeFormatException {
        List<String> headerLine = csv.readLine();
        String headerExceptionMessage = "First line must contain a tuple: " +
                "who's turn, win length, field height, field width";
        if (headerLine.size() != 4) {
            throw new TicTacToeFormatException(headerExceptionMessage);
        }
        try {
            _init(Player.parse(headerLine.get(0)),
                    Integer.parseUnsignedInt(headerLine.get(1)),
                    Integer.parseUnsignedInt(headerLine.get(2)),
                    Integer.parseUnsignedInt(headerLine.get(3)));
        } catch (RuntimeException exc) {
            throw new TicTacToeFormatException(headerExceptionMessage);
        }

        for (int rowIdx = 0; rowIdx < getFieldHeight(); rowIdx++) {
            List<String> line = csv.readLine();
            if (line.size() != getFieldWidth()) {
                throw new TicTacToeFormatException(
                        "Incorrect number of cells in column " +
                        rowIdx + ": " + line.size() +
                        " instead of " + getFieldWidth()
                );
            }
            for (int colIdx = 0; colIdx < line.size(); colIdx++) {
                try {
                    if (!"".equals(line.get(colIdx)))
                        field[rowIdx][colIdx] = Player.parse(line.get(colIdx));
                } catch (RuntimeException exc) {
                    throw new TicTacToeFormatException(
                            "Cannot parse value at row="+rowIdx + " col="+colIdx);
                }
            }
        }
    }

    public Game(GameView game) {
        _init(game.getCurrentPlayer(), game.getWinLength(),
              game.getFieldHeight(), game.getFieldWidth());
        for (int row = 0; row < game.getFieldHeight(); row++) {
            for (int col = 0; col < game.getFieldWidth(); col++) {
                field[row][col] = game.getCellState(row, col);
            }
        }
    }

    public synchronized void save(Csv.Writer csv) {
        csv.newLine()
                .value(whosTurn.toString())
                .value(Integer.toString(getWinLength()))
                .value(Integer.toString(getFieldHeight()))
                .value(Integer.toString(getFieldWidth()));
        for (int row = 0; row < getFieldHeight(); row++) {
            csv.newLine();
            for (int col = 0; col < getFieldWidth(); col++) {
                csv.value(field[row][col].toString());
            }
        }
    }

    public void makeTurn(Turn turn) {
        makeTurn(turn.row, turn.col);
    }

    public synchronized void makeTurn(int row, int col) {
        if (0 > row || row > field.length) throw new IllegalArgumentException();
        if (0 > col || col > field[0].length) throw new IllegalArgumentException();
        if (field[row][col] != null) throw new IllegalArgumentException();
        log.fine("makeTurn: "+whosTurn+" -> ("+row+", "+col+")");
        field[row][col] = whosTurn;
        whosTurn = whosTurn.opponent();
        resultIsValid = false;
    }

    public synchronized Result getResult() {
        if (!resultIsValid) {
            lastResult = computeResult();
            resultIsValid = true;
            log.fine("cached game result: "+(
                    lastResult == null ? "null" : lastResult.getWinner()
            ));
        } else {
            log.fine("using cached game result: "+(
                    lastResult == null ? "null" : lastResult.getWinner()
            ));
        }
        return lastResult;
    }

    private synchronized Result computeResult() {
        log.fine("computing game result");
        WinChecker checker = new WinChecker();

        // horizontal strikes
        for (int row = 0; row < getFieldHeight(); row++) {
            checker.reset();
            for (int col = 0; col < getFieldWidth(); col++) {
                if (checker.check(field[row][col])) {
                    int end = col+1;
                    return new Result(
                            checker.getCurrentPlayer(),
                            row, row,
                            end-getWinLength(), end
                    );
                }
            }
        }

        // vertical strikes
        for (int col = 0; col < getFieldWidth(); col++) {
            checker.reset();
            for (int row = 0; row < getFieldHeight(); row++) {
                if (checker.check(field[row][col])) {
                    int end = row+1;
                    return new Result(
                            checker.getCurrentPlayer(),
                            end-getWinLength(), end,
                            col, col
                    );
                }
            }
        }

        // diagonal strikes
        int nDiagonals = 1 + getFieldHeight() + getFieldWidth() - 2*getWinLength();
        for (int diagIdx = -nDiagonals/2; diagIdx < nDiagonals/2+1; diagIdx++) {
            // main diagonal
            checker.reset();
            int row = -Math.min(0, diagIdx);
            int col = Math.max(0, diagIdx);
            for (; col < getFieldWidth() && row < getFieldHeight(); row++, col++) {
                if (checker.check(field[row][col])) {
                    int endRow = row+1, endCol = col+1;
                    return new Result(
                            checker.getCurrentPlayer(),
                            endRow-getWinLength(), endRow,
                            endCol-getWinLength(), endCol
                    );
                }
            }
            // collate diagonal
            checker.reset();
            row = -Math.min(0, diagIdx);
            col = getFieldWidth() - 1 - Math.max(0, diagIdx);
            for (; col >= 0 && row < getFieldHeight(); row++, col--) {
                if (checker.check(field[row][col])) {
                    int endRow = row+1, endCol = col+1;
                    return new Result(
                            checker.getCurrentPlayer(),
                            endRow-getWinLength(), endRow,
                            endCol, endCol+getWinLength()
                    );
                }
            }
        }

        for (int row = 0; row < getFieldHeight(); row++) {
            for (int col = 0; col < getFieldWidth(); col++) {
                if (field[row][col] == null) return null;
            }
        }

        return new Result(null, 0, 0, 0, 0);
    }

    public int getWinLength() {
        return winLength;
    }

    public int getFieldWidth() {
        return field[0].length;
    }

    public int getFieldHeight() {
        return field.length;
    }

    public synchronized Player getCellState(int row, int col) {
        return field[row][col];
    }

    public synchronized Player getCurrentPlayer() {
        return whosTurn;
    }

    public class TicTacToeFormatException extends IOException {

        public TicTacToeFormatException(String message) {
            super(message);
        }

    }

    private class WinChecker {
        private Player p;
        private int counter;

        WinChecker() {
            reset();
        }

        boolean check(Player p) {
            if (p != null && p.equals(this.p)) {
                counter++;
                if (counter == winLength) {
                    return true;
                }
            } else {
                this.p = p;
                counter = (p != null) ? 1 : 0;
            }
            return false;
        }

        void reset() {
            p = null;
            counter = 0;
        }

        Player getCurrentPlayer() {
            return p;
        }
    }

    public class Result implements GameView.Result {

        Player winner;
        int winRowStart, winRowEnd;
        int winColStart, winColEnd;

        Result(Player winner,
               int winRowStart, int winRowEnd,
               int winColStart, int winColEnd) {
            this.winner = winner;
            this.winRowStart = winRowStart;
            this.winRowEnd = winRowEnd;
            this.winColStart = winColStart;
            this.winColEnd = winColEnd;
        }

        public Player getWinner() {
            return winner;
        }

        public int[] getWinRowIndices() {
            if (winner == null) return null;
            int[] rowIndices = new int[Game.this.getWinLength()];
            if (winRowStart == winRowEnd) {
                Arrays.fill(rowIndices, winRowStart);
            } else {
                for (int i = 0; i < Game.this.getWinLength(); i++) {
                    rowIndices[i] = i + winRowStart;
                }
            }
            return rowIndices;
        }

        public int[] getWinColIndices() {
            if (winner == null) return null;
            int[] colIndices = new int[Game.this.getWinLength()];
            if (winColStart == winColEnd) {
                Arrays.fill(colIndices, winColStart);
            } else {
                for (int i = 0; i < Game.this.getWinLength(); i++) {
                    colIndices[i] = i + winColStart;
                }
            }
            return colIndices;
        }
    }

}
