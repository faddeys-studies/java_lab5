package loginwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import utils.Procedure;

public class MainWindow {

    private JFrame frame;
    private Procedure onClose;

    private static final int WINDOW_WIDTH = 350;
    private static final int WINDOW_HEIGHT = 200;

    public MainWindow() {

        frame = new JFrame();
        onClose = null;

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.setContentPane(rootPanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(
                ((int)screenSize.getWidth()-WINDOW_WIDTH)/2,
                ((int)screenSize.getHeight()-WINDOW_HEIGHT)/2,
                WINDOW_WIDTH,
                WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onClose != null) {
                    onClose.execute();
                }
                System.exit(0);
            }
        });


    }

    public void setContent(String name, JComponent component) {
        frame.setTitle(name);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.repaint();
    }

    void setCloseCallback(Procedure onClose) {
        this.onClose = onClose;
    }

    void close() {
        frame.dispose();
    }

}
