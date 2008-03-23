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
 *  Filename: HbA1cFrame.java
 *  Purpose:  gives a "guess" about the current HbA1c
 *
 *  Author:   schultd
 */

package ggc.gui.dialogs;


import ggc.data.HbA1cValues;
import ggc.gui.view.HbA1cView;
import ggc.util.DataAccess;
import ggc.util.I18nControl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.atech.help.HelpCapable;


public class HbA1cDialog extends JDialog implements ActionListener, HelpCapable
{
    
    JButton help_button;    

    private I18nControl m_ic = I18nControl.getInstance();        

    //private static HbA1cFrame singleton = null;
    private HbA1cView hbView;
    private HbA1cValues hbValues;

    private JLabel lblHbA1c;
    private JLabel lblExp;
    private JLabel lblBGAvg;
    private JLabel lblReadings;
    private JLabel lblReadingsPerDay;

    private DataAccess m_da = null;


    public HbA1cDialog(DataAccess da)
    {
        super(da.getMainParent(), "HbA1c", true);
        this.m_da = da;
        init();

        hbValues = this.m_da.getHbA1c(new GregorianCalendar());
        updateLabels();

        hbView.setHbA1cValues(hbValues);
        
        this.m_da.enableHelp(this);
        
	this.setVisible(true);
    }

    public void updateLabels()
    {
        lblExp.setText(hbValues.getValuation());
        lblHbA1c.setText(hbValues.getHbA1c_Method1() + "");
        lblBGAvg.setText(hbValues.getAvgBG() + "");
        lblReadings.setText(hbValues.getReadings() + "");
        lblReadingsPerDay.setText(hbValues.getReadingsPerDay() + "");
    }

    private void init()
    {
        getContentPane().setLayout(new BorderLayout());
        setBounds(100, 100, 500, 430);

        JPanel infoPanel = new JPanel(new BorderLayout());

        Box a = Box.createHorizontalBox();
        a.add(new JLabel(m_ic.getMessage("VALUATION")+":"));
        a.add(lblExp = new JLabel());

        Box b = Box.createHorizontalBox();
        b.add(new JLabel(m_ic.getMessage("BG")+" "+ m_ic.getMessage("AVG")+":"));
        b.add(lblBGAvg = new JLabel());

        Box c = Box.createHorizontalBox();
        c.add(new JLabel(m_ic.getMessage("READINGS")+":"));
        c.add(lblReadings = new JLabel());

        Box d = Box.createHorizontalBox();
        d.add(new JLabel(m_ic.getMessage("READINGS_SLASH_DAY")+":"));
        d.add(lblReadingsPerDay = new JLabel());

        Box e = Box.createVerticalBox();
        e.add(new JLabel(m_ic.getMessage("YOUR_CURRENT_HBA1C")+":"));
        lblHbA1c = new JLabel();
        lblHbA1c.setFont(new Font("Dialog", Font.BOLD, 16));
        e.add(lblHbA1c);
        e.add(a);
        e.add(Box.createVerticalStrut(20));
        e.add(b);
        e.add(c);
        e.add(d);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("   " + m_ic.getMessage("CLOSE"));
        closeButton.setPreferredSize(new Dimension(100, 25));
        closeButton.setIcon(m_da.getImageIcon_22x22("cancel.png", this));        
	closeButton.addActionListener(this);
	closeButton.setActionCommand("close");

        buttonPanel.add(closeButton);

        //System.out.println("m_da: " + this.m_da);
        
        this.help_button = this.m_da.createHelpButtonBySize(100, 25, this);
        buttonPanel.add(help_button);
        
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);
        infoPanel.add(e, BorderLayout.NORTH);

        hbView = new HbA1cView();

        getContentPane().add(hbView, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.EAST);
    }

    
    
    
    public void setHbA1cText(String s)
    {
        lblHbA1c.setText(s);
    }

    public void setBGAvgText(String s)
    {
        lblBGAvg.setText(s);
    }

    public void setReadingsText(String s)
    {
        lblReadings.setText(s);
    }

    public void setReadingsPerDayText(String s)
    {
        lblReadingsPerDay.setText(s);
    }

    public void setExpressivnessText(String s)
    {
        lblExp.setText(s);
    }


    private void closeDialog()
    {
        hbView = null;
        this.dispose();
    }



    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) 
    {
        String action = e.getActionCommand();

        if (action.equals("close")) 
        {
            this.closeDialog();
        }
        else
            System.out.println("HbA1cDialog:Unknown command: " + action);
    }
    
    
    // ****************************************************************
    // ******              HelpCapable Implementation             *****
    // ****************************************************************
    
    /* 
     * getComponent - get component to which to attach help context
     */
    public Component getComponent()
    {
	return this.getRootPane();
    }

    /* 
     * getHelpButton - get Help button
     */
    public JButton getHelpButton()
    {
	return this.help_button;
    }

    /* 
     * getHelpId - get id for Help
     */
    public String getHelpId()
    {
	return "pages.GGC_BG_HbA1c";
    }

    
}
