package cz.datalite.zk.components.lovbox;

import cz.datalite.utils.HashMapAutoCreate;
import cz.datalite.zk.components.cascade.Cascadable;
import cz.datalite.zk.components.cascade.CascadableExt;
import cz.datalite.zk.components.cascade.CascadeUtil;
import cz.datalite.zk.components.list.DLListboxEvents;
import cz.datalite.zk.components.list.DLListboxGeneralController;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;

/**
 * <p>Controller for new lovbox which can substitude combobox. Main advantage is
 * that this component operates only with part of the model, no with all data.
 * As result of this is much greater speed especially with huge data lists.
 * The other strong advantage is integrated filter which helps user to find
 * required record more quickly.</p>
 * <p>One of the disadvantages is unsupported intellisense. </p>
 * @param <T> main entity class in this component
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLLovboxGeneralController<T> implements DLLovboxExtController<T> {

    // controller
    /** multifunction controller for the listbox, paging and quickfilter */
    protected DLListboxExtController<T> listboxController;
    protected CascadeUtil<T> cascadeUtil;
    // model
    /** model for this component. There is stored selected entity for example */
    protected DLLovboxModel<T> model = new DLLovboxModel<T>();
    // view
    /** component which user can see */
    protected DLLovbox<T> lovbox;
    /** list of the listeners */
    protected Map<String, EventListeners> listeners = new HashMapAutoCreate<String, EventListeners>( EventListeners.class );

    /**
     * Creates lovbox controller with this multifuncion listbox controller
     * @param listboxController multifunction listbox controller
     */
    public DLLovboxGeneralController( final DLListboxExtController<T> listboxController ) {

        assert listboxController != null : "Null parameter listboxController in lovbox general controller constructor.";

        this.listboxController = listboxController;
        this.listboxController.addListener( DLListboxEvents.ON_DLSELECT, new EventListener() {

            public void onEvent( final Event event ) {
                onSelect( true );
            }
        } );
        this.listboxController.lockModel();
        cascadeUtil = new CascadeUtil<T>( this );
    }

    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final Component comp ) throws Exception {
        lovbox = ( DLLovbox<T> ) comp;
        lovbox.registerController( this );

        // initialize component - set atributes and controllers
        lovbox.init();
        cascadeUtil.addDefaultParent( lovbox );
    }

    public DLListboxExtController<T> getListboxExtController() {
        return listboxController;
    }

    public DLListboxExtController<T> getListboxController() {
        return listboxController;
    }

    /**
     * Reacts on select event. In this method is setted new selected entity,
     * and also there is invoked fireChanges methods a posted select event
     * @param close close after event
     */
    protected void onSelect( final boolean close ) {
        model.setSelectedItem( listboxController.getSelectedItem() );
        lovbox.fireChanges();
        cascadeUtil.dofireParentChanges();
        if ( close ) {
            lovbox.close();
        }
        final Set<T> set = Collections.singleton( model.getSelectedEntity() );
        callListeners( new SelectEvent( Events.ON_SELECT, lovbox, set ) );
        callListeners( new SelectEvent( DLListboxEvents.ON_DLSELECT, lovbox, set ) );
    }

    public T getSelectedItem() {
        return model.getSelectedEntity();
    }

    public void setSelectedItem( final T selectedItem ) {

        // ensure, that listbox is loaded
        if ( listboxController.isLocked() ) {
            lovbox.onOpen();
        }

        listboxController.setSelectedItem( selectedItem );

        onSelect( true );
    }

    public void addParent( final Cascadable parent, final String column ) {
        cascadeUtil.addParent( parent, column );
    }

    public void addFollower( final CascadableExt follower ) {
        cascadeUtil.addFollower( follower );
    }

    public void fireParentChanges( final Cascadable parent ) {

        if ( listboxController.isLocked() ) {
            listboxController.unlockModel();
        }

        listboxController.getModel().getFilterModel().getEasy().get( DLFilterOperator.EQUAL ).put( cascadeUtil.getParentColumn( parent ), parent.getSelectedItem() );

        listboxController.refreshDataModel();

        listboxController.setSelectedIndex( 0 );

        onSelect( false );
    }

    public DLLovboxModel<T> getModel() {
        return model;
    }

    public DLLovbox<T> getLovBox() {
        return lovbox;
    }

    public void addListener( final String event, final EventListener listener ) {
        listeners.get( event ).add( listener );
    }

    public boolean removeListener( final String event, final EventListener listener ) {
        return listeners.get( event ).remove( listener );
    }

    protected void callListeners( final Event event ) {
        Events.postEvent( event );
        for ( EventListener listener : listeners.get( event.getName() ) ) {
            try {
                listener.onEvent( event );
            } catch ( Exception ex ) {
                Logger.getLogger( DLListboxGeneralController.class.getName() ).error( "Event couldn't be send. Error has occured in DLListboxGeneralController.", ex );
            }
        }
    }

    public static class EventListeners extends LinkedList<EventListener> {
    }
}