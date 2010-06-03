package ggc.plugin.gui;

import ggc.plugin.DevicePlugInServer;
import ggc.plugin.cfg.DeviceConfigEntry;
import ggc.plugin.data.DeviceDataHandler;
import ggc.plugin.device.DeviceInterface;
import ggc.plugin.device.DownloadSupportType;
import ggc.plugin.gui.file.ImportFileSelectorDialog;
import ggc.plugin.gui.file.MultipleFileSelectorDialog;
import ggc.plugin.protocol.ConnectionProtocols;
import ggc.plugin.util.DataAccessPlugInBase;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.atech.db.DbDataReadingFinishedInterface;
import com.atech.help.HelpCapable;
import com.atech.i18n.I18nControlAbstract;
import com.atech.utils.ATSwingUtils;
import com.atech.utils.TimeZoneUtil;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       GGC PlugIn Base (base class for all plugins)
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
 *  Filename:     DeviceInstructionsDialog
 *  Description:  Dialog showing us device, and instructions on how to connect and download data.
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public class DeviceInstructionsDialog extends JDialog implements ActionListener, DbDataReadingFinishedInterface, HelpCapable
{

    private static final long serialVersionUID = -1199131576461686895L;
    private DataAccessPlugInBase m_da = null; //= DataAccessMeter.getInstance();
    I18nControlAbstract m_ic = null; //m_da.getI18nControlInstance();
    DeviceInterface device_interface;
    DeviceConfigEntry configured_device;
    DeviceDataHandler m_ddh;
    JFrame m_parent = null;
    JButton button_start, help_button;
    JLabel label_waiting;
    boolean reading_old_done = false;
    //DeviceTransferData m_dtd = null;
    
    //Hashtable<String,?> device_data = null;
    //int x,y;
    //JFrame parentMy;
    //DbDataReaderAbstract reader;
    //DevicePlugInServer server;
    int continuing_type = DeviceDataHandler.TRANSFER_READ_DATA;

    /**
     * Continuing Type: Read Data
     */
    //public static final int CONTINUING_TYPE_READ_DATA = 1;
    
    /**
     * Continuing Type: Read Configuration
     */
    //public static final int CONTINUING_TYPE_READ_CONFIGURATION = 2;
    
    
    /**
     * Constructor (for standalone start)
     * 
     * @param parent 
     * @param da 
     * @param continued_type 
     */
   /* public DeviceInstructionsDialog(Container parent, DataAccessPlugInBase da, int continued_type)
    {
        super();
        this.m_da = da;
        this.m_ic = da.getI18nControlInstance();
        
        //this.m_dtd = DeviceTransferData.getInstance();
        
        this.m_ddh = m_da.getDeviceDataHandler();
        this.m_ddh.setTransferType(continued_type);
        //this.m_dtd.device_data_handler = this.m_ddh;
        this.checkReading(this.m_ddh.isOldDataReadingFinished());
        this.m_ddh.setReadingFinishedObject(this);
        
        if (!loadConfiguration())
        {
            notConfigured();
            return;
        }

        init();

        m_da.centerJDialog(this, parent);
        this.setResizable(false);
        this.setVisible(true);
        
    }
*/
    
    /**
     * Constructor

     * @param da 
     * @param parent 
     * @param server
     * @param _continued_type 
     */
    public DeviceInstructionsDialog(Container parent, DataAccessPlugInBase da, DevicePlugInServer server, int _continued_type)
    {
        super((JFrame)parent, "", true);

        this.m_da = da;
        this.m_ic = da.getI18nControlInstance();
        this.m_parent = (JFrame)parent;
        
        this.m_ddh = m_da.getDeviceDataHandler();
        this.m_ddh.setDevicePlugInServer(server);
        //this.m_ddh.setDbDataReader(reader);
        this.checkReading(this.m_ddh.isOldDataReadingFinished());
        this.m_ddh.setReadingFinishedObject(this);
        m_da.addComponent(this);
        
        this.continuing_type = _continued_type; 
        this.m_ddh.setTransferType(this.continuing_type);

        
        if (!loadConfiguration())
        {
            notConfigured();
            return;
        }

        //this.m_da.listComponents();
        int read_stats = this.device_interface.getDownloadSupportType();
        
        if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_DATA)
        {
            if (!DownloadSupportType.isOptionSet(read_stats, DownloadSupportType.DOWNLOAD_FROM_DEVICE))
            {
                boolean warned = showNoSupportDialog(read_stats);
                
                if (!warned)
                {
                    String msg = String.format(m_ic.getMessage("DEVICE_HAS_NO_DEVICE_DOWNLOAD_SUPPORT"), 
                        this.configured_device.device_device,
                        this.configured_device.device_company);
                    this.showWarningDialog(msg);
                }

                return;
            }
        }
        else if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_FILE)
        {
            if (!DownloadSupportType.isOptionSet(read_stats, DownloadSupportType.DOWNLOAD_FROM_DEVICE_FILE))
            {
                boolean warned = showNoSupportDialog(read_stats);
                
                if (!warned)
                {
                    String msg = String.format(m_ic.getMessage("DEVICE_HAS_NO_FILE_DOWNLOAD_SUPPORT"), 
                        this.configured_device.device_device,
                        this.configured_device.device_company);
                    this.showWarningDialog(msg);
                }

                return;
            }
            
        }
        else if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_CONFIGURATION)
        {
            if (!DownloadSupportType.isOptionSet(read_stats, DownloadSupportType.DOWNLOAD_CONFIG_FROM_DEVICE))
            {
                boolean warned = showNoSupportDialog(read_stats);
                
                if (!warned)
                {
                    String msg = String.format(m_ic.getMessage("DEVICE_HAS_NO_CONFIG_DOWNLOAD_SUPPORT"), 
                        this.configured_device.device_device,
                        this.configured_device.device_company);
                    this.showWarningDialog(msg);
                }

                return;
            }
            
        }
        else
        {
            System.out.println("System error: This option is not supported!");
        }
            
        //this.reader = reader;
        //this.server = server;
        init();

        m_da.centerJDialog(this, parent);
        this.setResizable(false);
        this.setVisible(true);
    }

    
    private boolean showNoSupportDialog(int status)
    {
        
        if (DownloadSupportType.isOptionSet(status, DownloadSupportType.DOWNLOAD_SUPPORT_NA_DEVICE))
        {
            String msg = String.format(m_ic.getMessage("DEVICE_HAS_NO_DOWNLOAD_SUPPORT"), 
                         this.configured_device.device_device,
                         this.configured_device.device_company);
            showWarningDialog(msg);
            return true;
        }
        else if (DownloadSupportType.isOptionSet(status, DownloadSupportType.DOWNLOAD_SUPPORT_NO))
        {
            String msg = String.format(m_ic.getMessage("DEVICE_DOWNLOAD_NOT_SUPPORTED_BY_GGC"), 
                this.configured_device.device_device,
                this.configured_device.device_company);
            showWarningDialog(msg);
            return true;
        }
        else if (DownloadSupportType.isOptionSet(status, DownloadSupportType.DOWNLOAD_SUPPORT_NA_GENERIC_DEVICE)) 
        {
            showWarningDialog(m_ic.getMessage("DEVICE_DOWNLOAD_NOT_SUPPORTED_GENERIC"));
            return true;
        }
        
        return false;
        
    }
    
    
    
    private void showWarningDialog(String msg)
    {
        JOptionPane.showMessageDialog(m_da.getCurrentComponentParent(), msg, m_ic.getMessage("WARNING"), JOptionPane.WARNING_MESSAGE);
    }
    
    
    
    
    
    private void notConfigured()
    {
        String dv = m_ic.getMessage("DEVICE_NAME_NORMAL");
        String msg = String.format(m_ic.getMessage("OOPS_DEVICE_NOT_CONFIGURED"), dv, dv, dv);
        JOptionPane.showMessageDialog(this, msg, m_ic.getMessage("ERROR"), JOptionPane.ERROR_MESSAGE);
    }
    
    private boolean loadConfiguration()
    {
        this.configured_device = this.m_da.getDeviceConfiguration().getSelectedDeviceInstance(); //mc.getDefaultMeter();
        
        if (this.configured_device==null)
            return false;
        
        //this.m_dtd.configured_device = configured_device;
        this.m_ddh.setConfiguredDevice(configured_device);
        
        DeviceInterface mi = m_da.getManager().getDevice(this.configured_device.device_company, this.configured_device.device_device);

        this.device_interface = mi;
        this.device_interface.setConnectionParameters(this.configured_device.communication_port_raw);
        
        
        this.m_ddh.setDeviceInterface(this.device_interface);
        
        /*
        if (mi.hasPreInit())
        {
            DeviceAbstract dva = (DeviceAbstract)mi;
            dva.setDataAccessInstance(m_da);
            dva.setOutputWriter(new ConsoleOutputWriter());
            dva.preInitDevice();
        }*/
        
        return (this.device_interface!=null);
    }
    
    


    private ImageIcon getDeviceIcon()
    {
        
        String root = m_da.getDeviceImagesRoot();
        
        
        if (this.configured_device == null)
        {
            return m_da.getImageIcon(root, "no_device.gif");
        }
        else
        {
            if (this.device_interface.getIconName()==null)
                return m_da.getImageIcon(root, "no_device.gif");
            else
                return m_da.getImageIcon(root, this.device_interface.getIconName());
        }
    }
    
    
    private static final int DEVICE_INTERFACE_PARAM_CONNECTION_TYPE = 1;
    private static final int DEVICE_INTERFACE_PARAM_STATUS = 2;
    
    
    private String getMeterInterfaceParameter(int param)
    {
        switch(param)
        {
            case DeviceInstructionsDialog.DEVICE_INTERFACE_PARAM_CONNECTION_TYPE:
            {
                if (this.device_interface==null)
                {
                    return m_ic.getMessage("UNKNOWN");
                }
                else
                {
                    return m_ic.getMessage(ConnectionProtocols.connectionProtocolDescription[this.device_interface.getConnectionProtocol()]);
                }
            } 

            case DeviceInstructionsDialog.DEVICE_INTERFACE_PARAM_STATUS:
            {
                if (this.device_interface==null)
                {
                    return m_ic.getMessage("ERROR_IN_CONFIG");
                }
                else
                {
                    return m_ic.getMessage("READY"); 
                }
            } 
            
            default:
                return m_ic.getMessage("UNKNOWN");
        }
    }
    


    protected void init()
    {
        ATSwingUtils.initLibrary();
        
        setTitle(String.format(m_ic.getMessage("CONFIGURED_DEVICE_INSTRUCTIONS"), m_ic.getMessage("DEVICE_NAME_BIG")));

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(600, 700);

        JLabel label;

        Font normal = m_da.getFont(DataAccessPlugInBase.FONT_NORMAL);
        
        setBounds(0, 0, 650, 600);
        m_da.centerJDialog(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(panel, BorderLayout.CENTER);

  
        
        // panel icon
        JPanel panel_icon = ATSwingUtils.getPanel(10, 10, 280, 380, 
            null, 
            new TitledBorder(String.format(m_ic.getMessage("DEVICE_ICON"), m_ic.getMessage("DEVICE_NAME_BIG"))), 
            panel);
            
        label = new JLabel(this.getDeviceIcon());
        label.setBounds(10, 20, 260, 350);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        panel_icon.add(label);
        
        
        // panel configured device
        JPanel panel_device = ATSwingUtils.getPanel(300, 10, 330, 170, 
            null, 
            new TitledBorder(m_ic.getMessage("CONFIGURED_DEVICE")), 
            panel);

        ATSwingUtils.getLabel(m_ic.getMessage("MY_DEVICE_NAME") + ":", 
            15, 20, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);
        
        ATSwingUtils.getLabel(this.configured_device.name, 
            130, 20, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);
        
        
        ATSwingUtils.getLabel(m_ic.getMessage("DEVICE_COMPANY") + ":", 
            15, 40, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);
        
        ATSwingUtils.getLabel(this.configured_device.device_company, 
            130, 40, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);


        ATSwingUtils.getLabel(m_ic.getMessage("DEVICE_NAME_") + ":", 
            15, 60, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);

        ATSwingUtils.getLabel(this.configured_device.device_device, 
            130, 60, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);

        
        ATSwingUtils.getLabel(m_ic.getMessage("CONNECTION_TYPE") + ":", 
            15, 80, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);
        
        ATSwingUtils.getLabel(this.getMeterInterfaceParameter(DEVICE_INTERFACE_PARAM_CONNECTION_TYPE), 
            130, 80, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);


        ATSwingUtils.getLabel(m_ic.getMessage("CONNECTION_PARAMETER") + ":", 
            15, 100, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);

        
        ATSwingUtils.getLabel(this.configured_device.communication_port, 
            130, 100, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);

        
        ATSwingUtils.getLabel(m_ic.getMessage("DAYLIGHTSAVINGS_FIX") + ":", 
            15, 120, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);

        
        label = ATSwingUtils.getLabel("", 
            130, 120, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);

        if (m_da.getDeviceConfigurationDefinition().doesDeviceSupportTimeFix())
        {
            if (this.configured_device.ds_fix)
            {
                label.setText(this.configured_device.getDayLightFix());
                label.setToolTipText(this.configured_device.getDayLightFixLong());
            }
            else
            {
                label.setText(this.configured_device.getDayLightFix());
            }
        }
        else
        {
            label.setText(m_ic.getMessage("DEVICE_DOESNT_SUPPORT_DS_FIX"));
            label.setToolTipText(m_ic.getMessage("DEVICE_DOESNT_SUPPORT_DS_FIX"));
        }
        

        ATSwingUtils.getLabel(m_ic.getMessage("STATUS") + ":", 
            15, 140, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL_BOLD);
        
        label = ATSwingUtils.getLabel(this.getMeterInterfaceParameter(DEVICE_INTERFACE_PARAM_STATUS), 
            130, 140, 320, 25, panel_device, ATSwingUtils.FONT_NORMAL);
        
        
        // meter instructions
        JPanel panel_instruct = ATSwingUtils.getPanel(300, 190, 330, 200, 
            new FlowLayout(), 
            new TitledBorder(m_ic.getMessage("INSTRUCTIONS")), 
            panel);
        
        label = ATSwingUtils.getLabel(m_ic.getMessage(this.device_interface.getInstructions()), 
            5, 0, 280, 180, panel_instruct, ATSwingUtils.FONT_NORMAL_SMALLER);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        
        
        // bottom 
        label = ATSwingUtils.getLabel(m_ic.getMessage("INSTRUCTIONS_DESC"), 
            20, 400, 590, 120, panel, ATSwingUtils.FONT_NORMAL);
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        

        
        help_button = m_da.createHelpButtonByBounds(30, 520, 130, 25, this);
        help_button.setFont(normal);
        panel.add(help_button);
       
        m_da.enableHelp(this);
        
        ATSwingUtils.getButton(m_ic.getMessage("CANCEL"), 170, 520, 130, 25, panel, 
            ATSwingUtils.FONT_NORMAL, null, "cancel", this, m_da);
        

        label_waiting = ATSwingUtils.getLabel(m_ic.getMessage("WAIT_UNTIL_OLD_DATA_IS_READ"), 
            310, 495, 300, 25, panel, ATSwingUtils.FONT_NORMAL);
        label_waiting.setVerticalAlignment(JLabel.TOP);
        label_waiting.setHorizontalAlignment(JLabel.RIGHT);

        button_start = ATSwingUtils.getButton(m_ic.getMessage("START_DOWNLOAD"), 370, 520, 240, 25, panel, 
            ATSwingUtils.FONT_NORMAL, null, "start_download", this, m_da);
        this.button_start.setEnabled(false);

        
        if (this.reading_old_done)
        {
            this.button_start.setEnabled(true);
            this.label_waiting.setText("");
        }
        
            
        //this.m_ddh.setReadingFinishedObject(this);
        
 /*       
        if (this.m_ddh.getDbDataReader()!=null)
        {
            if (this.m_ddh.getDbDataReader().isFinished())
            {
                //this.m_ddh.readingFinished();
                this.readingFinished();
            }
            /*else
            {
                this.m_ddh.setReadingFinishedObject(this);
            } */
 /*       }
        else
        {
            //this.m_ddh.readingFinished();
            this.readingFinished();
        }
   */     
    }



    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) 
    {
        String action = e.getActionCommand();

        if (action.equals("cancel")) 
        {
            m_da.removeComponent(this);
            this.dispose();
        }
        else if (action.equals("start_download"))
        {
            
            if ((this.m_da.getDeviceConfigurationDefinition().doesDeviceSupportTimeFix()) &&
                (this.configured_device.ds_fix))
            {
                TimeZoneUtil  tzu = TimeZoneUtil.getInstance();
                
                tzu.setTimeZone(this.configured_device.ds_area);
                tzu.setWinterTimeChange(this.configured_device.ds_winter_change);
                tzu.setSummerTimeChange(this.configured_device.ds_summer_change);
            }
            
            this.dispose();
         
            if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_DATA)
            {
                m_da.removeComponent(this);
                new DeviceDisplayDataDialog(m_parent, m_da, m_ddh);
            }
            else if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_CONFIGURATION)
            {
                m_da.removeComponent(this);
                new DeviceDisplayConfigDialog(m_parent, m_da, m_ddh);
            }
            else if (this.continuing_type==DeviceDataHandler.TRANSFER_READ_FILE)
            {
                
                //System.out.println(this.device_interface.getFileDownloadTypes());
                
                if (this.device_interface.getFileDownloadTypes()==null)
                {
                    showWarningDialog(this.m_ic.getMessage("INTERNAL_CONFIGURATION_ERROR"));
                    m_da.removeComponent(this);
                }
                else if (this.device_interface.getFileDownloadTypes().length == 1)
                {
                    m_ddh.selected_file_context = this.device_interface.getFileDownloadTypes()[0]; 
                    m_da.removeComponent(this);
                    new ImportFileSelectorDialog(m_da, this, m_ddh);
                }
                else
                {
                    m_da.removeComponent(this);
                    new MultipleFileSelectorDialog(m_da, this, m_ddh);
                }
                //new DeviceDisplayConfigDialog(m_da, m_ddh);
            }
            
        }
        else
            System.out.println("DeviceInstructionsDialog::Unknown command: " + action);

    }

    
    /** 
     * readingFinished
     */
    public void readingFinished()
    {
        checkReading(true);
        //System.out.println("DeviceInstructionDialog:readingFinished");
        
        if (this.button_start!=null)
        {
            this.button_start.setEnabled(true);
            this.label_waiting.setText("");
        }
    }

    
    private synchronized void checkReading(boolean status)
    {
        //System.out.println("CheckReading: " + status);
        reading_old_done = status;
    }
    
    
    // ****************************************************************
    // ******              HelpCapable Implementation             *****
    // ****************************************************************
    
    /** 
     * getComponent - get component to which to attach help context
     */
    public Component getComponent()
    {
        return this.getRootPane();
    }

    /** 
     * getHelpButton - get Help button
     */
    public JButton getHelpButton()
    {
        return this.help_button;
    }

    /** 
     * getHelpId - get id for Help
     */
    public String getHelpId()
    {
        return m_da.getDeviceConfigurationDefinition().getHelpPrefix() + "Read_Instruction";
    }
    
    
    
}
