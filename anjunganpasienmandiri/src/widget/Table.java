package widget;

import com.formdev.flatlaf.extras.components.FlatTable;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class Table extends FlatTable {
    public Table() {
        super();
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(true);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? getBackground() : UIManager.getColor("Table.alternateRowColor"));
        }

        return c;
    }
}
