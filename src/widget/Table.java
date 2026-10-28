package widget;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 *
 * @author usu
 */
public class Table extends JTable {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public Table() {
        super();
        //setBackground(new Color(255,235,255));
        //setGridColor(new Color(245,170,245));
        //setForeground(new Color(90,90,90));
        setBackground(new Color(255,255,255));
        setGridColor(new Color(226,231,221));
        setForeground(new Color(50,50,50));
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setRowHeight(22);
        setSelectionBackground(new Color(255,255,255));
        setSelectionForeground(new Color(255,0,0));
        getTableHeader().setForeground(new Color(50,50,50));
        getTableHeader().setBackground(new Color(255,250,250));
        getTableHeader().setBorder(javax.swing.BorderFactory.createLineBorder(new Color(255,250,250)));
        getTableHeader().setFont(new java.awt.Font("Tahoma", 0, 11));
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    public Point getPopupLocation(MouseEvent e) {
        if (null != e) {
            int r = rowAtPoint(e.getPoint());
            int c = columnAtPoint(e.getPoint());
            if (r != -1 && c != -1 && !isRowSelected(r)) {
                changeSelection(r, c, false, false);
            }
            super.processMouseEvent(rightButtonEvent(e, e.getID()));
            super.processMouseEvent(rightButtonEvent(e, MouseEvent.MOUSE_CLICKED));
        }
        return super.getPopupLocation(e);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (MouseEvent.BUTTON3 == e.getButton()) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                case MouseEvent.MOUSE_RELEASED:
                    if (e.isPopupTrigger() && null != getComponentPopupMenu()) {
                        if (!e.isConsumed()) {
                            Point pt = getPopupLocation(e);
                            if (null == pt) {
                                pt = e.getPoint();
                            }
                            getComponentPopupMenu().show(this, pt.x, pt.y);
                            e.consume();
                        }
                    } else {
                        super.processMouseEvent(rightButtonEvent(e, e.getID()));
                    }
                    return;
                case MouseEvent.MOUSE_CLICKED:
                    return;
                default:
                    break;
            }
        }
        super.processMouseEvent(e);
    }

    private MouseEvent rightButtonEvent(MouseEvent e, int id) {
        int modifiers = MouseEvent.MOUSE_PRESSED == id ? e.getModifiersEx() | InputEvent.BUTTON3_DOWN_MASK : e.getModifiersEx() & ~InputEvent.BUTTON3_DOWN_MASK;
        return new MouseEvent(this, id, e.getWhen(), modifiers, e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), 1, false, MouseEvent.BUTTON3);
    }
}
