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
import ggc.util.VersionChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame
{
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
    GGCProperties props = GGCProperties.getInstance();

    //constructor
    public MainFrame(String title)
    {
        setTitle(title);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseListener());

        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu readMenu = new JMenu("Read");
        JMenu optionMenu = new JMenu("Option");
        JMenu helpMenu = new JMenu("Help");
        fileMenu.setMnemonic('F');
        viewMenu.setMnemonic('V');
        optionMenu.setMnemonic('O');
        helpMenu.setMnemonic('H');

        connectAction = new GGCAction("Connect", "Connect to DataBase");
        connectAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/connect.png")));
        disconnectAction = new GGCAction("Disconnect", "Disconnect from DataBase");
        disconnectAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/disconnect.png")));

        newAction = new GGCAction("New", KeyStroke.getKeyStroke('N', Event.CTRL_MASK), "Create a new DataBase");
        newAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/new.png")));
        openAction = new GGCAction("Open", KeyStroke.getKeyStroke('O', Event.CTRL_MASK), "Open existing DataBase");
        openAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/open.png")));
        closeAction = new GGCAction("Close", KeyStroke.getKeyStroke('C', Event.CTRL_MASK), "Close current DataBase");
        closeAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/close.png")));
        quitAction = new GGCAction("Quit", KeyStroke.getKeyStroke('Q', Event.CTRL_MASK), "Quit ggc");

        viewDailyAction = new GGCAction("Daily", KeyStroke.getKeyStroke('D', Event.CTRL_MASK), "View Daily Stats");
        viewDailyAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/daily.png")));
        viewCourseGraphAction = new GGCAction("Course", KeyStroke.getKeyStroke('R', Event.CTRL_MASK), "View Course");
        viewCourseGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/course.png")));
        viewSpreadGraphAction = new GGCAction("Spread", "View Spread");
        viewSpreadGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/spread.png")));
        viewFrequencyGraphAction = new GGCAction("Frequency", "View frequency of values");
        viewFrequencyGraphAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/frequency.png")));
        viewHbA1cAction = new GGCAction("HbA1c", "View your HbA1c");

        readMeterAction = new GGCAction("from Meter", KeyStroke.getKeyStroke('R', Event.CTRL_MASK), "Read Data From Meter");
        readMeterAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("/icons/readmeter.png")));

        prefAction = new GGCAction("Preferences", KeyStroke.getKeyStroke('P', Event.CTRL_MASK), "Preferences");

        aboutAction = new GGCAction("About", KeyStroke.getKeyStroke('A', Event.CTRL_MASK), "About GGC");
        checkVersionAction = new GGCAction("Check for Update", "Checks if a new version is available");

        addMenuItem(fileMenu, connectAction);
        addMenuItem(fileMenu, disconnectAction);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, newAction);
        addMenuItem(fileMenu, openAction);
        addMenuItem(fileMenu, closeAction);
        fileMenu.addSeparator();
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

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(readMenu);
        menuBar.add(optionMenu);
        menuBar.add(helpMenu);

        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        addToolBarButton(connectAction);
        addToolBarButton(disconnectAction);
        addToolBarSpacer();
        addToolBarButton(newAction);
        addToolBarButton(openAction);
        addToolBarButton(closeAction);
        addToolBarSpacer();
        addToolBarButton(viewDailyAction);
        addToolBarButton(viewCourseGraphAction);
        addToolBarButton(viewSpreadGraphAction);
        addToolBarButton(viewFrequencyGraphAction);
        addToolBarSpacer();
        addToolBarButton(viewHbA1cAction);
        addToolBarSpacer();
        addToolBarButton(readMeterAction);

        getContentPane().add(toolBar, BorderLayout.NORTH);

        statusPanel = StatusBar.getInstance();
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        statusPanel.setDataSourceText(props.getDataSource() + "[no Connection]");
        statusPanel.setStatusMessage("Initialising");

        dbH = DataBaseHandler.getInstance();

        if (props.getAutoConnect())
            dbH.connect();

        if (dbH.isConnected()) {
            if (dbH.isConnectedToDB())
                setActionEnabledStateDBOpened();
            else
                setActionEnabledStateDBClosed();
        } else
            setActionEnabledStateDisconnected();

        //Information Portal Setup
        informationPanel = new InfoPanel();
        getContentPane().add(informationPanel, BorderLayout.CENTER);
    }

    private void setActionEnabledStateDisconnected()
    {
        setConActions(false);
        setDBActionsAllFalse();
    }

    private void setActionEnabledStateConnected()
    {
        setConActions(true);
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

    private void setConActions(boolean connected)
    {
        connectAction.setEnabled(!connected);
        disconnectAction.setEnabled(connected);
    }

    private void setDBActions(boolean opened)
    {
        openAction.setEnabled(!opened);
        closeAction.setEnabled(opened);
        newAction.setEnabled(!opened);

        viewDailyAction.setEnabled(opened);
        viewSpreadGraphAction.setEnabled(opened);
        viewCourseGraphAction.setEnabled(opened);
        viewFrequencyGraphAction.setEnabled(opened);
        viewHbA1cAction.setEnabled(opened);

        readMeterAction.setEnabled(opened);
    }

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
    }

    private void close()
    {
        //write to prefs to file on close.
        props.write();

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
        JLabel lbl = new JLabel(new ImageIcon(getClass().getResource("/icons/spacer.png")));
        lbl.setEnabled(false);
        toolBar.add(lbl);
    }

    private JButton addToolBarButton(Action action)
    {
        final JButton button = toolBar.add(action);

        button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        button.setFocusPainted(false);

        button.setPreferredSize(new Dimension(24, 24));

        button.addMouseListener(new MouseListener()
        {
            public void mouseEntered(MouseEvent e)
            {
                if (button.isEnabled()) {
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
        private String command = null;

        GGCAction(String name)
        {
            super(name);
            command = name;
        }

        GGCAction(String name, KeyStroke keystroke)
        {
            this(name);
            if (keystroke != null)
                putValue(ACCELERATOR_KEY, keystroke);
        }

        GGCAction(String name, String tooltip)
        {
            this(name);
            if (tooltip != null)
                putValue(SHORT_DESCRIPTION, tooltip);
        }

        GGCAction(String name, KeyStroke keystroke, String tooltip)
        {
            this(name, keystroke);
            if (tooltip != null)
                putValue(SHORT_DESCRIPTION, tooltip);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (command.equals("Quit")) {

                close();

            } else if (command.equals("Connect")) {

                dbH = DataBaseHandler.getInstance();
                dbH.connect();
                if (dbH.isConnected()) {
                    if (dbH.isConnectedToDB())
                        setActionEnabledStateDBOpened();
                    else
                        setActionEnabledStateDBClosed();
                } else
                    setActionEnabledStateDisconnected();
                informationPanel.refreshPanels();

            } else if (command.equals("Disconnect")) {

                dbH.closeConnection();
                if (dbH.isConnected())
                    setActionEnabledStateConnected();
                else
                    setActionEnabledStateDisconnected();
                DataBaseHandler.killHandler();
                dbH = null;
                informationPanel.refreshPanels();

            } else if (command.equals("New")) {

                if (dbH == null)
                    return;
                String tmpName = JOptionPane.showInputDialog("Enter DB Name to create:");
                if (tmpName != null && !tmpName.equals("")) {
                    dbH.createNewDataBase(tmpName);
                    if (dbH.isConnectedToDB())
                        setActionEnabledStateDBOpened();
                    else
                        setActionEnabledStateDBClosed();
                } else
                    JOptionPane.showMessageDialog(null, "Invalid Name for Database", "GGC Error - Invalid Name", JOptionPane.ERROR_MESSAGE);
                informationPanel.refreshPanels();

            } else if (command.equals("Open")) {

                //dbH.setDBName(JOptionPane.showInputDialog("Enter DB Name to open:"));
                if (dbH == null)
                    return;
                dbH.openDataBase(true);
                if (dbH.isConnectedToDB())
                    setActionEnabledStateDBOpened();
                else
                    setActionEnabledStateDBClosed();
                informationPanel.refreshPanels();

            } else if (command.equals("Close")) {

                if (dbH == null)
                    return;
                dbH.closeDataBase();
                if (dbH.isConnectedToDB())
                    setActionEnabledStateDBOpened();
                else
                    setActionEnabledStateDBClosed();
                informationPanel.refreshPanels();

            } else if (command.equals("Daily")) {
                DailyStatsFrame.showMe();
            } else if (command.equals("Course")) {
                CourseGraphFrame.showMe();
            } else if (command.equals("Spread")) {
                SpreadGraphFrame.showMe();
            } else if (command.equals("Frequency")) {
                FrequencyGraphFrame.showMe();
            } else if (command.equals("HbA1c")) {
                HbA1cFrame.showMe();
            } else if (command.equals("Preferences")) {
                PropertiesFrame.showMe();
            } else if (command.equals("from Meter")) {
                //ReadMeterFrame.showMe();
                ReadMeterDialog.showMe(MainFrame.this);
            } else if (command.equals("About")) {
                JOptionPane.showMessageDialog(null, "GNU Gluco Control v0.0.1", "About GGC", JOptionPane.INFORMATION_MESSAGE);
            } else if (command.equals("Check for Update")) {
                new VersionChecker().checkForUpdate();
            }
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