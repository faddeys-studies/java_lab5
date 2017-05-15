package tictactoe;


import tictactoe.game.*;
import tictactoe.player.Agent;
import tictactoe.player.AgentManager;
import tictactoe.player.AlphaBetaAIAgent;
import tictactoe.player.UserAgent;
import tictactoe.ui.GameField;
import tictactoe.ui.SettingsDialog;
import utils.Procedure;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuKeyEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    private static final Logger log = Logger.getLogger(AppController.class.getName());

    private AgentManager agentManager;
    private GameField fieldCtl;
    private Game currentGame;

    public AppController() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

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

        initMenu(window);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);

        startGame(
                new UserAgent(fieldCtl),
                new UserAgent(fieldCtl)
        );
    }

    private void initMenu(JFrame window) {
        JMenuBar menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        setAction(newItem, KeyEvent.VK_N, () -> {
            new SettingsDialog(
                fieldCtl,
                (SettingsDialog.GameConfig conf) -> {
                    fieldCtl.update(conf.game);
                    agentManager.requestStop();
                    currentGame = conf.game;
                    startGame(conf.crosses, conf.zeros);
                    window.pack();
                });
        });
    }

    private void setAction(JMenuItem item, int key, Procedure procedure) {
        item.setAccelerator(KeyStroke.getKeyStroke(
                key, ActionEvent.CTRL_MASK
        ));
        item.addActionListener((ActionEvent ae) -> procedure.execute());
    }

    public void startGame(Agent agent1, Agent agent2) {
        log.fine("starting new game");
        agentManager = new AgentManager(agent1, agent2);
        startNextTurn();
    }

    private void startNextTurn() {
        log.fine("starting new turn");
        agentManager.askForTurn(currentGame, (tictactoe.game.Point t) -> {
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
                rootLogger.setLevel(Level.FINE);

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
