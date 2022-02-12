package app.smallbasicide.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.ArrayList;
import java.util.function.IntFunction;

public class HBoxFactory {

    public static IntFunction<Node> buildSideBars(CodeArea ta, int breakpoint) {
        IntFunction<Node> numberFactory = LineNumberFactory.get(ta);
        IntFunction<Node> graphicFactory = line -> {
            Node[] children = new Node[2];
            children[0] = numberFactory.apply(line);
            children[1] = new ArrowFactory(new ObservableValue<Integer>() {
                @Override
                public void addListener(ChangeListener<? super Integer> listener) {

                }

                @Override
                public void removeListener(ChangeListener<? super Integer> listener) {

                }

                @Override
                public Integer getValue() {
                    return breakpoint;
                }

                @Override
                public void addListener(InvalidationListener listener) {

                }

                @Override
                public void removeListener(InvalidationListener listener) {

                }
            }).apply(line);
            HBox hbox = new HBox(children);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setStyle("-fx-font-size: 1em");
            return hbox;
        };

        return graphicFactory;
    }

}
