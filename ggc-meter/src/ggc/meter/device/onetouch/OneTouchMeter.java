package ggc.meter.device.onetouch;

import ggc.meter.data.MeterValuesEntry;
import ggc.meter.device.AbstractSerialMeter;
import ggc.meter.manager.company.LifeScan;
import ggc.meter.util.DataAccessMeter;
import ggc.plugin.device.DeviceIdentification;
import ggc.plugin.device.PlugInBaseException;
import ggc.plugin.manager.company.AbstractDeviceCompany;
import ggc.plugin.output.AbstractOutputWriter;
import ggc.plugin.output.OutputWriter;
import ggc.plugin.protocol.SerialProtocol;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

import java.io.IOException;
import java.util.StringTokenizer;

import com.atech.utils.ATechDate;
import com.atech.utils.TimeZoneUtil;


public abstract class OneTouchMeter extends AbstractSerialMeter
{
    
    
    public static final int LIFESCAN_COMPANY          = 2;
    
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRA      = 20001;
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRA_2    = 20002;
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRASMART = 20003;
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRALINK  = 20004;
    public static final int METER_LIFESCAN_ONE_TOUCH_SELECT     = 20005;
    public static final int METER_LIFESCAN_INDUO                = 20006;
    public static final int METER_LIFESCAN_ONE_TOUCH_BASIC      = 20007;
    public static final int METER_LIFESCAN_ONE_TOUCH_SURESTEP   = 20008;
    public static final int METER_LIFESCAN_ONE_TOUCH_FASTTAKE   = 20009;
    public static final int METER_LIFESCAN_ONE_TOUCH_PROFILE    = 20010;
    
    // these have different protocol and are extended from OneTouchMeter2
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRA_MINI = 20050;
    public static final int METER_LIFESCAN_ONE_TOUCH_ULTRA_EASY = 20050;
    
    
    
    protected boolean device_running = true;
    //protected ArrayList<MeterValuesEntry> data = null;
//    protected OutputWriter m_output_writer;
    public TimeZoneUtil tzu = TimeZoneUtil.getInstance();
    //public int meter_type = 20000;
    private int entries_max = 0;
    private int entries_current = 0;
    private int reading_status = 0;
    
    private int info_tokens;
    private String date_order;
    
    
    
    /**
     * Constructor
     */
    public OneTouchMeter()
    {
    }
    
    /**
     * Constructor for device manager
     * 
     * @param cmp
     */
    public OneTouchMeter(AbstractDeviceCompany cmp)
    {
        super(cmp);
    }
    
    
    
    public OneTouchMeter(String portName, OutputWriter writer)
    {
        super(DataAccessMeter.getInstance());
        
        this.setCommunicationSettings( 
                  9600,
                  SerialPort.DATABITS_8, 
                  SerialPort.STOPBITS_1, 
                  SerialPort.PARITY_NONE,
                  SerialPort.FLOWCONTROL_NONE, 
                  SerialProtocol.SERIAL_EVENT_BREAK_INTERRUPT|SerialProtocol.SERIAL_EVENT_OUTPUT_EMPTY);
                
        // output writer, this is how data is returned (for testing new devices, we can use Consol
        this.output_writer = writer; 
        this.output_writer.getOutputUtil().setMaxMemoryRecords(this.getMaxMemoryRecords());
        
        // set meter type (this will be deprecated in future, but it's needed for now
        this.setMeterType("OneTouch", this.getName());

        // set device company (needed for now, will also be deprecated)
        this.setDeviceCompany(new LifeScan());
        

        // settting serial port in com library
        try
        {
            this.setSerialPort(portName);
    
            if (!this.open())
            {
                this.m_status = 1;
            }

            this.output_writer.writeHeader();
            
        }
        catch(Exception ex)
        {
            System.out.println("OneTouchMeter -> Error adding listener: " + ex);
            ex.printStackTrace();
        }
        
        if (this.getDeviceId()==OneTouchMeter.METER_LIFESCAN_ONE_TOUCH_ULTRA)
        {
            this.info_tokens = 3;
            this.date_order = "MDY";
        }
        else
        {
            this.info_tokens = 8;
        }
        
    }


    
    
    /* 
     * getComment
     */
    public String getComment()
    {
        // TODO Auto-generated method stub
        return null;
    }



    /* 
     * getImplementationStatus
     */
    public int getImplementationStatus()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* 
     * getInstructions
     */
    public String getInstructions()
    {
        // TODO Auto-generated method stub
        return null;
    }

   


    public void readDeviceDataFull()
    {
        
        System.out.println("loadInitionalData");

        //waitTime(5000);
        
        //this.output_writer.
        
        
        try
        {
            
            write("D".getBytes());
            waitTime(100);
            write("M".getBytes());
            waitTime(100);
            write("?".getBytes());
            waitTime(500);
            
            String line;

            System.out.println("Serial Number: " + this.readLine());
            System.out.println("Serial Number: " + this.readLine());
            
            
            while((line=this.readLine())==null)
            {
                
            }
            
            System.out.println("Serial Number: " + line);
            //System.out.println("Serial Number: " + this.readLine());
            //System.out.println("Serial Number: " + this.readLine());
            
            
            write("D".getBytes());
            waitTime(100);
            write("M".getBytes());
            waitTime(100);
            write("P".getBytes());
            waitTime(100);
            
            //write("DM?".getBytes());
            
            //write("DMP".getBytes());
            
/*
            this.device_running = true;
            
            */
            /*
            write(5);  // ENQ
            waitTime(1000); 
            write(6);  // ACK
  */
            //String line;

            System.out.println("loadInitialData()::");
         
            //powerOn();
            /* Z
            int ch;
            boolean running = true;
            while(running)
            {
                ch = this.read();
                System.out.print((char)ch + " int: " + ch);
            }
            /*
            while ((ch = this.read()) != -1) && (!isDeviceStopped(line)))
            {
                System.out.print((char)ch);
            }*/
//            String line="";
            
            System.out.println("Out");
            //String line;    
            
            //while (((line = this.readLine()) != null) && (!isDeviceStopped(line)))
            while (((line = this.readLine()) != null) && (!isDeviceStopped(line)))
            {
                processEntry(line);
                //System.out.println("D: " + line);
                //processData(line.trim());
                //sendToProcess(line);
                //write(6);
                
                System.out.println(this.entries_current + "/" + this.entries_max  );
                
                if (line==null)
                    break;
                
            }
        
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
            
        }
        
        if (this.isDeviceFinished())
        {
        	this.output_writer.endOutput();
        }
        
        
        
        System.out.println("Reading finsihed");

        
        
    }

    private boolean isDeviceFinished()
    {
    	return (this.entries_current==this.entries_max);
    }
    
    
 
    /**
     * This is method for reading partitial data from device. All reading from actual device should be done from 
     * here. Reading can be done directly here, or event can be used to read data.
     */
    public void readDeviceDataPartitial() throws PlugInBaseException
    {
        
    }


    /** 
     * This is method for reading configuration
     * 
     * @throws MeterExceptions
     */
    public void readConfiguration() throws PlugInBaseException
    {
    }
    

    /**
     * This is for reading device information. This should be used only if normal dump doesn't retrieve this
     * information (most dumps do). 
     * @throws MeterExceptions
     */
    public void readInfo() throws PlugInBaseException
    {
    }
    
    
    
    
    private boolean isDeviceStopped(String vals)
    {
    	if ((vals == null) ||
    	    ((this.reading_status==1) && (vals.length()==0)) ||
            (!this.device_running) ||
            (this.output_writer.isReadingStopped()))
    		return true;
    	
        return false;
    }
    
    
    
    
    private void processEntry(String entry)
    {
        if ((entry==null) || (entry.length()==0))
            return;
        
        
        StringTokenizer strtok = new StringTokenizer(entry, ",");
        
        System.out.println("tokens: " + strtok.countTokens());
        
        if (strtok.countTokens()==this.info_tokens) // we can have different info tokens for different meters
        {
        	if (this.reading_status==0) // info can be read only before reading of data, if we receive entry 
        		this.readInfo(strtok);  // with same nr of parameters, this is error
        	else
        		setDeviceStopped();
        	
        }
        else if (strtok.countTokens()==5)
        {
            this.readBGEntry(strtok, entry);
        }
        else
        {
//        	System.out.println("wrong oaraomn: ");
        	setDeviceStopped();
        }
        
        
        
    }
    
    
    
    
    
    public void setDeviceStopped()
    {
        this.device_running = false;
        this.output_writer.endOutput();
    }
    
    
    public void readInfo(StringTokenizer strtok)
    {
/*        P nnn,� MeterSN �,�MG/DL �
        (1) (2) (3)
        (1) Number of datalog records to follow (0 � 150)
        (2) Meter serial number (9 characters)
        (3) Unit of measure for glucose values */
        
        //strtok 
        
        String num_x = strtok.nextToken(); // 1 token: number of entries
        num_x = num_x.substring(2).trim();
        
        this.entries_max = Integer.parseInt(num_x);
        
        
        String dev = strtok.nextToken(); // 2. token: device id
        
        DeviceIdentification di = new DeviceIdentification(ic);
        di.device_serial_number = dev;
        di.company = "OneTouch";
        di.device_selected = "Ultra";
        
        this.output_writer.setDeviceIdentification(di);
        this.output_writer.writeDeviceIdentification();

        
        if (this.getDeviceId()!=OneTouchMeter.METER_LIFESCAN_ONE_TOUCH_ULTRA)
        {
            //'P nnn,"MeterSN","ENGL. ","M.D.Y. ","AM/PM","MG/DL","!min","!max" cksm' 
            //"M.D.Y. " or "D.M.Y. "
            strtok.nextToken(); // 3
            
            String dx = this.getParameterValue(strtok.nextToken()); // 4. token: date
            
            if (dx.equals("M.D.Y."))
                this.date_order = "MDY";
            else
                this.date_order = "DMY";
        }
        
        
        reading_status = 1;
    }

    
    
    
    protected String getParameterValue(String val)
    {
        String d = val.substring(1, val.length()-1);
        return d.trim();
    }
    

    public void readBGEntry(StringTokenizer strtok, String entry)
    {
        try
        {
            System.out.println("BG: " + entry);
            /*
            P �dow�,�mm/dd/yy�,�hh:mm:30 �,�xxxxx �, n cksm<CR><LF>
            (4) (5) (6) (7)
            (4) Day of week (SUN, MON, TUE, WED, THU, FRI, SAT)
            (5) Date of reading
            (6) Time of reading (If two or more readings were taken within the same minute, they will be
            separated by 8 second intervals)
            (7) Result format:
            � nnn � - blood test result (mg/dL)
            � HIGH � - blood test result >600 mg/dL
            �C nnn � - control solution test result (mg/dL)
            �CHIGH � - control solution test result >600 mg/dL
            
            
            (12) Result format: (Profile) 
                 "  nnn " - blood test result (mg/dL)
                 "MMnn.n" - blood test result (mmol/dL)
                 " HIGH " - blood test result > 600 mg/dL
                 "! nnn" - check strip test result (mg/dL)
                 "! nn.n " - check strip test result (mmol/L)
                 "!HIGH " - check strip test result > 600 mg/dL
                 "C nnn " - control solution test result (mg/dL)
                 "C nn.n" - control solution test result (mmol/L)
                 "CHIGH " - control solution test result > 600 mg/dL
                 "I 000 " to "I 150 " - CARB records
                 "I 00.0 " to "I 20.0 " - BOLUS insulin records
                 "I  00 " to "I  99 " - all other insulin records             
            
      glucose value formatting (there are language variations): (OT2)
        If the datalog record had a checksum error, the last character
          is forced to be a question mark (e.g., '...," nnn?",...') All
          data in a record so flagged must be considered "suspect".
        If glucose > 600 mg/dL, '...," HIGH ",...' is transmitted.
        If UNITS are mmol/L, '...,"MMnn.n ",...' is transmitted.
        If a checkstrip reading, ',,,,"! nnn ",...' (mg/dL) or
          '...,"! n.n ",...' (mmol/L) is transmitted.
        If flagged as control solution, '...,"C nnn ",...' (mg/dL) or
          '...,"C nn.n ",...' (mmol/L) is transmitted.  (The 'C' becomes
          a 'K' if the languages is SVENS or DEUTS.)             
            
            */
            
            strtok.nextToken();
            String date = strtok.nextToken();
            String time = strtok.nextToken();
            
            String res = this.getParameterValue(strtok.nextToken());
            
            //if ((res.contains("CHIGH")) || (res.contains("C ")))
            if ((res.startsWith("C")) ||  // control solution
                (res.startsWith("!")) ||  // check strip
                (res.startsWith("I")) ||  // insulin or other data
                (res.startsWith("K")))    // control solution (on OT2 when language is SVENS or DEUTSCH)
            {
            	this.entries_current++;
            }
            else
            {
            	this.entries_current++;

            	MeterValuesEntry mve = new MeterValuesEntry();
                mve.setBgUnit(DataAccessMeter.BG_MGDL);
                mve.setDateTime(getDateTime(date, time));
                
                
                if (res.contains("HIGH"))
                {
                    mve.setBgValue("600");
                    mve.addParameter("RESULT", "High");
                }
                else
                {
                    if (res.contains("?"))
                    {
                        res = m_da.replaceExpression(res, "?", " ").trim();
                        mve.addParameter("RESULT", "Suspect Entry");
                    }
                    
                    if (res.contains("MM"))
                    {
                        // mmol value, this is not supported by ultra, but some other meters support this
                        res = m_da.replaceExpression(res, "MM", " ").trim();
                        
                        try
                        {
                            mve.setBgValue("" + m_da.getBGValueByType(DataAccessMeter.BG_MMOL, DataAccessMeter.BG_MGDL, res));
                        }
                        catch(Exception ex)
                        {}
                        
                    }
                    else
                        mve.setBgValue(res);
                }
                
                this.output_writer.writeData(mve);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex);
            System.out.println("Entry: " + entry);
            ex.printStackTrace();
        }
        
    }
    
    
    private ATechDate getDateTime(String date, String time)
    {
        // �mm/dd/yy�,�hh:mm:30 �
        
        date = this.getParameterValue(date); 
        time = this.getParameterValue(time); 
        
       
        String dt = "";
        
        StringTokenizer st = new StringTokenizer(date, "/");
        
        String m="",d="",y="";
        
        if (this.date_order.equals("MDY"))
        {
            m = st.nextToken();
            d = st.nextToken();
            y = st.nextToken();
        }
        else
        {
            d = st.nextToken();
            m = st.nextToken();
            y = st.nextToken();
        }
        		
        
        try
        {
            int year = Integer.parseInt(y);
            
            if (year<100)
            {
                if (year>70)
                    dt += "19" + m_da.getLeadingZero(year, 2);
                else
                    dt += "20" + m_da.getLeadingZero(year, 2);
            }
            else
                dt += year;
            
        }
        catch(Exception ex)
        {
            
        }
        
        dt += m;
        dt += d;

        if (time.contains(" "))
        {

            st = new StringTokenizer(time, ":");
            
            String hr = st.nextToken();
            String min = st.nextToken();
            
            int hr_s = Integer.parseInt(hr);
            
            if (time.contains("AM"))
            {
                // am
                if (hr_s==12)
                {
                    dt += "00";
                }
                else
                {
                    dt += m_da.getLeadingZero(hr_s, 2);
                }
                
                
            }
            else
            {
                // pm
                if (hr_s==12)
                {
                    dt += "12";
                }
                else
                {
                    hr_s += 12;
                    dt += m_da.getLeadingZero(hr_s, 2);
                }
                
            }
            
            dt += min;
        }
        else
        {
            st = new StringTokenizer(time, ":");
            
            dt += st.nextToken();
            dt += st.nextToken();
        }
        
        return tzu.getCorrectedDateTime(new ATechDate(Long.parseLong(dt)));
        
    }

    
    //private void 
    
    
    
    /**
     * Returns short name for meter (for example OT Ultra, would return "Ultra")
     * 
     * @return short name of meter
     */
    public abstract String getShortName();
    
    
    
    
    
    /**
     * We don't use serial event for reading data, because process takes too long, we use serial event just 
     * to determine if device is stopped (interrupted) 
     */
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
                //setDeviceStopped();
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
    
    
    @SuppressWarnings("unused")
    private class OldOTProcessor extends OneTouchMeter //AbstractSerialMeter
    {
        
        
        public OldOTProcessor(String portName, OutputWriter writer)
        {
            super(portName, writer);
        }

        protected void writeCmd(short line[]) throws IOException, InterruptedException
        {
            for (int c = 0; c < line.length; c++)
                writeCmd(line[c]);

        }

        protected void writeCmd(int c) throws IOException, InterruptedException
        {
            // addDebug(c, D_WR);
            this.write(c);
            this.waitTime(commandPause);

        }

        protected void writeCmd(String line) throws IOException, InterruptedException
        {
            for (int c = 0; c < line.length(); c++)
            {
                // addDebug(line.charAt(c), D_WR);
                this.write(line.charAt(c));
                this.waitTime(commandPause);
            }

            this.waitTime(commandPause);
        }

        int commandPause = 100;

        void SendCommand(String command) throws Exception
        {
            int x;
            char c;

            x = command.length();
            c = command.charAt(0);
            int com_curr = 0;

            if (c == 'D')
            {
                while (x > 0)
                {
                    c = command.charAt(com_curr);
                    this.write(c);
                    waitTime(100);
                    x--;
                    com_curr++;
                }
            }
            else
                System.out.println("Invalid Command: " + command);
            return;
        }

        public int getCompanyId()
        {
            return 0;
        }

        public int getMaxMemoryRecords()
        {
            return 0;
        }

        public String getDeviceClassName()
        {
            return null;
        }

        public int getDeviceId()
        {
            return 0;
        }

        public String getIconName()
        {
            return null;
        }

        public String getName()
        {
            return null;
        }

        @Override
        public String getShortName()
        {
            // TODO Auto-generated method stub
            return null;
        }




        /*
        char *ReadLine(void) {
            int nBytes, x=0;
            char cComm, *line;
            struct timeval last, now;
            line = malloc(80);
            gettimeofday(&last, NULL);

            while ( 1 ) {
                nBytes = read(fd_ComX, &cComm, 1);
                    if ( (nBytes>0) && (cComm!=EOF) ) {
                    *line = cComm;
                    line++;
                    x++;
                    gettimeofday(&last, NULL);
                    if (cComm == '\n') break;
                }
                gettimeofday(&now, NULL);
                if ((last.tv_sec + 2) < now.tv_sec) break;
            }
            line = line - x;
            return line;
        } */
        
        
        
    }
    
    
    
}