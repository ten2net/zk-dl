package cz.datalite.zk.components.combo;

import cz.datalite.zk.components.cascade.Cascadable;
import org.zkoss.zk.ui.util.Composer;

/**
 * Controller for the exteded component functionality which supports
 * loading data onOpen event. Also there is supported simple combobox
 * cascade.
 *
 * @param <T> main combobox entity
 * @author Karel Cemus
 * @deprecated since 1.4.0, ZK 6, databinding 2. This component is not usable anymore
 */
@Deprecated
public interface DLComboboxController<T> extends Composer, Cascadable<T> {

    /**
     * Returns actual index of the selected entity
     * @return index of the actual selected entity
     */
    int getSelectedIndex();
}
