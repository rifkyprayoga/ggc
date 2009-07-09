package ggc.pump.manager.company; 

import ggc.pump.device.animas.AnimasIR1200;
import ggc.pump.device.animas.AnimasIR1250;
import ggc.pump.device.animas.AnimasIR2020;
import ggc.pump.device.animas.AnimasPing;
import ggc.pump.manager.PumpManager;
import ggc.pump.util.I18nControl;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       Pump Tool (support for Pump devices)
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
 *  Filename:      Animas  
 *  Description:   Pump Company - Animas
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public class Animas extends AbstractPumpDeviceCompany
{

    
    /**
     * Constructor
     */
    public Animas()
    {
        super(I18nControl.getInstance(), false);
        
        //this.addDevice(new GenericPumpDevice(this));
        this.addDevice(new AnimasIR1200(this));
        this.addDevice(new AnimasIR1250(this));
        this.addDevice(new AnimasIR2020(this));
        this.addDevice(new AnimasPing(this));
        
    }


    /**
     * getName - Get Name of pump company. 
     * 
     * @return name of pump company
     */
    public String getName()
    {
        return "Animas";
    }

    
    /**
     * getCompanyId - Get Company Id 
     * 
     * @return id of company
     */
    public int getCompanyId()
    {
        return PumpManager.PUMP_COMPANY_ANIMAS;
    }
    
    
    /**
     * getInstructions - get instructions for device
     * 
     * @return instructions for reading data 
     */
    public String getDescription()
    {
       return "ANIMAS_DESC"; 
    }
    
    
    
    /**
     * getImplementationStatus - Get Implementation status 
     * 
     * @return implementation status as number
     * @see ggc.plugin.manager.DeviceImplementationStatus
     */
    public int getImplementationStatus()
    {
        return 0;
    }
    
    
    
}
