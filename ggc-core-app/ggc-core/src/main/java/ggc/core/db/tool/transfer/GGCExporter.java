package ggc.core.db.tool.transfer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import com.atech.data.mng.DataDefinitionEntry;
import com.atech.db.hibernate.HibernateBackupSelectableObject;
import com.atech.db.hibernate.HibernateConfiguration;
import com.atech.db.hibernate.tool.data.management.common.ImportExportStatusType;
import com.atech.db.hibernate.transfer.BackupRestoreObject;
import com.atech.db.hibernate.transfer.BackupRestoreWorkGiver;
import com.atech.db.hibernate.transfer.ExportTool;
import com.atech.plugin.PlugInClient;

import ggc.core.db.GGCDb;
import ggc.core.db.datalayer.DailyValue;
import ggc.core.db.datalayer.SettingsColorScheme;
import ggc.core.db.hibernate.pen.DayValueH;
import ggc.core.db.hibernate.settings.ColorSchemeH;
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
 *  Filename:     GGCExporter
 *  Description:  GGC Exporter (this is exporter, which will be used for all exports,
 *                when all objects will be fully implemented)
 * 
 *  Author: andyrozman {andy@atech-software.com}  
 */

public class GGCExporter extends ExportTool implements Runnable
{

    DataAccess da = DataAccess.getInstance();


    /**
     * Constructor
     * 
     * @param giver
     */
    public GGCExporter(BackupRestoreWorkGiver giver)
    {
        super(DataAccess.getInstance().getDb().getHibernateConfiguration());

        checkPrerequisitesForAutoBackup();

        this.setStatusReceiver(giver);
        this.setTypeOfStatus(ImportExportStatusType.Special);

        // exportAll();
    }


    /**
     * Constructor
     * 
     * @param cfg
     */
    public GGCExporter(HibernateConfiguration cfg)
    {
        super(cfg);

        this.setTypeOfStatus(ImportExportStatusType.Dot);

        checkPrerequisites();
        exportAll();
    }


    private void checkPrerequisites()
    {
        File f = new File("../data");

        if (!f.exists())
        {
            f.mkdir();
        }

        f = new File("../data/export");

        if (!f.exists())
        {
            f.mkdir();
        }

        this.setRootPath("../data/export/");
        this.setFileLastPart("_" + getCurrentDateForFile());
    }


    private void checkPrerequisitesForAutoBackup()
    {
        File f = new File("../data");

        if (!f.exists())
        {
            f.mkdir();
        }

        f = new File("../data/export");

        if (!f.exists())
        {
            f.mkdir();
        }

        f = new File("../data/export/tmp");

        if (!f.exists())
        {
            f.mkdir();
        }

        this.setRootPath("../data/export/tmp/");
        this.setFileLastPart("");
    }


    private void exportAll()
    {
        System.out.println("export: all");

        if (this.backup_object != null)
        {
            exportData(this.getBackupRestoreObject(this.backup_object));
            this.backup_object = null;
        }
        // export(_DailyValues();
    }


    /*
     * private void sleep(long ms) { try { Thread.sleep(ms); } catch(Exception
     * ex) {
     * } }
     */

    /**
     * Get Active Session
     */
    @Override
    public int getActiveSession()
    {
        return 2;
    }


    private BackupRestoreObject getBackupRestoreObject(String class_name)
    {
        // core
        if (class_name.equals("ggc.core.db.hibernate.pen.DayValueH"))
            return new DailyValue();
        else if (class_name.equals("ggc.core.db.hibernate.settings.ColorSchemeH"))
            return new SettingsColorScheme();

        for (PlugInClient pic : da.getPlugins().values())
        {
            //String key = en.nextElement();
            //PlugInClient pic = da.getPlugIn(key);

            if (pic.getBackupRestoreHandler().doesContainBackupRestoreObject(class_name))
                return pic.getBackupRestoreHandler().getBackupRestoreObject(class_name);
        }

        return null;
    }


    private BackupRestoreObject getBackupRestoreObject(Object obj, BackupRestoreObject bro)
    {
        if (bro.getBackupClassName().equals("ggc.core.db.hibernate.pen.DayValueH"))
        {
            DayValueH eh = (DayValueH) obj;
            return new DailyValue(eh);
        }
        else if (bro.getBackupClassName().equals("ggc.core.db.hibernate.settings.ColorSchemeH"))
        {
            ColorSchemeH eh = (ColorSchemeH) obj;
            return new SettingsColorScheme(eh);
        }

        for (PlugInClient pic : da.getPlugins().values())
        {
            if (pic.getBackupRestoreHandler().doesContainBackupRestoreObject(bro.getBackupClassName()))
                return pic.getBackupRestoreHandler().getBackupRestoreObject(obj, bro);
        }

        return null;

    }


    /**
     * Export Data (object name)
     * 
     * @param name
     */
    public void exportData(String name)
    {
        exportData(this.getBackupRestoreObject(name));
    }


    // /**
    // * Export Data (clazz)
    // *
    // * @param clazz
    // */
    // public <E extends HibernateBackupSelectableObject> void
    // exportData(Class<E> clazz)
    // {
    // exportData(clazz, true);
    // }

    /**
     * Export Data (object)
     * 
     * @param bro BackupRestoreObject
     */
    public void exportData(BackupRestoreObject bro)
    {
        // System.out.println("export: first");

        // System.out.println("BRO: " + bro);

        openFile(this.getRootPath() + bro.getBackupFile() + this.getFileLastPart() + ".dbe");

        writeHeader(bro, GGCDb.CURRENT_DB_VERSION);

        Session sess = getSession();

        Query q = sess.createQuery("select grp from " + bro.getClassName() + " as grp order by grp.id asc");

        this.statusSetMaxEntry(q.list().size());

        Iterator<?> it = q.iterate();

        int dot_mark = 5;
        int count = 0;

        while (it.hasNext())
        {
            // System.out.println("export: next");
            BackupRestoreObject bt = getBackupRestoreObject(it.next(), bro);

            // DayValueH eh = (DayValueH) it.next();

            this.writeToFile(bt);

            /*
             * this.writeToFile(eh.getId() + "|" + eh.getDtInfo() + "|" +
             * eh.getBg() + "|" + eh.getIns1() + "|"
             * + eh.getIns2() + "|" + eh.getCh() + "|" + eh.getMealsIds() + "|"
             * + eh.getExtended() + "|"
             * + eh.getPersonId() + "|" + eh.getComment() + "|" +
             * eh.getChanged() + "\n");
             */
            // sleep(25);
            count++;
            this.writeStatus(dot_mark, count);
        }

        closeFile();
    }


    /**
     * Export Data (object)
     *
     */
    public <E extends HibernateBackupSelectableObject> void exportData(Class<E> clazz,
            DataDefinitionEntry defintionEntry)
    {
        // System.out.println("export: first");

        // System.out.println("BRO: " + bro);

        openFile(this.getRootPath() + clazz.getSimpleName() + this.getFileLastPart() + ".dbe");

        writeHeader(defintionEntry, GGCDb.CURRENT_DB_VERSION);

        Session sess = getSession();

        Criteria criteria = getSession().createCriteria(clazz).addOrder(Order.asc("id"));

        List resultsData = criteria.list();

        this.statusSetMaxEntry(resultsData.size());

        Iterator it = resultsData.iterator();

        int dot_mark = 5;
        int count = 0;

        while (it.hasNext())
        {
            // System.out.println("export: next");
            HibernateBackupSelectableObject bt = (E) it.next();

            // DayValueH eh = (DayValueH) it.next();

            this.writeToFile(bt);

            count++;
            this.writeStatus(dot_mark, count);
        }

        closeFile();
    }

    boolean running = true;
    String backup_object = null;


    /**
     * Set Backup Object
     * 
     * @param name
     */
    public void setBackupObject(String name)
    {
        this.backup_object = name;
    }


    /**
     * Run for Thread
     */
    public void run()
    {
        while (running)
        {
            exportAll();
            try
            {
                Thread.sleep(2000);
            }
            catch (Exception ex)
            {}
        }
    }


    /**
     * Stop Thread
     */
    public void stopThread()
    {
        this.running = false;
    }


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        GGCDb db = new GGCDb();
        db.initDb();
        new ExportDailyValues(db.getHibernateConfiguration());
    }

}
