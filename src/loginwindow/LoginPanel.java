package loginwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.*;
import utils.Procedure;


public class LoginPanel {

    private JPanel panel;
    private BiConsumer<String, String> onLoginCommand;
    private Procedure onRegisterCommand;
    private Procedure onCancelCommand;

    public LoginPanel() {
        onLoginCommand = null;
        onRegisterCommand = null;
        onCancelCommand = null;

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel loginLabel = new JLabel("Login:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(loginLabel, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, gbc);

        JTextField loginField = new JTextField("");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 5;
        panel.add(loginField, gbc);

        JPasswordField passwordField = new JPasswordField("");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 5;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Log in");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);
        loginButton.addActionListener((ActionEvent ae) -> {
            if (onLoginCommand != null)
                onLoginCommand.accept(loginField.getText(),
                        new String(passwordField.getPassword()));
        });

        JButton registerButton = new JButton("Register");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);
        registerButton.addActionListener((ActionEvent ae) -> {
            if (onRegisterCommand != null)
                onRegisterCommand.execute();
        });

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        panel.add(cancelButton, gbc);
        cancelButton.addActionListener((ActionEvent ae) -> {
            if (onCancelCommand != null)
                onCancelCommand.execute();
        });

        panel.setVisible(true);
    }

    public JComponent getRootComponent() {
        return panel;
    }

    public void setLoginCallback(BiConsumer<String, String> onLoginCommand) {
        this.onLoginCommand = onLoginCommand;
    }

    public void setRegisterCallback(Procedure onRegisterCommand) {
        this.onRegisterCommand = onRegisterCommand;
    }

    public void setCancelCallback(Procedure onCancelCommand) {
        this.onCancelCommand = onCancelCommand;
    }

}
