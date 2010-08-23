package cz.datalite.zk.components.list.window.controller;

import cz.datalite.helpers.excel.export.Cell;
import cz.datalite.helpers.excel.export.DataCell;
import cz.datalite.helpers.excel.export.DataSource;
import cz.datalite.helpers.excel.export.ExcelExportUtils;
import cz.datalite.helpers.excel.export.HeadCell;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.view.DLListbox;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import org.zkoss.lang.Strings;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Window;

/**
 * Controller for the export manager
 * @author Karel Čemus <cemus@datalite.cz>
 */
@SuppressWarnings( "unchecked" )
public class ListboxExportManagerController extends GenericAutowireComposer {

    // model
    protected List<Map<String, Object>> usedModel = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> unusedModel = new ArrayList<Map<String, Object>>();
    protected Integer rows;
    protected String sheetName = "data";
    protected String fileName = "report";
    // view
    DLListbox usedListbox;
    DLListbox unusedListbox;
    // controller
    protected ListboxSelectorController selector;
    protected DLListboxExtController masterController;
    protected Composer windowCtl;

    @Override
    public void doAfterCompose( final Component comp ) throws Exception {
        super.doAfterCompose( comp );
        comp.setVariable( "ctl", this, true );

        // save masterController
        masterController = ( DLListboxExtController ) arg.get( "master" );
        // save componentu okna
        windowCtl = ( Composer ) arg.get( "windowCtl" );

        // save row count
        rows = ( Integer ) arg.get( "rows" );

        final List<Map<String, Object>> columnModels = ( List<Map<String, Object>> ) arg.get( "columnModels" );

        for ( Map<String, Object> map : columnModels ) {
            if ( ( Boolean ) map.get( "visible" ) ) {
                usedModel.add( prepareMap( map ) );
            } else {
                unusedModel.add( prepareMap( map ) );
            }
        }

        java.util.Collections.sort( usedModel, new java.util.Comparator<Map<String, Object>>() {

            public int compare( final Map<String, Object> o1, final Map<String, Object> o2 ) {
                return ( Integer ) o1.get( "order" ) - ( Integer ) o2.get( "order" );
            }
        } );

        selector = new ListboxSelectorController( usedModel, unusedModel, usedListbox, unusedListbox );

        (( Window ) self).setTitle( Labels.getLabel( "muj.test" ) );
    }

    protected DataSource prepareSource() {
        return new DataSource() {

            public List<Cell> getCells() {
                try {
                    return prepareCells();
                } catch ( WriteException ex ) {
                    Logger.getLogger( ListboxExportManagerController.class.getName() ).log( Level.SEVERE, null, ex );
                    return new ArrayList<Cell>();
                }
            }

            @Override
            public int getCellCount() {
                return usedModel.size();
            }
        };
    }

    protected List<Cell> prepareCells() throws WriteException {
        final List<HeadCell> heads = new ArrayList<HeadCell>();
        final WritableCellFormat headFormat = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD ) );
        headFormat.setBackground( Colour.LIGHT_GREEN );
        int column = 0;
        int row = 0;
        for ( Map<String, Object> unit : usedModel ) {
            heads.add( new HeadCell( unit.get( "label" ), column, row, headFormat ) );
            column++;
        }


        final List<Cell> cells = new LinkedList<Cell>( heads );
        for ( Object entity : masterController.loadData( Math.min( rows, 36000 ) ).getData() ) {
            row++;
            column = 0;

            for ( Map<String, Object> unit : usedModel ) {
                try {
                    final String columnName = ( String ) unit.get( "column" );
                    Object value = (Strings.isEmpty( columnName )) ? entity : Fields.getByCompound( entity, columnName );
                    if ( ( Boolean ) unit.get( "isConverter" ) ) {
                        value = convert( value, ( Method ) unit.get( "converter" ) );
                    }
                    cells.add( new DataCell( row, value, heads.get( column ) ) );
                } catch ( InvocationTargetException ex ) {
                    throw new RuntimeException( ex );
                } catch ( InstantiationException ex ) {
                    throw new RuntimeException( ex );
                } catch ( Exception ex ) { // ignore
                    org.apache.log4j.Logger.getLogger( ListboxExportManagerController.class ).warn(
                            "Error occured during exporting column " + ( String ) unit.get( "column" ) + ".", ex );
                }
                column++;
            }
        }

        return cells;
    }

    protected Object convert( final Object value, final Method converter ) throws InvocationTargetException, InstantiationException {
        if ( "coerceToUi".equals( converter.getName() ) ) {
            return convertWithTypeConverter( value, converter );
        } else {
            return convertWithClt( value, converter );
        }
    }

    protected Object convertWithClt( final Object value, final Method converter ) throws InvocationTargetException {
        try {
            return converter.invoke( windowCtl, new Object[]{ value } );
        } catch ( IllegalAccessException ex ) {
            return value;
        } catch ( IllegalArgumentException ex ) {
            return value;
        }
    }

    protected Object convertWithTypeConverter( final Object value, final Method converter ) throws InvocationTargetException, InstantiationException {
        try {
            return converter.invoke( converter.getDeclaringClass().newInstance(), new Object[]{ value, null } );
        } catch ( IllegalAccessException ex ) {
            return value;
        } catch ( IllegalArgumentException ex ) {
            return value;
        }
    }

    protected Map<String, Object> prepareMap( final Map<String, Object> map ) {
        return new java.util.HashMap<String, Object>( map );
    }

    public void onOk() throws FileNotFoundException, IOException {
        org.zkoss.zk.ui.event.Events.postEvent( new org.zkoss.zk.ui.event.Event(
                "onSave", self,
                ExcelExportUtils.exportSimple( fileName, sheetName, prepareSource() ) ) );
        self.detach();
    }

    public void onStorno() {
        self.detach();
    }

    public ListboxSelectorController getSelector() {
        return selector;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows( final Integer rows ) {
        this.rows = rows;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName( final String fileName ) {
        this.fileName = fileName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName( final String sheetName ) {
        this.sheetName = sheetName;
    }
}