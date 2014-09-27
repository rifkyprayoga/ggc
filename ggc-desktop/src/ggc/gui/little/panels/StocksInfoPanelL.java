package ggc.gui.little.panels;

import ggc.gui.panels.info.AbstractInfoPanel;
import ggc.gui.panels.info.InfoPanelsIds;

import java.awt.GridLayout;

import javax.swing.JLabel;

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
 *  Filename:     StocksInfoPanelL
 *  Description:  This Panel contains information about Stock of your medical supplies
 *
 *  Author: andyrozman {andy@atech-software.com}  
 */

public class StocksInfoPanelL extends AbstractInfoPanel
{
    private static final long serialVersionUID = 1947681641559281357L;

    /**
     * Constructor
     */
    public StocksInfoPanelL()
    {
        super("STOCKS");
        setLayout(new GridLayout(0, 1));
        init();
        refreshInfo();
    }

    private void init()
    {
        add(new JLabel());
        add(new JLabel(m_ic.getMessage("STOCK_DATA_HERE") + ":"));
        add(new JLabel(m_ic.getMessage("STO_WILL_BE_FOUND_HERE") + "..." + m_ic.getMessage("NOT_YET")));
        add(new JLabel());
    }

    /**
     * Refresh Information 
     */
    @Override
    public void refreshInfo()
    {
    }

    /**
     * Get Tab Name
     * 
     * @return name as string
     */
    @Override
    public String getTabName()
    {
        return "StocksInfo";
    }

    /**
     * Do Refresh - This method can do Refresh
     */
    @Override
    public void doRefresh()
    {
    }

    /**
     * Get Panel Id
     * 
     * @return id of panel
     */
    @Override
    public int getPanelId()
    {
        return InfoPanelsIds.INFO_PANEL_NONE;
    }

}
