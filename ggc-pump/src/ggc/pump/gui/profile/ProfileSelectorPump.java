package ggc.pump.gui.profile;

import ggc.plugin.cfg.DeviceConfigEntry;
import ggc.pump.manager.PumpManager;
import ggc.pump.manager.company.AbstractPumpDeviceCompany;
import ggc.pump.util.DataAccessPump;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.atech.graphics.dialogs.selector.ColumnSorter;
import com.atech.graphics.dialogs.selector.SelectableInterface;
import com.atech.graphics.dialogs.selector.SelectorAbstractDialog;

// TODO: Auto-generated Javadoc
/**
 * The Class ProfileSelector.
 */
public class ProfileSelectorPump extends SelectorAbstractDialog
{

    private static final long serialVersionUID = -1061136628528659695L;

    /** 
     * The da_local. 
     */
    DataAccessPump da_local = DataAccessPump.getInstance();

    /**
     * Constructor
     * 
     * @param parent
     * @param da
     */
    public ProfileSelectorPump(DataAccessPump da, JFrame parent) // ,
                                                                 // DataAccessPump
                                                                 // da)
    {
        super(parent, da, 0, null, true);
        da_local = da;
        this.showDialog();
    }

    /**
     * Constructor
     * 
     * @param parent
     * @param da
     */
    public ProfileSelectorPump(DataAccessPump da, JDialog parent) // ,
                                                                  // DataAccessPump
                                                                  // da)
    {
        super(parent, da, 0, null, true);
        da_local = da;
        this.showDialog();
    }

    // /**
    // * Constructor
    // *
    // * @param parent
    // * @param da
    // */
    // public ProfileSelector(JFrame parent, ATDataAccessAbstract da)
    // {
    // super(parent, da, 0, null, true);
    // da_local = (DataAccessPump)da;
    // this.showDialog();
    // }

    /** 
     * Check And Execute Action Edit
     */
    @Override
    public void checkAndExecuteActionEdit(SelectableInterface si)
    {
        /*
         * ProfileEditor pe = new ProfileEditor(this, (PumpProfileH)si);
         * pe.setVisible(true);
         * if (pe.actionSuccessful())
         * {
         * this.filterEntries();
         * }
         */

    }

    /** 
     * Check And Execute Action New
     */
    @Override
    public void checkAndExecuteActionNew()
    {

    }

    /** 
     * Check And Execute Action Select
     */
    @Override
    public void checkAndExecuteActionSelect()
    {
    }

    /** 
     * Get Full Data
     */
    @Override
    public void getFullData()
    {
        da_local = DataAccessPump.getInstance();
        this.full = new ArrayList<SelectableInterface>();
        this.full.addAll(getList());
    }

    /**
     * Get List
     * 
     * @return
     */
    public ArrayList<PumpProfileType> getList()
    {
        ArrayList<PumpProfileType> lst_out = new ArrayList<PumpProfileType>();
        // AbstractPumpDeviceCompany adc =
        // (AbstractPumpDeviceCompany)PumpManager.getInstance().getCompany("Minimed");
        // Vector<AbstractDeviceCompany> lst =
        // PumpManager.getInstance().getCompanies();
        DeviceConfigEntry de = DataAccessPump.getInstance().getDeviceConfiguration().getSelectedDeviceInstance();

        AbstractPumpDeviceCompany adc = (AbstractPumpDeviceCompany) PumpManager.getInstance().getCompany(
            de.device_company);
        String[] profiles = adc.getProfileNames();

        for (String profile : profiles)
        {
            lst_out.add(new PumpProfileType(profile));
        }

        return lst_out;
    }

    /**
     * Init Selector Values For Type
     */
    @Override
    public void initSelectorValuesForType()
    {
        setSelectorObject(new PumpProfileType());
        setSelectorName(ic.getMessage("PUMP_PROFILE_SELECTOR"));
        this.descriptions = new Hashtable<String, String>();
        this.descriptions.put("DESC_1", ic.getMessage("NAME_OF_PROFILE"));
        setAllowedActions(SelectorAbstractDialog.SELECTOR_ACTION_CANCEL | SelectorAbstractDialog.SELECTOR_ACTION_SELECT);
        this.setColumnSortingEnabled(false);
        this.setFilterType(SelectorAbstractDialog.SELECTOR_FILTER_TEXT);
        this.setHelpStringId("PumpTool_Profile_Type_Selector");
        this.setHelpEnabled(true);
        use_generic_select = true;
        // setNewItemString(ic.getMessage("NEW__PROFILE"));
    }

    /** 
     * Item State Changed
     */
    @Override
    public void itemStateChanged(ItemEvent e)
    {
    }

    private class PumpProfileType implements SelectableInterface
    {
        String name;

        public PumpProfileType()
        {

        }

        public PumpProfileType(String name)
        {
            this.name = name;
        }

        public int compareTo(SelectableInterface o)
        {

            // TODO Auto-generated method stub
            return 0;
        }

        public int getColumnCount()
        {
            return 1;
        }

        public String getColumnName(int num)
        {
            return ic.getMessage("NAME");
        }

        public String getColumnValue(int num)
        {
            return name;
        }

        public Object getColumnValueObject(int num)
        {
            return name;
        }

        public int getColumnWidth(int num, int width)
        {
            return width;
        }

        public long getItemId()
        {
            return 0;
        }

        public String getShortDescription()
        {
            return this.name;
        }

        public boolean isFound(String text)
        {
            return name.contains(text);
        }

        public boolean isFound(int value)
        {
            return true;
        }

        public boolean isFound(int from, int till, int state)
        {
            return true;
        }

        public void setColumnSorter(ColumnSorter cs)
        {
            // TODO Auto-generated method stub

        }

        public void setSearchContext()
        {
            // TODO Auto-generated method stub

        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }

}
