package ggc.meter.device.menarini;

import ggc.meter.data.MeterValuesEntry;
import ggc.meter.device.AbstractSerialMeter;
import ggc.meter.manager.MeterDevicesIds;
import ggc.meter.manager.company.Menarini;
import ggc.meter.util.DataAccessMeter;
import ggc.plugin.device.DeviceIdentification;
import ggc.plugin.device.PlugInBaseException;
import ggc.plugin.manager.DeviceImplementationStatus;
import ggc.plugin.manager.company.AbstractDeviceCompany;
import ggc.plugin.output.AbstractOutputWriter;
import ggc.plugin.output.OutputUtil;
import ggc.plugin.output.OutputWriter;
import ggc.plugin.protocol.SerialProtocol;
import ggc.plugin.util.DataAccessPlugInBase;
import gnu.io.SerialPort;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atech.utils.data.ATechDate;
import com.atech.utils.data.TimeZoneUtil;

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
 *  Filename:      GlucofixMio  
 *  Description:   Meter implementation - Glucofix Mio
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public class GlucofixMio extends AbstractSerialMeter
{

    //protected I18nControl ic = I18nControl.getInstance();

    protected TimeZoneUtil tzu = TimeZoneUtil.getInstance();
    private static Log log = LogFactory.getLog(GlucofixMio.class);
    private int entries_max = 0;
    private int entries_current = 0;
    boolean device_disconnected = false;
    
    
    /**
     * Constructor
     * 
     * @param cmp
     */
    public GlucofixMio(AbstractDeviceCompany cmp)
    {
        super(cmp);
    }
    
    

    /**
     * Constructor
     * 
     * @param comm_parameters 
     * @param writer
     * @param da
     */
    public GlucofixMio(String comm_parameters, OutputWriter writer)
    {
        this(comm_parameters, writer, DataAccessMeter.getInstance()); 
    }
    
    
    
    /**
     * Constructor
     * 
     * @param comm_parameters
     * @param writer
     * @param da 
     */
    public GlucofixMio(String comm_parameters, OutputWriter writer, DataAccessPlugInBase da)
    {
        super(comm_parameters, writer, da);

        // communcation settings for this meter(s)
        this.setCommunicationSettings( 
                  9600,
                  SerialPort.DATABITS_8, 
                  SerialPort.STOPBITS_1, 
                  SerialPort.PARITY_ODD,
                  SerialPort.FLOWCONTROL_NONE,
                  SerialProtocol.SERIAL_EVENT_BREAK_INTERRUPT|SerialProtocol.SERIAL_EVENT_OUTPUT_EMPTY);
                
        // output writer, this is how data is returned (for testing new devices, we can use Consol
        this.output_writer = writer; 
        this.output_writer.getOutputUtil().setMaxMemoryRecords(this.getMaxMemoryRecords());

        // set meter type (this will be deprecated in future, but it's needed for now
        this.setMeterType("Menarini", this.getName());

        // set device company (needed for now, will also be deprecated)
        this.setDeviceCompany(new Menarini());
        

        // settting serial port in com library
        try
        {
            this.setSerialPort(comm_parameters);
    
            if (!this.open())
            {
                //this.m_status = 1;
                return;
            }
            
            this.output_writer.writeHeader();

            
            this.serialPort.setDTR(true);
            this.serialPort.setRTS(true);
            
            //this.serialPort.notifyOnOutputEmpty(true);  // notify on empty for stopping
            //this.serialPort.notifyOnBreakInterrupt(true); // notify on break interrupt for stopping
            
            //end_strings[2] = (new Character((char)23)).toString(); // ETB - End of Text
            
            
        }
        catch(Exception ex)
        {
            log.error("Exception on create:" + ex, ex);
            //System.out.println("AscensiaMeter -> Exception on create: " + ex);
            //ex.printStackTrace();
        }
        
    }
    
    
    
    

    /**
     * getDeviceClassName - Get class name of device
     */
    public String getDeviceClassName()
    {
        return "ggc.meter.device.menarini.GlucofixMio";
    }

    /**
     * getDeviceId - Get Device Id, within MgrCompany class 
     * Should be implemented by device class.
     * 
     * @return id of device within company
     */
    public int getDeviceId()
    {
        return MeterDevicesIds.METER_MENARINI_GLUCOFIX_MIO;
    }

    /**
     * getIconName - Get Icon of meter
     * 
     * @return icon name
     */
    public String getIconName()
    {
        return "mn_glucofix_mio.jpg";
    }

    /**
     * getImplementationStatus - Get implementation status 
     * 
     * @return implementation status as number
     * @see ggc.plugin.manager.DeviceImplementationStatus
     */
    public int getImplementationStatus()
    {
        return DeviceImplementationStatus.IMPLEMENTATION_PLANNED;
    }

    /**
     * getInstructions - get instructions for device
     * 
     * @return instructions for reading data 
     */
    public String getInstructions()
    {
        return "INSTRUCTIONS_ABBOTT_OPTIUMXCEED";
    }

    /**
     * getName - Get Name of meter. 
     * 
     * @return name of meter
     */
    public String getName()
    {
        return "Glucofix Mio";
    }

    
    /**
     * This is for reading device information. This should be used only if normal dump doesn't retrieve this
     * information (most dumps do). 
     * @throws PlugInBaseException
     */
    public void readInfo() throws PlugInBaseException
    {
        waitTime(500);
        ArrayList<String> lst = new ArrayList<String>();
        
        try
        {

            write(0xA2);  
            
            String line;

            boolean start = false;
            boolean doWeBreak = false;
            //int counter = 0; // if we get 10 empty entries, device is probably not connected
            
            while (((line = this.readLine()) != null)) // && (!isDeviceStopped(line)))
            {
                if (line.contains("["))
                {
                    counter = 0;
                    start = true;
                }
                else if (line.contains("]"))
                {
                    doWeBreak = true;
                }
                else if (start)
                {
                    if (line.trim().length()!=0)
                        lst.add(line);
                    else
                    {
                        if (checkBreak())
                            return;
                    }
                }
                else if ((line.trim().length()==0) && (!start))
                {
                    if (checkBreak())
                        return;
                }
                    
                if (doWeBreak)
                    break;
             
            }

        }
        catch(Exception ex)
        {
            System.out.println("GlucofixMio. Exception: " + ex);
            //ex.printStackTrace();
        }

        
        if (lst.size()==2)
        {
            
            DeviceIdentification di = this.output_writer.getDeviceIdentification();
            
            StringTokenizer strtok = new StringTokenizer(lst.get(0), ",");
            
            di.device_hardware_version = strtok.nextToken();
            strtok.nextToken();
            strtok.nextToken();
            di.device_serial_number = strtok.nextToken(); 
            di.device_software_version = m_da.replaceExpression(strtok.nextToken(), "\n", "");
            
            this.output_writer.setSpecialProgress(4);
            this.entries_max = this.getMaxMemoryRecords(); 
            
            
            this.output_writer.setDeviceIdentification(di);
            this.output_writer.writeDeviceIdentification();
      
            this.output_writer.setSpecialProgress(5);
            
        }
        
    }
    
    int counter = 0;
    
    /**
     * readDeviceDataFull - This is method for reading data from device. All reading from actual device should 
     * be done from here. Reading can be done directly here, or event can be used to read data. Usage of events 
     * is discouraged because reading takes 3-4x more time.
     */
    public void readDeviceDataFull() throws PlugInBaseException
    {
        
        this.readInfo();

        
        if (this.device_disconnected)
            return;
        
        
        waitTime(500);
        //ArrayList<String> lst = new ArrayList<String>();
        
        try
        {
            write(0x80);  // ENQ
            
            String line;

            boolean start = false;
            boolean doWeBreak = false;
            //int counter = 0;
            
            while (((line = this.readLine()) != null)) // && (!isDeviceStopped(line)))
            {
                if (line.contains("["))
                {
                    counter = 0;
                    start = true;
                }
                else if (line.contains("]"))
                {
                    doWeBreak = true;
                }
                else if (start)
                {
                    if (line.trim().length()!=0)
                    {
                        if (line.startsWith("Glu"))
                        {
                            readBGEntry(line);
                        }
                    }
                    else
                    {
                        if (checkBreak())
                            return;
                    }
                    
                    this.entries_current++;
                    readingEntryStatus();
                    
                }
                else if ((line.trim().length()==0) && (!start))
                {
                    if (checkBreak())
                        return;
                }
                    
                if (doWeBreak)
                    break;
                
            }

            this.output_writer.setSpecialProgress(100);
            this.output_writer.setSubStatus(null);
            this.output_writer.endOutput();
            
        }
        catch(Exception ex)
        {
            System.out.println("GlucofixMio. Exception: " + ex);
            ex.printStackTrace();
        }
        
    }


    private boolean checkBreak()
    {
        counter++;
        
        if (counter>10)
        {
            this.device_disconnected = true;
            endReading();
            return true;
        }
        else
            return false;
        
    }
    
    
    private void readBGEntry(String entry)
    {
        // Glu,15.4,mmol/L,00,080531,0950
        String[] vals = entry.split(",");
        
        MeterValuesEntry mve = new MeterValuesEntry();
        
        if (vals[2].equals("mmol/L"))
            mve.setBgUnit(OutputUtil.BG_MMOL);
        else
            mve.setBgUnit(OutputUtil.BG_MGDL);
        
        if (vals[1].contains("HI"))
        {
            mve.setBgUnit(OutputUtil.BG_MMOL);
            vals[1] = "33.3";
        }
        else if (vals[1].contains("LO"))
        {
            mve.setBgUnit(OutputUtil.BG_MMOL);
            vals[1] = "1.1";
        }
            
        mve.setBgValue(vals[1]);
        
        
        if (vals[3].equals("01"))
            mve.addParameter("USER_MARKS", "Yes");  // User Marks (8)
        
        
        vals[5] = m_da.replaceExpression(vals[5], "\n", ""); //vals[5].re.replace(Char(13), '');
        vals[5] = vals[5].trim();
        
        mve.setDateTimeObject(getDateTime(vals[4], vals[5]));
        
        this.output_writer.writeData(mve);
        
    }
    
    
    /** 
     * This is method for reading configuration
     * 
     * @throws PlugInBaseException
     */
    public void readConfiguration() throws PlugInBaseException
    {
    }


    /**
     * This is method for reading partitial data from device. All reading from actual device should be done from 
     * here. Reading can be done directly here, or event can be used to read data.
     */
    public void readDeviceDataPartitial() throws PlugInBaseException
    {
    }



    /**
     * getComment - Get Comment for device 
     * 
     * @return comment or null
     */
    public String getComment()
    {
        return null;
    }
    
    /**
     * Maximum of records that device can store
     */
    public int getMaxMemoryRecords()
    {
        return 400;
    }
    
    
    private ATechDate getDateTime(String date, String time)
    {
        // 080531,0950
        
        long dt = 20000000L;
        dt += m_da.getLongValueFromString(date);
        
        dt *= 10000L;
        dt += m_da.getLongValueFromString(time);
        
        return tzu.getCorrectedDateTime(new ATechDate(ATechDate.FORMAT_DATE_AND_TIME_MIN, dt));
        
    }
    
    
    private void readingEntryStatus()
    {
        float proc_read = ((this.entries_current*1.0f)  / this.entries_max);
        float proc_total = 5 + (95 * proc_read);
        this.output_writer.setSpecialProgress((int)proc_total); //.setSubStatus(sub_status)
    }
    
    
    private void endReading()
    {
        this.output_writer.setSubStatus(null);
        this.output_writer.endOutput();
        this.output_writer.setStatus(AbstractOutputWriter.STATUS_STOPPED_DEVICE);
        System.out.println("Reading finished prematurely !");
    }
    
    
    
}
