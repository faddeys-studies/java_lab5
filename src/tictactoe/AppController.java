package tictactoe;


import javenue.csv.Csv;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
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
                    fieldCtl.update(conf.game, true);
                    agentManager.requestStop();
                    currentGame = conf.game;
                    startGame(conf.crosses, conf.zeros);
                    setWindowSize(window);
                });
        }, 0);

        JMenuItem saveAsItem = new JMenuItem("Save As");
        fileMenu.add(saveAsItem);
        setAction(saveAsItem, KeyEvent.VK_S, () -> {
            JFileChooser fileDialog = new JFileChooser();
            int answer = fileDialog.showSaveDialog(window);
            if (answer == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileDialog.getSelectedFile();
                Csv.Writer writer = new Csv.Writer(fileToSave);
                writer.value(agentManager.getCrossesAgent().isAI() ? "1" : "0")
                        .value(agentManager.getZerosAgent().isAI() ? "1" : "0");
                currentGame.save(writer);
                writer.close();
            }
        }, ActionEvent.SHIFT_MASK);

        JMenuItem openItem = new JMenuItem("Open");
        fileMenu.add(openItem);
        setAction(openItem, KeyEvent.VK_O, () -> {
            JFileChooser fileDialog = new JFileChooser();
            int answer = fileDialog.showOpenDialog(window);
            if (answer == JFileChooser.APPROVE_OPTION) {
                try {
                    File fileToRead = fileDialog.getSelectedFile();
                    Csv.Reader reader = new Csv.Reader(new FileReader(fileToRead));
                    List<String> line = reader.readLine();
                    if (line.size() < 2) throw new IOException();
                    Agent crosses = "1".equals(line.get(0)) ? new AlphaBetaAIAgent() : new UserAgent(fieldCtl);
                    Agent zeros = "1".equals(line.get(1)) ? new AlphaBetaAIAgent() : new UserAgent(fieldCtl);
                    Game game = new Game(reader);

                    agentManager.requestStop();
                    fieldCtl.update(game, true);
                    currentGame = game;
                    startGame(crosses, zeros);
                    setWindowSize(window);
                } catch (IOException | Csv.Exception exc) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Cannot read opened file",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }, 0);

        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);
        setAction(exitItem, KeyEvent.VK_Q, () -> {
            System.exit(0);
        }, 0);
    }

    private void setAction(JMenuItem item, int key, Procedure procedure, int modifiers) {
        item.setAccelerator(KeyStroke.getKeyStroke(
                key, ActionEvent.CTRL_MASK | modifiers
        ));
        item.addActionListener((ActionEvent ae) -> procedure.execute());
    }

    public void startGame(Agent agent1, Agent agent2) {
        log.fine("starting new game");
        agentManager = new AgentManager(agent1, agent2);
        startNextTurn();
    }

    private void setWindowSize(JFrame window) {
        final int windowHeight = 40 + GameField.BUTTON_SIZE*currentGame.getFieldHeight();
        final int windowWidth = GameField.BUTTON_SIZE*currentGame.getFieldWidth();
        window.setSize(windowWidth, windowHeight);
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
