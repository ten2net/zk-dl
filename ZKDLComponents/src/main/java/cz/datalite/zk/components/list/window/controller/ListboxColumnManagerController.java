package cz.datalite.zk.components.list.window.controller;

import cz.datalite.zk.components.list.view.DLListbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericAutowireComposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the column manager which allows to move and hide some columns
 * @author Karel Cemus
 */
public class ListboxColumnManagerController extends GenericAutowireComposer {

    // model
    protected List<Map<String, Object>> usedModel = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> unusedModel = new ArrayList<Map<String, Object>>();
    // view
    public DLListbox usedListbox;
    public DLListbox unusedListbox;
    // controller
    protected ListboxSelectorController selector;

    @Override
    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final org.zkoss.zk.ui.Component comp ) throws Exception {
        super.doAfterCompose(comp);
        comp.setAttribute("ctl", this, Component.SPACE_SCOPE);

//        usedListbox = ( DLListbox ) comp.getFellow( "usedListbox" );
//        unusedListbox = ( DLListbox ) comp.getFellow( "unusedListbox" );

        final List<Map<String, Object>> columnModels = ( List<Map<String, Object>> ) arg.get( "columnModels" );

        for ( Map<String, Object> map : columnModels ) {
            if ( ( Boolean ) map.get( "visible" ) ) {
                usedModel.add( prepareMap( map ) );
            } else {
                unusedModel.add( prepareMap( map ) );
            }
        }

        java.util.Collections.sort( usedModel, new java.util.Comparator<Map<String, Object>>() {

            public int compare( final Map<String, Object> object1, final Map<String, Object> object2 ) {
                return ( Integer ) object1.get( "order" ) - ( Integer ) object2.get( "order" );
            }
        } );

        selector = new ListboxSelectorController( usedModel, unusedModel, usedListbox, unusedListbox );
    }

    protected Map<String, Object> prepareMap( final Map<String, Object> map ) {
        return new java.util.HashMap<String, Object>( map );
    }

    public void onOk() {
        // setup ordering
        int i = 1;
        for (Map<String, Object> row : usedModel)
            row.put("order", i++);
        
        org.zkoss.zk.ui.event.Events.postEvent( new org.zkoss.zk.ui.event.Event( "onSave", self, usedModel ) );
        self.detach();
    }

    public void onStorno() {
        self.detach();
    }

    public ListboxSelectorController getSelector() {
        return selector;
    }
}
