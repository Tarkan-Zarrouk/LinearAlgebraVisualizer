package linear.algebra;

import linear.algebra.ui.MainFrame;

import javax.swing.*;

/**
 * Starts the linear algebra visualizer.
 */
public class Main {
    /**
     * Creates the application entry point.
     */
    public Main() {
    }

    /**
     * Starts the Swing interface on the event dispatch thread.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to the default look and feel
            }
            new MainFrame().setVisible(true);
        });
    }
}
