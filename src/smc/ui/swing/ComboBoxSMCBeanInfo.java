package smc.ui.swing;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Bean information for {@link ComboBoxSMC}. Keeps every introspected property and attaches
 * {@link ComboBoxEntriesEditorSMC} to the "entries" property for the GUI editor.
 *
 * @author smc
 */
public final class ComboBoxSMCBeanInfo extends SimpleBeanInfo {

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor[] descriptors = Introspector.getBeanInfo(ComboBoxSMC.class, Introspector.IGNORE_IMMEDIATE_BEANINFO).getPropertyDescriptors();

            for (PropertyDescriptor descriptor : descriptors) {
                if ("entries".equals(descriptor.getName())) {
                    descriptor.setPropertyEditorClass(ComboBoxEntriesEditorSMC.class);
                }
            }

            return descriptors;
        } catch (IntrospectionException e) {
            return null;
        }
    }
}
