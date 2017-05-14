package tictactoe;


import tictactoe.game.Game;
import tictactoe.game.Turn;
import tictactoe.player.Agent;
import tictactoe.player.AgentManager;
import tictactoe.player.UserAgent;
import tictactoe.ui.GameField;

import javax.swing.*;
import java.awt.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    private static final Logger log = Logger.getLogger(AppController.class.getName());

    private AgentManager agentManager;
    private GameField fieldCtl;
    private Game currentGame;

    public AppController() {
        JFrame window = new JFrame();
        fieldCtl = new GameField(window.getRootPane());
        currentGame = new Game(3, 3, 3);
        fieldCtl.update(currentGame);

        final int windowHeight = 40 + GameField.BUTTON_SIZE*currentGame.getFieldHeight();
        final int windowWidth = GameField.BUTTON_SIZE*currentGame.getFieldWidth();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds(
                ((int)screenSize.getWidth()-windowWidth)/2,
                ((int)screenSize.getHeight()-windowHeight)/2,
                windowWidth,
                windowHeight);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.repaint();

        startGame(new UserAgent(fieldCtl),
                  new UserAgent(fieldCtl));
    }

    public void startGame(Agent agent1, Agent agent2) {
        log.fine("starting new game");
        agentManager = new AgentManager(agent1, agent2);
        startNextTurn();
    }

    private void startNextTurn() {
        log.fine("starting new turn");
        agentManager.askForTurn(currentGame, (Turn t) -> {
            log.finer("received turn: "+t);
            currentGame.makeTurn(t);
            fieldCtl.update(currentGame);
            if (currentGame.getResult() == null) {
                startNextTurn();
            }
        }, this);
    }

    public static void main(String[] argv) {
        EventQueue.invokeLater(() -> {
            try {
                Logger rootLogger = Logger.getLogger("tictactoe");
                rootLogger.setLevel(Level.INFO);

                ConsoleHandler handler = new ConsoleHandler();
                handler.setLevel(Level.ALL);
                rootLogger.addHandler(handler);

                rootLogger.fine("test");
                new AppController();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
