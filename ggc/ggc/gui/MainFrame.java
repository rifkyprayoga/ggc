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
 *  Filename: MainFrame.java
 *  Purpose:  The MainFrame of the app. Contains MenuBars, ToolBars, StatusBars, ...
 *
 *  Author:   schultd
 */

package ggc.gui;


import ggc.db.DataBaseHandler;
import ggc.gui.infoPanel.InfoPanel;
import ggc.util.GGCProperties;
import ggc.util.I18nControl;
import ggc.util.VersionChecker;

import ggc.print.PrintMonthlyReport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame
{
    
    private I18nControl m_ic = I18nControl.getInstance();        

    //fields
    private JMenuBar menuBar = new JMenuBar();
    private JToolBar toolBar = new JToolBar();
    private JLabel lblTest = new JLabel();
    private GGCAction connectAction, disconnectAction, newAction, openAction, closeAction, quitAction;
    private GGCAction prefAction;
    private GGCAction readMeterAction;
    private GGCAction viewDailyAction, viewCourseGraphAction, viewSpreadGraphAction, viewFrequencyGraphAction;
    private GGCAction viewHbA1cAction;
    private GGCAction aboutAction, checkVersionAction;
    private DailyStatsFrame dailyStatsWindow;
    private StatusBar statusPanel;
    private InfoPanel informationPanel;
    DataBaseHandler dbH;

    public static boolean developer_version = false;

    GGCProperties props = GGCProperties.getInstance();

    //constructor
    public MainFrame(String title, boolean developer_version)
    {
        setTitle(title);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseListener());

        this.developer_version = developer_version;

        JMenu fileMenu = new JMenu(m_ic.getMessageWithoutMnemonic("MN_FILE"));
        JMenu viewMenu = new JMenu(m_ic.getMessageWithoutMnemonic("MN_VIEW"));
        JMenu readMenu = new JMenu(m_ic.getMessageWithoutMnemonic("MN_READ"));
        JMenu optionMenu = new JMenu(m_ic.getMessageWithoutMnemonic("MN_OPTION"));
        JMenu helpMenu = new JMenu(m_ic.getMessageWithoutMnemonic("MN_HELP"));
        JMenu testMenu = new JMenu("Test");
        fileMenu.setMnemonic(m_ic.getMnemonic("MN_FILE"));
        viewMenu.setMnemonic(m_ic.getMnemonic("MN_VIEW"));
        optionMenu.setMnemonic(m_ic.getMnemonic("MN_OPTION"));
        helpMenu.setMnemonic(m_ic.getMnemonic("MN_HELP"));


        connectAction = new GGCAction("MN_CONNECT", "MN_CONNECT_DESC", "file_connect");
        connectAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/connect.gif")));
        
	disconnectAction = new GGCAction("MN_DISCONNECT", "MN_DISCONNECT_DESC", "file_disconnect");
        disconnectAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/disconnect.gif")));
/*
        newAction = new GGCAction("MN_NEW", "MN_NEW_DESC", "file_new");
        newAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/new.gif")));
        openAction = new GGCAction("MN_OPEN", "MN_OPEN_DESC", "file_open");
        openAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/open.gif")));
        closeAction = new GGCAction("MN_CLOSE", "MN_CLOSE_DESC", "file_close");
        closeAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/close.gif")));
*/	
	
        quitAction = new GGCAction("MN_QUIT", "MN_QUIT_DESC", "file_quit");

        viewDailyAction = new GGCAction("MN_DAILY", "MN_DAILY_DESC", "view_daily");
        viewDailyAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/daily.gif")));
        viewCourseGraphAction = new GGCAction("MN_COURSE", "MN_COURSE_DESC", "view_course");
        viewCourseGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/course.gif")));
        viewSpreadGraphAction = new GGCAction("MN_SPREAD", "MN_SPREAD_DESC", "view_spread");
        viewSpreadGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/spread.gif")));
        viewFrequencyGraphAction = new GGCAction("MN_FREQUENCY", "MN_FREQUENCY_DESC", "view_freq");
        viewFrequencyGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/frequency.gif")));
        viewHbA1cAction = new GGCAction("MN_HBA1C", "MN_HBA1C_DESC", "view_hba1c");

        readMeterAction = new GGCAction("MN_FROM_METER", "MN_FROM_METER_DESC", "read_meter");
        readMeterAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/readmeter.gif")));

        prefAction = new GGCAction("MN_PREFERENCES", "MN_PREFERENCES_DESC", "option_pref");

        aboutAction = new GGCAction("MN_ABOUT", "MN_ABOUT_DESC", "hlp_about");
        checkVersionAction = new GGCAction("MN_CHECK_FOR_UPDATE", "MN_CHECK_FOR_UPDATE_DESC", "hlp_check");

        GGCAction test = new GGCAction("Print", "Print Test", "print_test");

        addMenuItem(fileMenu, connectAction);
        addMenuItem(fileMenu, disconnectAction);
        fileMenu.addSeparator();
        //addMenuItem(fileMenu, newAction);
        //addMenuItem(fileMenu, openAction);
        //addMenuItem(fileMenu, closeAction);
        //fileMenu.addSeparator();
        addMenuItem(fileMenu, quitAction);

        addMenuItem(viewMenu, viewDailyAction);
        addMenuItem(viewMenu, viewCourseGraphAction);
        addMenuItem(viewMenu, viewSpreadGraphAction);
        addMenuItem(viewMenu, viewFrequencyGraphAction);
        viewMenu.addSeparator();
        addMenuItem(viewMenu, viewHbA1cAction);

        addMenuItem(readMenu, readMeterAction);

        addMenuItem(optionMenu, prefAction);

        addMenuItem(helpMenu, aboutAction);
        addMenuItem(helpMenu, checkVersionAction);

        addMenuItem(testMenu, test);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(readMenu);
        menuBar.add(optionMenu);
        menuBar.add(helpMenu);
        
        if (this.developer_version)
            menuBar.add(testMenu);


        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        addToolBarButton(connectAction);
        addToolBarButton(disconnectAction);

        addToolBarSpacer();
        //addToolBarButton(newAction);
        //addToolBarButton(openAction);
        //addToolBarButton(closeAction);
        addToolBarSpacer();
        addToolBarButton(viewDailyAction);
        addToolBarButton(viewCourseGraphAction);
        addToolBarButton(viewSpreadGraphAction);
        addToolBarButton(viewFrequencyGraphAction);
        addToolBarSpacer();
	addToolBarSpacer();
        addToolBarButton(viewHbA1cAction);
        addToolBarSpacer();
	addToolBarSpacer();
        addToolBarButton(readMeterAction);

        getContentPane().add(toolBar, BorderLayout.NORTH);

        statusPanel = StatusBar.getInstance();
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        //statusPanel.setDataSourceText(props.getDataSource() + "[" + m_ic.getMessage("NO_CONNECTION") + "]");
        statusPanel.setStatusMessage("Initialising");

	dbH = DataBaseHandler.getInstance();
	dbH.setStatus();

	//statusPanel.setDataSourceText(props.getDataSource() + "[" + m_ic.getMessage("NO_CONNECTION") + "]");


        if (props.getAutoConnect())
            dbH.connectDb();

	setDbActions();

	/*
        if (dbH.isConnected()) {
            if (dbH.isConnectedToDB())
                setActionEnabledStateDBOpened();
            else
                setActionEnabledStateDBClosed();
        } else
            setActionEnabledStateDisconnected();
	*/
	
        //Information Portal Setup
        informationPanel = new InfoPanel();
        getContentPane().add(informationPanel, BorderLayout.CENTER);
    }

    /*
    private void setActionEnabledStateDisconnected()
    {
        setConActions(false);
        setDBActionsAllFalse();
    } */

/*
    private void setActionEnabledStateConnected()
    {
        setConActions(true);
        
	if (props.getDataSource().equals("HSQL"))
	    setDBActions(true);
	else
	    setDBActions(false);
    }

    private void setActionEnabledStateDBOpened()
    {
        setConActions(true);
        setDBActions(true);
    }

    private void setActionEnabledStateDBClosed()
    {
        setConActions(true);
        setDBActions(false);
    }
*/
/*    private void setConActions(boolean connected)
    {
        connectAction.setEnabled(!connected);
        disconnectAction.setEnabled(connected);
    }
    */

    private void setDbActions()
    {

	setDBActions(dbH.isConnected());

/*
	if (dbH.isConnected())
	{
	    connectAction.setEnabled(false);
	    disconnectAction.setEnabled(true);
	}
	else
	{
	    connectAction.setEnabled(true);
	    disconnectAction.setEnabled(false);
	}
  */
    }



    private void setDBActions(boolean opened)
    {

	connectAction.setEnabled(!opened);
	disconnectAction.setEnabled(opened);

        //openAction.setEnabled(!opened);
        //closeAction.setEnabled(opened);
        //newAction.setEnabled(!opened);

        viewDailyAction.setEnabled(opened);
        viewSpreadGraphAction.setEnabled(opened);
        viewCourseGraphAction.setEnabled(opened);
        viewFrequencyGraphAction.setEnabled(opened);
        viewHbA1cAction.setEnabled(opened);

        readMeterAction.setEnabled(opened);
    }


    /*
    private void setDBActionsAllFalse()
    {
        openAction.setEnabled(false);
        closeAction.setEnabled(false);
        newAction.setEnabled(false);

        viewDailyAction.setEnabled(false);
        viewSpreadGraphAction.setEnabled(false);
        viewCourseGraphAction.setEnabled(false);
        viewFrequencyGraphAction.setEnabled(false);
        viewHbA1cAction.setEnabled(false);

        readMeterAction.setEnabled(false);
    } */

    private void close()
    {
        //write to prefs to file on close.
        props.write();
	dbH.disconnectDb();
        dispose();
        System.exit(0);
    }

    private JMenuItem addMenuItem(JMenu menu, Action action)
    {
        JMenuItem item = menu.add(action);

        KeyStroke keystroke = (KeyStroke)action.getValue(action.ACCELERATOR_KEY);
        if (keystroke != null)
            item.setAccelerator(keystroke);
        return item;
    }

    private void addToolBarSpacer()
    {
        toolBar.addSeparator();
	
	//JLabel lbl = new JLabel(new ImageIcon(getClass().getResource("/icons/spacer.gif")));
        //lbl.setEnabled(false);
        //toolBar.add(lbl);
    }

    private JButton addToolBarButton(Action action)
    {
        final JButton button = toolBar.add(action);

        button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        button.setFocusPainted(false);

        button.setPreferredSize(new Dimension(24, 24));

	//button.setIcon((ImageIcon)action.getValue(Action.SMALL_ICON));

        button.addMouseListener(new MouseListener()
        {
            public void mouseEntered(MouseEvent e)
            {
                if (button.isEnabled()) 
                {
                    button.setBorder(BorderFactory.createLineBorder(new Color(8, 36, 106), 1));
                    button.setBackground(new Color(180, 190, 213));
                }
            }

            public void mouseExited(MouseEvent e)
            {
                button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                button.setBackground(new Color(213, 210, 205));
            }

            public void mouseClicked(MouseEvent e)
            {
            }

            public void mousePressed(MouseEvent e)
            {
            }

            public void mouseReleased(MouseEvent e)
            {
            }

        });


        //button.setRolloverIcon(new ImageIcon("ggc/icons/connect.png"));

        //button.setRolloverEnabled(true);
        return button;
    }

    class GGCAction extends AbstractAction
    {
        //private String command = null;

        GGCAction(String name, String command)
        {
            super();
            setName(m_ic.getMessageWithoutMnemonic(name));

            putValue(Action.NAME, m_ic.getMessageWithoutMnemonic(name));

            char ch = m_ic.getMnemonic(name);

            if (ch!='0') 
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ch, Event.CTRL_MASK));

            if (command!=null)
                putValue(ACTION_COMMAND_KEY, command);

            command = name;
        }
/*
        GGCAction(String name, KeyStroke keystroke)
        {
            this();
            setName(m_ic.getMessageWithoutMnemonic(name));
            if (keystroke != null)
                putValue(ACCELERATOR_KEY, keystroke);
        }
   */
        GGCAction(String name, String tooltip, String command)
        {
            super();
            setName(m_ic.getMessageWithoutMnemonic(name));

            putValue(Action.NAME, m_ic.getMessageWithoutMnemonic(name));

            char ch = m_ic.getMnemonic(name);

            if (ch!='0') 
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(ch, Event.CTRL_MASK));

            if (tooltip != null)
                putValue(SHORT_DESCRIPTION, m_ic.getMessage(tooltip));

            if (command!=null)
                putValue(ACTION_COMMAND_KEY, command);
        }

/*
        GGCAction(String name, KeyStroke keystroke, String tooltip)
        {
            this(name, keystroke);
            if (tooltip != null)
                putValue(SHORT_DESCRIPTION, tooltip);
        }
*/

        public void actionPerformed(ActionEvent e)
        {

            String command = e.getActionCommand();

	    //System.out.println("Command: " + command);

            if (command.equals("file_quit")) 
	    {
                close();
            } 
	    else if (command.equals("file_connect")) 
	    {

                //dbH = DataBaseHandler.getInstance();

		

                dbH.connectDb();


		//System.out.println("Connect" + dbH + "  " + dbH.isConnected());

		setDbActions();

		dbH.setStatus();
                
		//System.out.println(dbH.isConnected()

/*

		if (dbH.isConnected()) 
		{

                    if (dbH.isConnectedToDB())
                        setActionEnabledStateDBOpened();
                    else
                        setActionEnabledStateDBClosed();
                } 
		else
                    setActionEnabledStateDisconnected();
*/                
		informationPanel.refreshPanels();

            } 
            else if (command.equals("file_disconnect")) 
	    {

		dbH.disconnectDb();
		setDbActions();

		dbH.setStatus();

		/*
                if (dbH.isConnected())
                    setActionEnabledStateConnected();
                else
                    setActionEnabledStateDisconnected();
                DataBaseHandler.killHandler();
                dbH = null; */
                informationPanel.refreshPanels();

            } 
/*            else if (command.equals("file_new")) {

                if (dbH == null)
                    return;

		String tmpName; 

		//System.out.println(props.getDataSource());

		if (props.getDataSource().equals("HSQL"))
		{
		    tmpName = "HSQLDB";
		}
		else
		    tmpName = JOptionPane.showInputDialog(m_ic.getMessage("ENTER_DB_TO_CREATE")+":");
                
		if (tmpName != null && !tmpName.equals("")) 
		{
                    dbH.createNewDataBase(tmpName);
                    if (dbH.isConnectedToDB())
                        setActionEnabledStateDBOpened();
                    else
                        setActionEnabledStateDBClosed();
                } 
		else
                    JOptionPane.showMessageDialog(null, m_ic.getMessage("INVALID_NAME_FOR_DB"), "GGC " + m_ic.getMessage("ERROR")+ " - " + m_ic.getMessage("INVALID_NAME"), JOptionPane.ERROR_MESSAGE);

                informationPanel.refreshPanels();

            } 
            else if (command.equals("file_open")) {

                //dbH.setDBName(JOptionPane.showInputDialog("Enter DB Name to open:"));
                if (dbH == null)
                    return;
                dbH.openDataBase(true);
                if (dbH.isConnectedToDB())
                    setActionEnabledStateDBOpened();
                else
                    setActionEnabledStateDBClosed();
                informationPanel.refreshPanels();

            } 
            else if (command.equals("file_close")) {

                if (dbH == null)
                    return;
                dbH.disconnectDb();

		/*
                if (dbH.isConnectedToDB())
                    setActionEnabledStateDBOpened();
                else
                    setActionEnabledStateDBClosed();
		    */
/*                informationPanel.refreshPanels();

            } */
            else if (command.equals("view_daily")) 
            {
                DailyStatsFrame.showMe();
            } 
            else if (command.equals("view_course")) 
            {
                CourseGraphFrame.showMe();
            } 
            else if (command.equals("view_spread")) 
            {
                SpreadGraphFrame.showMe();
            } 
            else if (command.equals("view_freq")) 
            {
                FrequencyGraphFrame.showMe();
            } 
            else if (command.equals("view_hba1c")) 
            {
                HbA1cFrame.showMe();
            } 
            else if (command.equals("option_pref")) 
            {
                PropertiesFrame.showMe();
            } 
            else if (command.equals("read_meter")) 
            {
                ReadMeterDialog.showMe(MainFrame.this);
            } 
            else if (command.equals("hlp_about")) 
            {
                // FIX This
                JOptionPane.showMessageDialog(null, "GNU Gluco Control v0.0.1", "About GGC", JOptionPane.INFORMATION_MESSAGE);
            } 
            else if (command.equals("hlp_check")) 
            {
                new VersionChecker().checkForUpdate();
            }
            else if (command.equals("print_test")) 
            {
                new PrintMonthlyReport();

                try
                {
                    String pathToAcrobat = "d:/Program Files/Adobe/Acrobat 6.0/Reader/AcroRd32.exe"	;

                    Runtime.getRuntime().exec(pathToAcrobat+ " " + "HelloWorld2.pdf"); 
                }
                catch(Exception ex)
                {
                    System.out.println("Error running AcrobatReader");
                }

            }
            else
                System.out.println("Unknown Command: " + command);

        }
    }

    private class CloseListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            close();
        }
    }
}