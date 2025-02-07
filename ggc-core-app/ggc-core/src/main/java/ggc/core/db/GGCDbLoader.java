package ggc.core.db;

import java.io.File;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atech.graphics.observe.ObserverManager;

import ggc.core.data.defs.DatabaseStatusType;
import ggc.core.data.defs.GGCObservableType;
import ggc.core.data.defs.RefreshInfoType;
import ggc.core.plugins.GGCPluginType;
import ggc.core.plugins.NutriPlugIn;
import ggc.core.util.DataAccess;

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
 *  Filename:     GGCDbLoader  
 *  Description:  This is GGCDb Loader. It help system to load all needed data for 
 *                GGC Database Session.
 * 
 *  Author: andyrozman {andy@atech-software.com}  
 */

public class GGCDbLoader extends Thread
{

    // red status
    // 1 - init
    // yellow
    // 2 - load configuration
    // 3 - load statistics for display, apointments
    // blue
    // 4 - load doctors data
    // 5 - load nutrition(1) root data
    // 6 - load nutrition(2) root data
    // 7 - load meals root data

    // 99 - loading complete
    // green

    /**
     * Db Status: Not started
     */
    public static final int DB_NOT_STARTED = 0;
    /**
     * Db Status: Init done
     */
    public static final int DB_INIT_DONE = 1;
    /**
     * Db Status: Base data loaded
     */
    public static final int DB_DATA_BASE = 2;

    // public boolean part_start = true;
    /**
     * Db Status: Data from plugins loaded
     */
    public static final int DB_DATA_PLUGINS = 3;

    // public boolean debug = false;
    /**
     * Db Status: Db Initialization done - Load completed
     */
    public static final int DB_INIT_FINISHED = 4;

    private static final Logger LOG = LoggerFactory.getLogger(GGCDbLoader.class);
    /**
     * Part start. When this is enables, we don't load Food, Doctor's data
     */
    public boolean part_start = false;
    DataAccess m_da = null;
    // StatusBar m_bar = null;
    // StatusBarL m_barL = null;
    // private boolean real_run = false;
    private boolean run_once = false;

    private ObserverManager observerManager;


    /**
     * Constructor
     * 
     * @param da
     */
    public GGCDbLoader(DataAccess da)
    {
        m_da = da;
    }


    /**
     * Run (Thread)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run()
    {

        if (run_once)
            return;

        run_once = true;

        observerManager = m_da.getObserverManager();

        if (new File("../data/debug_no_food.txt").exists())
        {
            part_start = true;
        }

        boolean isUserManagementEnabled = m_da.getUserManagement().isEnabled();

        if (isUserManagementEnabled)
        {
            boolean autoLogin = m_da.getUserManagement().isAutoLoginEnabled();
        }

        isUserManagementEnabled = false;

        // 1 - init

        long start_time = System.currentTimeMillis();

        GGCDb db = new GGCDb(m_da);

        m_da.setDb(db);

        try
        {

            observerManager.setChangeOnEventSource(GGCObservableType.Status,
                "DB_NAME=" + db.getHibernateConfiguration().getConnectionName());

            db.initDb();

            setDbStatus(DatabaseStatusType.InitDone);
            m_da.setDbLoadingStatus(GGCDbLoader.DB_INIT_DONE);

            // 2 - load configuration
            if (isUserManagementEnabled)
            {
                // check if autologin
            }
            else
            {
                db.loadConfigData();
            }
            db.loadStaticData();
            // dataAccess.setDb(db);
            m_da.setDbLoadingStatus(GGCDbLoader.DB_DATA_BASE);

            observerManager.setChangeOnEventSource(GGCObservableType.InfoPanels, RefreshInfoType.GeneralInfo);

            // 3 - init plugins
            m_da.initPlugIns();

            // m_da.setChangeOnEventSource(DataAccess.OBSERVABLE_PANELS,
            // RefreshInfo.PANEL_GROUP_PLUGINS_ALL);
            observerManager.setChangeOnEventSource(GGCObservableType.InfoPanels, RefreshInfoType.DevicesConfiguration);

            // 4 - load daily data for display, appointments

            if (m_da.getParent() != null)
            {
                m_da.loadDailySettings(new GregorianCalendar(), true);
            }
            else
            {
                m_da.loadDailySettingsLittle(new GregorianCalendar(), true);
            }

            // dataAccess.loadSettingsFromDb();
            // m_da.setChangeOnEventSource(DataAccess.OBSERVABLE_PANELS,
            // RefreshInfo.PANEL_GROUP_ALL_DATA);
            observerManager.setChangeOnEventSource(GGCObservableType.InfoPanels, RefreshInfoType.DeviceDataAll);
            // m_da.setChangeOnEventSource(DataAccess.OBSERVABLE_STATUS,
            // m_da.getI18nControlInstance().getMessage("READY"));
            observerManager.setChangeOnEventSource(GGCObservableType.Status,
                m_da.getI18nControlInstance().getMessage("READY"));

            // mf.informationPanel.refreshPanels();
            // mf.statusPanel.setStatusMessage(dataAccess.getI18nControlInstance().getMessage("READY"));

            /*
             * if (dataAccess.getParent()!=null)
             * {
             * // GGC
             * MainFrame mf = dataAccess.getParent();
             * //mf.setDbActions(true);
             * dataAccess.loadSettingsFromDb();
             * mf.informationPanel.refreshPanels();
             * mf.statusPanel.setStatusMessage(dataAccess.getI18nControlInstance
             * ().
             * getMessage
             * ("READY"));
             * }
             * else
             * {
             * /// GGC Little
             * !! mf = dataAccess.getParentLittle();
             * //mf.setDbActions(true);
             * dataAccess.loadSettingsFromDb();
             * mf.getInformationPanel().dailyStats.getTableModel().
             * setDailyValues(
             * dataAccess
             * .getDayStats(new GregorianCalendar()));
             * mf.getInformationPanel().refreshPanels();
             * mf.getStatusPanel().setStatusMessage(dataAccess.
             * getI18nControlInstance
             * ().
             * getMessage("READY"));
             * }
             */
            setDbStatus(DatabaseStatusType.BaseDone);

            // 5 - Load plugin data

            if (m_da.isPluginAvailable(GGCPluginType.NutritionToolPlugin))
            {
                m_da.getPlugIn(GGCPluginType.NutritionToolPlugin).executeCommand(NutriPlugIn.COMMAND_LOAD_DATABASE);
            }
            m_da.setDbLoadingStatus(GGCDbLoader.DB_DATA_PLUGINS);
            setDbStatus(DatabaseStatusType.Loaded);

            // refreshMenus();

            // if (!part_start)
            {
                /*
                 * // 4 - load doctors data
                 * // TODO: in version 0.4
                 * // 5 - load nutrition(1) root data
                 * db.loadNutritionDbBase();
                 * db.loadNutritionDb1();
                 * // 6 - load nutrition(2) root data
                 * db.loadNutritionDb2();
                 * // 7 - load meals root data
                 * db.loadMealsDb();
                 * setDbStatus(RefreshInfo.DB_LOADED);
                 */
            }
            /*
             * else
             * {
             * db.loadNutritionDbBase();
             * setDbStatus(StatusBar.DB_LOADED);
             * }
             */

            /*
             * if (part_start)
             * {
             * db.loadNutritionDb1();
             * db.loadNutritionDb2();
             * db.loadMealsDb();
             * }
             * else
             * {
             * db.loadConfigData();
             * db.loadStaticData();
             * db.loadNutritionDb1();
             * // db.loadImplementedMeterData();
             * }
             */

            m_da.setDbLoadingStatus(GGCDbLoader.DB_INIT_FINISHED);
            m_da.runAfterDbLoad();

            long dif = System.currentTimeMillis() - start_time;

            // System.out.println("We needed " + (dif/1000) +
            // " seconds to startup.");
            LOG.debug("We needed " + dif / 1000 + " seconds to startup.");

        }
        catch (Exception ex)
        {
            LOG.error("Error loading database. " + ex.getMessage(), ex);
            observerManager.setChangeOnEventSource(GGCObservableType.Database, ex);
        }
    }


    /**
     * Set Db Status
     * 
     * @param status
     */
    public void setDbStatus(DatabaseStatusType status)
    {
        // if (part_start)
        // return;
        // dataAccess.setDbLoadingStatus(status);

        // m_da.setChangeOnEventSource(DataAccess.OBSERVABLE_STATUS, status);
        this.observerManager.setChangeOnEventSource(GGCObservableType.Status, status);
    }

}
