package widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.accessibility.Accessible;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;

/**
 * A ComboBox alternative that can be populated with a plain List or with key-value pairs (Map).
 * When editable, typing filters the items (exact match, then starts-with, then contains) and
 * Enter picks the top match. Its appearance follows the active look and feel.
 *
 * @author smc
 */
public final class ComboBoxSMC extends JComboBox<Object> {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    private FilterModel filterModel;
    private Dimension searchPreferredSize;
    private Dimension searchMinimumSize;
    private boolean searching;
    private boolean filtering;
    private boolean settingText;
    private boolean searchPending;

    public ComboBoxSMC() {
        super();
        setFont(new Font("Tahoma", 0, 11));
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(50, 50, 50));
        setSize(WIDTH, 23);
        filterModel = new FilterModel();
        super.setModel(filterModel);
    }

    public void setItems(List<?> items) {
        filterModel.replace(null == items ? Collections.emptyList() : items);
    }

    public void setItems(Map<?, ?> items) {
        List<Object> entries = new ArrayList<>();
        if (null != items) {
            items.forEach((key, value) -> entries.add(new Entry(key, value)));
        }
        filterModel.replace(entries);
    }

    public void addItem(Object key, Object value) {
        filterModel.addElement(new Entry(key, value));
    }

    public Object getSelectedKey() {
        return keyOf(getSelectedItem());
    }

    public Object getSelectedValue() {
        Object selected = getSelectedItem();
        return selected instanceof Entry ? ((Entry) selected).value : selected;
    }

    public void setSelectedKey(Object key) {
        Object match = null;
        for (Object element : filterModel.all) {
            if (Objects.equals(keyOf(element), key)) {
                match = element;
                break;
            }
        }
        setSelectedItem(match);
    }

    @Override
    public void setModel(ComboBoxModel<Object> model) {
        if (null == filterModel || filterModel == model) {
            super.setModel(model);
            return;
        }

        List<Object> elements = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            elements.add(model.getElementAt(i));
        }
        filterModel.replace(elements);
        filterModel.setSelectedItem(filterModel.find(model.getSelectedItem()));
    }

    @Override
    public void setEditor(ComboBoxEditor editor) {
        super.setEditor(null == editor || editor instanceof EditableComboBoxEditor ? editor : new EditableComboBoxEditor(editor));
    }

    @Override
    public void setEditable(boolean editable) {
        if (!editable && null != filterModel) {
            endSearch();
        }
        super.setEditable(editable);
    }

    @Override
    public void setSelectedItem(Object item) {
        if (null == filterModel) {
            super.setSelectedItem(item);
            return;
        }

        Object element = filterModel.find(item);
        if (null != item && null == element) {
            return;
        }

        boolean wasSearching = searching;
        endSearch();
        super.setSelectedItem(element);
        if (wasSearching) {
            configureEditor(getEditor(), getSelectedItem());
        }
    }

    @Override
    public void removeAllItems() {
        if (null == filterModel) {
            super.removeAllItems();
            return;
        }

        filterModel.clear();
        if (isEditable()) {
            configureEditor(getEditor(), null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        commit(null == evt || getEditor() != evt.getSource());
    }

    @Override
    public Dimension getPreferredSize() {
        return null == searchPreferredSize ? super.getPreferredSize() : new Dimension(searchPreferredSize);
    }

    @Override
    public Dimension getMinimumSize() {
        return null == searchMinimumSize ? super.getMinimumSize() : new Dimension(searchMinimumSize);
    }

    @Override
    protected void fireActionEvent() {
        if (!filtering) {
            super.fireActionEvent();
        }
    }

    private void commit(boolean lenient) {
        Object previous = getSelectedItem();
        Object chosen = resolve(lenient);
        setPopupVisible(false);
        restore(chosen);

        if (lenient || previous != chosen) {
            String command = getActionCommand();
            setActionCommand("comboBoxEdited");
            fireActionEvent();
            setActionCommand(command);
        }
    }

    private void leave() {
        if (searching) {
            commit(false);
        }
    }

    private Object resolve(boolean lenient) {
        Object selected = filterModel.getSelectedItem();
        String text = editorText().trim();
        if (null != selected && label(selected).equalsIgnoreCase(text)) {
            return selected;
        }

        if (lenient && searching) {
            JList<?> list = popupList();
            Object highlighted = null == list ? null : list.getSelectedValue();
            if (null != highlighted && filterModel.visible.contains(highlighted)) {
                return highlighted;
            }
        }

        if (text.isEmpty()) {
            return selected;
        }

        List<Object> matches = filterModel.rank(text);
        if (matches.isEmpty()) {
            return selected;
        }

        Object top = matches.get(0);
        return lenient || label(top).equalsIgnoreCase(text) ? top : selected;
    }

    private void restore(Object selection) {
        endSearch();
        filterModel.setSelectedItem(selection);
        configureEditor(getEditor(), getSelectedItem());
    }

    private void endSearch() {
        searching = false;
        if (filterModel.isFiltered()) {
            filtering = true;
            try {
                filterModel.show(filterModel.all);
            } finally {
                filtering = false;
            }
        }

        if (null != searchPreferredSize) {
            searchPreferredSize = null;
            searchMinimumSize = null;
            revalidate();
        }
    }

    private void type() {
        if (settingText || filtering || searchPending || null == filterModel || !isEditable()) {
            return;
        }

        ComboBoxEditor editor = getEditor();
        if (null == editor || null == editor.getEditorComponent() || !editor.getEditorComponent().isFocusOwner()) {
            return;
        }

        searchPending = true;
        SwingUtilities.invokeLater(this::search);
    }

    private void search() {
        searchPending = false;
        if (!isEditable() || !isShowing()) {
            return;
        }

        String text = editorText().trim();
        List<Object> matches = filterModel.rank(text);
        int limit = getMaximumRowCount();
        boolean heightChanged = Math.min(filterModel.getSize(), limit) != Math.min(matches.size(), limit);

        if (!searching) {
            searchPreferredSize = getPreferredSize();
            searchMinimumSize = getMinimumSize();
            searching = true;
        }

        filtering = true;
        try {
            filterModel.show(matches);
            if (matches.isEmpty()) {
                setPopupVisible(false);
            } else {
                if (heightChanged && isPopupVisible()) {
                    setPopupVisible(false);
                }
                if (!isPopupVisible()) {
                    setPopupVisible(true);
                }
            }
        } finally {
            filtering = false;
        }

        highlight(text.isEmpty() ? getSelectedIndex() : 0);
    }

    private void press(KeyEvent evt) {
        if (!searching) {
            return;
        }

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                if (isPopupVisible() && moveHighlight(1)) {
                    evt.consume();
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                if (isPopupVisible() && moveHighlight(-1)) {
                    evt.consume();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                setPopupVisible(false);
                restore(getSelectedItem());
                evt.consume();
                break;
            default:
                break;
        }
    }

    private boolean moveHighlight(int step) {
        JList<?> list = popupList();
        if (null == list) {
            return false;
        }

        int size = list.getModel().getSize();
        if (0 < size) {
            highlight(Math.max(0, Math.min(size - 1, list.getSelectedIndex() + step)));
        }
        return true;
    }

    private void highlight(int index) {
        JList<?> list = popupList();
        if (null == list) {
            return;
        }

        if (0 > index || index >= list.getModel().getSize()) {
            list.clearSelection();
            return;
        }

        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    private JList<?> popupList() {
        ComboBoxUI ui = getUI();
        if (null == ui) {
            return null;
        }

        for (int i = 0; i < ui.getAccessibleChildrenCount(this); i++) {
            Accessible child = ui.getAccessibleChild(this, i);
            if (child instanceof ComboPopup) {
                return ((ComboPopup) child).getList();
            }
        }
        return null;
    }

    private String editorText() {
        ComboBoxEditor editor = getEditor();
        Component component = null == editor ? null : editor.getEditorComponent();
        return component instanceof JTextComponent ? ((JTextComponent) component).getText() : "";
    }

    private static String label(Object element) {
        return null == element ? "" : element.toString();
    }

    private static Object keyOf(Object element) {
        return element instanceof Entry ? ((Entry) element).key : element;
    }

    private static final class Entry {

        private final Object key;
        private final Object value;

        Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return null == value ? "" : value.toString();
        }
    }

    /**
     * Holds every item alongside the currently visible (filtered) items. The selected item
     * survives filtering, so narrowing the list never changes the selection.
     */
    private final class FilterModel extends AbstractListModel<Object> implements MutableComboBoxModel<Object> {

        /*
         * Serial version UID
         */
        private static final long serialVersionUID = 1L;

        private final List<Object> all = new ArrayList<>();
        private List<Object> visible = all;
        private Object selected;

        @Override
        public int getSize() {
            return visible.size();
        }

        @Override
        public Object getElementAt(int index) {
            return 0 <= index && index < visible.size() ? visible.get(index) : null;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public void setSelectedItem(Object element) {
            if (selected == element || (null != selected && selected.equals(element))) {
                return;
            }
            selected = element;
            fireContentsChanged(this, -1, -1);
        }

        @Override
        public void addElement(Object element) {
            endSearch();
            all.add(element);
            fireIntervalAdded(this, all.size() - 1, all.size() - 1);
            if (1 == all.size() && null == selected && null != element) {
                setSelectedItem(element);
            }
        }

        @Override
        public void insertElementAt(Object element, int index) {
            endSearch();
            all.add(index, element);
            fireIntervalAdded(this, index, index);
        }

        @Override
        public void removeElement(Object element) {
            int index = all.indexOf(element);
            if (-1 != index) {
                removeElementAt(index);
            }
        }

        @Override
        public void removeElementAt(int index) {
            endSearch();
            if (all.get(index) == selected) {
                if (0 == index) {
                    setSelectedItem(1 == all.size() ? null : all.get(1));
                } else {
                    setSelectedItem(all.get(index - 1));
                }
            }
            all.remove(index);
            fireIntervalRemoved(this, index, index);
        }

        private boolean isFiltered() {
            return visible != all;
        }

        private void replace(Collection<?> elements) {
            clear();
            if (elements.isEmpty()) {
                return;
            }
            all.addAll(elements);
            fireIntervalAdded(this, 0, all.size() - 1);
            setSelectedItem(all.get(0));
        }

        private void clear() {
            endSearch();
            int size = all.size();
            boolean hadSelection = null != selected;
            all.clear();
            selected = null;
            if (0 < size) {
                fireIntervalRemoved(this, 0, size - 1);
            } else if (hadSelection) {
                fireContentsChanged(this, -1, -1);
            }
        }

        private void show(List<Object> elements) {
            if (visible == elements) {
                return;
            }

            int oldSize = visible.size();
            int newSize = elements.size();
            visible = elements;
            if (oldSize > newSize) {
                fireIntervalRemoved(this, newSize, oldSize - 1);
            } else if (newSize > oldSize) {
                fireIntervalAdded(this, oldSize, newSize - 1);
            }
            if (0 < Math.min(oldSize, newSize)) {
                fireContentsChanged(this, 0, Math.min(oldSize, newSize) - 1);
            }
        }

        private List<Object> rank(String text) {
            String keyword = text.trim().toLowerCase();
            if (keyword.isEmpty()) {
                return all;
            }

            List<Object> exact = new ArrayList<>();
            List<Object> prefix = new ArrayList<>();
            List<Object> contains = new ArrayList<>();
            for (Object element : all) {
                String candidate = label(element).toLowerCase();
                if (candidate.equals(keyword)) {
                    exact.add(element);
                } else if (candidate.startsWith(keyword)) {
                    prefix.add(element);
                } else if (candidate.contains(keyword)) {
                    contains.add(element);
                }
            }
            exact.addAll(prefix);
            exact.addAll(contains);
            return exact;
        }

        private Object find(Object item) {
            if (null == item) {
                return null;
            }

            for (Object element : all) {
                if (item == element || item.equals(element) || item.equals(keyOf(element))) {
                    return element;
                }
            }

            String text = item.toString();
            for (Object element : all) {
                if (text.equals(label(element))) {
                    return element;
                }
            }
            return null;
        }
    }

    /**
     * Wraps the editor supplied by the look and feel. The editor keeps its look and feel
     * appearance, while getItem() always returns an item from the list instead of raw text.
     */
    private final class EditableComboBoxEditor implements ComboBoxEditor, UIResource {

        private final ComboBoxEditor mainEditor;

        EditableComboBoxEditor(ComboBoxEditor editor) {
            this.mainEditor = editor;
            Component component = editor.getEditorComponent();
            if (component instanceof JTextComponent) {
                ((JTextComponent) component).getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent evt) {
                        type();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent evt) {
                        type();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent evt) {
                    }
                });
            }

            if (null != component) {
                component.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent evt) {
                        press(evt);
                    }
                });
                component.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent evt) {
                        if (!evt.isTemporary()) {
                            SwingUtilities.invokeLater(ComboBoxSMC.this::leave);
                        }
                    }
                });
            }
        }

        @Override
        public Component getEditorComponent() {
            return mainEditor.getEditorComponent();
        }

        @Override
        public void setItem(Object item) {
            if (filtering) {
                return;
            }
            settingText = true;
            try {
                mainEditor.setItem(item);
            } finally {
                settingText = false;
            }
        }

        @Override
        public Object getItem() {
            return null == filterModel ? mainEditor.getItem() : resolve(true);
        }

        @Override
        public void selectAll() {
            mainEditor.selectAll();
        }

        @Override
        public void addActionListener(ActionListener listener) {
            mainEditor.addActionListener(listener);
        }

        @Override
        public void removeActionListener(ActionListener listener) {
            mainEditor.removeActionListener(listener);
        }
    }
}
