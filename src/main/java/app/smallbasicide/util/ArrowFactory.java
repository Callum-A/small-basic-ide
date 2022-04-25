package app.smallbasicide.util;
import java.util.function.IntFunction;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.reactfx.value.Val;

/**
 * Class to build breakpoint indicators on line numbers.
 */
public class ArrowFactory implements IntFunction<Node> {
    private final ObservableValue<Integer> shownLine; // The line where the breakpoint is

    /**
     * Constructor taking the shown line
     */
    public ArrowFactory(ObservableValue<Integer> shownLine) {
        this.shownLine = shownLine;
    }

    /**
     * Apply the arrow factory to a given line number displaying the breakpoint
     * indicator if appropriate.
     */
    @Override
    public Node apply(int lineNumber) {
        Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
        triangle.setFill(Color.GREEN);

        ObservableValue<Boolean> visible = Val.map(
                shownLine,
                sl -> sl == lineNumber);

        triangle.visibleProperty().bind(visible);

        return triangle;
    }
}

