package ggc.gui.dialogs;


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
 *  Filename: HbA1cFrame.java
 *  Purpose:  gives a "guess" about the current HbA1c
 *  
 *  @author andyrozman {andy@atech-software.com}
 * 
 */


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

import com.atech.graphics.components.about.AboutCustomPanel;
import com.atech.graphics.components.about.AboutDialog;
import com.atech.graphics.components.about.CreditsEntry;
import com.atech.graphics.components.about.CreditsGroup;
import com.atech.graphics.components.about.LibraryInfoEntry;
import com.atech.graphics.components.about.LicenceInfo;

import ggc.util.DataAccess;


public class AboutGGCDialog extends AboutDialog 
{

    public AboutGGCDialog(JFrame parent)
    {
        super(parent, true, DataAccess.getInstance().getI18nInstance());

        // licence
        this.setLicenceType(LicenceInfo.LICENCE_LGPL_v2_1);

        // credits
        ArrayList<CreditsGroup> lst_credits = new ArrayList<CreditsGroup>();

        CreditsGroup cg = new CreditsGroup(m_ic.getMessage("CURRENT_DEVELOPERS"));
        cg.addCreditsEntry(new CreditsEntry("Dieter Schultschik", "schultd@users.sourceforge.net", "Creator and Designer of application"));
        cg.addCreditsEntry(new CreditsEntry("Aleksander Rozman (Andy)", "andyrozman@users.sourceforge.net", "Current main developer"));
        cg.addCreditsEntry(new CreditsEntry("Reinhold Rumberger", "rumbi@users.sourceforge.net", "Tester and developer"));
        lst_credits.add(cg);

        cg = new CreditsGroup(m_ic.getMessage("PREVIOUS_DEVELOPERS"));
        cg.addCreditsEntry(new CreditsEntry("Stephan Schrader", "sschrade@users.sourceforge.net", "First meters supported..."));
        lst_credits.add(cg);

        this.setCredits(lst_credits);

        // set display system properties
        this.setDisplayProperties(true);

        // libraries
        ArrayList<LibraryInfoEntry> lst_libs = new ArrayList<LibraryInfoEntry>();
        lst_libs.add(new LibraryInfoEntry("Hibernate", "3.1", "www.hibernate.org", "LGPL", "Library for Object oriented access to Db"));

        LibraryInfoEntry li = new LibraryInfoEntry("HSqlDb", "1.8.0", "hsqldb.org", "Hypersonic", "Internal Java Db", "Copyright (c) 1995-2000 by the Hypersonic SQL Group. All rights reserved.");
        li.setCopyRightNotice2("Copyright (c) 2001-2005, The HSQL Development Group. All rights reserved.");
        lst_libs.add(li);

        lst_libs.add(new LibraryInfoEntry("Atech-Tools", "0.1.6", "www.atech-software.com", "LGPL", "Helper Library for Swing/Hibernate/...", "Copyright (c) 2006-2007 Atech Software Ltd. All rights reserved."));
        lst_libs.add(new LibraryInfoEntry("SkinLF", "6.7", "www.l2fprod.com", "LGPL", "Skins Library", "Copyright (c) 2000-2006 L2FProd.com.  All rights reserved."));
        lst_libs.add(new LibraryInfoEntry("iText", "2.0.7", "www.lowagie.com/iText/", "MPL", "Library for PDF creation (printing)"));
        lst_libs.add(new LibraryInfoEntry("RXTXcomm", "2.1.7", "www.rxtx.org", "LGPL", "Comm API"));

        this.setLibraries(lst_libs);

        // custom page
        createCustomTab();

        // title
        this.setTitle(m_ic.getMessage("ABOUT_GGC"));

        // finalize
        this.createAbout();
        this.setSize(500, 400);

        this.showAbout();
    }

    public void initCustom()
    {
	System.out.println("init Custom");
	this.about_image = "/icons/t_asc_dex.gif";
    }


    public void createCustomTab()
    {
        AboutCustomPanel acp = new AboutCustomPanel(m_ic);
        acp.setTabName(m_ic.getMessage("ABOUT"));


        acp.setLayout(new BoxLayout(acp, BoxLayout.PAGE_AXIS));
        //new BoxLayout(acp, BoxLayout.LINE_AXIS);

        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        JLabel l = new JLabel(new ImageIcon(DataAccess.getInstance().getImage("/icons/about_logo.gif", this).getScaledInstance(500,125,java.awt.Image.SCALE_SMOOTH)));
        p1.add(l, BorderLayout.CENTER);

        JLabel l2 = new JLabel();
        l2.setPreferredSize(new Dimension(100,10));
        

        p1.add(l2, BorderLayout.SOUTH);


        /*
        JButton jButton1 = new JButton();
        jButton1.setIcon(new ImageIcon(DataAccess.getInstance().getImage("/icons/jat_church.jpg", this).getScaledInstance(300,225,java.awt.Image.SCALE_SMOOTH)));
        */

        //JPanel p = new JPanel();

        //p.add(jButton1, BoxLayout.PAGE_AXIS);
        //acp.add(jButton1);
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
                                 m_ic.getMessage("GGC_TITLE") +"</b><br>&nbsp;&nbsp;(c) 2002-2008  " +
                                 m_ic.getMessage("GGC_DEVELOPMENT_TEAM")+ "<br>" +
                                 m_ic.getMessage("SEE_CREDITS") + 
                                 "<br><A HREF=\"http://ggc.sourceforge.net/\">http://ggc.sourceforge.net/</A><br>" + 
                                 m_ic.getMessage("LICENCE") + " LGPL v2.1<br></font></body></html>");

        acp.add(jEditorPaneAbout); //, BoxLayout.PAGE_AXIS);



        /*
        java.awt.GridBagConstraints gridBagConstraints;

    // tabbed
    //jTabbedPane1 = new javax.swing.JTabbedPane();

    // about

        JPanel jPanel1 = new javax.swing.JPanel();
        JButton jButton2 = new javax.swing.JButton();
        JPanel jPanel2 = new javax.swing.JPanel();
        //jButton1 = new javax.swing.JButton();
        JLabel jButton1 = new javax.swing.JLabel();
        JEditorPane jEditorPaneAbout = new javax.swing.JEditorPane();
        JEditorPane jEditorPane1 = new javax.swing.JEditorPane();
        //jScrollPane2 = new javax.swing.JScrollPane();
        //jEditorPane2 = new javax.swing.JEditorPane();



        jButton1.setIcon(new ImageIcon(DataAccess.getInstance().getImage("/icons/jat_church.jpg", this).getScaledInstance(300,225,java.awt.Image.SCALE_SMOOTH)));
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        //jButton1.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton1.setOpaque(false);
    //jButton1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        acp.add(jButton1, gridBagConstraints);
        
        jEditorPaneAbout.setBackground(new java.awt.Color(204, 204, 204));
        jEditorPaneAbout.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jEditorPaneAbout.setEditable(false);
        jEditorPaneAbout.setMinimumSize(new java.awt.Dimension(104, 90));
        jEditorPaneAbout.setOpaque(false);
        jEditorPaneAbout.setPreferredSize(new java.awt.Dimension(104, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        acp.add(jEditorPaneAbout, gridBagConstraints);

//        jTabbedPane1.addTab(m_ic.getMessage("ABOUT"), jPanel2);
        

        // jPanel3, jScroolPane1

        // jScrollPane3, Jpanel4
*/

        this.addCustomPanel(AboutDialog.PLACEMENT_BEFORE_STATIC_TABS, acp);


    }


}
