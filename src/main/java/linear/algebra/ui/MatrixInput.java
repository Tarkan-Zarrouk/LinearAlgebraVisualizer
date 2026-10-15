package linear.algebra.ui;

import linear.algebra.core.Fraction;
import linear.algebra.core.Matrix;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Provides the shared editable matrix grid.
 */
public class MatrixInput extends JPanel {
    /**
     * A named matrix preset and its augmentation setting.
     */
    private record Preset(String name, int[][] values, boolean augmented) {
    }

    private static final Preset[] PRESETS = {
        new Preset("Presets...", null, false),
        new Preset("System: one solution", new int[][]{{1, 1, 3}, {1, -1, 1}}, true),
        new Preset("System: parallel lines", new int[][]{{1, 1, 2}, {2, 2, 5}}, true),
        new Preset("System: same line", new int[][]{{1, 1, 2}, {2, 2, 4}}, true),
        new Preset("System: free variable", new int[][]{{1, 2, -1, 3}, {2, 4, 1, 3}}, true),
        new Preset("System 3x3", new int[][]{{1, 2, 1, 4}, {2, 5, 0, 7}, {1, 1, -2, 0}}, true),
        new Preset("Rotation 90", new int[][]{{0, -1}, {1, 0}}, false),
        new Preset("Reflection in y = x", new int[][]{{0, 1}, {1, 0}}, false),
        new Preset("Shear", new int[][]{{1, 1}, {0, 1}}, false),
        new Preset("Scaling by 2", new int[][]{{2, 0}, {0, 2}}, false),
        new Preset("Projection onto x-axis", new int[][]{{1, 0}, {0, 0}}, false),
        new Preset("Invertible 3x3", new int[][]{{2, 1, 1}, {1, 3, 2}, {1, 0, 0}}, false),
        new Preset("Singular 3x3", new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, false),
    };

    private final JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
    private final JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    private final JCheckBox augmented = new JCheckBox("Augmented (last column is b)", true);
    private final JPanel grid = new JPanel();
    private final List<List<JTextField>> fields = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private final Random random = new Random();
    private boolean updating;

    /**
     * Creates the shared matrix editor.
     */
    public MatrixInput() {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel size = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        size.add(new JLabel("Rows"));
        size.add(rowsSpinner);
        size.add(new JLabel("Cols"));
        size.add(colsSpinner);

        JComboBox<String> presets = new JComboBox<>();
        for (Preset preset : PRESETS) {
            presets.addItem(preset.name());
        }
        presets.addActionListener(e -> {
            Preset preset = PRESETS[presets.getSelectedIndex()];
            if (preset.values() != null) {
                updating = true;
                augmented.setSelected(preset.augmented());
                updating = false;
                setMatrix(Matrix.ofInts(preset.values()));
            }
        });

        JButton randomButton = new JButton("Random");
        randomButton.addActionListener(e -> randomize());

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        for (JComponent component : new JComponent[]{size, augmented, presets, randomButton}) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            top.add(component);
            top.add(Box.createVerticalStrut(4));
        }
        add(top, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);

        rowsSpinner.addChangeListener(e -> resize());
        colsSpinner.addChangeListener(e -> resize());
        augmented.addActionListener(e -> fire());
        rebuild(2, 3);
        setMatrix(Matrix.ofInts(PRESETS[1].values()));
    }

    /**
     * Registers a callback for matrix input changes.
     * @param listener the callback to register
     */
    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Reports whether the final column is treated as the right-hand side.
     * @return true if the final column is treated as the right-hand side
     */
    public boolean isAugmented() {
        return augmented.isSelected() && (Integer) colsSpinner.getValue() > 1;
    }

    /**
     * Returns the entered matrix, or null while an entry is invalid.
     * @return the entered matrix, or null if an entry is invalid
     */
    public Matrix getMatrix() {
        int rowCount = fields.size();
        int columnCount = fields.get(0).size();
        Fraction[][] entries = new Fraction[rowCount][columnCount];
        try {
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    entries[i][j] = Fraction.parse(fields.get(i).get(j).getText());
                }
            }
        } catch (RuntimeException ex) {
            return null;
        }
        return new Matrix(entries);
    }

    /**
     * Replaces the entries shown in the editor.
     * @param matrix the matrix to display
     */
    public void setMatrix(Matrix matrix) {
        updating = true;
        rowsSpinner.setValue(matrix.rows());
        colsSpinner.setValue(matrix.cols());
        rebuild(matrix.rows(), matrix.cols());
        for (int i = 0; i < matrix.rows(); i++) {
            for (int j = 0; j < matrix.cols(); j++) {
                fields.get(i).get(j).setText(matrix.get(i, j).toString());
            }
        }
        updating = false;
        fire();
    }

    /**
     * Fills the editor with random integer entries.
     */
    private void randomize() {
        int rowCount = (Integer) rowsSpinner.getValue();
        int columnCount = (Integer) colsSpinner.getValue();
        int[][] values = new int[rowCount][columnCount];
        for (int[] row : values) {
            for (int j = 0; j < columnCount; j++) {
                row[j] = random.nextInt(11) - 5;
            }
        }
        setMatrix(Matrix.ofInts(values));
    }

    /**
     * Resizes the grid while preserving existing cell text.
     */
    private void resize() {
        if (updating) {
            return;
        }
        int rowCount = (Integer) rowsSpinner.getValue();
        int columnCount = (Integer) colsSpinner.getValue();
        List<List<String>> cellText = new ArrayList<>();
        for (List<JTextField> row : fields) {
            List<String> rowText = new ArrayList<>();
            row.forEach(field -> rowText.add(field.getText()));
            cellText.add(rowText);
        }
        updating = true;
        rebuild(rowCount, columnCount);
        for (int i = 0; i < rowCount && i < cellText.size(); i++) {
            for (int j = 0; j < columnCount && j < cellText.get(i).size(); j++) {
                fields.get(i).get(j).setText(cellText.get(i).get(j));
            }
        }
        updating = false;
        fire();
    }

    /**
     * Recreates the matrix entry grid.
     * @param rowCount the number of rows
     * @param columnCount the number of columns
     */
    private void rebuild(int rowCount, int columnCount) {
        grid.removeAll();
        fields.clear();
        grid.setLayout(new GridLayout(rowCount, columnCount, 4, 4));
        for (int i = 0; i < rowCount; i++) {
            List<JTextField> row = new ArrayList<>();
            for (int j = 0; j < columnCount; j++) {
                JTextField field = new JTextField("0", 4);
                field.setHorizontalAlignment(SwingConstants.CENTER);
                field.setFont(Theme.MONO);
                field.getDocument().addDocumentListener(new DocumentListener() {
                    /**
                     * Notifies listeners after text is inserted.
                     * @param e the document event
                     */
                    public void insertUpdate(DocumentEvent e) {
                        fire();
                    }

                    /**
                     * Notifies listeners after text is removed.
                     * @param e the document event
                     */
                    public void removeUpdate(DocumentEvent e) {
                        fire();
                    }

                    /**
                     * Notifies listeners after document attributes change.
                     * @param e the document event
                     */
                    public void changedUpdate(DocumentEvent e) {
                        fire();
                    }
                });
                row.add(field);
                grid.add(field);
            }
            fields.add(row);
        }
        Theme.apply(grid);
        grid.revalidate();
        grid.repaint();
    }

    /**
     * Notifies listeners that the matrix input changed.
     */
    private void fire() {
        if (!updating) {
            listeners.forEach(Runnable::run);
        }
    }
}
