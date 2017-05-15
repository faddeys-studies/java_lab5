package tictactoe.ui;

import tictactoe.game.GameView;
import tictactoe.game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class GameField {

    public static final int BUTTON_SIZE = 35;
    private static final Logger log = Logger.getLogger(GameField.class.getName());

    private JPanel fieldPanel;
    private JTextArea statusText;
    private JButton buttons[][] = null;

    private int keyCounter = 0;
    private final Map<Integer, BiConsumer<Integer, Integer>> cellClickListeners = new HashMap<>();
    private boolean buttonsClickable;

    public GameField(JComponent parent) {
        statusText = new JTextArea();
        fieldPanel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        parent.setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 0;
        constraints.gridy = 0;
        constraints.gridx = 0;
        parent.add(statusText, constraints);

        constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        parent.add(fieldPanel, constraints);

    }

    public int addCellClickListener(BiConsumer<Integer, Integer> onCellClick) {
        synchronized (cellClickListeners) {
            log.fine("adding listener under key "+keyCounter);
            cellClickListeners.put(keyCounter, onCellClick);
            keyCounter++;
            return keyCounter-1;
        }
    }

    public void removeCellClickListener(int key) {
        synchronized (cellClickListeners) {
            log.fine("removing listener under key "+keyCounter);
            cellClickListeners.remove(key);
        }
    }

    public void update(GameView game) {
        update(game, false);
    }

    public void update(GameView game, boolean forceReset) {
        log.fine("update() invoked");
        if (forceReset || !buttonsMatchFieldSize(game.getFieldHeight(), game.getFieldWidth())) {
            log.finer("recreating buttons");
            removeButtons();
            createButtons(game.getFieldHeight(), game.getFieldWidth());
        }
        for (int row = 0; row < game.getFieldHeight(); row++) {
            for (int col = 0; col < game.getFieldWidth(); col++) {
                Player cell = game.getCellState(row, col);
                JButton b = buttons[row][col];
                if (cell != null) {
                    b.setText(cell.toString());
                } else {
                    b.setText("");
                }

            }
        }
        GameView.Result result = game.getResult();
        if (result != null) {
            buttonsClickable = false;
            if (result.getWinner() != null) {
                statusText.setText(result.getWinner().name() + " win!");
                int[] winRows = result.getWinRowIndices();
                int[] winCols = result.getWinColIndices();
                for (int i = 0; i < game.getWinLength(); i++) {
                    buttons[winRows[i]][winCols[i]].setForeground(Color.RED);
                }
            } else {
                statusText.setText("Draft!");
            }
        } else {
            statusText.setText(game.getCurrentPlayer().name() + " turn");
        }
        log.finer("updating done");
    }

    private boolean buttonsMatchFieldSize(int fieldHeight, int fieldWidth) {
        if (buttons== null) return false;
        return buttons.length == fieldHeight && buttons[0].length == fieldWidth;
    }

    private void removeButtons() {
        if (buttons != null) {
            fieldPanel.removeAll();
            buttons = null;
        }
    }

    private void createButtons(int fieldHeight, int fieldWidth) {
        buttonsClickable = true;
        fieldPanel.setLayout(new GridLayout(fieldHeight, fieldWidth));
        buttons = new JButton[fieldHeight][];
        for (int i = 0; i < fieldHeight; i++) {
            buttons[i] = new JButton[fieldWidth];
            for (int j = 0; j < fieldWidth; j++) {
                JButton b = new JButton();
                b.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                b.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                b.setMargin(new Insets(0, 0, 0, 0));
                b.addActionListener(makeButtonCallback(b, i, j));
                b.setFocusPainted(false);
                b.setOpaque(false);
                fieldPanel.add(b);
                buttons[i][j] = b;
            }
        }
    }

    private ActionListener makeButtonCallback(JButton btn, int row, int col) {
        return (ActionEvent ae) -> {
            if (buttonsClickable) {
                if (!btn.getText().equals("")) return;
                synchronized (cellClickListeners) {
                    log.fine("calling "+cellClickListeners.size()+" listeners");
                    for(BiConsumer<Integer, Integer> cb : cellClickListeners.values()) {
                        cb.accept(row, col);
                    }
                }
            }
        };
    }
}
