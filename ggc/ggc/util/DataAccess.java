/*
 *  GGC - GNU Gluco Control
 *
 *  A pure java app to help you manage your diabetes.
 *
 *  See AUTHORS for copyright information.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Filename: DataAccess
 *  Purpose:  Used for utility works and static data handling (this is singelton
 *      class which holds all our definitions, so that we don't need to create them
 *      again for each class.      
 *
 *  Author:   andyrozman
 */

package ggc.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ImageIcon;

import ggc.data.DailyValues;
import ggc.data.HbA1cValues;
import ggc.data.WeeklyValues;
import ggc.data.cfg.ConfigurationManager;
import ggc.data.meter.MeterManager;
import ggc.db.GGCDb;
import ggc.db.GGCDbLoader;
import ggc.db.tool.DbToolApplicationGGC;
import ggc.gui.MainFrame;
import ggc.gui.StatusBar;
import ggc.gui.little.GGCLittle;
import ggc.gui.little.StatusBarL;
import ggc.gui.nutrition.GGCTreeRoot;


public class DataAccess 
{

    // LF
    //    Hashtable<String,String> availableLF_full = null;
    //    Object[]  availableLF = null;
    //    Object[]  availableLang = null;
    //    private LanguageInfo m_lang_info = null;

    //    String selectedLF = null;
    //    String subSelectedLF = null;

    // Config file
    //    Hashtable<String,String> config_db_values = null;
    //    public int selected_db = -1;
    //    public int selected_lang = 1;
    //    public String selected_LF_Class = null; // class
    //  public String selected_LF_Name = null; // name
    //    public String skinLFSelected = null;
    //    String allDbs[] = null;

    public static final String pathPrefix = ".";

    public I18nControl m_i18n = null;

    private static DataAccess s_da = null; // This is handle to unique 

    // singelton instance

    public GGCDb m_db = null;

    public MainFrame m_main = null;

    public GGCLittle m_main_little = null;

    public Font fonts[] = null;

    public GGCTreeRoot m_nutrition_treeroot = null;

    public GGCTreeRoot m_meals_treeroot = null;

    // daily and weekly data
    private GregorianCalendar m_date = null, m_dateStart = null;

    private HbA1cValues m_HbA1c = null;

    private DailyValues m_dvalues = null;

    private WeeklyValues m_dRangeValues = null;

    private MeterManager m_meterManager = null;

    private GGCProperties m_settings = null;

    private DbToolApplicationGGC m_configFile = null;
    private ConfigurationManager m_cfgMgr = null;


    public static DecimalFormat MmolDecimalFormat = new DecimalFormat("#0.0");

    /**
     * Which BG unit is used: BG_MGDL = mg/dl, BG_MMOL = mmol/l
     */
    public int m_BG_unit = BG_MGDL;

    public String[] availableLanguages = { "English", "Deutsch", "Slovenski", };

    public String[] avLangPostfix = { "en", "de", "si", };

    public String[] bg_units = { "mg/dl", "mmol/l" };

    public Hashtable<String,String> timeZones;


    public ImageIcon config_icons[] = null;
/*
    {
	    new ImageIcon("/icons/cfg_general.png"), 
	    new ImageIcon("/icons/cfg_medical.png"), 
	    new ImageIcon("icons/cfg_colors.png"), 
	    new ImageIcon("icons/cfg_render.png"), 
	    new ImageIcon("icons/cfg_meter.png"), 
	    new ImageIcon("icons/cfg_print.png")
	};
*/
/*	public ImageIcon config_icons[] = {
	    new ImageIcon("images/cfg_db.gif"), 
	    new ImageIcon("images/cfg_look.gif"), 
	    new ImageIcon("images/cfg_myparish.gif"), 
	    new ImageIcon("images/cfg_masses.gif"), 
	    new ImageIcon("images/cfg_users.gif"), 
	    new ImageIcon("images/cfg_lang.gif"), 
	    new ImageIcon("images/cfg_web.gif"), 
	    null
	};

    public String config_types[] = { 
	m_ic.getMessage("GENERAL"),
	m_ic.getMessage("MEDICAL_DATA"),
	m_ic.getMessage("COLORS_AND_FONTS"),
	m_ic.getMessage("RENDERING_QUALITY"),
	m_ic.getMessage("METER_CONFIGURATION"),
	m_ic.getMessage("PRINTING")
    };

*/
        public String[] options_yes_no = null;




    // ********************************************************
    // ******      Constructors and Access methods        *****    
    // ********************************************************

    //   Constructor:  DataAccess
    /**
     *
     *  This is DataAccess constructor; Since classes use Singleton Pattern,
     *  constructor is protected and can be accessed only with getInstance() 
     *  method.<br><br>
     *
     */
    private DataAccess()
    {
        this.loadFonts();

        //m_i18n.createInstance(this);
        //        loadAvailableLFs();
        //        loadLanguageInfo();
        m_meterManager = new MeterManager();

        this.m_configFile = new DbToolApplicationGGC();
        this.m_configFile.loadConfig();

        m_cfgMgr = new ConfigurationManager(this);

        this.m_settings = new GGCProperties(this, this.m_configFile, m_cfgMgr);

        m_i18n = I18nControl.getInstance();

        loadOptions();

        //this.verifyComConfig();
        this.loadTimeZones();

        checkPrerequisites();

    } 

    //  Method:       getInstance
    //  Author:       Andy
    /**
     *
     *  This method returns reference to OmniI18nControl object created, or if no 
     *  object was created yet, it creates one.<br><br>
     *
     *  @return Reference to OmniI18nControl object
     * 
     */
    public static DataAccess getInstance()
    {
        if (s_da == null)
            s_da = new DataAccess();
        return s_da;
    }

    public static DataAccess createInstance(MainFrame main)
    {
        if (s_da == null)
        {
            //GGCDb db = new GGCDb();
            s_da = new DataAccess();
            s_da.setParent(main);
        }

        return s_da;
    }

    public static DataAccess createInstance(GGCLittle main)
    {
        if (s_da == null)
        {
            //GGCDb db = new GGCDb();
            s_da = new DataAccess();
            s_da.setParent(main);
        }

        return s_da;
    }

    public void checkPrerequisites()
    {
	// check that ../data/temp exists (needed for printing)

	File f = new File("../data/temp/");
	if (!f.exists())
	{
	    f.mkdir();
	}
    }


    /*
     static public DataAccess getInstance()
     {
     return m_da;
     }
     */

    //  Method:       deleteInstance
    /**
     *  This method sets handle to DataAccess to null and deletes the instance. <br><br>
     */
    public void deleteInstance()
    {
        m_i18n = null;
    }

    public void startDb(StatusBar bar)
    {
        GGCDbLoader loader = new GGCDbLoader(this, bar);
        loader.start();
    }

    public void startDb(StatusBarL bar2)
    {
        GGCDbLoader loader = new GGCDbLoader(this, bar2);
        loader.start();
    }

    public GGCDb getDb()
    {
        return m_db;
    }


    // ********************************************************
    // ******                 Icons                       *****    
    // ********************************************************

    private void loadIcons()
    {
        config_icons = new ImageIcon[6];
        config_icons[0] = new ImageIcon(getImage("/icons/cfg_general.png", m_main));
        config_icons[1] = new ImageIcon(getImage("/icons/cfg_medical.png", m_main));
        config_icons[2] = new ImageIcon(getImage("/icons/cfg_colors.png", m_main));
        config_icons[3] = new ImageIcon(getImage("/icons/cfg_render.png", m_main));
        config_icons[4] = new ImageIcon(getImage("/icons/cfg_meter.png", m_main));
        config_icons[5] = new ImageIcon(getImage("/icons/cfg_print.png", m_main));

    }



    // ********************************************************
    // ******                    Db                       *****    
    // ********************************************************


    public DbToolApplicationGGC getDbConfig()
    {
	return this.m_configFile;
    }


    // ********************************************************
    // ******                   Meters                    *****    
    // ********************************************************

    public MeterManager getMeterManager()
    {
        return this.m_meterManager;
    }


    // ********************************************************
    // ******                   Meters                    *****    
    // ********************************************************

    public void loadTimeZones()
    {
        this.timeZones = new Hashtable<String,String>();

        // Posible needed enchancment. We should probably list all ID's as values. On windows default ID can be different 
        // as in this table. We should add this names, if we encounter problems.

        this.timeZones.put("(GMT+13:00) Nuku'alofa", "Pacific/Tongatapu");
        this.timeZones.put("(GMT+12:00) Fiji, Kamchatka, Marshall Is.", "Pacific/Fiji");
        this.timeZones.put("(GMT+12:00) Auckland, Wellington", "Pacific/Auckland");
        this.timeZones.put("(GMT+11:00) Magadan, Solomon Is., New Caledonia", "Asia/Magadan");
        this.timeZones.put("(GMT+10:00) Vladivostok", "Asia/Vladivostok");
        this.timeZones.put("(GMT+10:00) Hobart", "Australia/Hobart");
        this.timeZones.put("(GMT+10:00) Guam, Port Moresby", "Pacific/Guam");
        this.timeZones.put("(GMT+10:00) Canberra, Melbourne, Sydney", "Australia/Sydney");
        this.timeZones.put("(GMT+10:00) Brisbane", "Australia/Brisbane");
        this.timeZones.put("(GMT+09:30) Adelaide", "Australia/Adelaide");
        this.timeZones.put("(GMT+09:00) Yakutsk", "Asia/Yakutsk");
        this.timeZones.put("(GMT+09:00) Seoul", "Asia/Seoul");
        this.timeZones.put("(GMT+09:00) Osaka, Sapporo, Tokyo", "Asia/Tokyo");
        this.timeZones.put("(GMT+08:00) Taipei", "Asia/Taipei");
        this.timeZones.put("(GMT+08:00) Perth", "Australia/Perth");
        this.timeZones.put("(GMT+08:00) Kuala Lumpur, Singapore", "Asia/Kuala_Lumpur");
        this.timeZones.put("(GMT+08:00) Irkutsk, Ulaan Bataar", "Asia/Irkutsk");
        this.timeZones.put("(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi", "Asia/Hong_Kong");
        this.timeZones.put("(GMT+07:00) Krasnoyarsk", "Asia/Krasnoyarsk");
        this.timeZones.put("(GMT+07:00) Bangkok, Hanoi, Jakarta", "Asia/Bangkok");
        this.timeZones.put("(GMT+06:30) Rangoon", "Asia/Rangoon");
        this.timeZones.put("(GMT+06:00) Sri Jayawardenepura", "Asia/Colombo");
        this.timeZones.put("(GMT+06:00) Astana, Dhaka", "Asia/Dhaka");
        this.timeZones.put("(GMT+06:00) Almaty, Novosibirsk", "Asia/Almaty");
        this.timeZones.put("(GMT+05:45) Kathmandu", "Asia/Katmandu");
        this.timeZones.put("(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi", "Asia/Calcutta");
        this.timeZones.put("(GMT+05:00) Islamabad, Karachi, Tashkent", "Asia/Karachi");
        this.timeZones.put("(GMT+05:00) Ekaterinburg", "Asia/Yekaterinburg");
        this.timeZones.put("(GMT+04:30) Kabul", "Asia/Kabul");
        this.timeZones.put("(GMT+04:00) Baku, Tbilisi, Yerevan", "Asia/Baku");
        this.timeZones.put("(GMT+04:00) Abu Dhabi, Muscat", "Asia/Dubai");
        this.timeZones.put("(GMT+03:30) Tehran", "Asia/Tehran");
        this.timeZones.put("(GMT+03:00) Nairobi", "Africa/Nairobi");
        this.timeZones.put("(GMT+03:00) Moscow, St. Petersburg, Volgograd", "Europe/Moscow");
        this.timeZones.put("(GMT+03:00) Kuwait, Riyadh", "Asia/Kuwait");
        this.timeZones.put("(GMT+03:00) Baghdad", "Asia/Baghdad");
        this.timeZones.put("(GMT+02:00) Jerusalem", "Asia/Jerusalem");
        this.timeZones.put("(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius", "Europe/Helsinki");
        this.timeZones.put("(GMT+02:00) Harare, Pretoria", "Africa/Harare");
        this.timeZones.put("(GMT+02:00) Cairo", "Africa/Cairo");
        this.timeZones.put("(GMT+02:00) Bucharest", "Europe/Bucharest");
        this.timeZones.put("(GMT+02:00) Athens, Istanbul, Minsk", "Europe/Athens");
        this.timeZones.put("(GMT+01:00) West Central Africa", "Africa/Lagos");
        this.timeZones.put("(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb", "Europe/Warsaw");
        this.timeZones.put("(GMT+01:00) Brussels, Copenhagen, Madrid, Paris", "Europe/Brussels");
        this.timeZones.put("(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", "Europe/Prague,Europe/Belgrade");
        this.timeZones.put("(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", "Europe/Amsterdam");
        this.timeZones.put("(GMT) Casablanca, Monrovia", "Africa/Casablanca");
        this.timeZones.put("(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London", "Europe/Dublin");
        this.timeZones.put("(GMT-01:00) Azores", "Atlantic/Azores");
        this.timeZones.put("(GMT-01:00) Cape Verde Is.", "Atlantic/Cape_Verde");
        this.timeZones.put("(GMT-02:00) Mid-Atlantic", "Atlantic/South_Georgia");
        this.timeZones.put("(GMT-03:00) Brasilia", "America/Sao_Paulo");
        this.timeZones.put("(GMT-03:00) Buenos Aires, Georgetown", "America/Buenos_Aires");
        this.timeZones.put("(GMT-03:00) Greenland", "America/Thule");
        this.timeZones.put("(GMT-03:30) Newfoundland", "America/St_Johns");
        this.timeZones.put("(GMT-04:00) Atlantic Time (Canada)", "America/Halifax");
        this.timeZones.put("(GMT-04:00) Caracas, La Paz", "America/Caracas");
        this.timeZones.put("(GMT-04:00) Santiago", "America/Santiago");
        this.timeZones.put("(GMT-05:00) Bogota, Lima, Quito", "America/Bogota");
       this.timeZones.put("(GMT-05:00) Eastern Time (US & Canada)", " America/New_York");
       this.timeZones.put("(GMT-05:00) Indiana (East)", "America/Indianapolis");
       this.timeZones.put("(GMT-06:00) Central America", "America/Costa_Rica");
       this.timeZones.put("(GMT-06:00) Central Time (US & Canada)", "America/Chicago");
       this.timeZones.put("(GMT-06:00) Guadalajara, Mexico City, Monterrey", "America/Mexico_City");
       this.timeZones.put("(GMT-06:00) Saskatchewan", "America/Winnipeg");
       this.timeZones.put("(GMT-07:00) Arizona", "America/Phoenix");
       this.timeZones.put("(GMT-07:00) Chihuahua, La Paz, Mazatlan", "America/Tegucigalpa");
       this.timeZones.put("(GMT-07:00) Mountain Time (US & Canada)", "America/Denver");
       this.timeZones.put("(GMT-08:00) Pacific Time (US & Canada); Tijuana", "America/Los_Angeles");
       this.timeZones.put("(GMT-09:00) Alaska", "America/Anchorage");
       this.timeZones.put("(GMT-10:00) Hawaii", "Pacific/Honolulu");
       this.timeZones.put("(GMT-11:00) Midway Island, Samoa", "Pacific/Apia");
       this.timeZones.put("(GMT-12:00) International Date Line West", "MIT");

    }




    // ********************************************************
    // ******                  Settings                   *****    
    // ********************************************************

    public GGCProperties getSettings()
    {
        return this.m_settings;
    }

    public void loadSettingsFromDb()
    {
        this.m_settings.load();
    }

    public Color getColor(int color)
    {
        return new Color(color);
    }

    public ConfigurationManager getConfigurationManager()
    {
	return this.m_cfgMgr;
    }


    // ********************************************************
    // ******                  Language                   *****    
    // ********************************************************

    public String[] getAvailableLanguages()
    {
        return this.availableLanguages;
    }

    public int getSelectedLanguageIndex()
    {
        return this.getLanguageIndex(this.getSettings().getLanguage());
    }

    public int getLanguageIndex(String postfix)
    {
        //System.out.println(postfix);

        for (int i = 0; i < this.avLangPostfix.length; i++)
        {
            if (this.avLangPostfix[i].equals(postfix))
                return i;
        }

        return 0;
    }

    public int getLanguageIndexByName(String name)
    {
        //stem.out.println(name);

        for (int i = 0; i < this.availableLanguages.length; i++)
        {
            if (this.availableLanguages[i].equals(name))
                return i;
        }

        return 0;
    }

    // ********************************************************
    // ******             BG Measurement Type             *****    
    // ********************************************************

    public static final int BG_MGDL = 1;

    public static final int BG_MMOL = 2;

    public int getBGMeasurmentType()
    {
        return this.m_BG_unit;
    }

    public void setBGMeasurmentType(int type)
    {
        this.m_BG_unit = type;
    }

    private static final float MGDL_TO_MMOL_FACTOR = 0.0555f;

    private static final float MMOL_TO_MGDL_FACTOR = 18.016f;

    /**
     * Depending on the return value of <code>getBGMeasurmentType()</code>, either
     * return the mg/dl or the mmol/l value of the database's value. Default is mg/dl.
     * @param dbValue - The database's value (in float)
     * @return the BG in either mg/dl or mmol/l
     */
    public float getDisplayedBG(float dbValue)
    {
        switch (this.m_BG_unit)
        {
        case BG_MMOL:
            // this POS should return a float rounded to 3 decimal places,
            // if I understand the docu correctly
            return (new BigDecimal(dbValue * MGDL_TO_MMOL_FACTOR,
                    new MathContext(3, RoundingMode.HALF_UP)).floatValue());
        case BG_MGDL:
        default:
            return dbValue;
        }
    }

    public float getBGValue(float bg_value)
    {
        switch (this.m_BG_unit)
        {
        case BG_MMOL:
            return (bg_value * MGDL_TO_MMOL_FACTOR);
        case BG_MGDL:
        default:
            return bg_value;
        }

    }


    public float getBGValueByType(int type, float bg_value)
    {
        switch (type)
        {
        case BG_MMOL:
            return (bg_value * MGDL_TO_MMOL_FACTOR);
        case BG_MGDL:
        default:
            return bg_value;
        }

    }


    public float getBGValueByType(int input_type, int output_type, float bg_value)
    {
        
        if (input_type==output_type)
            return bg_value;
        else
        {
            if (output_type==DataAccess.BG_MGDL)
            {
                return bg_value * DataAccess.MGDL_TO_MMOL_FACTOR;
            }
            else
            {
                return bg_value * DataAccess.MMOL_TO_MGDL_FACTOR;
            }
        }

    }


    public float getBGValueDifferent(int type, float bg_value)
    {

            if (type==DataAccess.BG_MGDL)
            {
                return bg_value * DataAccess.MGDL_TO_MMOL_FACTOR;
            }
            else
            {
                return bg_value * DataAccess.MMOL_TO_MGDL_FACTOR;
            }

    }


    // ********************************************************
    // ******                   Fonts                     *****    
    // ********************************************************

    public static final int FONT_BIG_BOLD = 0;

    public static final int FONT_NORMAL = 1;

    public static final int FONT_NORMAL_BOLD = 2;

    public void loadFonts()
    {
        fonts = new Font[3];
        fonts[0] = new Font("SansSerif", Font.BOLD, 22);
        fonts[1] = new Font("SansSerif", Font.PLAIN, 12);
        fonts[2] = new Font("SansSerif", Font.BOLD, 12);
    }

    public Font getFont(int font_id)
    {
        return fonts[font_id];
    }

    // ********************************************************
    // ******          Parent handling (for UIs)          *****    
    // ********************************************************

    public void setParent(MainFrame main)
    {
        m_main = main;
        loadIcons();
    }

    public void setParent(GGCLittle main)
    {
        m_main_little = main;
    }

    public MainFrame getParent()
    {
        return m_main;
    }

    public GGCLittle getParentLittle()
    {
        return m_main_little;
    }

    public I18nControl getI18nInstance()
    {
        return m_i18n;
    }

    /**
     *  Utils
     */


    public Image getImage(String filename, Component cmp)
    {
        Image img;

        InputStream is = this.getClass().getResourceAsStream(filename);

        if (is==null)
            System.out.println("Error reading image: "+filename);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            int c;
            while ((c = is.read()) >=0)
            baos.write(c);


            //JDialog.getT
            //JFrame.getToolkit();
            img = cmp.getToolkit().createImage(baos.toByteArray());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return img;
    }



    // ********************************************************
    // ******               Look and Feel                 *****    
    // ********************************************************

    /*
     public void loadAvailableLFs()
     {

     availableLF_full = new Hashtable<String,String>();
     UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();

     availableLF = new Object[info.length+1];

     //ring selectedLF = null;
     //String subSelectedLF = null;

     int i;
     for (i=0; i<info.length; i++) 
     {
     String name = info[i].getName();
     String className = info[i].getClassName();

     availableLF_full.put(name, className);
     availableLF[i] = name;

     //System.out.println(humanReadableName);
     }     

     availableLF_full.put("SkinLF", "com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
     availableLF[i] = "SkinLF";

     }

     public Object[] getAvailableLFs()
     {
     return availableLF;
     }


     public static String[] getLFData()
     {
     String out[] = new String[2];

     try
     {
     Properties props = new Properties();

     FileInputStream in = new FileInputStream(pathPrefix  + "/data/PIS_Config.properties");
     props.load(in);

     out[0] = (String)props.get("LF_CLASS");
     out[1] = (String)props.get("SKINLF_SELECTED");

     return out;

     }
     catch(Exception ex)
     {
     System.out.println("DataAccess::getLFData::Exception> " + ex);
     return null;
     }
     }
     */


    public static String[] getLFData()
    {
        String out[] = new String[2];

        try
        {
            Properties props = new Properties();

            FileInputStream in = new FileInputStream("../data/GGC_Config.properties");
            props.load(in);

            out[0] = (String)props.get("LF_CLASS");
            out[1] = (String)props.get("SKINLF_SELECTED");

            return out;

        }
        catch(Exception ex)
        {
            System.out.println("DataAccess::getLFData::Exception> " + ex);
            return null;
        }
    }



    // ********************************************************
    // ******          COMMUNICATION API Config           *****    
    // ********************************************************
    /**
     * Preveri konfiguracijsko datoteko in vse parametre v njej.
     */
    private void verifyComConfig()
    {
	/*&
        Properties pr = new Properties();

        pr.put("Driver", "com.ibm.comm.IBMCommDriver");

        /*
         if (System.getProperty("os.name").indexOf("Win")!=-1) 
         {
         pr.put("Driver", "com.ibm.comm.IBMCommDriver");
         }
         else if (System.getProperty("os.name").indexOf("Lin")!=-1) 
         {
         pr.put("Driver", "com.ibm.comm.NSCommDriver");
         pr.put("Polltime", "3");
         }
         else
         {
         editor.append(" [OPOZORILO]   Obstaja mo\u017Enost, da uporabljate nepodprto platformo. \u010Ce ste zagnali aplikacijo drugje kot na\n"+
         "                     Windows ali Linux platformi, vedite, da ne bo delovala.\n");
         logger.warn("[ServerSBApp]  Moznost nepodprte platforme: "+System.getProperty("os.name"));
         }



         if (System.getProperty("java.version").indexOf("1.4")==-1) 
         {
         editor.append(" [NAPAKA]   Uporabljate verzijo Jave, ki ni podprta. Instalirajte verzijo Jave 1.4.x in za\u017Eenite program znova.\n");
         logger.error("[ServerSBApp]  Nepodprta Verzija Jave: "+System.getProperty("java.version"));
         }
         */

	/*
        // XXX: is this really necessary?
        String jh = System.getProperty("java.home");

        try
        {
            FileOutputStream fos = new FileOutputStream(jh + File.pathSeparator
                    + "lib" + File.pathSeparator + "gnu.io.properties");
            pr.store(fos, " Generated gnu.io.properties by GGC");
        } catch (Exception ex)
        {
            System.out
                    .println("Problem writing COMM Api config file gnu.io.properties into %JAVA_HOME%"
                            + File.pathSeparator
                            + "lib"
                            + File.pathSeparator
                            + ": " + ex);
        }
*/

    }


    // ********************************************************
    // ******                  Options                    *****    
    // ********************************************************

    public void loadOptions()
    {
        this.options_yes_no = new String[2];
        this.options_yes_no[0] = m_i18n.getMessage("YES");
        this.options_yes_no[1] = m_i18n.getMessage("NO");
    }


    // ********************************************************
    // ******          Dates and Times Handling           *****    
    // ********************************************************

    public String getCurrentDateString()
    {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.get(Calendar.DAY_OF_MONTH) + "."
                + (gc.get(Calendar.MONTH) + 1) + "." + gc.get(Calendar.YEAR);
    }

    public String[] getMonthsArray()
    {
        String arr[] = new String[12];

        arr[0] = m_i18n.getMessage("JANUARY");
        arr[1] = m_i18n.getMessage("FEBRUARY");
        arr[2] = m_i18n.getMessage("MARCH");
        arr[3] = m_i18n.getMessage("APRIL");
        arr[4] = m_i18n.getMessage("MAY");
        arr[5] = m_i18n.getMessage("JUNE");
        arr[6] = m_i18n.getMessage("JULY");
        arr[7] = m_i18n.getMessage("AUGUST");
        arr[8] = m_i18n.getMessage("SEPTEMBER");
        arr[9] = m_i18n.getMessage("OCTOBER");
        arr[10] = m_i18n.getMessage("NOVEMBER");
        arr[11] = m_i18n.getMessage("DECEMBER");

        return arr;

    }

    public String getDateString(int date)
    {
        // 20051012

        int year = date / 10000;
        int months = date - (year * 10000);

        months = months / 100;

        int days = date - (year * 10000) - (months * 100);

        if (year == 0)
            return getLeadingZero(days, 2) + "/" + getLeadingZero(months, 2);
        else
            return getLeadingZero(days, 2) + "/" + getLeadingZero(months, 2)
                    + "/" + year;
    }

    public String getTimeString(int time)
    {
        int hours = time / 100;
        int min = time - hours * 100;

        return getLeadingZero(hours, 2) + ":" + getLeadingZero(min, 2);
    }

    public String getDateTimeString(long date)
    {
        return getDateTimeString(date, 1);
    }

    public String getDateTimeAsDateString(long date)
    {
        return getDateTimeString(date, 2);
    }

    public String getDateTimeAsTimeString(long date)
    {
        return getDateTimeString(date, 3);
    }

    // ret_type = 1 (Date and time)
    // ret_type = 2 (Date)
    // ret_type = 3 (Time)

    public static final int DT_DATETIME = 1;

    public static final int DT_DATE = 2;

    public static final int DT_TIME = 3;

    public String getDateTimeString(long dt, int ret_type)
    {

        int y = (int) (dt / 100000000L);
        dt -= y * 100000000L;

        int m = (int) (dt / 1000000L);
        dt -= m * 1000000L;

        int d = (int) (dt / 10000L);
        dt -= d * 10000L;

        int h = (int) (dt / 100L);
        dt -= h * 100L;

        int min = (int) dt;

        if (ret_type == DT_DATETIME)
            return getLeadingZero(d, 2) + "." + getLeadingZero(m, 2) + "." + y
                    + "  " + getLeadingZero(h, 2) + ":"
                    + getLeadingZero(min, 2);
        else if (ret_type == DT_DATE)
            return getLeadingZero(d, 2) + "." + getLeadingZero(m, 2) + "." + y;
        else
            return getLeadingZero(h, 2) + ":" + getLeadingZero(min, 2);

    }

    public Date getDateTimeAsDateObject(long dt)
    {

        //Date dt_obj = new Date();
        GregorianCalendar gc = new GregorianCalendar();

        int y = (int) (dt / 100000000L);
        dt -= y * 100000000L;

        int m = (int) (dt / 1000000L);
        dt -= m * 1000000L;

        int d = (int) (dt / 10000L);
        dt -= d * 10000L;

        int h = (int) (dt / 100L);
        dt -= h * 100L;

        int min = (int) dt;

        gc.set(Calendar.DATE, d);
        gc.set(Calendar.MONTH, m - 1);
        gc.set(Calendar.YEAR, y);
        gc.set(Calendar.HOUR_OF_DAY, h);
        gc.set(Calendar.MINUTE, min);

        /*
         dt_obj.setHours(h);
         dt_obj.setMinutes(min);

         dt_obj.setDate(d);
         dt_obj.setMonth(m);
         dt_obj.setYear(y);

         return dt_obj;
         */

        return gc.getTime();

    }

    public long getDateTimeLong(long dt, int ret_type)
    {

        int y = (int) (dt / 100000000L);
        dt -= y * 100000000L;

        int m = (int) (dt / 1000000L);
        dt -= m * 1000000L;

        int d = (int) (dt / 10000L);
        dt -= d * 10000L;

        int h = (int) (dt / 100L);
        dt -= h * 100L;

        int min = (int) dt;

        if (ret_type == DT_DATETIME)
        {
            return Integer.parseInt(y + getLeadingZero(m, 2)
                    + getLeadingZero(d, 2) + getLeadingZero(h, 2)
                    + getLeadingZero(min, 2));
        } else if (ret_type == DT_DATE)
        {
            return Integer.parseInt(getLeadingZero(d, 2) + getLeadingZero(m, 2)
                    + y);
        } else
            return Integer.parseInt(getLeadingZero(h, 2)
                    + getLeadingZero(min, 2));

    }

    public long getDateTimeFromDateObject(Date dt)
    {

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dt);

        String dx = "";

        dx += "" + gc.get(Calendar.YEAR);
        dx += "" + getLeadingZero(gc.get(Calendar.MONTH + 1), 2);
        dx += "" + getLeadingZero(gc.get(Calendar.DAY_OF_MONTH), 2);
        dx += "" + getLeadingZero(gc.get(Calendar.HOUR_OF_DAY), 2);
        dx += "" + getLeadingZero(gc.get(Calendar.MINUTE), 2);

        return Long.parseLong(dx);

    }

    // 1 = Db Date: yyyyMMdd
    // 2 = Db Full: yyyyMMddHHMM (24h format)
    public String getDateTimeStringFromGregorianCalendar(GregorianCalendar gc,
            int type)
    {
        String st = "";

        if (gc.get(Calendar.YEAR) < 1000)
        {
            st += gc.get(Calendar.YEAR) + 1900;
        } else
        {
            st += gc.get(Calendar.YEAR);
        }

        st += getLeadingZero(gc.get(Calendar.MONTH) + 1, 2);
        st += getLeadingZero(gc.get(Calendar.DAY_OF_MONTH), 2);

        if (type == 2)
        {
            st += getLeadingZero(gc.get(Calendar.HOUR_OF_DAY), 2);
            st += getLeadingZero(gc.get(Calendar.MINUTE), 2);
        }

        //System.out.println(st);

        return st;
    }

    public String getDateTimeString(int date, int time)
    {
        return getDateString(date) + " " + getTimeString(time);
    }

    public String getLeadingZero(int number, int places)
    {
        String nn = "" + number;

        while (nn.length() < places)
        {
            nn = "0" + nn;
        }

        return nn;
    }

    public String getLeadingZero(String number, int places)
    {
        number = number.trim();

        while (number.length() < places)
        {
            number = "0" + number;
        }

        return number;
    }

    public int getStartYear()
    {
        return 1800;
    }

    public float getFloatValue(Object aValue)
    {
        float out = 0.0f;

        if (aValue == null)
            return out;

        if (aValue instanceof Float)
        {
            try
            {
                Float f = (Float) aValue;
                out = f.floatValue();
            } catch (Exception ex)
            {
            }
        } else if (aValue instanceof String)
        {
            String s = (String) aValue;
            if (s.length() > 0)
            {
                try
                {
                    out = Float.parseFloat(s);
                } catch (Exception ex)
                {
                }
            }
        }

        return out;
    }

    public int getIntValue(Object aValue)
    {
        int out = 0;

        if (aValue == null)
            return out;

        if (aValue instanceof Integer)
        {
            try
            {
                Integer i = (Integer) aValue;
                out = i.intValue();
            } catch (Exception ex)
            {
            }
        } else if (aValue instanceof String)
        {
            String s = (String) aValue;
            if (s.length() > 0)
            {
                try
                {
                    out = Integer.parseInt(s);
                } catch (Exception ex)
                {
                }
            }
        }

        return out;
    }

    public long getLongValue(Object aValue)
    {
        long out = 0L;

        if (aValue == null)
            return out;

        if (aValue instanceof Long)
        {
            try
            {
                Long i = (Long) aValue;
                out = i.longValue();
            } catch (Exception ex)
            {
            }
        } else if (aValue instanceof String)
        {
            String s = (String) aValue;
            if (s.length() > 0)
            {
                try
                {
                    out = Long.parseLong(s);
                } catch (Exception ex)
                {
                }
            }
        }

        return out;
    }

    /*
     public Date m_date = null;
     public HbA1cValues m_HbA1c = null;
     public DailyValues m_dvalues = null;
     */
    public synchronized void loadDailySettings(GregorianCalendar day,
            boolean force)
    {
        if ((m_db == null) || (m_db.getLoadStatus() < 2))
            return;

        if ((isSameDay(day)) && (!force))
            return;

        System.out.println("Reload daily settings (force:" + force + ")");

        m_date = day;
        m_HbA1c = m_db.getHbA1c(day);
        m_dvalues = m_db.getDayStats(day);

        m_dateStart = (GregorianCalendar) day.clone();
        m_dateStart.add(Calendar.DAY_OF_MONTH, -6);

        m_dRangeValues = m_db.getDayStatsRange(m_dateStart, m_date);
    }

    public synchronized void loadDailySettingsLittle(GregorianCalendar day,
            boolean force)
    {
        if ((m_db == null) || (m_db.getLoadStatus() < 2))
            return;

        if ((isSameDay(day)) && (!force))
            return;

        System.out.println("(Re)Load daily settings Little - (force:" + force
                + ")");

        m_date = day;
        //m_HbA1c = m_db.getHbA1c(day);
        m_dvalues = m_db.getDayStats(day);

        //m_dateStart = (GregorianCalendar) day.clone();
        //m_dateStart.add(GregorianCalendar.DAY_OF_MONTH, -6);
        //m_dateEnd = day;

        //m_dRangeValues = m_db.getDayStatsRange(m_dateStart, m_date);
    }

    public HbA1cValues getHbA1c(GregorianCalendar day)
    {
        //System.out.println("DA::getHbA1c");
        //if (!isSameDay(day))
        loadDailySettings(day, false);

        //if (m_HbA1c==null)
        //    return new HbA1cValues();
        //else
        return m_HbA1c;
    }

    public DailyValues getDayStats(GregorianCalendar day)
    {
        //System.out.println("DA::getDayStats");

        //if (!isSameDay(day))
        loadDailySettings(day, false);

        return m_dvalues;
    }

    public WeeklyValues getDayStatsRange(GregorianCalendar start,
            GregorianCalendar end)
    {
        //System.out.println("DA::getDayStatsRange");

        // we load dialy if not loaded
        if (this.m_date == null)
            loadDailySettings(end, false);

        if ((isSameDay(start, this.m_dateStart)) && (isSameDay(m_date, end)))
        {
            //System.out.println("Same day");
            return m_dRangeValues;
        } else
        {
            //System.out.println("other range");
            return m_db.getDayStatsRange(start, end);
        }
    }

    public boolean isSameDay(GregorianCalendar day)
    {
        return isSameDay(m_date, day);
    }

    public boolean isDatabaseInitialized()
    {
        if ((m_db == null) || (m_db.getLoadStatus() < 2))
            return false;
        else
            return true;
    }

    public boolean isSameDay(GregorianCalendar gc1, GregorianCalendar gc2)
    {

        if ((gc1 == null) || (gc2 == null))
        {
            return false;
        } else
        {

            if ((gc1.get(Calendar.DAY_OF_MONTH) == gc2
                    .get(Calendar.DAY_OF_MONTH))
                    && (gc1.get(Calendar.MONTH) == gc2.get(Calendar.MONTH))
                    && (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)))
            {
                return true;
            } else
            {
                return false;
            }

        }
    }

    public GregorianCalendar getGregorianCalendar(Date date)
    {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);

        return gc;
    }

    public static void notImplemented(String source)
    {
        System.out.println("Not Implemented: " + source);
    }

    /**
     * For replacing strings.<br>
     * 
     * @param input   Input String
     * @param replace What to seatch for.
     * @param replacement  What to replace with.
     * 
     * @return Parsed string.
     */
    public String replaceExpression(String input, String replace,
            String replacement)
    {

        int idx;
        if ((idx = input.indexOf(replace)) == -1)
        {
            return input;
        }

        StringBuffer returning = new StringBuffer();

        while (idx != -1)
        {
            returning.append(input.substring(0, idx));
            returning.append(replacement);
            input = input.substring(idx + replace.length());
            idx = input.indexOf(replace);
        }
        returning.append(input);

        return returning.toString();

    }



}
