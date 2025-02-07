package ggc.plugin.cfg;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import com.atech.graphics.dialogs.selector.SelectableInterface;
import com.atech.graphics.dialogs.selector.SelectorAbstractDialog;
import com.atech.utils.ATDataAccessAbstract;
import ggc.plugin.util.DataAccessPlugInBase;

/**
 * Selector component for Meters (Simple Configuration)...
 * @author arozman
 *
 */

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       GGC PlugIn Base (base class for all plugins)
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
 *  Filename:     DeviceSelectorDialog  
 *  Description:  Dialog for selecting supported devices
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public class DeviceSelectorDialog extends SelectorAbstractDialog
{

    private static final long serialVersionUID = -391406165609998040L;
    DeviceConfigurationDefinition dcd = null;


    /**
     * Constructor
     * 
     * @param parent
     * @param da
     */
    public DeviceSelectorDialog(JDialog parent, ATDataAccessAbstract da)
    {
        super(parent, da, 0, null, true);
        this.showDialog();
    }


    /**
     * Get Device Configuration Definition
     * 
     * @return DeviceConfigurationDefinition instance
     */
    public DeviceConfigurationDefinition getDeviceConfigurationDefinition()
    {
        if (this.dcd == null)
        {
            this.dcd = ((DataAccessPlugInBase) dataAccess).getDeviceConfigurationDefinition();
        }

        return this.dcd;
    }


    /**
     * Init Selector Values For Type
     * 
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#initSelectorValuesForType()
     */
    @Override
    public void initSelectorValuesForType()
    {
        setSelectorObject((SelectableInterface) this.getDeviceConfigurationDefinition().getDummyObject());
        setSelectorName(
            String.format(i18nControl.getMessage("SELECTOR_DEVICE"), i18nControl.getMessage("DEVICE_NAME_BIG")));
        setAllowedActions(SelectorAbstractDialog.SELECTOR_ACTION_CANCEL_AND_SELECT);
        this.setColumnSortingEnabled(false);
        this.setHelpStringId("DeviceTool_Configuration_DeviceSelector");
        this.setHelpEnabled(true);
    }


    /**
     * Get Full Data
     * 
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#getFullData()
     */
    @Override
    public void getFullData()
    {
        this.full = new ArrayList<SelectableInterface>();
        this.full.addAll(this.getDeviceConfigurationDefinition().getSupportedDevices());

        Collections.sort(this.full);

        // System.out.println("Counter: " + this.full.size());

    }


    /**
     * Set Help Context
     */
    public void setHelpContext()
    {
        // TODO:
        // dataAccess.getHelpContext().getMainHelpBroker().enableHelpOnButton(this.getHelpButton(),
        // this.getHelpId(), null);
        // dataAccess.getHelpContext().getMainHelpBroker().enableHelpKey(this.getRootPane(),
        // this.getHelpId(), null);
    }


    /**
     * Check And Execute Action : Edit
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#checkAndExecuteActionEdit(SelectableInterface)
     */
    @Override
    public void checkAndExecuteActionEdit(SelectableInterface si)
    {
    }


    /**
     * Check And Execute Action : New
     * 
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#checkAndExecuteActionNew()
     */
    @Override
    public void checkAndExecuteActionNew()
    {
    }


    /** 
     * Check And Execute Action : Select
     * 
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#checkAndExecuteActionSelect()
     */
    @Override
    public void checkAndExecuteActionSelect()
    {
        if (table != null)
        {
            // System.out.println("in select action");
            if (table.getSelectedRow() == -1)
                return;

            this.selected_object = list.get(table.getSelectedRow());
            // System.out.println("in select action: selected:" +
            // this.selected_object);

            this.dispose();
        }

    }


    /**
     * Item State Changed
     * 
     * @see com.atech.graphics.dialogs.selector.SelectorAbstractDialog#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e)
    {
    }

}
