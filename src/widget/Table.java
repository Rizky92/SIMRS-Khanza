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
            super.processMouseEvent(new MouseEvent(this, MouseEvent.MOUSE_CLICKED, e.getWhen(), e.getModifiersEx() & ~InputEvent.BUTTON3_DOWN_MASK, e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), 1, false, MouseEvent.BUTTON3));
        }
        return super.getPopupLocation(e);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (MouseEvent.MOUSE_CLICKED == e.getID() && MouseEvent.BUTTON3 == e.getButton()) {
            return;
        }
        super.processMouseEvent(e);
    }
}
