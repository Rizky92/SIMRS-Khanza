
package widget;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

/**
 *
 * @author usu
 */
public class ScrollPane extends JScrollPane {


    public ScrollPane() {
        super();
        // setViewport(new ViewPortGlass());
        setOpaque(false);
        // setBorder(new LineBorder(new Color(235,140,235)));
        // setBackground(new Color(255,235,255));
        setBorder(new LineBorder(new Color(239,244,234)));
        setBackground(new Color(255,255,255));
        getVerticalScrollBar().setUnitIncrement(20);
        getViewport().setBackground(new Color(255, 255, 255));
    }
}
