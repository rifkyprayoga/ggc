package ggc.cgms.plugin;

import com.atech.db.hibernate.transfer.BackupRestoreObject;
import com.atech.db.hibernate.transfer.BackupRestoreRunner;
import com.atech.i18n.I18nControlAbstract;
import com.atech.plugin.BackupRestorePlugin;

import ggc.cgms.data.db.CGMSData;
import ggc.cgms.data.db.CGMSDataExtended;
import ggc.cgms.util.DataAccessCGMS;
import ggc.core.db.hibernate.cgms.CGMSDataExtendedH;
import ggc.core.db.hibernate.cgms.CGMSDataH;
import ggc.core.db.tool.transfer.GGCExporter;
import ggc.core.db.tool.transfer.GGCImporter;

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
 *  Filename:     PluginDb  
 *  Description:  This is master class for using Db instance within plug-in. In most cases, we 
 *                would want data to be handled by outside authority (GGC), but in some cases
 *                we wouldn't want that.
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public class BackupRestoreCGMSHandler extends BackupRestorePlugin
{

    I18nControlAbstract ic = DataAccessCGMS.getInstance().getI18nControlInstance();

    private String[] object_desc = { ic.getMessage("CGMS_DATA"), ic.getMessage("CGMS_DATA_EXTENDED"), };

    private String[] object_name = { "ggc.core.db.hibernate.cgms.CGMSDataH",
                                    "ggc.core.db.hibernate.cgms.CGMSDataExtendedH", };


    /**
     * Constructor
     */
    public BackupRestoreCGMSHandler()
    {
        // i18nControlAbstract = I18nControl.getInstance();
    }


    /**
     * Do Backup
     */
    @Override
    public void doBackup(BackupRestoreRunner brr)
    {

        for (int i = 0; i < this.object_desc.length; i++)
        {
            if (brr.isBackupObjectSelected(this.object_desc[i]))
            {
                System.out.println("Selected: " + this.object_desc[i]);
                brr.setTask(this.object_desc[i]);
                GGCExporter ge = new GGCExporter(brr);
                ge.exportData(this.object_name[i]);
                brr.setStatus(100);
            }
            else
            {
                System.out.println("NOT Selected: " + this.object_desc[i]);
            }

        }

    }


    /**
     * Do Restore
     */
    @Override
    public void doRestore(BackupRestoreRunner brr)
    {

        for (int i = 0; i < this.object_desc.length; i++)
        {
            if (brr.isRestoreObjectSelected(this.object_name[i]))
            {
                brr.setTask(this.object_desc[i]);
                GGCImporter ge = new GGCImporter(brr, brr.getRestoreObject(this.object_name[i]));
                ge.importData(this.object_name[i]);
                brr.setStatus(100);
            }
        }

    }


    /** 
     * Get Backup Restore Object
     */
    @Override
    public BackupRestoreObject getBackupRestoreObject(String class_name)
    {
        if (class_name.equals("ggc.core.db.hibernate.cgms.CGMSDataH"))
            return new CGMSData();
        else if (class_name.equals("ggc.core.db.hibernate.cgms.CGMSDataExtendedH"))
            return new CGMSDataExtended();
        else
            return null;
    }


    /** 
     * Get Backup Restore Object
     */
    @Override
    public BackupRestoreObject getBackupRestoreObject(Object obj, BackupRestoreObject bro)
    {
        if (bro.getBackupClassName().equals("ggc.core.db.hibernate.cgms.CGMSDataH"))
        {
            CGMSDataH eh = (CGMSDataH) obj;
            return new CGMSData(eh);
        }
        else if (bro.getBackupClassName().equals("ggc.core.db.hibernate.cgms.CGMSDataExtendedH"))
        {
            CGMSDataExtendedH eh = (CGMSDataExtendedH) obj;
            return new CGMSDataExtended(eh);
        }
        else
            return null;

    }


    /** 
     * Does Contain Backup Restore Object
     */
    @Override
    public boolean doesContainBackupRestoreObject(String bro_name)
    {
        for (String element : this.object_name)
        {
            if (element.equals(bro_name))
                return true;
        }

        return false;
    }

}
