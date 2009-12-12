

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
 *  Filename: <filename>
 *
 *  Purpose:  <enter purpose here>
 *
 *  Author:   andyrozman {andy@atech-software.com}
 *
 */
package ggc.gui.dialogs;

import ggc.core.db.hibernate.DoctorH;
import ggc.core.util.DataAccess;
import ggc.gui.dialogs.defs.ButtonDef;
import ggc.gui.dialogs.defs.GUIListDefAbstract;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

import com.atech.help.HelpCapable;
import com.atech.i18n.I18nControlAbstract;
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
 *  Filename:     zzz  
 *  Description:  zzz
 * 
 *  Author: andyrozman {andy@atech-software.com}  
 */

// fix this

// list of stocks and all entry

public class GUIListDialog extends JDialog implements ActionListener, HelpCapable, ItemListener, DocumentListener
{

    private DataAccess m_da = DataAccess.getInstance();
    private I18nControlAbstract m_ic = m_da.getI18nControlInstance();

//x    private boolean m_actionDone = false;

//x    private JTextField tfName;
    private JComboBox cb_template = null;
    private JTable t_stocks = null;
//x    private String[] schemes_names = null;

    private String sel_combo = null;
    
    
    GregorianCalendar gc = null;
    GUIListDefAbstract definition;
    JTextField tf_filter;
    
    /**
     * Filter Type
     */
    public String[] filter_types = 
    {
        m_ic.getMessage("FILTER_VISIBLE"),
        m_ic.getMessage("FILTER_ALL")
    };

    @SuppressWarnings("unused")
    private ArrayList<DoctorH> list_full;
    private ArrayList<DoctorH> active_list = new ArrayList<DoctorH>();
    
    
    JButton help_button;
    Font font_normal, font_normal_bold;


    /**
     * Constructor
     * 
     * @param frame
     */
    public GUIListDialog(JFrame frame, GUIListDefAbstract def) 
    {
        super(frame, "", true);

        this.definition = def;
        
        this.setSize(this.definition.getWindowSize());
        m_da.centerJDialog(this, frame);
        
//        setBounds(x-175, y-150, 450, 380);
        this.setLayout(null);

        init();

        this.list_full = new ArrayList<DoctorH>();
        populateList();
        
        //this.cb_template.setSelectedIndex(type-1);

        this.setVisible(true);
    }



    private void init() 
    {

        ATSwingUtils.initLibrary();
        
        JPanel panel = new JPanel();
        panel.setBounds(0, 0, this.getWidth(), this.getHeight());
        panel.setLayout(null);
    
        this.getContentPane().add(panel);

        JLabel label = new JLabel(m_ic.getMessage("STOCKS_LIST"));
        label.setFont(m_da.getFont(DataAccess.FONT_BIG_BOLD));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(0, 20, this.getWidth(), 35);
        panel.add(label);
        
        int y = 80;
        
        if (this.definition.hasFilter())
        {
            
            ATSwingUtils.getLabel(this.definition.getFilterTexts()[0], 
                40, y, 150, 25, panel, ATSwingUtils.FONT_NORMAL_BOLD);
            
            JComboBox cb = ATSwingUtils.getComboBox(this.definition.getFilterOptions(), 
                200, y, 220, 25, panel, ATSwingUtils.FONT_NORMAL_BOLD);
            
            sel_combo = this.definition.getFilterOptions()[0];
            cb.addItemListener(this);

            
            if (this.definition.getFilterType()==GUIListDefAbstract.FILTER_COMBO_AND_TEXT)
            {
                y += 30;
                
                ATSwingUtils.getLabel(this.definition.getFilterTexts()[1], 
                    40, y, 150, 25, panel, ATSwingUtils.FONT_NORMAL_BOLD);
                
                tf_filter = ATSwingUtils.getTextField("", 200, y, 220, 25, panel);
                
                
                Document styledDoc = tf_filter.getDocument();
                AbstractDocument doc = (AbstractDocument)styledDoc;
                doc.addDocumentListener(this);
                
                
                
            }
            
    
            /*
            label = new JLabel(m_ic.getMessage("FILTER") + ":" );
            label.setFont(this.font_normal_bold);
            label.setBounds(40, 75, 100, 25);
            panel.add(label);*/
        
        /*
            cb_template = new JComboBox(filter_types);
            cb_template.setFont(this.font_normal);
            cb_template.setBounds(120, 75, 80, 25);
            panel.add(cb_template); */
        }
            
        
        
        this.t_stocks = new JTable(new AbstractTableModel()
            {


        /**
                 * 
                 */
                private static final long serialVersionUID = 9088931662879087375L;

        public int getColumnCount()
        {
            // TODO Auto-generated method stub
            return 2;
        }

        public int getRowCount()
        {
            active_list.size();
            return 0;
        }

        public Object getValueAt(int row, int column)
        {
            // TODO Auto-generated method stub
            DoctorH dh = active_list.get(row);
            
            switch(column)
            {
            case 0:
                return dh.getName();
                
            case 1:
                return m_ic.getMessage(dh.getDoctor_type().getName());
            }
            
            return null;
        }
                
            })  ;  
        
        Rectangle r = this.definition.getTableSize(y + 40);
        
        JScrollPane scp = new JScrollPane(this.t_stocks);
        scp.setBounds(r);
        panel.add(scp);
            

        int pos_x = r.x + r.width + 20; 
        int pos_y = r.y;
        
        int pic_size[] = {22,22};
        
        for(int i=0; i<this.definition.getButtonDefinitions().size(); i++)
        {
            ButtonDef bd = this.definition.getButtonDefinitions().get(i);
            
            JButton b = ATSwingUtils.getButton("   " + bd.text, 
                pos_x, pos_y + (i *40), 120, 30, panel, 
                ATSwingUtils.FONT_NORMAL , bd.icon_name, bd.action, 
                this, m_da, pic_size);
            b.setHorizontalAlignment(JButton.LEFT);
        }
        
        
        JButton b = ATSwingUtils.getButton("   " + m_ic.getMessage("CLOSE"), 
            pos_x, r.y + r.height + 20, 120, 30, panel, 
            ATSwingUtils.FONT_NORMAL , "exit.png", "close", 
            this, m_da, pic_size);
        b.setHorizontalAlignment(JButton.LEFT);
        
        help_button = m_da.createHelpButtonByBounds(pos_x - 120 - 20, r.y + r.height + 20, 120, 30, 
                                                    panel, ATSwingUtils.FONT_NORMAL);
        help_button.setHorizontalAlignment(JButton.LEFT);
        panel.add(help_button);
        
        

    }


    private void populateList()
    {
    }
    
    
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
    
        if (action.equals("close"))
        {
            this.dispose();
        }
        else
        {
            this.definition.doTableAction(action);
        }

    }



    public Component getComponent()
    {
        return this;
    }



    public JButton getHelpButton()
    {
        // TODO Auto-generated method stub
        return null;
    }



    public String getHelpId()
    {
        // TODO Auto-generated method stub
        return null;
    }



    public void itemStateChanged(ItemEvent e)
    {
        String s = (String)e.getItem();
        
        if (!s.equals(sel_combo))
        {
            sel_combo = s;
            this.definition.setFilterCombo(s);
        }
    }



    public void changedUpdate(DocumentEvent e)
    {
    }



    public void insertUpdate(DocumentEvent e)
    {
        this.definition.setFilterText(this.tf_filter.getText());
    }



    public void removeUpdate(DocumentEvent e)
    {
        this.definition.setFilterText(this.tf_filter.getText());
    }


}
