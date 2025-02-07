package ggc.core.util;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.*;

import com.atech.utils.file.PropertiesFile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pygmy.core.Server;

import com.atech.data.enums.InternalSetting;
import com.atech.db.hibernate.HibernateDb;
import com.atech.db.hibernate.transfer.BackupRestoreCollection;
import com.atech.graphics.components.DialogSizePersistInterface;
import com.atech.graphics.observe.EventSource;
import com.atech.graphics.observe.ObserverManager;
import com.atech.help.HelpContext;
import com.atech.i18n.I18nControlAbstract;
import com.atech.i18n.I18nControlLangMgr;
import com.atech.i18n.I18nControlLangMgrDual;
import com.atech.i18n.mgr.LanguageManager;
import com.atech.i18n.tool.simple.data.TranslationToolConfigurationDto;
import com.atech.plugin.PlugInClient;
import com.atech.utils.ATDataAccessLMAbstract;
import com.atech.utils.ATSwingUtils;
import com.atech.utils.data.Rounding;
import com.atech.utils.java.VersionResolver;
import com.atech.utils.logs.RedirectScreen;

import ggc.core.data.*;
import ggc.core.data.cfg.ConfigurationManager;
import ggc.core.data.cfg.ConfigurationManagerWrapper;
import ggc.core.data.defs.*;
import ggc.core.data.defs.lang.GGCLanguageModule;
import ggc.core.data.defs.lang.GGCSupportedLanguages;
import ggc.core.data.graph.v2.GGCGraphContext;
import ggc.core.db.GGCDb;
import ggc.core.db.GGCDbLoader;
import ggc.core.db.datalayer.*;
import ggc.core.db.hibernate.doc.DoctorAppointmentH;
import ggc.core.db.hibernate.doc.DoctorH;
import ggc.core.db.hibernate.doc.DoctorTypeH;
import ggc.core.db.hibernate.inventory.InventoryH;
import ggc.core.db.hibernate.inventory.InventoryItemH;
import ggc.core.db.hibernate.inventory.InventoryItemTypeH;
import ggc.core.db.hibernate.settings.ColorSchemeH;
import ggc.core.db.hibernate.settings.SettingsH;
import ggc.core.db.tool.DbToolApplicationGGC;
import ggc.core.db.tool.data.GGCDatabaseTableConfiguration;
import ggc.core.plugins.*;
import ggc.gui.dialogs.selector.GGCSelectorConfiguration;

/**
 *  Application:   GGC - GNU Gluco Control
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
 *  Filename:     DataAccess
 *  Description:  This class is singelton instance and contains most of utility 
 *                methods and also hold instances of several important classes
 *                used through whole application it also hold most of "static" data.      
 * 
 *  Author: andyrozman {andy@atech-software.com}  
 */

@Slf4j
public class DataAccess extends ATDataAccessLMAbstract
{

    /**
     * Core Version
     */
    public static String CORE_VERSION = "0.7.0";

    private static final Logger LOG = LoggerFactory.getLogger(DataAccess.class);

    private Hashtable<String, EventSource> observables = null;

    private static DataAccess s_da = null; // This is handle to unique singelton
                                           // instance

    private GGCDb m_db = null;

    private Component m_main = null;

    // daily and weekly data
    private GregorianCalendar m_date = null, m_dateStart = null;

    private HbA1cValues m_HbA1c = null;
    private DailyValues m_dvalues = null;
    private WeeklyValues m_dRangeValues = null;

    protected GGCProperties m_settings = null;
    protected DbToolApplicationGGC m_configFile = null;
    protected ConfigurationManager m_cfgMgr = null;

    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
    protected static Server web_server;

    // /**
    // * Decimal with zero decimals
    // */
    // public static DecimalFormat Decimal0Format = new DecimalFormat("#0");
    //
    // /**
    // * Decimal with 1 decimal
    // */
    // public static DecimalFormat Decimal1Format = new DecimalFormat("#0.0");
    //
    // /**
    // * Decimal with 2 decimal
    // */
    // public static DecimalFormat Decimal2Format = new DecimalFormat("#0.00");
    //
    // /**
    // * Decimal with 3 decimal
    // */
    // public static DecimalFormat Decimal3Format = new DecimalFormat("#0.000");

    /**
     * Which BG unit is used: BG_MGDL = mg/dl, BG_MMOL = mmol/l
     */
    // public int m_BG_unit = BG_MGDL;

    private String[] availableLanguages = { "English", "Deutsch", "Slovensko", "Fran\u00e7ais", "Language Tool" };

    /**
     * Available Language Extensions (posfixes)
     */
    public String[] avLangPostfix = { "en", "de", "si", "fr", "lt" };

    // public Locale[] realLocales = { Locale.ENGLISH, Locale.GERMANY, Locale.

    /**
     * BG Units
     */
    // public String[] bg_units = { "", "mg/dl", "mmol/l" };

    /**
     * BG Units for configuration
     */
    // public String[] bg_units_config = { "mg/dl", "mmol/l" };

    /**
     * Config Icons 
     */
    public ImageIcon config_icons[] = null;

    GGCI18nControl ggci18nControl;

    /**
     * Converter: BG     
     */
    public static final String CONVERTER_BG = "BG";

    /**
     * Extended Handler: Daily Values Row
     */
    public static final String EXTENDED_HANDLER_DailyValuesRow = "DailyValuesRow";

    private GGCGraphContext graphContext;
    protected ConfigurationManagerWrapper configurationManagerWrapper;

    protected GlucoseUnitType glucoseUnitType;

    // private int current_person_id = 1;
    // NutriI18nControl m_nutri_i18n = NutriI18nControl.getInstance();

    public static boolean dontLoadIcons = false;




    /**
     * Developer Version
     */
    // public boolean developer_version = false;

    // ********************************************************
    // ****** Constructors and Access methods *****
    // ********************************************************

    // Constructor: DataAccess
    /**
     * 
     * This is DataAccess constructor; Since classes use Singleton Pattern,
     * constructor is protected and can be accessed only with getInstance()
     * method.<br>
     * <br>
     * 
     */
    protected DataAccess() {
        super(new LanguageManager(new GGCLanguageManagerRunner()), new GGCCoreICRunner());
        initSpecial();
    }


    /**
     * Init Special
     */
    @Override
    public void initSpecial()
    {
        doTest();

        loadLanguageIntoContext();

        HelpContext hc = new HelpContext("/" + this.languageManager.getHelpSet());
        this.setHelpContext(hc);
        this.helpEnabled = true;

        this.m_configFile = new DbToolApplicationGGC();
        this.m_configFile.loadConfig();

        m_cfgMgr = new ConfigurationManager(this);
        configurationManagerWrapper = new ConfigurationManagerWrapper(m_cfgMgr);

        this.m_settings = new GGCProperties(this, this.m_configFile, configurationManagerWrapper);

        loadOptions();

        if (!new File(userDataDirectory.getParsedUserDataPath("%USER_DATA_DIR%/debug.txt")).exists())
        {
            new RedirectScreen();
        }

        this.loadGraphConfigProperties();
        this.startWebServer();
        // this.loadSpecialParameters(); this will be loaded with GGCDbLoader
        this.loadConverters();
        this.loadGraphContext();

        this.loadVersion();

        this.translateEnums();

        // FIXME this will be determined by user management at later time
        current_user_id = 1;

        /*
         * System.out.println(Locale.getAvailableLocales());
         * Locale[] lcls = Locale.getAvailableLocales();
         * for(int i=0; i<lcls.length; i++)
         * {
         * System.out.println(lcls[i].getDisplayName() + "," +
         * lcls[i].getISO3Country() + "," + lcls[i].getISO3Language());
         * }
         */

        // this.loadBackupRestoreCollection();
    }


    private void translateEnums()
    {
        GlucoseUnitType.translateKeywords(this.m_i18n);
        GGCSoftwareMode.translateKeywords(this.m_i18n);
        InventoryItemUnit.translateKeywords(this.m_i18n);
        InventoryGroupType.translateKeywords(this.m_i18n);
        Health.translateKeywords(this.m_i18n);
        ExerciseStrength.translateKeywords(this.m_i18n);
    }


    private void loadVersion()
    {
        CORE_VERSION = VersionResolver.getVersion("ggc.core.util.Version", this.getClass().getSimpleName());
        // System.out.println("Core Version: " + CORE_VERSION);
    }


    private void loadGraphContext()
    {
        this.graphContext = new GGCGraphContext();
    }


    private void loadLanguageIntoContext()
    {
        GGCI18nControlContext ctx = GGCI18nControlContext.getInstance();

        ctx.setDefaultLanguage(this.getLanguageManager().getDefaultLanguage());
        ctx.setSelectedLanguage(this.getLanguageManager().getSelectedLanguage());

        ctx.setDefaultLanguageRecognitionInitialized(true);

        ctx.addLanguageInstance(GGCPluginType.Core, ctx.getSelectedLanguage(), this.getI18nControlInstanceBase());

        if (!ctx.isSelectedLanguageDefaultLanguage())
        {
            I18nControlLangMgr mgr = this.getLanguageManager().getI18nControl(this.i18nControlRunner);

            if (mgr instanceof I18nControlLangMgrDual)
            {
                I18nControlLangMgrDual mgrd = (I18nControlLangMgrDual) mgr;
                mgrd.getDefaultLanguageInstance();

                ctx.addLanguageInstance(GGCPluginType.Core, ctx.getDefaultLanguage(), this.getI18nControlInstanceBase());

            }
            else
            {
                LOG.error("I18nControl instance is not Mgr Dual !!!!!!");
            }
        }

        ctx.prepareContext();

        ggci18nControl = new GGCI18nControl(GGCPluginType.Core);

    }


    public I18nControlAbstract getI18nControlInstanceBase()
    {
        return this.m_i18n;
    }


    @Override
    public I18nControlAbstract getI18nControlInstance()
    {
        return this.ggci18nControl;
    }


    /**
     * Run After Db Load
     */
    public void runAfterDbLoad()
    {
        loadSpecialParameters();
    }


    // Method: getInstance
    // Author: Andy
    /**
     * 
     * This method returns reference to OmniI18nControl object created, or if no
     * object was created yet, it creates one.<br>
     * <br>
     * 
     * @return Reference to OmniI18nControl object
     * 
     */
    public static DataAccess getInstance()
    {
        if (s_da == null)
        {
            s_da = new DataAccess();
        }
        return s_da;
    }


    /**
     * Create Instance
     * 
     * @param main
     * @return
     */
    public static DataAccess createInstance(Component main)
    {
        s_da = null;

        if (s_da == null)
        {
            // System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // "
            // + main);
            // GGCDb db = new GGCDb();

            // System.out.println("create new Instance");
            s_da = new DataAccess();
            // System.out.println("setParent");
            s_da.setParent(main);
            // System.out.println("setMainParent");
            s_da.setMainParent((JFrame) main);
            // System.out.println("addComponet");
            // s_da.addComponent(main);
        }

        return s_da;
    }


    /**
     * Create Instance
     * 
     * @param main
     * @return
     */
    /*
     * public static DataAccess createInstance(GGCLittle main)
     * {
     * if (s_da == null)
     * {
     * // GGCDb db = new GGCDb();
     * s_da = new DataAccess();
     * s_da.setParent(main);
     * s_da.setMainParent(main);
     * }
     * return s_da;
     * }
     */
    /*
     * static public DataAccess getInstance() { return dataAccess; }
     */

    // Method: deleteInstance
    /**
     * This method sets handle to DataAccess to null and deletes the instance. <br>
     * <br>
     */
    public static void deleteInstance()
    {
        if (web_server!=null) {
            web_server.shutdown();
            web_server = null;
        }

        // m_i18n = null;
        DataAccess.s_da = null;
    }


    /**
     * Start Db
     */
    public void startDb() // StatusBar bar)
    {
        GGCDbLoader loader = new GGCDbLoader(this);
        loader.start();
    }


    /**
     * Start Db
     * 
     * @param bar2
     */
    /*
     * public void startDb(StatusBarL bar2)
     * {
     * GGCDbLoader loader = new GGCDbLoader(this, bar2);
     * loader.start();
     * }
     */
    /**
     * Get Db
     * @return
     */
    public GGCDb getDb()
    {
        return m_db;
    }


    /**
     * Set Db
     * 
     * @param db
     */
    public void setDb(GGCDb db)
    {
        this.m_db = db;
    }


    /**
     * Is Pen/Injection Mode
     * 
     * @return
     */
    public boolean isPenInjectionMode()
    {
        return getSoftwareMode() == GGCSoftwareMode.PEN_INJECTION_MODE;
    }


    /**
     * Is Pump Mode
     * 
     * @return
     */
    public boolean isPumpMode()
    {
        return getSoftwareMode() == GGCSoftwareMode.PUMP_MODE;
    }


    /**
     * Get Software Mode
     * 
     * @return
     */
    public GGCSoftwareMode getSoftwareMode()
    {
        return GGCSoftwareMode.getEnum(this.m_cfgMgr.getIntValue("SW_MODE"));
    }


    /**
     * Get Software Mode Description
     * 
     * @return
     */
    public String getSoftwareModeDescription()
    {
        // System.out.println("Sw Mode Desc: " +
        // this.m_cfgMgr.getStringValue("SW_MODE_DESC"));

        return this.m_cfgMgr.getStringValue("SW_MODE_DESC");
    }


    // ********************************************************
    // ****** Static Methods *****
    // ********************************************************

    /**
     * Get Float As String
     * 
     * @param f
     * @param decimalPlaces
     * @return
     */
    public String getFloatAsString(float f, String decimalPlaces)
    {
        return getFloatAsString(f, Integer.parseInt(decimalPlaces));
    }


    /**
     * Get Float As String
     * 
     * @param f
     * @param decimalPlaces
     * @return
     */
    public String getFloatAsString(float f, int decimalPlaces)
    {
        return getDecimalHandler().getDecimalNumberAsString(f, decimalPlaces);
    }


    // ********************************************************
    // ****** Abstract Methods *****
    // ********************************************************

    /**
     * Get Application Name
     * 
     * @return
     */
    @Override
    public String getApplicationName()
    {
        return "GGC";
    }


    /**
     * Check Prerequisites
     */
    @Override
    public void checkPrerequisites()
    {
        // check that ../data/temp exists (needed for printing)

        File f = new File("../data/temp/");
        if (!f.exists())
        {
            f.mkdir();
        }
    }


    /**
     * Get Images Root (Must have ending back-slash)
     * 
     * @return
     */
    @Override
    public String getImagesRoot()
    {
        return "/icons/";
    }


    /**
     * Load Backup Restore Collection
     */
    @Override
    public void loadBackupRestoreCollection()
    {
        // System.out.println("loadBackupRestoreCollection");
        //
        // BackupRestoreCollection brc_full = new
        // BackupRestoreCollection("GGC_BACKUP", this.m_i18n);
        // brc_full.addNodeChild(new DailyValue(this.m_i18n));
        //
        // BackupRestoreCollection brc1 = new
        // BackupRestoreCollection("CONFIGURATION", this.m_i18n);
        // brc1.addNodeChild(new Settings(this.m_i18n));
        // brc1.addNodeChild(new SettingsColorScheme(this.m_i18n));
        // brc_full.addNodeChild(brc1);
        //
        // // for(int i=0; i<)
        //
        // for (Enumeration<String> en = this.plugins.keys();
        // en.hasMoreElements();)
        // {
        // PlugInClient pic = this.plugins.get(en.nextElement());
        //
        // System.out.println("Plugin: " + pic.getName());
        //
        // BackupRestoreCollection brc = pic.getBackupObjects();
        //
        // System.out.println("Plugin: " + pic.getName() + " Backup Collection:
        // " + brc);
        //
        // if (brc != null)
        // {
        // brc_full.addNodeChild(brc);
        // }
        // }
        //
        // /*
        // * BackupRestoreCollection brc_nut = new
        // * BackupRestoreCollection("NUTRITION_OBJECTS", this.m_i18n);
        // * brc_nut.addNodeChild(new FoodGroup(this.m_i18n));
        // * brc_nut.addNodeChild(new FoodDescription(this.m_i18n));
        // * brc_nut.addNodeChild(new MealGroup(this.m_i18n));
        // * brc_nut.addNodeChild(new Meal(this.m_i18n));
        // * brc.addNodeChild(brc_nut);
        // * brc_nut = new BackupRestoreCollection("PUMP_TOOL", this.m_i18n);
        // * brc_nut.addNodeChild(new PumpData(this.m_i18n));
        // * brc_nut.addNodeChild(new PumpDataExtended(this.m_i18n));
        // * brc_nut.addNodeChild(new PumpProfile(this.m_i18n));
        // * brc.addNodeChild(brc_nut);
        // */
        //
        // this.backupRestoreCollection = brc_full;
    }


    /** 
     * Get BackupRestoreCollection
     */
    @Override
    public BackupRestoreCollection getBackupRestoreCollection()
    {
        BackupRestoreCollection brc_full = new BackupRestoreCollection("GGC_BACKUP", this.m_i18n);
        brc_full.addNodeChild(new DailyValue(this.m_i18n));
        brc_full.addNodeChild(new DailyValueOld(this.m_i18n));

        BackupRestoreCollection brc1 = new BackupRestoreCollection("DOC_APPOINTMENT_BACKUP", this.m_i18n, true);
        brc1.addNodeChild(new DoctorTypeH(this.m_i18n));
        brc1.addNodeChild(new DoctorH(this.m_i18n));
        brc1.addNodeChild(new DoctorAppointmentH(this.m_i18n));
        brc_full.addNodeChild(brc1);

        brc1 = new BackupRestoreCollection("CONFIGURATION", this.m_i18n);
        brc1.addNodeChild(new Settings(this.m_i18n));
        brc1.addNodeChild(new SettingsColorScheme(this.m_i18n));
        brc_full.addNodeChild(brc1);

        for (PlugInClient pic : getPlugins().values())
        {
            // PlugInClient pic = this.plugins.get(en.nextElement());

            if (pic.isBackupRestoreEnabled())
            {
                brc_full.addNodeChild(pic.getBackupObjects());
            }
        }

        return brc_full;
    }


    public BackupRestoreCollection getBackupRestoreCollection(boolean restore)
    {
        BackupRestoreCollection brc_full = new BackupRestoreCollection("GGC_BACKUP", this.m_i18n);
        brc_full.addNodeChild(new DailyValue(this.m_i18n));

        if (restore)
            brc_full.addNodeChild(new DailyValueOld(this.m_i18n));

        BackupRestoreCollection brc1 = new BackupRestoreCollection("DOC_APPOINTMENT_BACKUP", this.m_i18n, true);
        brc1.addNodeChild(new DoctorTypeH(this.m_i18n));
        brc1.addNodeChild(new DoctorH(this.m_i18n));
        brc1.addNodeChild(new DoctorAppointmentH(this.m_i18n));
        brc_full.addNodeChild(brc1);

        brc1 = new BackupRestoreCollection("CONFIGURATION", this.m_i18n);
        brc1.addNodeChild(new Settings(this.m_i18n));
        brc1.addNodeChild(new SettingsColorScheme(this.m_i18n));
        if (restore)
        {
            brc1.addNodeChild(new SettingsOld(this.m_i18n));
            brc1.addNodeChild(new SettingsColorSchemeOld(this.m_i18n));
        }

        brc_full.addNodeChild(brc1);

        for (PlugInClient pic : getPlugins().values())
        {
            if (pic.isBackupRestoreEnabled())
            {
                brc_full.addNodeChild(pic.getBackupObjects());
            }
        }

        return brc_full;
    }


    /**
     * Load Graph Config Properties
     */
    @Override
    public void loadGraphConfigProperties()
    {
        // this.graph_config = this.m_settings;
        this.graph_config = this.configurationManagerWrapper;
    }


    // ********************************************************
    // ****** Icons *****
    // ********************************************************

    private void loadIcons()
    {
        if (config_icons == null)
        {
            config_icons = new ImageIcon[11];
            config_icons[0] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_global.png", m_main));
            config_icons[1] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_mode.png", m_main));
            config_icons[2] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_general.png", m_main));
            config_icons[3] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_medical.png", m_main));
            config_icons[4] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_colors.png", m_main));
            config_icons[5] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_render.png", m_main));
            config_icons[6] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_print.png", m_main));
            config_icons[7] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_lang.png", m_main));
            config_icons[8] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_pump.png", m_main));
            config_icons[9] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_cgms.png", m_main));
            config_icons[10] = new ImageIcon(ATSwingUtils.getImage("/icons/cfg_users.png", m_main));
        }
    }


    // ********************************************************
    // ****** Db *****
    // ********************************************************

    /**
     * Get Db Config (DbToolApplicationGGC)
     * 
     * @return
     */
    public DbToolApplicationGGC getDbConfig()
    {
        return this.m_configFile;
    }


    // ********************************************************
    // ****** Settings *****
    // ********************************************************

    /**
     * Get Settings
     * 
     * @return
     */
    public GGCProperties getSettings()
    {
        return this.m_settings;
    }


    /**
     * Load Settings from Db
     */
    public void loadSettingsFromDb()
    {
        this.m_settings.load();
    }


    /**
     * Get Color
     * 
     * @param color
     * @return
     */
    public Color getColor(int color)
    {
        return new Color(color);
    }


    /**
     * Get Configuration Manager (Db)
     *
     * @deprecated use Get Configuration Manager Wrapper instead
     * @return
     */
    @Deprecated
    public ConfigurationManager getConfigurationManager()
    {
        return this.m_cfgMgr;
    }


    /**
     * Init PlugIns
     */
    public void initPlugIns()
    {

        LOG.debug("Init Plugin: Meter Tool");
        addPlugIn(GGCPluginType.MeterToolPlugin.getKey(), //
            new MetersPlugIn(this.m_main, this.ggci18nControl));

        LOG.debug("Init Plugin: Pumps Tool");
        addPlugIn(GGCPluginType.PumpToolPlugin.getKey(), //
            new PumpsPlugIn(this.m_main, this.ggci18nControl));

        LOG.debug("Init Plugin: CGMS Tool");
        addPlugIn(GGCPluginType.CGMSToolPlugin.getKey(), //
            new CGMSPlugIn(this.m_main, this.ggci18nControl));

        LOG.debug("Init Plugin: Nutrition Tool");
        addPlugIn(GGCPluginType.NutritionToolPlugin.getKey(), //
            new NutriPlugIn(this.m_main, this.ggci18nControl));

        LOG.debug("init Plugin: Connect Tool");
        addPlugIn(GGCPluginType.ConnectToolPlugin.getKey(), //
            new ConnectPlugIn(this.m_main, this.ggci18nControl));

        // dynamically load all device handlers

        observerManager.setChangeOnEventSource(GGCObservableType.InfoPanels, RefreshInfoType.PluginsAll);

    }


    public PlugInClient getPlugIn(GGCPluginType pluginType)
    {
        return this.getPlugIn(pluginType.getKey());
    }


    public boolean isPluginAvailable(GGCPluginType pluginType)
    {
        return isPluginAvailable(pluginType.getKey());
    }


    /**
     * Get Hibernate Db
     */
    @Override
    public HibernateDb getHibernateDb()
    {
        return this.m_db;
    }


    // ********************************************************
    // ****** Observer/Observable *****
    // ********************************************************

    @Override
    public void initObserverManager()
    {
        this.observerManager = new ObserverManager(false);
        this.observerManager.initObserverManager( //
            GGCObservableType.InfoPanels, //
            GGCObservableType.Database, //
            GGCObservableType.Status);
    }


    // ********************************************************
    // ****** Language *****
    // ********************************************************

    /**
     * Get Available Languages
     * 
     * @return
     */
    public String[] getAvailableLanguages()
    {
        return this.availableLanguages;
    }


    /**
     * Get Selected Language Index
     * 
     * @return
     */
    public int getSelectedLanguageIndex()
    {
        return this.getLanguageIndex(this.getSettings().getLanguage());
    }


    /**
     * Get Language Index
     * 
     * @param postfix
     * @return
     */
    public int getLanguageIndex(String postfix)
    {
        // System.out.println(postfix);

        for (int i = 0; i < this.avLangPostfix.length; i++)
        {
            if (this.avLangPostfix[i].equals(postfix))
                return i;

        }

        return 0;
    }


    /**
     * Get Language Index By Name
     * 
     * @param name
     * @return
     */
    public int getLanguageIndexByName(String name)
    {
        // stem.out.println(name);

        for (int i = 0; i < this.availableLanguages.length; i++)
        {
            if (this.availableLanguages[i].equals(name))
                return i;
        }

        return 0;
    }


    // ********************************************************
    // ****** BG Measurement Type *****
    // ********************************************************

    /**
     * Depending on the return value of <code>getBGMeasurmentType()</code>,
     * either return the mg/dl or the mmol/l value of the database's value.
     * Default is mg/dl.
     * 
     * @param dbValue The database's value (in float)
     * @return the BG in either mg/dl or mmol/l
     */
    public float getDisplayedBG(float dbValue)
    {
        switch (this.getGlucoseUnitType())
        {
            case mmol_L:
                return this.converters.get("BG").getValueDifferent(Converter_mgdL_mmolL.UNIT_mg_dL, dbValue);

            case mg_dL:
            case None:
            default:
                return dbValue;
        }
    }


    /**
     * Get BG Value
     *
     * @param bgValue BG value
     * @return BG value as float
     */
    public float getBGValue(String bgValue)
    {
        return getFloatValueFromString(bgValue, 0.0f);
    }


    /**
     * Get BG Value
     *
     * @param bgValue BG value
     * @return correct BG values (as string)
     */
    public String getBGValueAsString(String bgValue)
    {
        float f = getFloatValueFromString(bgValue, 0.0f);

        return getBGValueAsString(f);
    }


    /**
     * Get BG Value
     *
     * @param bgValue BG value
     * @return correct BG value (as string)
     */
    public String getBGValueAsString(float bgValue)
    {
        switch (this.getGlucoseUnitType())
        {
            case mmol_L:
                return getDecimalHandler().getDecimalNumberAsString(bgValue, 1);

            case None:
            case mg_dL:
            default:
                return getDecimalHandler().getDecimalNumberAsString(bgValue, 0);
        }
    }


    /**
     * Get BG Value By Type
     *
     * @param outputType Output GlucoseUnitType
     * @param bgValue BG value
     *
     * @return correct BG value
     */
    public float getBGValueByTypeFromDefault(GlucoseUnitType outputType, float bgValue)
    {
        switch (outputType)
        {
            case mmol_L:
                return this.getBGConverter().getValueByType(GlucoseUnitType.mg_dL, GlucoseUnitType.mmol_L, bgValue);

            case mg_dL:
            case None:
            default:
                return bgValue;
        }
    }


    /**
     * Get BG Value By Type
     *
     * @param inputType Input GlucoseUnitType
     * @param outputType Output GlucoseUnitType
     * @param bgValue BG value
     *
     * @return correct BG value
     *
     */
    public Float getBGValueByType(GlucoseUnitType inputType, GlucoseUnitType outputType, Number bgValue)
    {
        return this.getBGConverter().getValueByType(inputType, outputType, bgValue);
    }


    /**
     * Get BG Value Different
     *
     * @param inputType Input GlucoseUnitType
     * @param bgValue BG value
     *
     * @return correct BG value
     */
    public float getBGValueDifferent(GlucoseUnitType inputType, Number bgValue)
    {
        return this.getBGConverter().getValueByType(inputType,
            inputType == GlucoseUnitType.mmol_L ? GlucoseUnitType.mg_dL : GlucoseUnitType.mmol_L, bgValue);
    }


    // ********************************************************
    // ****** Parent handling (for UIs) *****
    // ********************************************************

    /**
     * Set Parent
     * 
     * @param main
     */
    public void setParent(Component main)
    {
        m_main = main;
        if (!dontLoadIcons)
            loadIcons();
    }


    /**
     * Set Parent
     * 
     * @param main
     */
    /*
     * public void setParent(GGCLittle main)
     * {
     * m_main_little = main;
     * }
     */

    /**
     * Get Parent
     */
    @Override
    public Component getParent()
    {
        return m_main;
    }


    /**
     * Get Parent Little
     * 
     * @return 
     */
    /*
     * public GGCLittle getParentLittle()
     * {
     * return m_main_little;
     * }
     */

    /**
     * Utils
     */
    /*
     * @Override
     * public Image getImage(String filename, Component cmp)
     * {
     * Image img;
     * InputStream is = this.getClass().getResourceAsStream(filename);
     * // System.out.println("getImage: " + filename);
     * if (is == null)
     * System.out.println("Error reading image: " + filename);
     * ByteArrayOutputStream baos = new ByteArrayOutputStream();
     * try
     * {
     * int c;
     * while ((c = is.read()) >= 0)
     * baos.write(c);
     * // JDialog.getT
     * // JFrame.getToolkit();
     * img = cmp.getToolkit().createImage(baos.toByteArray());
     * }
     * catch (IOException ex)
     * {
     * ex.printStackTrace();
     * return null;
     * }
     * return img;
     * }
     */

    // ********************************************************
    // ****** Person Id / Login *****
    // ********************************************************

    // /**
    // * Get Current Person Id
    // *
    // * @return
    // */
    // @Override
    // public int getCurrentUserId()
    // {
    // return this.current_user_id;
    // }

    // ********************************************************
    // ****** I18n Utils *****
    // ********************************************************

    /**
     * Get Nutrition I18n Control
     * 
     * @return
     */
    /*
     * public NutriI18nControl getNutriI18nControl()
     * {
     * return this.m_nutri_i18n;
     * }
     */
    // ********************************************************
    // ****** Look and Feel *****
    // ********************************************************

    /*
     * public void loadAvailableLFs() {
     * availableLF_full = new Hashtable<String,String>();
     * UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
     * availableLF = new Object[info.length+1];
     * //ring selectedLF = null; //String subSelectedLF = null;
     * int i; for (i=0; i<info.length; i++) { String name = info[i].getName();
     * String className = info[i].getClassName();
     * availableLF_full.put(name, className); availableLF[i] = name;
     * //System.out.println(humanReadableName); }
     * availableLF_full.put("SkinLF",
     * "com.l2fprod.gui.plaf.skin.SkinLookAndFeel"); availableLF[i] = "SkinLF";
     * }
     * public Object[] getAvailableLFs() { return availableLF; }
     * public static String[] getLFData() { String out[] = new String[2];
     * try { Properties props = new Properties();
     * FileInputStream in = new FileInputStream(pathPrefix +
     * "/data/PIS_Config.properties"); props.load(in);
     * out[0] = (String)props.get("LF_CLASS"); out[1] =
     * (String)props.get("SKINLF_SELECTED");
     * return out;
     * } catch(Exception ex) {
     * System.out.println("DataAccess::getLFData::Exception> " + ex); return
     * null; } }
     */

    /**
     * Get Look & Feel Data
     * 
     * @return
     */
    public static String[] getLFData()
    {
        String out[] = new String[2];

        try
        {
            Properties props = new Properties();

            FileInputStream in = new FileInputStream(userDataDirectory.getUserDataDirectory()
                    + "/GGC_Config.properties");
            props.load(in);

            out[0] = (String) props.get("LF_CLASS");
            out[1] = (String) props.get("SKINLF_SELECTED");

            return out;

        }
        catch (Exception ex)
        {
            System.out.println("DataAccess::getLFData::Exception> " + ex);
            return null;
        }
    }

    // ********************************************************
    // ****** Component Id *****
    // ********************************************************

    private long component_id_last;


    /**
     * Get New Component Id
     * 
     * @return
     */
    @Override
    public String getNewComponentId()
    {
        component_id_last++;
        return "" + this.component_id_last;
    }


    // ********************************************************
    // ****** Options *****
    // ********************************************************

    /**
     * Load Options
     */
    public void loadOptions()
    {
        this.options_yes_no = new String[2];
        this.options_yes_no[0] = m_i18n.getMessage("YES");
        this.options_yes_no[1] = m_i18n.getMessage("NO");
    }


    // ********************************************************
    // ****** Dates and Times Handling *****
    // ********************************************************

    /**
     * Get Start Year
     */
    @Override
    public int getStartYear()
    {
        return 1800;
    }


    /**
     * Load Daily Settings
     * 
     * @param day
     * @param force
     */
    public synchronized void loadDailySettings(GregorianCalendar day, boolean force)
    {
        if (m_db == null || m_db.getLoadStatus() < 2)
            return;

        if (isSameDay(day) && !force)
            return;

        // System.out.println("Reload daily settings (force:" + force + ")");
        LOG.debug("Reload daily settings (force:" + force + ")");

        m_date = day;
        m_HbA1c = m_db.getHbA1c(day, force);
        m_dvalues = m_db.getDayStats(day);

        m_dateStart = (GregorianCalendar) day.clone();
        m_dateStart.add(Calendar.DAY_OF_MONTH, -6);

        m_dRangeValues = m_db.getDayStatsRange(m_dateStart, m_date);
    }


    /**
     * Load Daily Settings (Little)
     * 
     * @param day
     * @param force
     */
    public synchronized void loadDailySettingsLittle(GregorianCalendar day, boolean force)
    {
        if (m_db == null || m_db.getLoadStatus() < 2)
            return;

        if (isSameDay(day) && !force)
            return;

        // System.out.println("(Re)Load daily settings Little - (force:" + force
        // + ")");
        LOG.debug("(Re)Load daily settings Little - (force:" + force + ")");

        m_date = day;
        // m_HbA1c = m_db.getHbA1c(day);
        m_dvalues = m_db.getDayStats(day);

        // m_dateStart = (GregorianCalendar) day.clone();
        // m_dateStart.add(GregorianCalendar.DAY_OF_MONTH, -6);
        // m_dateEnd = day;

        // m_dRangeValues = m_db.getDayStatsRange(m_dateStart, m_date);
    }


    /**
     * Get HbA1c
     * 
     * @param day
     * @return
     */
    public HbA1cValues getHbA1c(GregorianCalendar day)
    {
        // System.out.println("DA::getHbA1c");
        // if (!isSameDay(day))
        loadDailySettings(day, false);

        // if (m_HbA1c==null)
        // return new HbA1cValues();
        // else
        return m_HbA1c;
    }


    /**
     * Get Day Stats
     * 
     * @param day
     * @return
     */
    public DailyValues getDayStats(GregorianCalendar day)
    {
        // System.out.println("DA::getDayStats");

        // if (!isSameDay(day))
        loadDailySettings(day, false);

        return m_dvalues;
    }


    /**
     * Get Day Stats Range
     * 
     * @param start
     * @param end
     * @return
     */
    public WeeklyValues getDayStatsRange(GregorianCalendar start, GregorianCalendar end)
    {
        // System.out.println("DA::getDayStatsRange");

        // we load dialy if not loaded
        if (this.m_date == null)
        {
            loadDailySettings(end, false);
        }

        if (isSameDay(start, this.m_dateStart) && isSameDay(m_date, end))
            // System.out.println("Same day");
            return m_dRangeValues;
        else
            // System.out.println("other range");
            return m_db.getDayStatsRange(start, end);
    }


    /**
     * Is Same Day (if we compare current day with day we got stats for main display from)
     * 
     * @param gc
     * @return
     */
    public boolean isSameDay(GregorianCalendar gc)
    {
        return isSameDay(m_date, gc);
    }


    /**
     * Is Database Initialized
     * 
     * @return
     */
    public boolean isDatabaseInitialized()
    {
        if (m_db == null || m_db.getLoadStatus() < 2)
            return false;
        else
            return true;
    }


    /**
     * Is Same Day (GregorianCalendars)
     * 
     * @param gc1
     * @param gc2 
     * @return
     */
    public boolean isSameDay(GregorianCalendar gc1, GregorianCalendar gc2)
    {

        if (gc1 == null || gc2 == null)
            return false;
        else
        {

            if (gc1.get(Calendar.DAY_OF_MONTH) == gc2.get(Calendar.DAY_OF_MONTH)
                    && gc1.get(Calendar.MONTH) == gc2.get(Calendar.MONTH)
                    && gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR))
                return true;
            else
                return false;

        }
    }


    /**
     * Start Internal Web Server
     */
    public void startWebServer() {
        try
        {
            log.info("Start internal Web Server");

            if (web_server!=null) {
                log.info(" ... Web Server is already running !");
                return;
            }

            PropertiesFile propertiesFile = new PropertiesFile("WebLister.properties", true, true);
            propertiesFile.readFile();

            // TODO use internal configuration

//            String[] cnf = { "-config", //
//                            userDataDirectory.getParsedUserDataPath("%USER_DATA_DIR%/tools/WebLister.properties") };

            if (propertiesFile.wasFileRead()) {
                //log.debug("Loaded properies: {}", gson.toJson(propertiesFile.getProperties()));
                web_server = new Server(propertiesFile.getProperties());
                web_server.start();
            } else {
                log.warn("Configuration not found. WebServer not started.");
            }

            // pygmy.core.Server

        }
        catch (Exception ex)
        {
            System.out.println("Error starting WebServer on 4444. Ex: " + ex);
        }

    }


    @Override
    protected void initDataDefinitionManager()
    {
        // TODO
        // addDisplayManagerEntry(StockSubTypeDto.class,
        // "STOCK_TYPE,NAME,DESCRIPTION", "20,40,40", "", 1, "");

        // OK
        addDisplayManagerEntry(SettingsH.class, //
            GGCSelectorConfiguration.None, GGCDatabaseTableConfiguration.SettingsH);

        addDisplayManagerEntry(ColorSchemeH.class, //
            GGCSelectorConfiguration.None, GGCDatabaseTableConfiguration.ColorSchemeH);

        // DOCTOR / APPOINTMENT
        addDisplayManagerEntry(DoctorH.class, //
            GGCSelectorConfiguration.DoctorH, GGCDatabaseTableConfiguration.DoctorH);

        addDisplayManagerEntry(DoctorTypeH.class, //
            GGCSelectorConfiguration.DoctorTypeH, GGCDatabaseTableConfiguration.DoctorTypeH);

        addDisplayManagerEntry(DoctorAppointmentH.class, //
            GGCSelectorConfiguration.DoctorAppointmentH, GGCDatabaseTableConfiguration.DoctorAppointmentH);

        // FIXME - INVENTORY Work in Progress (no db support yet)
        addDisplayManagerEntry(InventoryH.class, //
            GGCSelectorConfiguration.InventoryH, null);

        addDisplayManagerEntry(InventoryItemH.class, //
            GGCSelectorConfiguration.InventoryItemH, null);

        addDisplayManagerEntry(InventoryItemTypeH.class, //
            GGCSelectorConfiguration.InventoryItemTypeH, null);

        // // nutrition
        // FoodGroupH(1, FoodGroupH.class, //
        // "id, name, name_i18n, description"), //
        // FoodDescriptionH(1, FoodDescriptionH.class, //
        // "id, group_id, name, name_i18n, refuse, nutritions, home_weights"),
        // //
        // FoodUserGroupH(1, FoodUserGroupH.class, //
        // "id; name; name_i18n; description; parent_id; changed"), //
        // FoodUserDescriptionH(1, FoodUserDescriptionH.class, //
        // "id; name; name_i18n; group_id; refuse; description; home_weights;
        // nutritions; changed"), //
        // MealH(1, MealH.class, //
        // "id; name; name_i18n; group_id; description; parts; nutritions;
        // extended; comment; changed"), //
        // MealGroupH(1, MealGroupH.class, //
        // "id; name; name_i18n; description; parent_id; changed"), //
        // NutritionDefinitionH(1, NutritionDefinitionH.class, //
        // "id; weight_unit; tag; name; decimal_places; static_entry"), //
        // NutritionHomeWeightTypeH(1, NutritionHomeWeightTypeH.class, //
        // "id; name; static_entry"), //

    }


    @Override
    protected void initInternalSettings()
    {
        // FIXME
        this.internalSetting.put(InternalSetting.Help_Settings_UserAddEdit, "unknownKey");
        // TranslationTool

    }


    /**
     * Console message Not Implemented
     * @param source
     */
    public static void notImplemented(String source)
    {
        System.out.println("Not Implemented: " + source);
    }


    /**
     * Load Special Parameters
     * 
     * @see com.atech.utils.ATDataAccessAbstract#loadSpecialParameters()
     */
    @Override
    public void loadSpecialParameters()
    {
        this.glucoseUnitType = this.configurationManagerWrapper.getGlucoseUnit();

        this.special_parameters = new Hashtable<String, String>();
        this.special_parameters.put("BG", "" + this.getGlucoseUnitType().getCode());
        // this.m_BG_unit = this.m_settings.getBG_unit();
    }


    /**
     * This method is intended to load additional Language info. Either special langauge configuration
     * or special data required for real Locale handling.
     */
    @Override
    public void loadLanguageInfo()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Insulin - Pen/Injection 
     */
    public static final int INSULIN_PEN_INJECTION = 0;

    /**
     * Insulin - Pump 
     */
    public static final int INSULIN_PUMP = 1;


    /**
     * @param mode
     * @param value
     * @return
     */
    // public float getCorrectDecimalValueForInsulinFloat(int mode, float value)
    // {
    // // 1, 0.5, 0.1
    // // 1, 0.5, 0.1, 0.05, 0.01, 0.005, 0.001
    // return value;
    // }

    /**
     * @param mode
     * @param value
     * @return
     */
    // public String getCorrectDecimalValueForInsulinString(int mode, float
    // value)
    // {
    // // 1, 0.5, 0.1
    // // return value;
    // return null;
    // }

    /** 
     * Load PlugIns
     */
    @Override
    public void loadPlugIns()
    {
    }

    /**
     * Insulin Dose: Basal
     */
    public static final int INSULIN_DOSE_BASAL = 1;

    /**
     * Insulin Dose: Bolus
     */
    public static final int INSULIN_DOSE_BOLUS = 2;


    /**
     * Get Insulin Precision
     * 
     * @param mode
     * @param type
     * @return
     */
    public float getInsulinPrecision(int mode, int type)
    {
        if (mode == INSULIN_PEN_INJECTION)
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getFloatValue("PEN_BASAL_PRECISSION");
            else
                return this.m_cfgMgr.getFloatValue("PEN_BOLUS_PRECISSION");
        }
        else
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getFloatValue("PUMP_BASAL_PRECISSION");
            else
                return this.m_cfgMgr.getFloatValue("PUMP_BOLUS_PRECISSION");
        }

    }


    /**
     * Get Insulin Precision String
     * 
     * @param mode
     * @param type
     * @return
     */
    public String getInsulinPrecisionString(int mode, int type)
    {
        if (mode == INSULIN_PEN_INJECTION)
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getStringValue("PEN_BASAL_PRECISSION");
            else
                return this.m_cfgMgr.getStringValue("PEN_BOLUS_PRECISSION");
        }
        else
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getStringValue("PUMP_BASAL_PRECISSION");
            else
                return this.m_cfgMgr.getStringValue("PUMP_BOLUS_PRECISSION");
        }

    }


    /**
     * Reformat Insulin Amount To CorrectValue
     * 
     * @param mode
     * @param type
     * @param input_val
     * @return
     */
    public double reformatInsulinAmountToCorrectValue(int mode, int type, float input_val)
    {
        String prec = getInsulinPrecisionString(mode, type);

        // System.out.println("Precision: " + prec);

        return Rounding.specialRounding(input_val, prec);
    }


    /**
     * Reformat Insulin Amount To CorrectValue String
     * 
     * @param mode
     * @param type
     * @param input_val
     * @return
     */
    public String reformatInsulinAmountToCorrectValueString(int mode, int type, float input_val)
    {
        String prec = getInsulinPrecisionString(mode, type);
        return Rounding.specialRoundingString(input_val, prec);
    }


    /**
     * Get Max Values
     * 
     * @param mode
     * @param type
     * @return
     */
    public float getMaxValues(int mode, int type)
    {
        if (mode == INSULIN_PEN_INJECTION)
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getFloatValue("PEN_MAX_BASAL");
            else
                return this.m_cfgMgr.getFloatValue("PEN_MAX_BOLUS");
        }
        else
        {
            if (type == INSULIN_DOSE_BASAL)
                return this.m_cfgMgr.getFloatValue("PUMP_MAX_BASAL");
            else
                return this.m_cfgMgr.getFloatValue("PUMP_MAX_BOLUS");
        }

    }


    /**
     * For misc tests
     */
    public void doTest()
    {

        /*
         * //ColorUIResource cui = (ColorUIResource) UIManager.getLookAndFeel()
         * //.getDefaults().get("textText");
         * UIDefaults dd = UIManager.getLookAndFeel().getDefaults();
         * System.out.println(dd);
         * for(Enumeration en = dd.keys(); en.hasMoreElements(); )
         * {
         * String key = (String)en.nextElement();
         * if (key.contains("Table"))
         * System.out.println(key);
         * }
         * //ComponentUI cui = dd.getUI(new JTable());
         * Color c = dd.getColor("TableHeader.background");
         * c = Color.blue;
         */
    }


    /**
     * Get Max Decimals that will be used by DecimalHandler
     * 
     * @return
     */
    @Override
    public int getMaxDecimalsUsedByDecimalHandler()
    {
        return 3;
    }


    @Override
    public void loadExtendedHandlers()
    {
        this.addExtendedHandler(DataAccess.EXTENDED_HANDLER_DailyValuesRow, new ExtendedDailyValueHandler());
    }


    @Override
    public void loadConverters()
    {
        this.converters.put("BG", new Converter_mgdL_mmolL());
    }


    /**
     * Get BG Converter
     * 
     * @return
     */
    public Converter_mgdL_mmolL getBGConverter()
    {
        return (Converter_mgdL_mmolL) this.getConverter("BG");

    }


    public GGCGraphContext getGraphContext()
    {
        return graphContext;
    }


    public GlucoseUnitType getGlucoseUnitType()
    {
        return glucoseUnitType;
    }


    public void setGlucoseUnitType(GlucoseUnitType glucoseUnitType)
    {
        this.glucoseUnitType = glucoseUnitType;
    }


    // Configuration Manager Wrapper

    public ConfigurationManagerWrapper getConfigurationManagerWrapper()
    {
        return configurationManagerWrapper;
    }


    public void setConfigurationManagerWrapper(ConfigurationManagerWrapper configurationManagerWrapper)
    {
        this.configurationManagerWrapper = configurationManagerWrapper;
    }


    // Saving/ Loading sizes

    // DialogSizePersistInterface

    public void loadWindowSize(DialogSizePersistInterface dialogSizePersistInterface)
    {
        Dimension defaultDimension = dialogSizePersistInterface.getDefaultSize();

        Dimension minimalDimemsion = dialogSizePersistInterface.getMinimalSize();

        Dimension newDimension = this.configurationManagerWrapper.getDimensionFromParameter(
            dialogSizePersistInterface.getSettingKey(), defaultDimension.width, defaultDimension.height);

        double height = newDimension.getHeight();
        double width = newDimension.getWidth();

        if (height < minimalDimemsion.getHeight())
        {
            height = minimalDimemsion.getHeight();
        }

        if (width < minimalDimemsion.getWidth())
        {
            width = minimalDimemsion.getWidth();
        }

        dialogSizePersistInterface.getContainer().setSize((int) width, (int) height);
    }


    public void saveWindowSize(DialogSizePersistInterface dialogSizePersistInterface)
    {
        Dimension dimension = dialogSizePersistInterface.getContainer().getSize();

        this.configurationManagerWrapper.setDimensionToParameter(dialogSizePersistInterface.getSettingKey(), dimension);
    }


    public void loadUserTypes()
    {
        this.user_types = new String[5];

        this.user_types[0] = this.m_i18n.getMessage("SELECT");
        this.user_types[1] = m_i18n.getMessage("USER_NORMAL");
    }


    @Override
    public TranslationToolConfigurationDto getTranslationToolConfiguration()
    {
        TranslationToolConfigurationDto translationToolConfigurationDto = new TranslationToolConfigurationDto();

        translationToolConfigurationDto.setMainModule(GGCLanguageModule.Core.getMainModule());
        translationToolConfigurationDto.setModules(GGCLanguageModule.Core.getAllModules());
        translationToolConfigurationDto.setMasterLanguage(GGCSupportedLanguages.English.getMasterLanguage());
        translationToolConfigurationDto.setSupportedLanguages(GGCSupportedLanguages.English.getLanguagesToTranslate());

        return translationToolConfigurationDto;
    }


    @Override
    public void saveTranslationToolConfiguration(TranslationToolConfigurationDto configuration)
    {

    }





}
