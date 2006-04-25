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
 *  Filename: GeneralInfoPanel.java
 *  Purpose:  Shows general information about your Person. Like your name,
 *            Insulin used, your personal BG bounds, ...
 *
 *  Author:   schultd
 */

package ggc.gui.panels.info;

import ggc.util.I18nControl;

import javax.swing.*;
import java.awt.*;


public class GeneralInfoPanel extends AbstractInfoPanel
{
    private JLabel lblName = new JLabel();
    private JLabel lblIns1 = new JLabel();
    private JLabel lblIns2 = new JLabel();
    private JLabel lblMeter = new JLabel();
    private JLabel lblUnit = new JLabel();


    public GeneralInfoPanel()
    {
        super(I18nControl.getInstance().getMessage("GENERAL_INFORMATION")+":");
        init();
        refreshInfo();
    }

    public void init()
    {
        setLayout(new GridLayout(0, 2));

        add(new JLabel(m_ic.getMessage("YOUR_NAME")+":"));
        add(lblName);
        add(new JLabel(m_ic.getMessage("BOLUS_INSULIN")+":"));
        add(lblIns1);
        add(new JLabel(m_ic.getMessage("BASAL_INSULIN")+":"));
        add(lblIns2);
        add(new JLabel(m_ic.getMessage("GLUCOMETER")+":"));
        add(lblMeter);
        add(new JLabel(m_ic.getMessage("BG_UNIT")+":"));
        add(lblUnit);

        add(new JLabel());
        add(new JLabel());

    }

    public void refreshInfo()
    {
        lblName.setText(m_da.getSettings().getUserName());
        lblIns1.setText(m_da.getSettings().getIns1Name() + "  (" + m_da.getSettings().getIns1Abbr() + ")");
        lblIns2.setText(m_da.getSettings().getIns2Name() + "  (" + m_da.getSettings().getIns2Abbr() + ")");
        lblMeter.setText(m_da.getSettings().getMeterTypeString() + "  (" + m_da.getSettings().getMeterPort() + ")");
        lblUnit.setText(m_da.getSettings().getBG_unitString());
    }

/*
        <property name="meter_type"  type="int" />
        <property name="meter_port"  type="string" length="50" />
        <property name="bg_unit"  type="int" />  <!-- 1= mmol/l, 2=mg/dl -->
*/

}
