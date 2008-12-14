package ggc.gui.panels.info;

import ggc.core.data.HbA1cValues;
import ggc.core.util.DataAccess;
import ggc.core.util.I18nControl;
import ggc.core.db.GGCDb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JPanel;

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
 *  Filename:     HbA1cInfoPanel  
 *  Description:  Panel for HbA1c info
 *
 *  Author: schultd
 */


public class HbA1cInfoPanel extends AbstractInfoPanel
{

    private static final long serialVersionUID = -8750479135671337356L;
    private JLabel lblHbA1c;
    private JLabel lblVal;
    private JLabel lblAvgBG;
    private JLabel lblReadings;
    private JLabel lblReadingsPerDay;
    private DataAccess m_da = DataAccess.getInstance();

    /**
     * Constructor
     */
    public HbA1cInfoPanel()
    {
        super(I18nControl.getInstance().getMessage("HBA1C"));
        init();
        refreshInfo();
    }

    private void init()
    {
        setLayout(new BorderLayout());

        JPanel lblPanel = new JPanel(new GridLayout(5, 2));
        lblPanel.setBackground(Color.white);
        lblPanel.add(new JLabel(m_ic.getMessage("YOUR_CURRENT_HBA1C") + ":"));
        lblPanel.add(lblHbA1c = new JLabel());
        lblHbA1c.setFont(new Font("Dialog", Font.BOLD, 14));
        lblPanel.add(new JLabel(m_ic.getMessage("VALUATION") + ":"));
        lblPanel.add(lblVal = new JLabel());
        lblPanel.add(new JLabel(m_ic.getMessage("AVG_BG") + ":"));
        lblPanel.add(lblAvgBG = new JLabel());
        lblPanel.add(new JLabel(m_ic.getMessage("READINGS") + ":"));
        lblPanel.add(lblReadings = new JLabel());
        lblPanel.add(new JLabel(m_ic.getMessage("READINGS_SLASH_DAY") + ":"));
        lblPanel.add(lblReadingsPerDay = new JLabel());

        add(lblPanel, BorderLayout.NORTH);
    }

    /**
     * Refresh Information 
     */
    @Override
    public void refreshInfo()
    {
        HbA1cValues hbVal = null;

        GGCDb db = m_da.getDb();

        if ((db != null) && (db.isDbStarted()))
            hbVal = m_da.getHbA1c(new GregorianCalendar());

        DecimalFormat df = new DecimalFormat("#0.00");

        if (hbVal != null)
        {
            if (hbVal.getReadings() == 0)
                lblHbA1c.setText(m_ic.getMessage("NO_READINGS"));
            else
                lblHbA1c.setText(df.format(hbVal.getHbA1c_Method3()) + " %");

            // lblHbA1c.setText(df.format(hbVal.getHbA1c_Method1()) + " %");

            lblVal.setText(hbVal.getValuation());
            lblAvgBG.setText(df.format(hbVal.getAvgBG()));
            lblReadings.setText(hbVal.getReadings() + "");
            lblReadingsPerDay.setText(df.format(hbVal.getReadingsPerDay()));
        }
        else
        {
            lblHbA1c.setText(m_ic.getMessage("NO_DATASOURCE"));
            lblVal.setText("");
            lblAvgBG.setText("");
            lblReadings.setText("");
            lblReadingsPerDay.setText("");
        }
    }
}
