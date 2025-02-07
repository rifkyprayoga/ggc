package ggc.connect.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import ggc.connect.data.ConnectDataHandler;
import ggc.connect.db.GGCConnectDb;
import ggc.connect.defs.ConnectPluginDefinition;
import ggc.connect.enums.ConnectOperationType;
import ggc.plugin.util.DataAccessPlugInBase;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       CGMS Tool (support for CGMS devices)
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
 *  Filename:     DataAccessCGMS  
 *  Description:  Singelton class containing all data used through plugin
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public class DataAccessConnect extends DataAccessPlugInBase
{

    // private static final String EXTENDED_HANDLER_CGMSValuesExtendedEntry =
    // "CGMSValuesExtendedEntry";

    private static DataAccessConnect s_da = null; // This is handle to unique

    // private CGMSManager m_cgms_manager = null;

    /**
     * Value Type
     */
    public static String[] value_type = null;


    // ********************************************************
    // ****** Constructors and Access methods *****
    // ********************************************************

    /**
     *
     *  This is DataAccess constructor; Since classes use Singleton Pattern,
     *  constructor is protected and can be accessed only with getInstance() 
     *  method.<br><br>
     *
     * @param cgmsPluginDefinition
     */
    private DataAccessConnect(ConnectPluginDefinition cgmsPluginDefinition)
    {
        super(cgmsPluginDefinition);
    }


    /** 
     * Init Special - All methods that we support should be called here
     */
    @Override
    public void initSpecial()
    {
        // this.createWebListerContext();
        // this.createPlugInAboutContext();
        this.createConfigurationContext();
        loadDeviceDataHandler();
        // loadManager();
        // loadReadingStatuses();
        this.createPlugInDataRetrievalContext();
        this.createDeviceConfiguration();
        this.createOldDataReader();

        this.loadConverters();

        this.prepareTranslationForEnums();
        this.prepareGraphContext();
        
    }


    private void prepareTranslationForEnums()
    {
        ConnectOperationType.translateKeywords(this.i18n);
    }


    /**
     *
     *  This method returns reference to DataAccessCGM object created, or if no 
     *  object was created yet, it creates one.<br><br>
     *
     *  @return Reference to DataAccessCGM instance
     */
    public static DataAccessConnect getInstance()
    {
        // if (s_da == null)
        // s_da = new DataAccessCGM();
        return s_da;
    }


    /**
     * Create Instance
     * 
     * @param connectPluginDefinition
     * @return
     */
    public static DataAccessConnect createInstance(ConnectPluginDefinition connectPluginDefinition)
    {
        if (s_da == null)
        {
            s_da = new DataAccessConnect(connectPluginDefinition);
        }
        return s_da;
    }


    // Method: deleteInstance
    /**
     *  This method sets handle to DataAccess to null and deletes the instance. <br><br>
     */
    public void deleteInstance()
    {
        m_i18n = null;
    }

    // ********************************************************
    // ****** Abstract Methods *****
    // ********************************************************


    // ********************************************************
    // ****** Manager *****
    // ********************************************************

    /**
     * Get Device Manager
     * 
     * @return
     */
    // public CGMSManager getCGMManager()
    // {
    // return this.m_cgms_manager;
    // }

    // ********************************************************
    // ****** Parent handling (for UIs) *****
    // ********************************************************

    // ********************************************************
    // ****** Dates and Times Handling *****
    // ********************************************************

    @Override
    public String getCurrentDateString()
    {
        GregorianCalendar gc = new GregorianCalendar();
        return gc.get(Calendar.DAY_OF_MONTH) + "." + (gc.get(Calendar.MONTH) + 1) + "." + gc.get(Calendar.YEAR);
    }

    // ********************************************************
    // ****** Database *****
    // ********************************************************

    GGCConnectDb m_db;


    /**
     * Create Custom Db
     * 
     * This is for plug-in specific database implementation
     */
    @Override
    public void createCustomDb()
    {
        this.m_db = new GGCConnectDb(this.hibernateDb);
        this.pluginDb = this.m_db;
    }


    /**
     * Get Db
     * 
     * @return
     */
    public GGCConnectDb getDb()
    {
        return this.m_db;
    }


    // ********************************************************
    // ****** Configuration *****
    // ********************************************************

    /**
     * Create Configuration Context for plugin
     */
    @Override
    public void createConfigurationContext()
    {
        // this.device_config_def = new CGMSConfigurationDefinition();
    }


    /**
     * Create Device Configuration for plugin
     */
    @Override
    public void createDeviceConfiguration()
    {
        // this.device_config = new DeviceConfiguration(this);
    }

    // ********************************************************
    // ****** About Methods *****
    // ********************************************************

    // ********************************************************
    // ****** Web Lister Methods *****
    // ********************************************************


    /**
     * Create About Context for plugin
     */
    @Override
    public void createPlugInDataRetrievalContext()
    {
        this.data_download_screen_wide = true;
    }


    /**
     * Load Manager instance
     */
    @Override
    public void loadManager()
    {
        // this.m_cgms_manager = CGMSManager.getInstance(this);
        // this.m_manager = this.m_cgms_manager;
    }


    /**
     * Load Device Data Handler
     */
    @Override
    public void loadDeviceDataHandler()
    {
        this.deviceDataHandler = new ConnectDataHandler(this);
    }


    @Override
    public void loadExtendedHandlers()
    {
    }


    /**
     * Get Images for Devices
     * 
     * @see ggc.plugin.util.DataAccessPlugInBase#getDeviceImagesRoot()
     * @return String with images path 
     */
    @Override
    public String getDeviceImagesRoot()
    {
        return "/icons/connect/";
    }


    /**
     * Create Old Data Reader
     */
    @Override
    public void createOldDataReader()
    {
    }


    /**
     * Load Special Parameters
     * 
     * @see com.atech.utils.ATDataAccessAbstract#loadSpecialParameters()
     */
    @Override
    public void loadSpecialParameters()
    {
        this.special_parameters = new Hashtable<String, String>();
        this.special_parameters.put("BG", "" + this.getGlucoseUnitType());
    }


    @Override
    public void prepareGraphContext()
    {
    }

}
