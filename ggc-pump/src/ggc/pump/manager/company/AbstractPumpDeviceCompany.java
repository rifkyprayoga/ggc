package ggc.pump.manager.company;

import ggc.plugin.manager.company.AbstractDeviceCompany;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPumpDeviceCompany.
 */
public abstract class AbstractPumpDeviceCompany extends AbstractDeviceCompany
{
    
    /**
     * The profile_names
     */
    protected String[] profile_names;
    
    
    /**
     * Instantiates a new abstract pump device company
     * 
     * @param ic the ic
     * @param value the value
     */
    public AbstractPumpDeviceCompany(boolean value)
    {
        super(value);
        this.initProfileNames();
    }
    
    
    public AbstractPumpDeviceCompany(int company_id_, String company_name, String short_company_name, String company_desc, int implementation_status)
    {
        super(false, company_id_, company_name, short_company_name, company_desc, implementation_status);
        this.initProfileNames();
    }
    
    
    /**
     * Init Profile Names (for Profile Editor)
     */
    public abstract void initProfileNames();
    
    
    
    
    /**
     * Gets the profile names
     * 
     * @return the profile names
     */
    public String[] getProfileNames()
    {
        return this.profile_names;
    }
    
    
    
}


