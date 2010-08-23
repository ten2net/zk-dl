package cz.datalite.zk.components.list.controller;

import cz.datalite.dao.DLResponse;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.model.DLColumnModel;
import cz.datalite.zk.components.list.model.DLMasterModel;
import java.util.List;
import java.util.Map;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.util.Composer;

/**
 * Interface for the master listbox controller. This is private api.
 * @param <T> main entity type
 * @author Karel Čemus <cemus@datalite.cz>
 */
public interface DLListboxExtController<T> extends DLListboxController<T>, Composer {

    /**
     * Reacts on paging model change.
     */
    void onPagingModelChange();

    /**
     * Reacts on filter model change.
     */
    void onFilterChange( final String filterType );

    /**
     * Reacts on sorting model change.
     */
    void onSortChange();

    /**
     * Returns column model which is used for listheaders.
     * @return column model
     */
    DLColumnModel getColumnModel();

    /**
     * Returns normal filter model.
     * @return normal filter model
     */
    NormalFilterModel getNormalFilterModel();

    /**
     * Reacts on sort manager OK.
     * @param data data from manager
     */
    void onSortManagerOk( List<Map<String, Object>> data );

    /**
     * Reacts on column manager OK.
     * @param data data from manager
     */
    void onColumnManagerOk( List<Map<String, Object>> data );

    /**
     * Reacts on filter manager OK.
     * @param data data form manager
     */
    void onFilterManagerOk( NormalFilterModel data );

    /**
     * Reacts on export manager OK.
     * @param data data from the manager
     */
    void onExportManagerOk( AMedia data );

    /**
     * Reacts on onCreate event.
     */
    void onCreate();

    /**
     * Returns instance of type of the main entity
     * @return class of entity type
     */
    Class getEntityClass();

    /**
     * Returns column distinct list.
     * @param column column wiith unique data
     * @param normalFilter normal filter model
     * @return data list
     */
    List loadDistinctValues( String column, NormalFilterModel normalFilter );

    /**
     * Refreshes binding.
     */
    void refreshBinding();

    /**
     * Returns easyFilter controller
     * @return easyFilter controller
     */
    DLEasyFilterController getEasyFilterController();

    /**
     * Loads data with actual filter model model but not with paging.
     * @param rowLimit limit of entities
     * @return loaded data
     */
    DLResponse<T> loadData( int rowLimit );

    /**
     * Returns master model.
     * @return master model
     */
    DLMasterModel getModel();

    /**
     * Returns window controller - main controller on the page.
     * @return window controller
     */
    Composer getWindowCtl();

    /**
     * Reacts on onSelect event.
     * @throws Exception
     */
    void onSelect();

    /**
     * Sets selected item.
     * @param selectedItem selected item
     */
    void setSelectedItem( T selectedItem );

    /**
     * Sets selected index
     * @param index selected index
     */
    void setSelectedIndex( int index );
}