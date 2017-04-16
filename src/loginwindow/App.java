package loginwindow;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;


public class App {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new App().setLoginView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private MainWindow window;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private AuthService authService;

    public App() {
        authService = new AuthService();
        window = new MainWindow();
        loginPanel = new LoginPanel();
        registrationPanel = new RegistrationPanel();

        File storageFile = getPasswordStorageFile();
        try(Reader reader = new FileReader(storageFile)) {
            authService.load(reader);
        } catch (IOException fnfe) {}

        window.setCloseCallback(() -> {
            try(Writer writer = new FileWriter(storageFile)) {
                authService.save(writer);
                System.out.println("Passwords written to " + storageFile.getAbsolutePath());
            } catch (IOException ioe) {}
        });

        loginPanel.setCancelCallback(() -> window.close());
        loginPanel.setRegisterCallback(this::setRegistrationView);
        loginPanel.setLoginCallback((String login, String password) -> {
            boolean success = authService.authenticate(login, password);
            if (success)
                JOptionPane.showMessageDialog(
                        null,
                        "Successful login",
                        "Access granted",
                        JOptionPane.INFORMATION_MESSAGE
                );
            else
                JOptionPane.showMessageDialog(
                        null,
                        "Incorrect login or password",
                        "Access denied",
                        JOptionPane.ERROR_MESSAGE
                );
        });

        registrationPanel.setRegisterCallback((String login, String password) -> {
            authService.register(login, password);
            setLoginView();
        });
        registrationPanel.setCancelCallback(this::setLoginView);
    }

    private void setLoginView() {
        window.setContent("Log in", loginPanel.getRootComponent());
    }

    private void setRegistrationView() {
        window.setContent("Registration", registrationPanel.getRootComponent());
    }

    private static File getAppDirectory() {
        URL classUrl = App.class.getResource("App.class");
        return new File(classUrl.getPath()).getParentFile();
    }

    private static File getPasswordStorageFile() {
        return new File(getAppDirectory(), "passwords.csv");
    }

}
