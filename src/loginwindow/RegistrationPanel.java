package loginwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import utils.Procedure;


public class RegistrationPanel {

    private JPanel panel;
    private BiConsumer<String, String> onRegisterCommand;
    private Procedure onCancelCommand;

    public RegistrationPanel() {
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

        JLabel passwordAgainLabel = new JLabel("Password (again):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordAgainLabel, gbc);

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

        JPasswordField passwordAgainField = new JPasswordField("");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 5;
        panel.add(passwordAgainField, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        panel.add(registerButton, gbc);
        registerButton.addActionListener((ActionEvent ae) -> {
            if (onRegisterCommand != null) {
                String password = new String(passwordField.getPassword());
                String password2 = new String(passwordAgainField.getPassword());
                if (!password.equals(password2)) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Passwords are not the same",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                onRegisterCommand.accept(loginField.getText(), password);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
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

    public void setRegisterCallback(BiConsumer<String, String> onRegisterCommand) {
        this.onRegisterCommand = onRegisterCommand;
    }

    public void setCancelCallback(Procedure onCancelCommand) {
        this.onCancelCommand = onCancelCommand;
    }

}
