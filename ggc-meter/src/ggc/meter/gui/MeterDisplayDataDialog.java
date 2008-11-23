package ggc.meter.gui;

import ggc.core.db.hibernate.DayValueH;
import ggc.meter.data.MeterValuesEntry;
import ggc.meter.data.MeterValuesTable;
import ggc.meter.data.MeterValuesTableModel;
import ggc.meter.plugin.MeterPlugInServer;
import ggc.meter.util.DataAccessMeter;
import ggc.plugin.cfg.DeviceConfigEntry;
import ggc.plugin.device.DeviceIdentification;
import ggc.plugin.output.AbstractOutputWriter;
import ggc.plugin.output.OutputUtil;
import ggc.plugin.output.OutputWriter;
import ggc.plugin.output.OutputWriterData;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

import com.atech.i18n.I18nControlAbstract;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       Meter Tool (support for Meter devices)
 *
 *  See AUTHORS for copyright information.
 * 
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 2 of the License, or (at your option) any later
 *  version.
 * 
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.
 * 
 *  You should have received a copy of the GNU General Public License along with
 *  this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 *  Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *  Filename:      MeterDisplayDataDialog
 *  Description:   This is dialog for displaying data as it's been downloaded. 
 * 
 *  Author: Andy {andy@atech-software.com}
 */


//Try to assess possibility of super-classing


public class MeterDisplayDataDialog extends JDialog implements ActionListener, OutputWriter
{

    private static final long serialVersionUID = 3365114423740706212L;

    MeterReaderRunner mrr;

    private DataAccessMeter m_da = DataAccessMeter.getInstance();
    I18nControlAbstract m_ic = m_da.getI18nControlInstance();

    // private static ReadMeterDialog singleton = null;

    private JTextArea logText = null;
    public JProgressBar progress = null;

    // private GlucoValues glucoValues = null;
    private MeterValuesTableModel model = null;
    private MeterValuesTable table = null;

    
    private Hashtable<String,DayValueH> meter_data = null;
    DeviceConfigEntry configured_meter;
    
    
    private JButton bt_close, bt_import, bt_break;

    private JTabbedPane tabPane;
    // private SerialMeterImport meterImport = null;
    // private StartImportAction startImportAction = new StartImportAction();

    // private MeterInterface meterDevice = null;
    // private MeterImportManager m_mim = null;

    JLabel lbl_status, lbl_comment;

    JTextArea ta_info = null;

    
    
    // TimerThread m_timer = null;

    int x, y;

    JFrame parentMy;

    //MeterInterface meter_interface;

    
    MeterPlugInServer server;
    
    public String statuses[] =  
    { 
        m_ic.getMessage("STATUS_NONE"),
        m_ic.getMessage("STATUS_READY"),
        m_ic.getMessage("STATUS_DOWNLOADING"),
        m_ic.getMessage("STATUS_STOPPED_DEVICE"),
        m_ic.getMessage("STATUS_STOPPED_USER"),
        m_ic.getMessage("STATUS_DOWNLOAD_FINISHED"),
        m_ic.getMessage("STATUS_READER_ERROR"),
    }; 
    
    
    
    
    
    
    /**
     * Constructor for ReadMeterDialog.
     * 
     * @param owner
     * 
     * @throws HeadlessException
     */
/**    public MeterDisplayDataDialog(JFrame owner, MeterInterface mi)
    {
        super(owner);
        m_da.addComponent(this);
        meter_interface = mi;
        this.parentMy = owner;

        dialogPreInit();
    }

    public MeterDisplayDataDialog(MeterInterface mi)
    {
        super();
        m_da.addComponent(this);
        meter_interface = mi;

        dialogPreInit();
    }
*/
    public MeterDisplayDataDialog()
    {
        super();

        //this.loadConfiguration();

        this.mrr = new MeterReaderRunner(this.configured_meter, this);

        dialogPreInit();
    }

    
    public MeterDisplayDataDialog(DeviceConfigEntry mce)
    {
        super();

        this.configured_meter = mce;

        this.mrr = new MeterReaderRunner(this.configured_meter, this);

        dialogPreInit();
    }
    

    public MeterDisplayDataDialog(DeviceConfigEntry mce, Hashtable<String,DayValueH> meter_data, MeterPlugInServer server)
    {
        super();

        this.configured_meter = mce;
        this.meter_data = meter_data;

        this.mrr = new MeterReaderRunner(this.configured_meter, this);

        this.server = server;
        dialogPreInit();
    }
    
    
    /**
    private void loadConfiguration()
    {
        // TODO: this should be read from config

        this.configured_meter = new MeterConfigEntry();
        this.configured_meter.id = 1;
        this.configured_meter.communication_port = "COM9";
        this.configured_meter.name = "My Countour";
        this.configured_meter.meter_company = "Ascensia/Bayer";
        this.configured_meter.meter_device = "Contour";
        this.configured_meter.ds_area = "Europe/Prague";
        this.configured_meter.ds_winter_change = 0;
        this.configured_meter.ds_summer_change = 1;
        this.configured_meter.ds_fix = true;

        /**
         * tzu.setTimeZone("Europe/Prague"); tzu.setWinterTimeChange(0);
         * tzu.setSummerTimeChange(+1);
         */

        // MeterInterface mi =
        // MeterManager.getInstance().getMeterDevice(this.configured_meter
        // .meter_company, this.configured_meter.meter_device);
        // this.meter_interface = mi;
    //}

    private void dialogPreInit()
    {
  //      loadConfiguration();
        setTitle(String.format(m_ic.getMessage("READ_METER_DATA_TITLE"), this.configured_meter.device_device, 
                this.configured_meter.communication_port));

        m_da.addComponent(this);
        
        init();

        this.mrr.start();

        this.setVisible(true);

    }

    /**
    private void guiTest()
    {
        MeterValuesEntry mve = new MeterValuesEntry();
        mve.setBgUnit(OutputUtil.BG_MMOL);
        mve.setBgValue("8.7");
        mve.setDateTime(new ATechDate(200806121233L));
        mve.status = 2;

        this.model.addEntry(mve);

        mve = new MeterValuesEntry();
        mve.setBgUnit(OutputUtil.BG_MMOL);
        mve.setBgValue("10.1");
        mve.setDateTime(new ATechDate(200806121456L));
        mve.status = 1;

        this.model.addEntry(mve);

        mve = new MeterValuesEntry();
        mve.setBgUnit(OutputUtil.BG_MMOL);
        mve.setBgValue("10.1");
        mve.setDateTime(new ATechDate(200806121456L));
        mve.status = 0;

        this.model.addEntry(mve);

        mve = new MeterValuesEntry();
        mve.setBgUnit(OutputUtil.BG_MMOL);
        mve.setBgValue("10.1");
        mve.setDateTime(new ATechDate(200806121456L));
        mve.status = 3;

        this.model.addEntry(mve);

        mve = new MeterValuesEntry();
        mve.setBgUnit(OutputUtil.BG_MMOL);
        mve.setBgValue("10.1");
        mve.setDateTime(new ATechDate(200806121456L));
        mve.status = 2;

        this.model.addEntry(mve);

        this.setStatus(5);
    }
*/
    
    
    /**
     * If we have special status progress defined, by device, we need to set progress, by ourselves, this is 
     * done with this method.
     * @param value
     */
    public void setSpecialProgress(int value)
    {
        this.progress.setValue(value);
    }
    
    

    private void addLogText(String s)
    {
        logText.append(s + "\n");
    }


    protected void init()
    {

        model = new MeterValuesTableModel();
        model.setOldValues(meter_data);
        
        

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(600, 600);

        JLabel label;
 
        Font normal = m_da.getFont(DataAccessMeter.FONT_NORMAL);
        Font normal_b = m_da.getFont(DataAccessMeter.FONT_NORMAL_BOLD);
        
        setBounds(0, 0, 480, 580);
        m_da.centerJDialog(this);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(panel, BorderLayout.CENTER);

        // TabControl with two tabs: log and data

        logText = new JTextArea(m_ic.getMessage("LOG__") + ":\n", 8, 35);
        logText.setAutoscrolls(true);
        JScrollPane sp = new JScrollPane(logText, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        addLogText( m_ic.getMessage("LOG_IS_CURRENTLY_NOT_IMPLEMENTED"));
        
        // x resTable = new GlucoTable();

        // x JScrollPane sp2 = new JScrollPane(resTable,
        // ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        // ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.table = new MeterValuesTable(model);
        // this.table.removeAll();


        // TableColumnModel tcm = this.table.getColumnModel();
        // tcm.removeColumn(this.table.setC)
        /**
         * for (int k = 0; k < this.model.getColumnCount(); k++) {
         * 
         * 
         * 
         * TableCellRenderer renderer; if (this.model.isBoolean(k)) renderer =
         * new CheckCellRenderer(); else { DefaultTableCellRenderer textRenderer
         * = new DefaultTableCellRenderer();
         * textRenderer.setHorizontalAlignment(JLabel.CENTER); //
         * ExpenseReportData.m_columns[k].m_alignment); renderer = textRenderer;
         * }
         * 
         * 
         * TableCellEditor editor;
         * 
         * if (this.model.isEditableColumn(k)) { editor = new
         * DefaultCellEditor(new JCheckBox()); } else editor = null;
         * 
         * TableColumn column = new TableColumn(k, this.model.getColumnWidth(k,
         * 0), renderer, editor); table.addColumn(column); }
         */

        // this.table = MeterValuesTable.createMeterValuesTable(model);
        tabPane = new JTabbedPane();
        // tabPane.add("Values", sp2);
        tabPane.add(m_ic.getMessage("DATA"), this.createTablePanel(this.table));
        tabPane.add(m_ic.getMessage("LOG"), sp);
        tabPane.setBounds(30, 15, 410, 250);
        panel.add(tabPane);

        // Info

        label = new JLabel(m_ic.getMessage("METER_INFO") + ":");
        label.setBounds(30, 310, 310, 25);
        label.setFont(normal_b);
        panel.add(label);

        ta_info = new JTextArea();
        JScrollPane sp3 = new JScrollPane(ta_info);
        sp3.setBounds(30, 340, 410, 60);
        panel.add(sp3);

        ta_info.setText(""); // this.meter_interface.getDeviceInfo().
                             // getInformation(""));

        
        
        
        lbl_comment = new JLabel("");
        lbl_comment.setBounds(30, 270, 410, 25);
        //lbl_comment.setBorder(new LineBorder(Color.red));
        lbl_comment.setFont(normal);
        panel.add(lbl_comment);
        
        
        
        // meter status
        label = new JLabel(m_ic.getMessage("ACTION") + ":");
        label.setBounds(30, 415, 100, 25);
        label.setFont(normal_b);
        panel.add(label);

        lbl_status = new JLabel(m_ic.getMessage("READY"));
        lbl_status.setBounds(110, 415, 330, 25);
        //lbl_status.setBorder(new LineBorder(Color.red));
        lbl_status.setFont(normal);
        panel.add(lbl_status);

        this.progress = new JProgressBar();
        this.progress.setBounds(30, 450, 410, 20);
        this.progress.setStringPainted(true);
        // this.progress.setIndeterminate(true);
        panel.add(this.progress);

        bt_break = new JButton(m_ic.getMessage("BREAK_COMMUNICATION"));
        bt_break.setBounds(150, 490, 170, 25);
        // bt_break.setEnabled(this.m_mim.isStatusOK());
        bt_break.setActionCommand("break_communication");
        bt_break.addActionListener(this);
        panel.add(bt_break);

        JButton help_button = m_da.createHelpButtonByBounds(30, 490, 110, 25, this);
        panel.add(help_button);

        bt_close = new JButton(m_ic.getMessage("CLOSE"));
        bt_close.setBounds(330, 490, 110, 25);
        bt_close.setEnabled(false);
        bt_close.setActionCommand("close");
        bt_close.addActionListener(this);
        panel.add(bt_close);

        bt_import = new JButton(m_ic.getMessage("EXPORT_DATA"));
        bt_import.setBounds(270, 300, 170, 25);  // 270
        bt_import.setActionCommand("export_data");
        bt_import.addActionListener(this);
        bt_import.setEnabled(false);

        // button.setEnabled(meterDevice.isStatusOK());

        panel.add(bt_import);

    }


    
    public static final int FILTER_ALL = 0;
    public static final int FILTER_NEW = 1;
    public static final int FILTER_CHANGED = 2;
    public static final int FILTER_EXISTING = 3;
    public static final int FILTER_UNKNOWN = 4;
    public static final int FILTER_NEW_CHANGED = 5;
    public static final int FILTER_ALL_BUT_EXISTING = 6;
    
    
    String[] filter_states = { m_ic.getMessage("FILTER_ALL"), 
                               m_ic.getMessage("FILTER_NEW"),
                               m_ic.getMessage("FILTER_CHANGED"), 
                               m_ic.getMessage("FILTER_EXISTING"),
                               m_ic.getMessage("FILTER_UNKNOWN"), 
                               m_ic.getMessage("FILTER_NEW_CHANGED"),
                               m_ic.getMessage("FILTER_ALL_BUT_EXISTING") };

    JComboBox filter_combo;
    JButton sel_all, unsel_all;
    
    
    public JPanel createTablePanel(MeterValuesTable table_in)
    {

        JScrollPane scroller = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // final AddRowAction addRowAction = new AddRowAction(table);
        // final DeleteRowAction deleteRowAction = new DeleteRowAction(table);

        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        toolBar.setFloatable(false);

        toolBar.add(new JLabel(m_ic.getMessage("FILTER") + ":   "));
        toolBar.add(filter_combo = new JComboBox(this.filter_states));
        toolBar.add(new JLabel("   "));
        toolBar.add(sel_all = this.createButton("select_all", m_ic.getMessage("SELECT_ALL"), "element_selection.png"));
        toolBar.add(new JLabel(" "));
        toolBar.add(unsel_all = this.createButton("deselect_all", m_ic.getMessage("DESELECT_ALL"), "element_selection_delete.png"));

        filter_combo.setSelectedIndex(MeterDisplayDataDialog.FILTER_NEW_CHANGED);
        filter_combo.setEnabled(false);
        
        sel_all.setEnabled(false);
        unsel_all.setEnabled(false);

        filter_combo.addItemListener(new ItemListener() 
        {

            /** 
             * itemStateChanged
             */
            public void itemStateChanged(ItemEvent ev)
            {
                model.setFilter(filter_combo.getSelectedIndex());
            }
            
        }); 
        
        // toolBar.add(addRowAction);
        // toolBar.add(deleteRowAction);
        // UIUtilities.addToolBarButton(toolBar, addRowAction);
        // UIUtilities.addToolBarButton(toolBar, deleteRowAction);
        // toolBar.add(addRowAction);
        // toolBar.add(deleteRowAction);

        int[] cw = { 110, 80, 70, 80, 30 };

        TableColumn column = null;
        for (int i = 0; i < 5; i++)
        {
            column = table_in.getColumnModel().getColumn(i);
            column.setPreferredWidth(cw[i]);
        }

        JPanel container = new JPanel(new BorderLayout());
        container.add(toolBar, "North");
        container.add(scroller, "Center");

        return container;

    }

    public JButton createButton(String command_text, String tooltip, String image_d)
    {
        JButton b = new JButton();
        b.setIcon(m_da.getImageIcon(image_d, 15, 15, this));
        b.addActionListener(this);
        b.setActionCommand(command_text);
        b.setToolTipText(tooltip);
        return b;
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals("break_communication"))
        {
            this.setStatus(AbstractOutputWriter.STATUS_STOPPED_USER);
            this.setReadingStop();
        }
        else if (action.equals("close"))
        {
            m_da.removeComponent(this);
            this.dispose();
        }
        else if (action.equals("select_all"))
        {
            this.model.selectAll();
        }
        else if (action.equals("deselect_all"))
        {
            this.model.deselectAll();
        }
        else if (action.equals("export_data"))
        {
            Hashtable<String,ArrayList<DayValueH>> ht = this.model.getCheckedEntries();
            
            MeterExportDialog med = new MeterExportDialog(this, ht, this.server);
            
            if (med.wasAction())
            {
                this.bt_import.setEnabled(false);
            }
            
        }
        
        
        
        else
            System.out.println("MeterDisplayDataDialog::Unknown command: " + action);

    }

    /**
     * endOutput
     */
    public void endOutput()
    {
        System.out.println("endOutput()");
        // TODO Auto-generated method stub

    }

    DeviceIdentification device_ident;

    /**
     * getDeviceIdentification
     */
    public DeviceIdentification getDeviceIdentification()
    {
        return device_ident;
    }
    
    String sub_status = null;
    
    
    public void setSubStatus(String sub_status)
    {
        this.sub_status = sub_status;
        refreshStatus();
    }
    
    
    public String getSubStatus()
    {
        return this.sub_status;
    }
    
    

    OutputUtil output_util = OutputUtil.getInstance(this);

    /**
     * getOutputUtil
     */
    public OutputUtil getOutputUtil()
    {
        return this.output_util;
    }

    /**
     * interruptCommunication
     */
    public void interruptCommunication()
    {
        System.out.println("interComm()");

    }

    /**
     * setBGOutputType
     */
    public void setBGOutputType(int bg_type)
    {
        // TODO Auto-generated method stub
        System.out.println("setBGOutput()");
        this.output_util.setBGMeasurmentType(bg_type);
    }

    /**
     * setDeviceIdentification
     */
    public void setDeviceIdentification(DeviceIdentification di)
    {
        this.device_ident = di;
    }

    int count = 0;


    /**
     * writeDeviceIdentification
     */
    public void writeDeviceIdentification()
    {
        this.ta_info.setText(this.device_ident.getShortInformation());
    }

    /**
     * writeHeader
     */
    public void writeHeader()
    {
    }

    /**
     * writeRawData
     * 
     * @param input 
     * @param is_bg_data 
     */
    public void writeRawData(String input, boolean is_bg_data)
    {
    }

    boolean device_should_be_stopped = false;

    /**
     * User can stop readings from his side (if supported)
     */
    public void setReadingStop()
    {
        this.device_should_be_stopped = true;
    }

    /**
     * This should be queried by device implementation, to see if it must stop
     * reading
     */
    public boolean isReadingStopped()
    {
        return this.device_should_be_stopped;
    }

    int reading_status = AbstractOutputWriter.STATUS_READY;

    
    
    
    /**
     * This is status of device and also of GUI that is reading device (if we
     * have one) This is to set that status to see where we are. Allowed
     * statuses are: 1-Ready, 2-Downloading, 3-Stopped by device, 4-Stoped by
     * user,5-Download finished,6-Reader error
     */
    public void setStatus(int status)
    {
        if ((this.reading_status == AbstractOutputWriter.STATUS_STOPPED_DEVICE) || 
            (this.reading_status == AbstractOutputWriter.STATUS_STOPPED_USER) || 
            (this.reading_status == AbstractOutputWriter.STATUS_READER_ERROR))
            return;

        this.reading_status = status;
        setGUIStatus(status);
    }

    public int getStatus()
    {
        return this.reading_status;
    }

    public void refreshStatus()
    {
        setGUIStatus(current_status);
    }
    
    
    private int current_status = 0;
    
    
    public void setGUIStatus(int status)
    {
        
        current_status = status;
        
        if ((this.sub_status==null) || (this.sub_status.length()==0))
        {
            this.lbl_status.setText(this.statuses[status]);
        }
        else
        {
            this.lbl_status.setText(this.statuses[status] + " - " + m_ic.getMessage(this.sub_status));
        }

        switch (status)
        {

        
            case AbstractOutputWriter.STATUS_DOWNLOADING: // downloading
                {
                    this.bt_break.setEnabled(true);
                    this.bt_close.setEnabled(false);
                    this.bt_import.setEnabled(false);
                } break;
                
            case AbstractOutputWriter.STATUS_DOWNLOAD_FINISHED: // finished
                {
                    this.bt_break.setEnabled(false);
                    this.bt_close.setEnabled(true);
                    this.bt_import.setEnabled(true);
                    filter_combo.setEnabled(true);
                    sel_all.setEnabled(true);
                    unsel_all.setEnabled(true);
                }
                break;

            case AbstractOutputWriter.STATUS_READER_ERROR: // error
            {
                this.bt_break.setEnabled(false);
                this.bt_close.setEnabled(true);
                this.bt_import.setEnabled(false);
                filter_combo.setEnabled(false);
                sel_all.setEnabled(false);
                unsel_all.setEnabled(false);
            }
            break;
                
                
            case AbstractOutputWriter.STATUS_STOPPED_DEVICE: // stopped - device 
            case AbstractOutputWriter.STATUS_STOPPED_USER: // stoped - user
                {
                    this.bt_break.setEnabled(false);
                    this.bt_close.setEnabled(true);
                    this.bt_import.setEnabled(false);
                }
                break;
    
            case AbstractOutputWriter.STATUS_READY:  // ready
            //case 0:  // none
            default:
                {
                    this.bt_break.setEnabled(false);
                    this.bt_close.setEnabled(false);
                    this.bt_import.setEnabled(false);
                } break;
        }

    }


    public void setDeviceComment(String text)
    {
        this.lbl_comment.setText(m_ic.getMessage(text));
    }
    
    
    
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setSize(800,600);
        
        DataAccessMeter.getInstance().addComponent(f);
        
        // MeterReadDialog mrd =
        new MeterDisplayDataDialog(); // new AscensiaContour("COM12", new
                                      // ConsoleOutputWriter()));
    }


    public void writeData(OutputWriterData data)
    {
        count++;
        this.model.addEntry((MeterValuesEntry)data);
    }

    

    public void writeLog(int entry_type, String message)
    {
        // TODO Auto-generated method stub
        
    }


    public void writeLog(int entry_type, String message, Exception ex)
    {
        // TODO Auto-generated method stub
        
    }


}
