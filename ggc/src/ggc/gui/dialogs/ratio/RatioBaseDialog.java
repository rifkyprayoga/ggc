package ggc.gui.dialogs.ratio;

import ggc.core.data.DailyValues;
import ggc.core.data.DailyValuesRow;
import ggc.core.util.DataAccess;
import ggc.core.util.I18nControl;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.atech.graphics.components.DateTimeComponent;
import com.atech.graphics.components.JDecimalTextField;
import com.atech.help.HelpCapable;
import com.atech.utils.ATSwingUtils;


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
 *  Filename:     RatioBaseDialog  
 *  Description:  This is dialog for setting Base ratio (one for whole day). This is usable
 *                for pen/injection therapy, for pump therapy you will need to use extended 
 *                dialog.
 * 
 *  Author: Andy {andy@atech-software.com}  
 */


public class RatioBaseDialog extends JDialog implements ActionListener, KeyListener, HelpCapable, FocusListener
{

    private static final long serialVersionUID = -1240982985415603758L;
//    JComboBox cb_time_range, cb_icarb_rule, cb_sens_rule;
    JDecimalTextField dtf_ch_ins, dtf_ins_bg, dtf_bg_ch;
    

    /** 
     * focusGained
     */
    public void focusGained(FocusEvent arg0)
    {
    }


    boolean in_action = false;
    
    /** 
     * focusLost
     */
    public void focusLost(FocusEvent ev)
    {
//        System.out.println("focus lost [" + in_action + "]");

        if (in_action)
            return;

//        System.out.println("focus lost [" + in_action + "]");
	
        in_action = true;
        
        calculateRatio(ev.getSource());
        
        in_action = false;
//        System.out.println("focus lost [" + in_action + "]");
        
    }

    private I18nControl m_ic = I18nControl.getInstance();
    private DataAccess m_da = DataAccess.getInstance();
    //private GGCProperties props = m_da.getSettings();

    private boolean m_actionDone = false;

    //private long last_change = 0;

//    static AddRowFrame singleton = null;



    
    

    JLabel label_title = new JLabel();
    JLabel label_food;
    JCheckBox cb_food_set;

    DateTimeComponent dtc;

    JButton AddButton;

    String sDate = null;

    DailyValues dV = null;
    DailyValuesRow m_dailyValuesRow = null;

    NumberFormat bg_displayFormat, bg_editFormat;
    
    JComponent components[] = new JComponent[9];

    Font f_normal = m_da.getFont(DataAccess.FONT_NORMAL);
    Font f_bold = m_da.getFont(DataAccess.FONT_NORMAL);
    boolean in_process;
    boolean debug = true;
    JButton help_button = null;
    JPanel main_panel = null;


    
    //private boolean m_add_action = true;
    //private Container m_parent = null;


    

    /**
     * Constructor
     * 
     * @param dialog
     */
    public RatioBaseDialog(JFrame dialog) 
    {
        super(dialog, "", true);
        
        //m_parent = dialog;
        m_da.addComponent(this);

        init();
        load();
        
        this.setVisible(true);

    }








    


    /**
     * Load data
     */
    private void load()
    {
        this.dtf_ch_ins.setValue(new Float(this.m_da.getSettings().getRatio_CH_Insulin()));
        this.dtf_ins_bg.setValue(new Float(this.m_da.getSettings().getRatio_BG_Insulin()));
        calculateRatio(RATIO_BG_CH);
    }

    
    /**
     * Save data
     */
    private void save()
    {
        this.m_da.getSettings().setRatio_CH_Insulin(m_da.getFloatValue(this.dtf_ch_ins.getCurrentValue()));
        this.m_da.getSettings().setRatio_BG_Insulin(m_da.getFloatValue(this.dtf_ins_bg.getCurrentValue()));
        
        this.m_da.getSettings().save();
    }


    private void init()
    {
        
        ATSwingUtils.initLibrary();
        
        /*
        int x = 0;
        int y = 0;
        int width = 400;
        int height = 500;

        Rectangle bnd = m_parent.getBounds();

        x = (bnd.width/2) + bnd.x - (width/2);
        y = (bnd.height/2) + bnd.y - (height/2);*/
        
        this.setBounds(0, 0, 300, 340);
        
        m_da.centerJDialog(this);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 300, 400);
        panel.setLayout(null);

        main_panel = panel;
        
        this.getContentPane().add(panel);

        
        label_title = ATSwingUtils.getTitleLabel("", 0, 15, 300, 35, panel, ATSwingUtils.FONT_BIG_BOLD);
        
        setTitle(m_ic.getMessage("RATIO_BASE"));
        label_title.setText(m_ic.getMessage("RATIO_BASE"));

        ATSwingUtils.getLabel(m_ic.getMessage("INSULIN_CARB_RATIO"), 30, 80, 150, 25, panel, ATSwingUtils.FONT_NORMAL_BOLD);
        dtf_ch_ins = ATSwingUtils.getNumericTextField(3, 2, new Float(0.0f), 180, 80, 80, 25, panel);
        dtf_ch_ins.addFocusListener(this);
        dtf_ch_ins.addKeyListener(this);
        
        ATSwingUtils.getLabel(m_ic.getMessage("SENSITIVITY_FACTOR_LONG"), 30, 120, 150, 45, panel, ATSwingUtils.FONT_NORMAL_BOLD);
        dtf_ins_bg = ATSwingUtils.getNumericTextField(3, 2, new Float(0.0f), 180, 130, 80, 25, panel);
        dtf_ins_bg.addFocusListener(this);
        dtf_ins_bg.addKeyListener(this);

        ATSwingUtils.getLabel(m_ic.getMessage("BG_OH_RATIO"), 30, 180, 150, 25, panel, ATSwingUtils.FONT_NORMAL_BOLD);
        dtf_bg_ch = ATSwingUtils.getNumericTextField(3, 2, new Float(0.0f), 180, 180, 80, 25, panel);
        dtf_bg_ch.addFocusListener(this);
        dtf_bg_ch.addKeyListener(this);

        
        ATSwingUtils.getButton("  " + m_ic.getMessage("OK"), 
                               30, 260, 110, 25, panel, ATSwingUtils.FONT_NORMAL, 
                               "ok.png", 
                               "ok", this, m_da);

        ATSwingUtils.getButton("  " + m_ic.getMessage("CANCEL"), 
                               150, 260, 110, 25, panel, ATSwingUtils.FONT_NORMAL, 
                               "cancel.png", 
                               "cancel", this, m_da);
        
        
        help_button = m_da.createHelpButtonByBounds(150, 230, 110, 25, this);

        panel.add(help_button);

        m_da.enableHelp(this);
        
    }

    private static final int RATIO_CH_INSULIN = 1;
    private static final int RATIO_BG_INSULIN = 2;
    private static final int RATIO_BG_CH = 3;

    
    private void calculateRatio(Object obj)
    {
        if (obj.equals(this.dtf_ch_ins))
        {
            calculateRatio(RATIO_CH_INSULIN);
        }
        else if (obj.equals(this.dtf_ins_bg))
        {
            calculateRatio(RATIO_BG_INSULIN);
        }
        else if (obj.equals(this.dtf_bg_ch))
        {
            calculateRatio(RATIO_BG_CH);
        }
    }
    
    
    private void calculateRatio(int type)
    {
        //System.out.println("calculate Ratio: " + type);
        
        float v1 = this.m_da.getFloatValue(this.dtf_ch_ins.getCurrentValue());
        float v2 = this.m_da.getFloatValue(this.dtf_ins_bg.getCurrentValue());
        float v3 = this.m_da.getFloatValue(this.dtf_bg_ch.getCurrentValue());

        
        if (type==RATIO_CH_INSULIN)
        {
            if (checkSet(v1,v2))
            {
                float v4 = v1/v2;
//                System.out.println("calculate Ratio [type=" + type + ",check=1,2;value=" + v4);
                this.dtf_bg_ch.setValue(new Float(v4));
            }
            else if (checkSet(v1,v3))
            {
                float v4 = v1/v3;
//                System.out.println("calculate Ratio [type=" + type + ",check=1,3;value=" + v4);
                this.dtf_ins_bg.setValue(new Float(v4));
            }
//            else
//                System.out.println("calculate Ratio [type=" + type + ",check NO");

        }
        else if (type==RATIO_BG_INSULIN)
        {
            if (checkSet(v2,v1))
            {
                float v4 = v1/v2;
//                System.out.println("calculate Ratio [type=" + type + ",check=2,1;value=" + v4);
                this.dtf_bg_ch.setValue(new Float(v4));
            }
            else if (checkSet(v2,v3))
            {
                float v4 = v2*v3;
//                System.out.println("calculate Ratio [type=" + type + ",check=2,3;value=" + v4);
                this.dtf_ch_ins.setValue(new Float(v4));
            }
//            else
//                System.out.println("calculate Ratio [type=" + type + ",check NO");
        }
        else
        {
            if (checkSet(v3,v1))
            {
                float v4 = v1/v3;
//                System.out.println("calculate Ratio [type=" + type + ",check=3,1;value=" + v4);
                this.dtf_ins_bg.setValue(new Float(v4));
            }
            else if (checkSet(v3,v2))
            {
                float v4 = v2*v3;
//                System.out.println("calculate Ratio [type=" + type + ",check=3,2;value=" + v4);
                this.dtf_ch_ins.setValue(new Float(v4));
            }
//            else
//                System.out.println("calculate Ratio [type=" + type + ",check NO");
        }
        
        
//        JDecimalTextField dtf_ch_ins, dtf_ins_bg, dtf_bg_ch;

    }
    
    private boolean checkSet(float v1, float v2)
    {
//        System.out.println("checkSet [v1=" + v1 + ",v2=" + v2 + "]");
        if ((v1!=0.0f) && (v2!=0.0f))
            return true;
        else
            return false;
    }
    

    


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals("cancel"))
        {
            m_da.removeComponent(this);
            this.dispose();
        }
        else if (action.equals("ok"))
        {
            float v1 = this.m_da.getFloatValue(this.dtf_ch_ins.getCurrentValue());
            float v2 = this.m_da.getFloatValue(this.dtf_ins_bg.getCurrentValue());

            if (this.checkSet(v1, v2))
            {
                m_da.removeComponent(this);
                cmdOk();
                this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(this, m_ic.getMessage("FILL_RATIO_ENTRIES"), 
                    m_ic.getMessage("ERROR"), JOptionPane.ERROR_MESSAGE);
            }
            
        }
        else
            System.out.println("RatioBaseDialog::unknown command: " + action);

    }

/*
    String button_command[] = { "update_ch", m_ic.getMessage("UPDATE_FROM_FOOD"),
            "edit_food", m_ic.getMessage("EDIT_FOOD"),
            "ok", m_ic.getMessage("OK"),
            "cancel", m_ic.getMessage("CANCEL"),
//                                  "help", m_ic.getMessage("HELP")
    
  */  
    
    
    private void cmdOk()
    {
        this.save();
    }

/*
    public boolean isFieldSet(String text)
    {
    	if ((text == null) || (text.trim().length()==0))
    	    return false;
    	else
    	    return true;
    } */
    
    /**
     * Action Succesful
     * 
     * @return
     */
    public boolean actionSuccesful()
    {
        return m_actionDone;
    }


    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {}
    
    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {}

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     */
    public void keyReleased(KeyEvent e)
    {

        //System.out.println("key released [" + in_action + "]");
        
        if (in_action)
            return;
    
        in_action = true;
        
        //System.out.println("key released [" + in_action + "]");
        calculateRatio(e.getSource());
        
        in_action = false;

        //System.out.println("key released [" + in_action + "]");
        
        /*
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            cmdOk();
        }*/

    }

    
    
    // ****************************************************************
    // ******              HelpCapable Implementation             *****
    // ****************************************************************
    
    /** 
     * getComponent - get component to which to attach help context
     */
    public Component getComponent()
    {
        return this.getRootPane();
    }

    /** 
     * getHelpButton - get Help button
     */
    public JButton getHelpButton()
    {
        return this.help_button;
    }

    /** 
     * getHelpId - get id for Help
     */
    public String getHelpId()
    {
        return "pages.GGC_Ratio_Base";
    }
    
    
    
    
}
