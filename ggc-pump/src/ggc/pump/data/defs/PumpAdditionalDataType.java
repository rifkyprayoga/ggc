package ggc.pump.data.defs;

import com.atech.utils.data.CodeEnumWithTranslation;
import ggc.pump.data.PumpValuesEntryExt;
import ggc.pump.util.DataAccessPump;

import java.util.ArrayList;
import java.util.Hashtable;

import com.atech.i18n.I18nControlAbstract;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       Pump Tool (support for Pump devices)
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
 *  Filename:     PumpAdditionalDataType  
 *  Description:  Pump Additional Data Types 
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public enum PumpAdditionalDataType implements CodeEnumWithTranslation
{

    Activity(1, "ADD_DATA_ACTIVITY"), //
    Comment(2, "ADD_DATA_COMMENT"), //
    BloodGlucose(3, "ADD_DATA_BG"), //
    Urine(4, "ADD_DATA_URINE"), //
    Carbohydrates(5, "ADD_DATA_CH"), //
    FoodDb(6, "ADD_DATA_FOOD_DB"), //
    FoodDescription(7, "ADD_DATA_FOOD_DESC")
    ;



    /**
     * Additional data description
     */
    public static String[] addata_desc = null;



    /**
     * Get Type Description
     * 
     * @param idx index
     * @return
     */
    public String getTypeDescription(int idx)
    {
        return this.addata_desc[idx];
    }

    /**
     * Get Descriptions (array)
     * 
     * @return array of strings with description
     */
    public String[] getDescriptions()
    {
        return this.addata_desc;
    }




    static Hashtable<String, PumpAdditionalDataType> translationMapping = new Hashtable<String, PumpAdditionalDataType>();
    static Hashtable<Integer, PumpAdditionalDataType> codeMapping = new Hashtable<Integer, PumpAdditionalDataType>();

    static
    {
        I18nControlAbstract ic = DataAccessPump.getInstance().getI18nControlInstance();

        for (PumpAdditionalDataType pbt : values())
        {
            pbt.setTranslation(ic.getMessage(pbt.i18nKey));
            translationMapping.put(pbt.getTranslation(), pbt);
            codeMapping.put(pbt.code, pbt);
        }

        String[] addata_desc_lcl = { ic.getMessage("SELECT_ADDITIONAL_DATA"), ic.getMessage("ADD_DATA_ACTIVITY"),
                ic.getMessage("ADD_DATA_COMMENT"), ic.getMessage("ADD_DATA_BG"),
                ic.getMessage("ADD_DATA_URINE"), ic.getMessage("ADD_DATA_CH"),
                ic.getMessage("ADD_DATA_FOOD_DB"), ic.getMessage("ADD_DATA_FOOD_DESC"), };
    }

    int code;
    String i18nKey;
    String translation;

    private PumpAdditionalDataType(int code, String i18nKey)
    {
        this.code = code;
        this.i18nKey = i18nKey;
    }


    public String getTranslation()
    {
        return translation;
    }

    public void setTranslation(String translation)
    {
        this.translation = translation;
    }

    public int getCode()
    {
        return code;
    }

    public String getI18nKey()
    {
        return i18nKey;
    }


    /**
     * Get Type from Description
     *
     * @param str
     *            type as string
     * @return type as int
     */
    public int getTypeFromDescription(String str)
    {
        if (translationMapping.containsKey(str))
        {
            return translationMapping.get(str).getCode();
        }
        else
        {
            return 0;
        }
    }


    public static PumpAdditionalDataType getByDescription(String description)
    {
        if (translationMapping.containsKey(description))
        {
            return translationMapping.get(description);
        }
        else
        {
            return PumpAdditionalDataType.Comment;
        }
    }

    public static PumpAdditionalDataType getByCode(int code)
    {
        if (codeMapping.containsKey(code))
        {
            return codeMapping.get(code);
        }
        else
        {
            return PumpAdditionalDataType.Comment;
        }
    }

    public static Object[] createItems(Hashtable<String, PumpValuesEntryExt> old_data)
    {
        ArrayList<String> items = new ArrayList<String>();

        for (int i = 1; i < addata_desc.length; i++)
        {
            if (!old_data.containsKey(addata_desc[i]))
            {
                items.add(addata_desc[i]);
            }
        }

        return items.toArray();
    }


}
