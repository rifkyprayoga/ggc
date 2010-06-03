package ggc.plugin.gui;

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
 *  Filename:     DeviceSpecialConfigPanelInterface
 *  Description:  
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public interface DeviceSpecialConfigPanelInterface
{
    
    /**
     * Init Panel
     */
    public void initPanel();
    
    
    /**
     * Get Height
     * 
     * @return
     */
    public int getHeight();
    
    
    /**
     * Init Parameters
     */
    public void initParameters();

    
    /**
     * Has Default Parameter
     * 
     * @return
     */
    public boolean hasDefaultParameter();
    
    
    /**
     * Are Connection Parameters Valid
     * 
     * @return
     */
    public boolean areConnectionParametersValid();

    
    /**
     * Load Connection Parameters
     * 
     * @param param
     */
    public void loadConnectionParameters(String param);
    
    
    /**
     * Save Connection Parameters
     * 
     * @return
     */
    public String saveConnectionParameters();

    
    /**
     * Load Parameters To GUI
     */
    public void loadParametersToGUI();
    
    
    /**
     * Read Parameters From GUI
     */
    public void readParametersFromGUI();
    
    
    /**
     * Get Default Parameter
     * 
     * @return
     */
    public String getDefaultParameter();
    

    /**
     * Set Default Parameter
     * 
     * @param par
     */
    public void setDefaultParameter(String par);
    
    
    /**
     * Get Parameter
     * 
     * @param key
     * @return
     */
    public String getParameter(String key);
    
    
}
