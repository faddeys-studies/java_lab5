package tictactoe.ui;


import tictactoe.game.Game;
import tictactoe.player.Agent;
import tictactoe.player.AlphaBetaAIAgent;
import tictactoe.player.UserAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.util.Scanner;
import java.util.function.Consumer;

public class SettingsDialog {

    private enum AgentType {
        User, AI
    }

    private final GameField fieldCtl;
    private final AgentBuilder builders[] = new AgentBuilder[2];
    private final JTextField fieldWidthInput, fieldHeightInput, winLengthInput;

    public SettingsDialog(GameField fieldCtl, Consumer<GameConfig> callback) {
        JFrame window = new JFrame();
        this.fieldCtl = fieldCtl;

        JComboBox<String> agent1TypeSelect = new JComboBox<>();
        for (AgentType type : AgentType.values()) {
            agent1TypeSelect.addItem(type.name());
        }
        agent1TypeSelect.addActionListener((ActionEvent ae) -> {
            changeBuilder(0, (String)agent1TypeSelect.getSelectedItem());
        });
        agent1TypeSelect.setSelectedItem(AgentType.User.name());

        JComboBox<String> agent2TypeSelect = new JComboBox<>();
        for (AgentType type : AgentType.values()) {
            agent2TypeSelect.addItem(type.name());
        }
        agent2TypeSelect.addActionListener((ActionEvent ae) -> {
            changeBuilder(1, (String)agent2TypeSelect.getSelectedItem());
        });
        agent2TypeSelect.setSelectedItem(AgentType.User.name());

        fieldWidthInput = new JTextField("3");
        fieldHeightInput = new JTextField("3");
        winLengthInput = new JTextField("3");

        GridBagLayout layout = new GridBagLayout();
        window.setLayout(layout);

        JPanel fieldFormPanel = new JPanel();
        fieldFormPanel.setLayout(new BoxLayout(fieldFormPanel, BoxLayout.X_AXIS));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        window.add(fieldFormPanel, c);
        fieldFormPanel.add(new JLabel("height:"));
        fieldFormPanel.add(fieldHeightInput);
        fieldFormPanel.add(new JLabel("width:"));
        fieldFormPanel.add(fieldWidthInput);
        fieldFormPanel.add(new JLabel("win:"));
        fieldFormPanel.add(winLengthInput);

        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1;
        window.add(agent1TypeSelect, c);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 1;
        window.add(agent2TypeSelect, c);

        JButton doneBtn = new JButton("Ok");
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 2;
        window.add(doneBtn, c);

        window.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(
                (int)screenSize.getWidth()/2 - 50,
                (int)screenSize.getHeight()/2 - 50
        );
        window.setVisible(true);

        doneBtn.addActionListener((ActionEvent ae) -> {
            int width= -1, height= -1, winLength = -1;
            boolean success = true;

            if (!isPositiveInt(fieldWidthInput.getText())) {
                fieldWidthInput.setForeground(Color.RED);
                success = false;
            } else {
                fieldWidthInput.setForeground(Color.BLACK);
                width = Integer.parseInt(fieldWidthInput.getText());
                if (width < 3) success = false;
            }

            if (!isPositiveInt(fieldHeightInput.getText())) {
                fieldHeightInput.setForeground(Color.RED);
                success = false;
            } else {
                fieldHeightInput.setForeground(Color.BLACK);
                height = Integer.parseInt(fieldHeightInput.getText());
                if (height < 3) success = true;
            }

            if (!isPositiveInt(winLengthInput.getText())) {
                winLengthInput.setForeground(Color.RED);
                success = false;
            } else {
                winLengthInput.setForeground(Color.BLACK);
                winLength = Integer.parseInt(winLengthInput.getText());
                if (winLength < 3) success = true;
            }

            Agent crosses = builders[0].buildAgent();
            Agent zeros = builders[1].buildAgent();

            if (crosses == null && zeros ==null) success = false;

            if (success) {
                callback.accept(new GameConfig(
                        crosses, zeros,
                        new Game(winLength, height, width)
                ));
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private void changeBuilder(int idx, String typeName) {
        AgentType type = AgentType.valueOf(typeName);
        AgentBuilder builder = null;
        switch (type) {
            case AI:
                builder = new AlphaBetaAgentBuilder();
                break;
            case User:
                builder = new UserAgentBuilder(fieldCtl);
                break;
        }
        builders[idx] = builder;
    }

    public static class GameConfig {
        public final Agent crosses;
        public final Agent zeros;
        public final Game game;

        public GameConfig(Agent crosses, Agent zeros, Game game) {
            this.crosses = crosses;
            this.zeros = zeros;
            this.game = game;
        }
    }

    private boolean isPositiveInt(String s) {
        Scanner scanner = new Scanner(s);
        if (!scanner.hasNextInt()) return false;
        int value = scanner.nextInt();
        return value > 0 && !scanner.hasNext();
    }

    interface AgentBuilder {

        Agent buildAgent();

    }

    class UserAgentBuilder implements AgentBuilder {

        private GameField fieldCtl;

        public UserAgentBuilder(GameField fieldCtl) {
            this.fieldCtl = fieldCtl;
        }

        @Override
        public Agent buildAgent() {
            return new UserAgent(fieldCtl);
        }
    }

    class AlphaBetaAgentBuilder implements AgentBuilder {

        @Override
        public Agent buildAgent() {
            return new AlphaBetaAIAgent();
        }
    }

}
