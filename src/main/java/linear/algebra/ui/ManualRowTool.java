package linear.algebra.ui;

import linear.algebra.core.Fraction;
import linear.algebra.core.Matrix;

import javax.swing.*;
import java.awt.*;

/**
 * Lets a student apply row operations one at a time.
 */
public class ManualRowTool extends JPanel implements Tool {
    private final JTextArea log = new JTextArea();
    private final JSpinner rowIndexSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
    private final JSpinner otherRowSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
    private final JTextField factorField = new JTextField("1", 5);
    private final JLabel message = new JLabel(" ");
    private Matrix start;
    private Matrix current;
    private int divider = -1;
    private StringBuilder history = new StringBuilder();

    /**
     * Creates the manual row-operation controls.
     */
    public ManualRowTool() {
        super(new BorderLayout());
        log.setEditable(false);
        log.setFont(Theme.MONO);
        log.setMargin(new Insets(8, 8, 8, 8));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Ri"));
        controls.add(rowIndexSpinner);
        controls.add(new JLabel("Rj"));
        controls.add(otherRowSpinner);
        controls.add(new JLabel("k"));
        controls.add(factorField);
        JButton swap = new JButton("Ri <-> Rj");
        JButton scale = new JButton("Ri -> k Ri");
        JButton add = new JButton("Ri -> Ri + k Rj");
        JButton reset = new JButton("Reset");
        for (JButton button : new JButton[]{swap, scale, add, reset}) {
            controls.add(button);
        }
        swap.addActionListener(e -> apply('s'));
        scale.addActionListener(e -> apply('m'));
        add.addActionListener(e -> apply('a'));
        reset.addActionListener(e -> reset());

        JPanel north = new JPanel(new BorderLayout());
        north.add(controls, BorderLayout.NORTH);
        north.add(message, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(log), BorderLayout.CENTER);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Manual row ops";
    }

    /**
     * Returns the component displayed in the tab.
     * @return the component to display
     */
    @Override
    public JComponent view() {
        return this;
    }

    /**
     * Refreshes this tool for the supplied matrix.
     * @param matrix the matrix to process
     * @param augmented whether the final column is the right-hand side
     */
    @Override
    public void update(Matrix matrix, boolean augmented) {
        start = matrix;
        if (augmented) {
            divider = matrix.cols() - 1;
        } else {
            divider = -1;
        }
        reset();
    }

    /**
     * Restores the starting matrix and clears the operation history.
     */
    private void reset() {
        current = start;
        history = new StringBuilder("Start\n" + current.format(divider) + "\n");
        message.setText(" ");
        log.setText(history.toString());
    }

    /**
     * Applies the selected row operation or component styling.
     * @param operation the selected row operation
     */
    private void apply(char operation) {
        int rowIndex = (Integer) rowIndexSpinner.getValue() - 1;
        int otherRowIndex = (Integer) otherRowSpinner.getValue() - 1;
        if (rowIndex >= current.rows() || (operation != 'm' && otherRowIndex >= current.rows())) {
            message.setText("Row out of range");
            return;
        }
        Fraction factor;
        try {
            factor = Fraction.parse(factorField.getText());
        } catch (RuntimeException ex) {
            message.setText("k is not a valid number");
            return;
        }
        if (operation == 'm' && factor.isZero()) {
            message.setText("Can't scale a row by 0");
            return;
        }
        if (operation != 'm' && rowIndex == otherRowIndex) {
            message.setText("Pick two different rows");
            return;
        }

        Fraction[][] entries = new Fraction[current.rows()][current.cols()];
        for (int dataRow = 0; dataRow < entries.length; dataRow++) {
            for (int columnIndex = 0; columnIndex < entries[dataRow].length; columnIndex++) {
                entries[dataRow][columnIndex] = current.get(dataRow, columnIndex);
            }
        }
        String description;
        switch (operation) {
            case 's' -> {
                Fraction[] temporaryRow = entries[rowIndex];
                entries[rowIndex] = entries[otherRowIndex];
                entries[otherRowIndex] = temporaryRow;
                description = "R" + (rowIndex + 1) + " <-> R" + (otherRowIndex + 1);
            }
            case 'm' -> {
                for (int columnIndex = 0; columnIndex < entries[rowIndex].length; columnIndex++) {
                    entries[rowIndex][columnIndex] = entries[rowIndex][columnIndex].mul(factor);
                }
                description = "R" + (rowIndex + 1) + " -> (" + factor + ")R" + (rowIndex + 1);
            }
            default -> {
                for (int columnIndex = 0; columnIndex < entries[rowIndex].length; columnIndex++) {
                    entries[rowIndex][columnIndex] = entries[rowIndex][columnIndex].add(factor.mul(entries[otherRowIndex][columnIndex]));
                }
                description = "R" + (rowIndex + 1) + " -> R" + (rowIndex + 1) + " + (" + factor + ")R" + (otherRowIndex + 1);
            }
        }
        current = new Matrix(entries);
        message.setText(" ");
        history.append(description).append('\n').append(current.format(divider)).append('\n');
        log.setText(history.toString());
    }
}
