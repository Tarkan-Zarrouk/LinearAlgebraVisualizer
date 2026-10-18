package linear.algebra.ui;

import linear.algebra.core.Matrix;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Hosts the matrix editor and visualizer tools.
 */
public class MainFrame extends JFrame {
    private final MatrixInput input = new MatrixInput();
    private final JTabbedPane tabs = new JTabbedPane();
    private final List<Tool> tools;

    /**
     * Creates the application window and its visualizer tabs.
     */
    public MainFrame() {
        super("MATH 1104 Linear Algebra Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tools = List.of(new RrefTool(), new ManualRowTool(), new SystemsTool(),
        new TransformTool(input::setMatrix), new DeterminantTool(), new InverseTool());
        for (Tool tool : tools) {
            tabs.addTab(tool.title(), tool.view());
        }

        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);
        JPanel left = new JPanel(new BorderLayout());
        left.add(input, BorderLayout.NORTH);
        left.setPreferredSize(new Dimension(300, 0));
        add(left, BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);

        input.addChangeListener(this::refresh);
        refresh();
        restyle();
        setSize(1100, 720);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the toolbar and its appearance controls.
     * @return the toolbar component
     */
    private JComponent buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JCheckBox dark = new JCheckBox("Dark mode");
        dark.addActionListener(e -> {
            Theme.setDark(dark.isSelected());
            restyle();
        });
        JComboBox<String> accent = new JComboBox<>(Theme.ACCENTS.keySet().toArray(new String[0]));
        accent.addActionListener(e -> {
            Theme.setAccent(Theme.ACCENTS.get((String) accent.getSelectedItem()));
            restyle();
        });
        JButton custom = new JButton("Custom colour...");
        custom.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Accent colour", Theme.accent());
            if (color != null) {
                Theme.setAccent(color);
                restyle();
            }
        });
        bar.add(dark);
        bar.add(new JLabel("Accent"));
        bar.add(accent);
        bar.add(custom);
        return bar;
    }

    /**
     * Updates the displayed summary and step log.
     */
    private void refresh() {
        Matrix matrix = input.getMatrix();
        if (matrix == null) {
            return;
        }
        boolean augmented = input.isAugmented();
        for (Tool tool : tools) {
            tool.update(matrix, augmented);
        }
    }

    /**
     * Applies the selected theme to the window.
     */
    private void restyle() {
        Theme.install();
        Theme.apply(getContentPane());
        SwingUtilities.updateComponentTreeUI(tabs);
        Theme.apply(getContentPane());
    }
}
