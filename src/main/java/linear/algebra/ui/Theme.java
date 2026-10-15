package linear.algebra.ui;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides the shared colour palette and component styling.
 */
public final class Theme {
    /**
     * The named accent colours available in the interface.
     */
    public static final Map<String, Color> ACCENTS = new LinkedHashMap<>();

    static {
        ACCENTS.put("Mono", null);
        ACCENTS.put("Blue", new Color(0x2563EB));
        ACCENTS.put("Green", new Color(0x16A34A));
        ACCENTS.put("Red", new Color(0xDC2626));
        ACCENTS.put("Orange", new Color(0xEA580C));
        ACCENTS.put("Purple", new Color(0x7C3AED));
    }

    /**
     * The shared monospaced font.
     */
    public static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private static boolean dark;
    private static Color accent;

    /** Prevents instances of this utility class. */
    private Theme() {
    }

    /**
     * Reports whether dark mode is enabled.
     * @return true when dark mode is enabled
     */
    public static boolean isDark() {
        return dark;
    }

    /**
     * Sets whether dark mode is enabled.
     * @param darkMode whether dark mode is enabled
     */
    public static void setDark(boolean darkMode) {
        dark = darkMode;
    }

    /**
     * Sets the active accent colour.
     * @param color the colour to use
     */
    public static void setAccent(Color color) {
        accent = color;
    }

    /**
     * Returns the current background colour.
     * @return the background colour
     */
    public static Color bg() {
        if (dark) {
            return new Color(0x111111);
        }
        return Color.WHITE;
    }

    /**
     * Returns the current foreground colour.
     * @return the foreground colour
     */
    public static Color fg() {
        if (dark) {
            return new Color(0xF2F2F2);
        }
        return Color.BLACK;
    }

    /**
     * Returns the current muted colour.
     * @return the muted colour
     */
    public static Color muted() {
        if (dark) {
            return new Color(0x8A8A8A);
        }
        return new Color(0x6B6B6B);
    }

    /**
     * Returns the current grid colour.
     * @return the grid colour
     */
    public static Color grid() {
        if (dark) {
            return new Color(0x2A2A2A);
        }
        return new Color(0xE6E6E6);
    }

    /**
     * Returns the active accent colour.
     * @return the active accent colour
     */
    public static Color accent() {
        if (accent != null) {
            return accent;
        }
        return fg();
    }

    /**
     * Installs the current palette in Swing UI defaults.
     */
    public static void install() {
        UIManager.put("TabbedPane.selected", grid());
        UIManager.put("TabbedPane.contentAreaColor", bg());
        UIManager.put("TabbedPane.background", bg());
        UIManager.put("TabbedPane.foreground", fg());
        UIManager.put("TabbedPane.light", bg());
        UIManager.put("TabbedPane.highlight", muted());
        UIManager.put("TabbedPane.shadow", muted());
        UIManager.put("TabbedPane.darkShadow", muted());
        UIManager.put("TabbedPane.focus", fg());
    }

    /**
     * Applies the current palette throughout a component tree.
     * @param component the component to style
     */
    public static void apply(Component component) {
        component.setBackground(bg());
        component.setForeground(fg());
        if (component instanceof JTextComponent textComponent) {
            textComponent.setCaretColor(fg());
            textComponent.setSelectionColor(muted());
            textComponent.setSelectedTextColor(bg());
            if (component instanceof JTextField) {
                textComponent.setBorder(BorderFactory.createLineBorder(muted()));
            }
        }
        if (component instanceof JButton button) {
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(muted()),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
            button.setFocusPainted(false);
            button.setOpaque(true);
        }
        if (component instanceof JComponent swingComponent
        && swingComponent.getBorder() instanceof javax.swing.border.TitledBorder titledBorder) {
            titledBorder.setTitleColor(fg());
        }
        if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBorder(BorderFactory.createLineBorder(grid()));
            scrollPane.getViewport().setBackground(bg());
        }
        if (component instanceof JTabbedPane tabbedPane) {
            tabbedPane.setBackground(bg());
            tabbedPane.setForeground(fg());
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                apply(child);
            }
        }
        component.repaint();
    }
}
