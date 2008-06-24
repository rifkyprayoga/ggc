package ggc.meter.device.ascensia;


import ggc.meter.data.MeterValuesEntry;
import ggc.meter.device.AbstractSerialMeter;
import ggc.meter.device.DeviceIdentification;
import ggc.meter.device.MeterException;
import ggc.meter.output.AbstractOutputWriter;
import ggc.meter.output.OutputUtil;
import ggc.meter.output.OutputWriter;
import ggc.meter.util.I18nControl;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import com.atech.utils.ATechDate;
import com.atech.utils.TimeZoneUtil;


public abstract class AscensiaMeter extends AbstractSerialMeter
//extends /*SerialIOProtocol*/  SerialProtocol implements MeterInterface
{

    public static final int ASCENSIA_COMPANY          = 1;
    
    public static final int METER_ASCENSIA_ELITE_XL   = 10001;
    public static final int METER_ASCENSIA_DEX        = 10002;
    public static final int METER_ASCENSIA_BREEZE     = 10003;
    public static final int METER_ASCENSIA_CONTOUR    = 10004;
    public static final int METER_ASCENSIA_BREEZE2    = 10005;
    
    
    protected int m_status = 0;
    protected I18nControl ic = I18nControl.getInstance();

    protected String m_info = "";
    protected int m_time_difference = 0;
    protected ArrayList<MeterValuesEntry> data = null;
    //protected OutputWriter m_output_writer;
    public TimeZoneUtil tzu = TimeZoneUtil.getInstance();

    public boolean device_running = false;

    boolean multiline = false;
    String multiline_body;
    
    
    String end_string;
    String end_strings[] = null;
    String text_def[] = null;
    
    
    public AscensiaMeter()
    {
    }
    

    public AscensiaMeter(String portName, OutputWriter writer)
    {
    	
		super(/*portName, */ 
		      9600,
			  //19200,
		      SerialPort.DATABITS_8, 
		      SerialPort.STOPBITS_1, 
		      SerialPort.PARITY_NONE);
	
		this.setCommunicationSettings( 
			      9600,
			      SerialPort.DATABITS_8, 
			      SerialPort.STOPBITS_1, 
			      SerialPort.PARITY_NONE,
			      SerialPort.FLOWCONTROL_NONE);
				
		this.setSerialPort(portName);
		
		data = new ArrayList<MeterValuesEntry>();
		
		this.output_writer = writer; 
		this.output_writer.getOutputUtil().setMaxMemoryRecords(this.getMaxMemoryRecords());
		
        this.setMeterType("Ascensia/Bayer", this.getName());

	
		try
		{
		    this.setPort(portName);
	
		    if (!this.open())
		    {
		    	this.m_status = 1;
		    }
		}
		catch(Exception ex)
		{
		    System.out.println("AscensiaMeter -> Error adding listener: " + ex);
		    ex.printStackTrace();
		}
		
		
        end_string = (new Character((char)13)).toString();
        
        //this.writer = new GGCFileOutputWriter();
        this.output_writer.writeHeader();

    
        
        //this.serialPort.
        
        this.serialPort.notifyOnOutputEmpty(true);
        this.serialPort.notifyOnBreakInterrupt(true);
        
        
        this.end_strings = new String[2];
        end_strings[0] = (new Character((char)3)).toString(); // ETX - End of Text
        end_strings[1] = (new Character((char)4)).toString(); // EOT - End of Transmission
        //end_strings[2] = (new Character((char)23)).toString(); // ETB - End of Text
        
        this.text_def = new String[3];
        this.text_def[0] = (new Character((char)2)).toString(); // STX - Start of Text
        this.text_def[1] = (new Character((char)3)).toString(); // ETX - Start of Text
        this.text_def[2] = (new Character((char)13)).toString(); // EOL - Start of Text
        
        
        try
        {
            this.serialPort.addEventListener(this);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
		
		
    }

    /**
     * Used for opening connection with device.
     * @return boolean - if connection established
     */
    public boolean open() throws MeterException
    {
    	return super.open();
    }


    /**
     * Will be called, when the import is ended and freeing resources.
     */
    @Override
    public void close()
    {
        return;
    }






    /**
     * getInfo - returns Meter information
     */
    public String getInfo()
    {
        return m_info;
    }






    
    //************************************************
    //***       Device Implemented methods         ***
    //************************************************
    

    
    public void readDeviceDataFull() throws MeterException
    {
        waitTime(2000);
        
        try
        {

            this.device_running = true;
            
            write(6);  // ENQ
            
            String line;

            
            while (((line = this.readLine()) != null) && (!isDeviceStopped(line)))
            {
                sendToProcess(line);
                write(6);
            }
            
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
            
        }
        
        
    }
    
    
    
    
    private void sendToProcess(String text)
    {
        boolean stx = false;
        int stx_idx = 0;
        boolean etx = false;
        int etx_idx = 0;
        boolean eol = false;
        int eol_idx = 0;
        
        //System.out.println("Send: " + text);
        
        if ((stx_idx = text.indexOf(this.text_def[0])) > -1)
        {
        //    System.out.println("STX");
            stx = true;
        }
        
        if ((etx_idx = text.indexOf(this.text_def[1])) > -1)
        {
        //    System.out.println("ETX");
            etx = true;
        }
    
        if ((eol_idx = text.indexOf(this.text_def[2])) > -1)
        {
        //    System.out.println("EOL");
            eol = true;
        }

        
        if (stx)
        {
         
            if ((etx) || (eol))
            {
                String t;
                stx_idx++;
                
                if (etx)
                {
                    t = text.substring(stx_idx, etx_idx);
                    
                }
                else
                {
                    t = text.substring(stx_idx, eol_idx);
                }
                //System.out.println(t);
                this.processData(t);
            }
            else
            {
                // only start, multiline
                this.multiline = true;
                this.multiline_body = text;
            }
        }
        else //if ((stx) && (!etx) || (!eol))
        {
            if ((etx) || (eol))
            {
                if (this.multiline)
                {
                    this.multiline_body += text;
                    
                    String txt = this.multiline_body;
                    
                    stx_idx = txt.indexOf(this.text_def[0]);
                    etx_idx = text.indexOf(this.text_def[1]);
                    eol_idx = text.indexOf(this.text_def[2]);

                    int end = 0;
                    
                    if (etx_idx!=-1)
                        end = etx_idx;
                    else
                        end = eol_idx;
                    
                    
                    String t = txt.substring(stx_idx, end);
          //          System.out.println("Multi: " + t);
                    this.processData(t);
                    
                    // reset
                    this.multiline = false;
                    this.multiline_body = "";
                    
                }
            }
            else
            {
                if (this.multiline)
                {
                    this.multiline_body += text;
                }
            }
        }
        
        
    }
    

    private boolean isDeviceStopped(String vals)
    {
        if (!this.device_running)
            return true;
        
        if (this.output_writer.isReadingStopped())
            return true;
        
        
//        if (vals.contains(this.end_strings[0]))
//            System.out.println("ETX");
        
        if (vals.contains(this.end_strings[1]))
        {
//            System.out.println("EOT");
            this.output_writer.endOutput();
//            System.out.println("EOT");
            return true;
        }
        
        
        return false;
        
    }
    
    
    public void setDeviceStopped()
    {
        this.device_running = false;
        this.output_writer.endOutput();
    }
    
    
    
    @Override
    public void serialEvent(SerialPortEvent event)
    {


        // Determine type of event.
        switch (event.getEventType()) 
        {
    
            // If break event append BREAK RECEIVED message.
            case SerialPortEvent.BI:
                System.out.println("recievied break");
                this.output_writer.setStatus(AbstractOutputWriter.STATUS_STOPPED_DEVICE);
                setDeviceStopped();
                break;
            case SerialPortEvent.CD:
                System.out.println("recievied cd");
                break;
            case SerialPortEvent.CTS:
                System.out.println("recievied cts");
                break;
            case SerialPortEvent.DSR:
                System.out.println("recievied dsr");
                break;
            case SerialPortEvent.FE:
                System.out.println("recievied fe");
                break;
            case SerialPortEvent.OE:
                System.out.println("recievied oe");
                System.out.println("Output Empty");
                break;
            case SerialPortEvent.PE:
                System.out.println("recievied pe");
                break;
            case SerialPortEvent.RI:
                System.out.println("recievied ri");
                break;
        }
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    
    


    protected void processData(String input)
    {
		input = m_da.replaceExpression(input, "||", "|_|"); 
	
		if (input.contains("|^^^Glucose|"))
		{
		    readData(input);
		}
		else if (input.contains("|Bayer"))
		{
		    readDeviceIdAndSettings(input);
		}
    }


    protected void readDeviceIdAndSettings(String input)
    {
        input = input.substring(input.indexOf("Bayer"));
    	
		StringTokenizer strtok = new StringTokenizer(input, "|");
	
		String devId = strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
		strtok.nextToken();
	
		String date = strtok.nextToken();
	
		//System.out.println("Device:\n" + devId + "\nDate: " + date);
		//System.out.println("Data (" + strtok.countTokens() + "): " + input);
	
		readDeviceId(devId);
		readDateInformation(date);

		this.output_writer.writeDeviceIdentification();
	
    }
    
    
    


    protected void readDeviceId(String input)
    {
        
        DeviceIdentification di = this.output_writer.getDeviceIdentification();
        
        System.out.println("readDeviceId: " + input);
    	StringTokenizer strtok = new StringTokenizer(input, "^");
    
    	String inf = "";
    
    	String id = strtok.nextToken();
    	String versions = strtok.nextToken();
    	String serial = strtok.nextToken();
    
    	inf += ic.getMessage("PRODUCT_CODE") + ": ";
    	
    	String tmp;
    
    	if ((id.equals("Bayer6115")) || (id.equals("Bayer6116")))
    	{
    	    //inf += "BREEZE Meter Family (";
    	    tmp = "Breeze Family (";
    	}
    	else if (id.equals("Bayer7150"))
    	{
    	    tmp = "CONTOUR Meter Family (";
    	}
    	else if (id.equals("Bayer3950"))
    	{
    	    tmp = "DEX Meter Family (";
    	}
    	else if (id.equals("Bayer3883"))
    	{
    	    tmp = "ELITE XL Meter Family (";
    	}
    	else
    	{
    	    tmp = "Unknown Meter Family (";
    	}
    
    	tmp+= id;
    	tmp+= ")";
    	
    	di.device_identified = tmp;
    	
    	
    	inf += tmp;
    	inf += "\n";
    
    	StringTokenizer strtok2 = new StringTokenizer(versions, "\\");
    
    	di.device_software_version = strtok2.nextToken();
    	di.device_hardware_version = strtok2.nextToken();
    	di.device_serial_number = serial; 
    	
    	
    	inf += ic.getMessage("SOFTWARE_VERSION") + ": " + di.device_software_version;
    	inf += ic.getMessage("\nEEPROM_VERSION") + ": " + di.device_hardware_version;
    
    	inf += ic.getMessage("\nSERIAL_NUMBER") + ": " + serial;
    
    	this.m_info = inf;

    }

    protected void readDateInformation(String dt)
    {

    	GregorianCalendar gc_meter = new GregorianCalendar();
    	gc_meter.setTime(m_da.getDateTimeAsDateObject(Long.parseLong(dt)));
    
    	GregorianCalendar gc_curr = new GregorianCalendar();
    	gc_curr.setTimeInMillis(System.currentTimeMillis());
    
    	GregorianCalendar gc_comp = new GregorianCalendar();
    	gc_comp.set(Calendar.DAY_OF_MONTH, gc_curr.get(Calendar.DAY_OF_MONTH));
    	gc_comp.set(Calendar.MONTH, gc_curr.get(Calendar.MONTH));
    	gc_comp.set(Calendar.YEAR, gc_curr.get(Calendar.YEAR));
    	gc_comp.set(Calendar.HOUR_OF_DAY, gc_curr.get(Calendar.HOUR_OF_DAY));
    	gc_comp.set(Calendar.MINUTE, gc_curr.get(Calendar.MINUTE));
    
    	long diff = gc_comp.getTimeInMillis() - gc_meter.getTimeInMillis();
    	this.m_time_difference = (-1) * (int)diff;
    
    	//System.out.println("Computer Time: " + gc_comp + "\nMeter Time: " + gc_meter + " Diff: " + this.m_time_difference);

    }


    boolean header_set = false;
    
    protected void readData(String input)
    {
    	try
    	{
	
	    	StringTokenizer strtok = new StringTokenizer(input, "|");
	    
	    	boolean found = false;
	    	
	    	// we search for entry containing Glucose... (in case that data was not 
	    	// received entirely)
	    	while ((!found) && (strtok.hasMoreElements()))
	    	{
	    		String s = strtok.nextToken();
	    		if (s.equals("^^^Glucose"))
	    		{
	    			found = true;
	    		}
	    	}
	    	
	    	if (!found)
	    		return;
	    	
	    	MeterValuesEntry mve = new MeterValuesEntry();
	    	
	    	mve.setBgValue(strtok.nextToken());  // bg_value
	    	String unit = strtok.nextToken();  // unit mmol/L^x, mg/dL^x
	    
	    	mve.addParameter("REF_RANGES", strtok.nextToken());  // Reference ranges (Dex Only) 
	    	mve.addParameter("RES_ABNORMAL_FLAGS", strtok.nextToken());  // Result abnormal flags (7)
            mve.addParameter("USER_MARKS", strtok.nextToken());  // User Marks (8)
	    	mve.addParameter("RES_STATUS_MARKER", strtok.nextToken());  // Result status marker
	    	strtok.nextToken();  // N/A
	    	strtok.nextToken();  // OperatorId (N/A)
	    
	    	String time = strtok.nextToken();  // datetime
	    
	    	mve.setDateTime(tzu.GetCorrectedDateTime(new ATechDate(Long.parseLong(time))));
	    	
	    	if (unit.startsWith("mg/dL"))
	    	{
	    		mve.setBgUnit(OutputUtil.BG_MGDL);
	    		
	    		//this.m_output.writeBGData(atd, bg_value, OutputUtil.BG_MGDL);
	    	    //dv.setBG(DailyValuesRow.BG_MGDL, value);
	    	}
	    	else
	    	{
	    		mve.setBgUnit(OutputUtil.BG_MMOL);
	    		//this.m_output.writeBGData(atd, bg_value, OutputUtil.BG_MMOL);
	    	    //dv.setBG(DailyValuesRow.BG_MMOLL, value);
	    	}
	    	
	    	this.output_writer.writeBGData(mve);
	    	
    	}
    	catch(Exception ex)
    	{
    		System.out.println("Exception: " + ex);
    		System.out.println("Entry: " + input);
    		ex.printStackTrace();
    	}
    
    	//this.data.add(dv);

    }














    //************************************************
    //***                    Test                  ***
    //************************************************

    public void test()
    {
    }



    /**
     * This is method for reading partitial data from device. All reading from actual device should be done from 
     * here. Reading can be done directly here, or event can be used to read data.
     */
    public void readDeviceDataPartitial() throws MeterException
    {
    }


    /** 
     * This is method for reading configuration
     * 
     * @throws MeterExceptions
     */
    public void readConfiguration() throws MeterException
    {
    }
    

    /**
     * This is for reading device information. This should be used only if normal dump doesn't retrieve this
     * information (most dumps do). 
     * @throws MeterExceptions
     */
    public void readInfo() throws MeterException
    {
    }



}
