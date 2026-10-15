package linear.algebra.ui;

import linear.algebra.core.Matrix;
import linear.algebra.core.RowReducer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Shows a summary and an optional step-by-step log.
 */
public class StepViewer extends JPanel {
    private final JTextArea area = new JTextArea();
    private final JCheckBox showSteps = new JCheckBox("Show steps", true);
    private String summary = "";
    private String steps = "";

    /**
     * Creates the summary and step viewer.
     */
    public StepViewer() {
        super(new BorderLayout());
        area.setEditable(false);
        area.setFont(Theme.MONO);
        area.setMargin(new Insets(8, 8, 8, 8));
        add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(showSteps);
        add(bar, BorderLayout.NORTH);
        showSteps.addActionListener(e -> refresh());
    }

    /**
     * Sets the summary and optional operation log.
     * @param summary the summary
     * @param steps the steps
     */
    public void setContent(String summary, String steps) {
        this.summary = summary;
        this.steps = steps;
        refresh();
    }

    /**
     * Updates the displayed summary and step log.
     */
    private void refresh() {
        if (showSteps.isSelected() && !steps.isEmpty()) {
            area.setText(summary + "\n" + steps);
        } else {
            area.setText(summary);
        }
        area.setCaretPosition(0);
    }

    /**
     * Formats recorded row operations and matrix states.
     * @param stepList the recorded steps
     * @param divider the number of columns before the divider, or -1 for none
     * @return the formatted operation log
     */
    public static String formatSteps(List<RowReducer.Step> stepList, int divider) {
        StringBuilder output = new StringBuilder();
        int stepNumber = 0;
        for (RowReducer.Step step : stepList) {
            Matrix state = step.state();
            if (stepNumber == 0) {
                output.append("Start");
            } else {
                output.append(stepNumber).append(". ").append(step.description());
            }
            output.append('\n');
            output.append(state.format(divider)).append('\n');
            stepNumber++;
        }
        return output.toString();
    }
}
