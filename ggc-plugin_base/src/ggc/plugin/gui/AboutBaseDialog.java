package ggc.plugin.gui;


/*
 *  GGC - GNU Gluco Control
 *
 *  A pure java application to help you manage your diabetes.
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
 *  Filename: AboutGGCDialog
 *  
 *  Purpose:  About dialog
 *  
 *  @author andyrozman {andy@atech-software.com}
 * 
 */


import ggc.plugin.util.DataAccessPlugInBase;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.atech.graphics.components.about.AboutCustomPanel;
import com.atech.graphics.components.about.AboutDialog;
import com.atech.graphics.components.about.LicenceInfo;


public class AboutBaseDialog extends AboutDialog 
{

    private static final long serialVersionUID = 586495485605943204L;
    DataAccessPlugInBase m_da;

	
    public AboutBaseDialog(JFrame parent, DataAccessPlugInBase da)
    {
        super(parent, true, da.getI18nControlInstance());

        this.m_da = da;
        
        // licence
        this.setLicenceType(LicenceInfo.LICENCE_GPL_v2_0);

        // credits
        this.setCredits(m_da.getPlugInDevelopers());

        // set display system properties
        this.setDisplayProperties(true);

        // libraries
        this.setLibraries(m_da.getPlugInLibraries());
        
        // features
        this.setFeatures(m_da.getPlugInFeatures());

        // custom page
        createCustomTab();

        // title
        this.setTitle(m_da.getAboutTitle());

        // finalize
        this.createAbout();
        this.setSize(500, 400);

        this.showAbout();
    }

    /*
    public void initCustom()
    {
	System.out.println("init Custom");
	this.about_image = "/icons/t_asc_dex.gif";
    } */


    public void createCustomTab()
    {
        AboutCustomPanel acp = new AboutCustomPanel(m_ic);
        acp.setTabName(m_ic.getMessage("ABOUT"));
        acp.setLayout(new BoxLayout(acp, BoxLayout.PAGE_AXIS));

        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        int[] sz = m_da.getAboutImageSize();
        
        JLabel l = new JLabel(new ImageIcon(m_da.getImage(m_da.getAboutImageName(), this).getScaledInstance(sz[0], sz[1] /*500,125*/,java.awt.Image.SCALE_SMOOTH)));
        p1.add(l, BorderLayout.CENTER);

        JLabel l2 = new JLabel();
        l2.setPreferredSize(new Dimension(100,10));

        p1.add(l2, BorderLayout.SOUTH);
        acp.add(p1);

        JEditorPane jEditorPaneAbout = new javax.swing.JEditorPane();
        jEditorPaneAbout.setBackground(new java.awt.Color(204, 204, 204));
        //jEditorPaneAbout.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jEditorPaneAbout.setEditable(false);
        jEditorPaneAbout.setMinimumSize(new java.awt.Dimension(104, 90));
        jEditorPaneAbout.setOpaque(false);
        jEditorPaneAbout.setPreferredSize(new java.awt.Dimension(104, 90));

        jEditorPaneAbout.setContentType("text/html");
        jEditorPaneAbout.setText("<HTML><body><font face=\"SansSerif\" size=\"3\"><center><b>" + 
                                 m_da.getAboutPlugInName() +"</b><br>&nbsp;&nbsp;(c) " + m_da.getAboutPluginCopyright() + "  " +
                                 m_ic.getMessage("GGC_DEVELOPMENT_TEAM")+ "<br>" +
                                 m_ic.getMessage("SEE_CREDITS") + 
                                 "<br><A HREF=\"http://ggc.sourceforge.net/\">http://ggc.sourceforge.net/</A><br>" + 
                                 m_ic.getMessage("LICENCE") + " GPL v2.0<br></font></body></html>");

        acp.add(jEditorPaneAbout); //, BoxLayout.PAGE_AXIS);

        this.addCustomPanel(AboutDialog.PLACEMENT_BEFORE_STATIC_TABS, acp);

    }


}
