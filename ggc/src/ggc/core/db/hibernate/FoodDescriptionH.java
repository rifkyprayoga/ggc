package ggc.core.db.hibernate;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class FoodDescriptionH implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7477410336917496421L;

    /** identifier field */
    private long id;

    /** nullable persistent field */
    private long group_id;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String name_i18n;

    /** nullable persistent field */
    private float refuse;

    /** nullable persistent field */
    private String nutritions;

    /** nullable persistent field */
    private String home_weights;

    /** full constructor 
     * @param group_id 
     * @param name 
     * @param name_i18n 
     * @param refuse 
     * @param nutritions 
     * @param home_weights */
    public FoodDescriptionH(long group_id, String name, String name_i18n, float refuse, String nutritions, String home_weights) {
        this.group_id = group_id;
        this.name = name;
        this.name_i18n = name_i18n;
        this.refuse = refuse;
        this.nutritions = nutritions;
        this.home_weights = home_weights;
    }

    /** default constructor */
    public FoodDescriptionH() {
    }

    /**
     * Get Id
     * 
     * @return
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * Set Id
     * 
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }


    public long getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    /**
     * Get Name
     * 
     * @return name
     */
    public String getName() 
    {
        return this.name;
    }

    /**
     * Set Name
     * 
     * @param name as string
     */
    public void setName(String name) 
    {
        this.name = name;
    }

    /**
     * Get Name (I18n)
     * 
     * @return name
     */
    public String getName_i18n() 
    {
        return this.name_i18n;
    }

    /**
     * Set Name (I18n)
     * 
     * @param name_i18n as string
     */
    public void setName_i18n(String name_i18n) 
    {
        this.name_i18n = name_i18n;
    }

    public float getRefuse() {
        return this.refuse;
    }

    public void setRefuse(float refuse) {
        this.refuse = refuse;
    }

    public String getNutritions() {
        return this.nutritions;
    }

    public void setNutritions(String nutritions) {
        this.nutritions = nutritions;
    }

    public String getHome_weights() {
        return this.home_weights;
    }

    public void setHome_weights(String home_weights) {
        this.home_weights = home_weights;
    }


    public boolean equals(Object other) {
        if ( !(other instanceof FoodDescriptionH) ) return false;
        FoodDescriptionH castOther = (FoodDescriptionH) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    /**
     * To String
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() 
    {
        return new ToStringBuilder(this).append("id", getId()).toString();
    }
    
    
    /**
     * Create Hash Code
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() 
    {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

}
