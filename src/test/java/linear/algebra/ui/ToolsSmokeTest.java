package linear.algebra.ui;

import linear.algebra.core.Matrix;
import linear.algebra.ui.model.MatrixInput;
import linear.algebra.ui.tools.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolsSmokeTest {
    @Test
    void everyToolHandlesCommonShapes() {
        List<Tool> tools = List.of(new RrefTool(), new ManualRowTool(), new SystemsTool(),
                new TransformTool(m -> { }), new DeterminantTool(), new InverseTool());
        Matrix[] inputs = {
                Matrix.ofInts(new int[][]{{1, 1, 3}, {1, -1, 1}}),
                Matrix.ofInts(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}),
                Matrix.ofInts(new int[][]{{5}}),
                Matrix.ofInts(new int[][]{{1, 2, 3, 4}, {0, 1, 0, 2}, {2, 0, 1, 1}, {1, 1, 1, 1}}),
        };
        for (Tool t : tools) {
            JComponent v = t.view();
            v.setSize(600, 500);
            for (Matrix m : inputs) {
                for (boolean aug : new boolean[]{true, false}) {
                    t.update(m, aug && m.cols() > 1);
                    v.doLayout();
                    var img = new BufferedImage(600, 500, BufferedImage.TYPE_INT_ARGB);
                    var g = img.createGraphics();
                    assertDoesNotThrow(() -> v.paintAll(g), t.title());
                    g.dispose();
                }
            }
        }
    }

    @Test
    void matrixInputRoundTrips() {
        MatrixInput in = new MatrixInput();
        Matrix m = Matrix.ofInts(new int[][]{{1, 2}, {3, 4}});
        in.setMatrix(m);
        assertEquals(m, in.getMatrix());
    }
}
