package ggc.meter.device;

import ggc.meter.util.DataAccessMeter;
import ggc.plugin.device.DeviceIdentification;
import ggc.plugin.device.DownloadSupportType;
import ggc.plugin.device.PlugInBaseException;
import ggc.plugin.manager.company.AbstractDeviceCompany;
import ggc.plugin.output.AbstractOutputWriter;
import ggc.plugin.output.OutputWriter;
import ggc.plugin.protocol.SerialProtocol;
import ggc.plugin.util.DataAccessPlugInBase;
import gnu.io.SerialPortEvent;

import com.atech.graphics.dialogs.selector.SelectableInterface;

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
 *  Filename:     AbstractSerialMeter
 *  Description:  This abstract class for all meters using serial interface
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public abstract class AbstractSerialMeter extends SerialProtocol implements MeterInterface, SelectableInterface
{

    //protected I18nControlAbstract ic = null; //DataAccessMeter.getInstance().getI18nControlInstance();
    //protected OutputWriter output_writer;
    //AbstractDeviceCompany device_company = null;
    protected int m_status = 0;
    protected boolean communication_established = false;
    
    
    /**
     * Constructor
     */
    public AbstractSerialMeter()
    {
        super(DataAccessMeter.getInstance());
    }

    
    /**
     * Constructor
     * @param cmp
     */
    public AbstractSerialMeter(AbstractDeviceCompany cmp)
    {
        super(DataAccessMeter.getInstance());
        this.setDeviceCompany(cmp);
        this.setMeterType(cmp.getName(), getName());
    }
    
    
    /**
     * Constructor
     * @param da
     */
    //public FreestyleMeter(String portName, OutputWriter writer, DataAccessPlugInBase da)
    public AbstractSerialMeter(String parameters, OutputWriter writer, DataAccessPlugInBase da)
    {
        super(parameters, writer, da);
    }


    /** 
     * Serial Event - for handling serial events, this method is called internally
     */
    public void serialEvent(SerialPortEvent event)
    {

    }


    /** 
     * Set Communication Settings
     */
    public void setCommunicationSettings(int baudrate, int databits, int stopbits, int parity, int flow_control, int event_type)
    {
        super.setCommunicationSettings(baudrate, databits, stopbits, parity, flow_control, event_type);
    }

    //String meter_group = null;
    //String meter_device = null;

    /**
     * Dispose this instance
     */
    public void dispose()
    {
        this.close();
    }

    /**
     * Set Meter type
     * 
     * @param group
     * @param device
     */
    public void setMeterType(String group, String device)
    {
//        this.device_name = device;
        
        DeviceIdentification di = new DeviceIdentification();
        di.company = group;
        di.device_selected = device;
        
        if (this.output_writer!=null)
            this.output_writer.setDeviceIdentification(di);
        //this.output_writer.
        //this.device_instance = MeterManager.getInstance().getMeterDevice(group, device);
        
        this.device_source_name = group + " " + device;
    }


    String serial_port = null;

    /**
     * Set Serial Port used
     * 
     * @param port
     * @throws PlugInBaseException
     */
    public void setSerialPort(String port) throws PlugInBaseException
    {
        this.serial_port = port;
        this.setPort(port);
    }

    /**
     * getConnectionPort - connection port data
     * 
     * @return connection port as string
     */
    public String getConnectionPort()
    {
        return this.serial_port;
    }

    /**
     * Get Serial port
     * 
     * @return
     */
    public String getSerialPort()
    {
        return this.serial_port;
    }

    /**
     * hasSpecialProgressStatus - in most cases we read data directly from device, in this case we have 
     *    normal progress status, but with some special devices we calculate progress through other means.
     * @return true is progress status is special
     */
    public boolean hasSpecialProgressStatus()
    {
        return false;
    }

    /**
     * Used for opening connection with device.
     * 
     * @return boolean - if connection established
     */
    public boolean open() throws PlugInBaseException
    {
        return (communication_established = super.open());
    }

    
    /**
     * Is Device Communicating
     * 
     * @return
     */
    public boolean isDeviceCommunicating()
    {
        return this.communication_established;
    }
    
    /**
     * Will be called, when the import is ended and freeing resources.
     */
    public void close()
    {
        if (this.serialPort == null)
            return;

        this.serialPort.removeEventListener();
        this.serialPort.close();
    }



    // ************************************************
    // *** Device Implemented methods ***
    // ************************************************



    
    protected void deviceDisconnected()
    {
        this.output_writer.setStatus(AbstractOutputWriter.STATUS_STOPPED_DEVICE);
        this.output_writer.endOutput();
    }
    

    

    
    /**
     * Get Download Support Type
     * 
     * @return
     */
    public int getDownloadSupportType()
    {
        return DownloadSupportType.DOWNLOAD_FROM_DEVICE;
    }
    
    
    /**
     * getInterfaceTypeForMeter - most meter devices, store just BG data, this use simple interface, but 
     *    there are some device which can store different kind of data (Ketones - Optium Xceed; Food, Insulin
     *    ... - OT Smart, etc), this devices require more extended data display. 
     * @return
     */
    public int getInterfaceTypeForMeter()
    {
        return MeterInterface.METER_INTERFACE_SIMPLE;
    }


    
}
