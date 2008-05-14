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
 *  Filename: NutritionTreeDialog
 *  Purpose:  Main class for displaying nutrition information.
 *
 *  Author:   andyrozman
 */

package ggc.core.nutrition.dialogs;
 
import ggc.core.nutrition.display.DailyFoodEntryDisplay;
import ggc.core.nutrition.display.NutritionDataDisplay;
import ggc.core.nutrition.panels.PanelMealSelector;
import ggc.core.util.DataAccess;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.atech.i18n.I18nControlAbstract;


/**
 * Main Dialog used for selecting meals and foods for Daily Values.
 * @author arozman
 *
 */
public class DailyValuesMealSelectorDialog extends JDialog implements ActionListener
{

    //private JPanel mainPane;
    //private JTree tree;
    
    JTextField tf_selected;
    JComboBox cb_type;
    //JLabel label_item, label_item_type;
    Font font_normal, font_normal_b;
    JButton button_select;

    private DataAccess m_da = null;
    private I18nControlAbstract ic = null;
    
    //Object action_object;
    //int action_object_type = 0;
    //long input_id = 0L;
    
    String[] type;  

    //private NumberFormat amountDisplayFormat;
    //private NumberFormat amountEditFormat;    
    //JFormattedTextField amountField;
    
    //MealPart meal_part;
    
    String meals_ids = null;
    PanelMealSelector panel_meal_selector = null;
    
    
    public DailyValuesMealSelectorDialog(DataAccess da, String meals_id) 
    {
        super(da.getParent(), "", true);

        m_da = da;
        ic = m_da.getNutriI18nControl();

	this.setTitle(ic.getMessage("MEALS_FOODS_SELECTOR_DAILY"));
	//this.input_id = meal_id;
        
        this.setBounds(160, 100, 550, 500);
        this.meals_ids = meals_id;
        init();
 
        this.setVisible(true);
    }

        
    
    public void init()
    {
	this.panel_meal_selector = new PanelMealSelector(this, this, this.meals_ids);
	//PanelMealSelector panel = new PanelMealSelector(this, this); 
	
	/*
	this.setLayout(null);

	type = new String[3];
	type[0] = ic.getMessage("USDA_NUTRITION");
	type[1] = ic.getMessage("USER_NUTRITION");
	type[2] = ic.getMessage("MEAL");
	
	
        font_normal_b = m_da.getFont(DataAccess.FONT_NORMAL_BOLD);
        font_normal = m_da.getFont(DataAccess.FONT_NORMAL);
	
	
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 300, 440);

        
        // sub panel - selector
        
        JPanel panel1 = new JPanel();
        panel1.setBorder(new TitledBorder(ic.getMessage("SELECTOR")));
        panel1.setLayout(null);
        panel1.setBounds(10, 10, 270, 140);
        panel.add(panel1, null);
        
        //new javax.swing.border.TitledBorder("Some");
        
        //javax.swing.border.
        
        JLabel label = new JLabel(ic.getMessage("NUTRITION_TYPE_FOR_SELECTOR"));
        label.setBounds(30, 20, 220, 25);
        label.setFont(font_normal_b);
        panel1.add(label, null);
        
        cb_type = new JComboBox(type);
        cb_type.setBounds(30, 50, 210, 25);
        panel1.add(cb_type);

        
        button_select = new JButton(ic.getMessage("SELECT_ITEM"));
        button_select.setActionCommand("select_item");
        button_select.setBounds(30, 95, 210, 25);
        button_select.addActionListener(this);
        panel1.add(button_select, null);
        
        //if (meal_part!=null)
        //    button.setEnabled(false);

        // sub panel - selected item
        
        JPanel panel2 = new JPanel();
        panel2.setBorder(new TitledBorder(ic.getMessage("SELECTED_ITEM")));
        panel2.setLayout(null);
        panel2.setBounds(10, 160, 270, 135);
        panel.add(panel2, null);
        
        
        label = new JLabel(ic.getMessage("NUTRITION_TYPE")+ ":");
        label.setBounds(20, 25, 220, 25);
        label.setFont(font_normal_b);
        panel2.add(label, null);
        
        label_item_type = new JLabel(ic.getMessage("NONE"));
        label_item_type.setBounds(20, 45, 220, 25);
        label_item_type.setFont(font_normal);
        panel2.add(label_item_type, null);
        
        label = new JLabel(ic.getMessage("SELECTED_ITEM") + ":");
        label.setBounds(20, 75, 220, 25);
        label.setFont(font_normal_b);
        panel2.add(label, null);
        
        label_item = new JLabel(ic.getMessage(ic.getMessage("NONE")) );
        label_item.setBounds(20, 95, 220, 25);
        label_item.setFont(font_normal);
        panel2.add(label_item, null);

        
        

        JPanel panel3 = new JPanel();
        panel3.setBorder(new TitledBorder(ic.getMessage("AMOUNT")));
        panel3.setLayout(null);
        panel3.setBounds(10, 310, 270, 65);
        panel.add(panel3, null);
        

        label = new JLabel(ic.getMessage("AMOUNT") + ":");
        label.setBounds(20, 25, 100, 25);
        panel3.add(label, null);
        
        
        //JFormattedTextField jftf = JFormattedTextField(percentFormat);

        amountDisplayFormat = NumberFormat.getNumberInstance();
        amountDisplayFormat.setMinimumFractionDigits(1);
        amountEditFormat = NumberFormat.getNumberInstance();        
        amountEditFormat.setMinimumFractionDigits(1);
        
        
        amountField = new JFormattedTextField(
                new DefaultFormatterFactory(
                    new NumberFormatter(amountDisplayFormat),
                    new NumberFormatter(amountDisplayFormat),
                    new NumberFormatter(amountEditFormat)));
        amountField.setValue(new Double(1.0d));
        amountField.setColumns(4);
        amountField.setBounds(140, 25, 100, 25);
        amountField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        panel3.add(amountField);
        
      /*
        percentDisplayFormat = NumberFormat.getPercentInstance();
        percentDisplayFormat.setMinimumFractionDigits(2);
        percentEditFormat = NumberFormat.getNumberInstance();
        percentEditFormat.setMinimumFractionDigits(2);
        */        
        
        //amountField.addPropertyChangeListener("value", this);        
/*        
        JButton button = new JButton(ic.getMessage("OK"));
        button.setActionCommand("ok");
        button.setBounds(65, 387, 80, 25);
        button.addActionListener(this);
        panel.add(button, null);
        
        button = new JButton(ic.getMessage("CANCEL"));
        button.setActionCommand("cancel");
        button.setBounds(160, 387, 80, 25);
        button.addActionListener(this);
        panel.add(button);
        		*/
        this.add(this.panel_meal_selector, null); 
    }
    

    

/*    
    public float getAmountValue()
    {
	Object o = this.amountField.getValue();
	
	if (o instanceof Long)
	{
	    //System.out.println("Amount(long): " + this.amountField.getValue());
	    Long l = (Long)o;
	    return l.floatValue();
	}
	else
	{
	
	    //System.out.println("Amount(double): " + this.amountField.getValue());
	
	    Double d = (Double)this.amountField.getValue();
	    return d.floatValue();
	}
    }
  */  

    private boolean action_done = false;
    
    public void actionPerformed(ActionEvent e)
    {
	String cmd = e.getActionCommand();
	
	if (cmd.equals("ok"))
	{
	    action_done = true;
	    this.dispose();
	}
	else if (cmd.equals("cancel"))
	{
	    action_done = false;
	    //System.out.println("Cancel");
	    this.dispose();
	}
	/*else if (cmd.equals("add_food"))
	{
	    MealSpecialSelectorDialog mssd = new MealSpecialSelectorDialog(m_da, 0L);
	    DailyFoodEntryDisplay dfed = new DailyFoodEntryDisplay(ic, mssd.getDailyFoodEntry());
	    
	    this.panel_meal_selector.addFoodPart(dfed);
	}
        else if (cmd.equals("edit_food"))
        {
            
            if (this.panel_meal_selector.getFoodTable().getSelectedRowCount()==0)
            {
                JOptionPane.showConfirmDialog(this, ic.getMessage("SELECT_ITEM_FIRST"), ic.getMessage("ERROR"), JOptionPane.CLOSED_OPTION);
                return;
            }

            NutritionDataDisplay ndd = this.list_nutritions.get(this.table_1.getSelectedRow());

            FoodPartMainSelectorDialog fpmsd = new FoodPartMainSelectorDialog(m_da, ndd);            
            
            if (fpmsd.wasAction())
            {
        	System.out.println("Returned value: " + fpmsd.getAmountValue());
        	
        	ndd.setAmount(fpmsd.getAmountValue());
        	this.createModel(this.list_nutritions, this.table_1, this.ndd);
            }
            
        }
        else if (action.equals("remove_nutrition"))
        {
            if (this.table_1.getSelectedRowCount()==0)
            {
                JOptionPane.showConfirmDialog(this, ic.getMessage("SELECT_ITEM_FIRST"), ic.getMessage("ERROR"), JOptionPane.CLOSED_OPTION);
                return;
            }

            int ii = JOptionPane.showConfirmDialog(this, ic.getMessage("ARE_YOU_SURE_DELETE"), ic.getMessage("ERROR"), JOptionPane.YES_NO_OPTION);

            if (ii==JOptionPane.YES_OPTION)
            {
                NutritionDataDisplay ndd = this.list_nutritions.get(this.table_1.getSelectedRow());
        	
                this.list_nutritions.remove(ndd);
        	this.createModel(this.list_nutritions, this.table_1, this.ndd);
            }
            
        }*/
	
	/*else if (cmd.equals("select_item"))
	{
	    //System.out.println("Select item");
	    
	    NutritionTreeDialog ntd = new NutritionTreeDialog(m_da, this.cb_type.getSelectedIndex()+1, true);
	    
	    if (ntd.wasAction())
	    {
		
		if (this.cb_type.getSelectedIndex()==2)
		{
		    Meal m = (Meal)ntd.getSelectedObject();

		    if (m.getId() == this.input_id)
		    {
			JOptionPane.showMessageDialog(this, ic.getMessage("CANT_SELECT_CIRCULAR_MEAL"), ic.getMessage("WARNING"),JOptionPane.WARNING_MESSAGE);
			return;
		    }
		}
		
		
		this.label_item_type.setText("" + this.cb_type.getSelectedItem());
		
		this.action_object_type = (this.cb_type.getSelectedIndex() + 1);
		this.action_object = (ntd.getSelectedObject());
		
		if (this.cb_type.getSelectedIndex() < 2)
		{
		    FoodDescription fd = (FoodDescription)this.action_object;
		    this.label_item.setText(fd.getName());
		}
		else
		{
		    Meal m = (Meal)this.action_object;
		    this.label_item.setText(m.getName());
		}
	    }
	}*/
	else
	    System.out.println("DailyValuesMealSelectorDialog::unknown command = " + cmd);
	    
    }

    
    public boolean wasAction()
    {
	return this.action_done;
    }
    
    public String getStringForDb()
    {
	return this.panel_meal_selector.getStringForDb();
    }
    
    public String getCHSum()
    {
	return this.panel_meal_selector.getCHSumString();
    }
    
    
    public static void main(String[] args)
    {
	new DailyValuesMealSelectorDialog(DataAccess.getInstance(), null);
    }
    
    
    
}
