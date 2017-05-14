package tictactoe.player;


import tictactoe.game.GameView;
import tictactoe.game.Turn;
import tictactoe.ui.GameField;

import java.util.logging.Logger;

public class UserAgent implements Agent {

    private static final Logger log = Logger.getLogger(UserAgent.class.getName());

    private final Object lock = new Object();
    private boolean stopped = false;
    private Turn turn = null;
    private final GameField fieldCtl;

    public UserAgent(GameField fieldCtl) {
        this.fieldCtl = fieldCtl;
    }

    private void clickListener(int row, int col) {
        log.fine("received click at ("+row+", "+col+")");
        synchronized (lock) {
            turn = new Turn(row, col);
            lock.notify();
        }
    }

    @Override
    public Turn decideTurn(GameView game) {
        log.fine("started decideTurn() procedure");
        int key = fieldCtl.addCellClickListener(this::clickListener);
        try {
            synchronized (lock) {
                turn = null;
                while (turn == null) {
                    log.finer("waiting for user's click");
                    lock.wait();
                    if (stopped) {
                        log.finer("we are stopped, returning null");
                        return null;
                    }
                }
                log.finer("got turn: "+turn);
                Turn t = turn;
                turn = null;
                return t;
            }
        } catch (InterruptedException exc) {
            log.fine("interrupted");
            return null;
        } finally {
            fieldCtl.removeCellClickListener(key);
        }
    }

    @Override
    public boolean isAI() {
        return false;
    }

    @Override
    public void stop() {
        log.fine("requested stop");
        synchronized (lock) {
            stopped = true;
            lock.notify();
        }
    }
}
