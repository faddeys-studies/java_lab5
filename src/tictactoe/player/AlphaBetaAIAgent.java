package tictactoe.player;


import tictactoe.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class AlphaBetaAIAgent implements Agent {

    private static final Logger log = Logger.getLogger(AlphaBetaAIAgent.class.getName());

    private final int maxDepth;
    private final Heuristic heuristic;
    private final Random random;

    public AlphaBetaAIAgent(int maxDepth, Heuristic heuristic) {
        this.maxDepth = maxDepth;
        this.heuristic = heuristic;
        random = new Random();
    }

    @Override
    public Point decideTurn(GameView game) {
        log.fine("entering AlphaBeta algorithm");
        ABPAnswer answer = computeDecision(
                game, P.MAX, 0,
                -Float.MAX_VALUE, Float.MAX_VALUE,
                new Estimator(game.getCurrentPlayer(), heuristic)
        );
        log.fine("done AlphaBeta algorithm: "+answer.point);
        return answer.point;
    }

    @Override
    public boolean isAI() {
        return true;
    }

    @Override
    public void stop() {}

    private ABPAnswer computeDecision(GameView g, P p, int depth,
                                      float alpha, float beta,
                                      Estimator e) {
        if (g.isFinished() || depth >= maxDepth) {
            return new ABPAnswer(e.estimate(g, p), p);
        }
        ABPAnswer best = new ABPAnswer(p);
        for(Point t : getPossibleTurns(g)) {
            Game nextGame = new Game(g);
            nextGame.makeTurn(t);
            ABPAnswer nextAnswer = computeDecision(
                    nextGame, p.opposite(), depth+1, alpha, beta, e
            );
            best.maybeUpdate(t, nextAnswer);
            if (best.isPruning(alpha, beta)) break;
            if (p == P.MAX) alpha = Math.max(alpha, best.utility);
            else beta = Math.min(beta, best.utility);
        }
        return best;
    }

    private List<Point> getPossibleTurns(GameView g) {
        List<Point> result = new ArrayList<>();
        for (int row = 0; row < g.getFieldHeight(); row++) {
            for (int col = 0; col < g.getFieldWidth(); col++) {
                if (g.getCellState(row, col) == null) {
                    result.add(new Point(row, col));
                }
            }
        }
        return result;
    }

    private class ABPAnswer {
        float utility;
        final P player;
        Point point = null;
        ABPAnswer next = null;

        ABPAnswer(float utility, P p) {
            this.utility = utility;
            this.player = p;
        }

        ABPAnswer(P p) {
            this((p == P.MAX) ? -Float.MAX_VALUE : Float.MAX_VALUE, p);
        }

        void maybeUpdate(Point t, ABPAnswer nextAnswer) {
            if (isWorseThan(nextAnswer)) {
                point = t;
                utility = nextAnswer.utility;
                next = nextAnswer;
            }
        }

        boolean isPruning(float alpha, float beta) {
            if (player.equals(P.MAX)) {
                return utility >= beta;
            } else {
                return utility <= alpha;
            }
        }

        boolean isWorseThan(ABPAnswer answer) {
            if (player.equals(P.MAX)) {
                return utility < answer.utility;
            } else {
                return utility > answer.utility;
            }
        }
    }

    private enum P {
        MIN, MAX;
        P opposite() {return (this.equals(MIN)) ? MAX : MIN;}
    }

    private class Estimator {

        Player player;
        Heuristic heuristic;

        Estimator(Player player, Heuristic heuristic) {
            this.player = player;
            this.heuristic = heuristic;
        }

        float estimate(GameView g, P p) {
            if (g.isFinished()) {
                Player winner = g.getResult().getWinner();
                float utility = (winner == null) ? 0 : (
                        (winner.equals(this.player)) ? 1: -1
                );
                return utility * g.getWinLength();
            } else {
                Player estimateFor = (p == P.MAX) ? this.player : this.player.opponent();
                return heuristic.estimate(g, estimateFor);
            }
        }

    }

    public interface Heuristic {

        float estimate(GameView game, Player player);

    }

    public static class MaxStrikeLength implements Heuristic {

        public float estimate(GameView game, Player player) {
            MaxStrikeDetector msd = new MaxStrikeDetector(player);
            MaxStrikeDetector msdOpp = new MaxStrikeDetector(player.opponent());

            for (List<Cell> row : GameUtils.getRows(game)) {
                msd.accept(row);
                msdOpp.accept(row);
            }
            for (List<Cell> col : GameUtils.getCols(game)) {
                msd.accept(col);
                msdOpp.accept(col);
            }
            for (List<Cell> diag : GameUtils.getMainDiagonals(game)) {
                if (diag.size() < game.getWinLength()) continue;
                msd.accept(diag);
                msdOpp.accept(diag);
            }
            for (List<Cell> diag : GameUtils.getCollateDiagonals(game)) {
                if (diag.size() < game.getWinLength()) continue;
                msd.accept(diag);
                msdOpp.accept(diag);
            }

            int oursMax = msd.maxLength, theirsMax = msdOpp.maxLength;
            if (oursMax <= theirsMax) {
                oursMax = 0;
//            } else {
//                theirsMax++;
            }


            return (oursMax-theirsMax);
        }

        private static class MaxStrikeDetector {
            int maxLength = 0;
            final Player player;

            MaxStrikeDetector(Player p) {player = p;}

            void accept(List<Cell> cells) {
                int counter = 0;
                boolean beforeWasEmpty = false;
                for (Cell c : cells) {
                    if (c.player == null) {
                        if (counter > maxLength) maxLength = counter;
                        counter = 0;
                        beforeWasEmpty = true;
                    } else if (c.player.equals(player)){
                        counter++;
                    } else {
                        if (beforeWasEmpty && counter > maxLength) maxLength = counter;
                        counter = 0;
                        beforeWasEmpty = true;
                    }
                }
                if (beforeWasEmpty && counter > maxLength) maxLength = counter;
            }
        }

    }

}
