package tictactoe.player;


import tictactoe.game.GameView;
import tictactoe.game.Player;
import tictactoe.game.Turn;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class AgentManager {

    private static final Logger log = Logger.getLogger(AgentManager.class.getName());

    private final Agent crossesAgent;
    private final Agent zerosAgent;
    private final Thread workerThread;
    private final Queue<Request> requests;
    private boolean shouldStop = false;

    public AgentManager(Agent crosses, Agent zeros) {
        crossesAgent = crosses;
        zerosAgent = zeros;
        requests = new LinkedList<>();
        workerThread = new Thread(this::agentsLoop);
        workerThread.start();
    }

    public void askForTurn(GameView game, Consumer<Turn> callback, Object lock) {
        synchronized (requests) {
            if (shouldStop) {
                throw new IllegalStateException("AgentManager already requested to stop");
            }
            log.finest("adding new request");
            requests.add(new Request(game, callback, lock));
            requests.notify();
        }
    }

    public boolean isActive() {
        synchronized (requests) {
            return shouldStop;
        }
    }

    public boolean isAlive() {
        return workerThread.isAlive();
    }

    public void requestStop() {
        log.finest("stopping");
        synchronized (requests) {
            shouldStop = true;
        }
        crossesAgent.stop();
        zerosAgent.stop();
    }

    private void agentsLoop()  {
        try {
            while (true) {
                log.fine("starting new agents loop");
                Request r;
                synchronized (requests) {
                    if (shouldStop) {
                        log.fine("got stop signal, quitting loop");
                        return;
                    }
                    while (requests.isEmpty()) requests.wait();
                    r = requests.remove();
                }
                Agent a = r.game.getCurrentPlayer() == Player.CROSSES ? crossesAgent : zerosAgent;
                Turn t = a.decideTurn(r.game);
                if (t == null) {
                    if (isActive()) throw new RuntimeException(
                            "Agent returns nothing but manager is not stopped");
                    log.fine("got no turn from agent - we've been stopped");
                } else {
                    log.fine("got turn "+t+" from the agent");
                    synchronized (r.lock) {
                        r.callback.accept(t);
                    }
                }
            }
        } catch (InterruptedException exc) {
            log.fine("interrupted");
        }
    }

    private static class Request {
        final GameView game;
        final Consumer<Turn> callback;
        final Object lock;

        Request(GameView game, Consumer<Turn> callback, Object lock) {
            this.game = game;
            this.callback = callback;
            this.lock = lock;
        }
    }

}
