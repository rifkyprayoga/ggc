package ggc.pump.data.defs;

import com.atech.i18n.I18nControlAbstract;
import com.atech.utils.data.CodeEnumWithTranslation;
import ggc.pump.util.DataAccessPump;

import java.util.Hashtable;

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
 *  Filename:     PumpEvents  
 *  Description:  Pump Events 
 * 
 *  Author: Andy {andy@atech-software.com}
 */

public enum PumpEvents implements CodeEnumWithTranslation
{

    None(0, "NONE"), //
    PrimeInfusionSet(1, "EVENT_PRIME_INFUSION_SET"), //
    CartridgeChange(2, "EVENT_CARTRIDGE_CHANGED"), //
    CartridgeRewind(3, "EVENT_REWIND_INFUSION_SET"), //
    ReservoirLow(4, "EVENT_RESERVOIR_LOW"), //
    ReservoirLowDesc(5, "EVENT_RESERVOIR_LOW_DESC"), //
    FillCannula(6, "EVENT_FILL_CANNULA"), //
    SetTemporaryBasalRateType(10, "EVENT_SET_TEMPORARY_BASAL_RATE_TYPE"), //  Unit setting (1=%, 0=U)
    SetBasalPattern(15, "EVENT_SET_BASAL_PATTERN"), //

    BasalRun(20, "EVENT_BASAL_RUN"), //
    BasalStop(21, "EVENT_BASAL_STOP"), //
    PowerDown(22, "EVENT_POWER_DOWN"), //
    PowerUp(23, "EVENT_POWER_UP"), //

    SelfTest(30, "EVENT_SELF_TEST"), //
    Download(31, "EVENT_DOWNLOAD"), //

    DateTimeSet(40, "EVENT_DATETIME_SET"), //
    DateTimeCorrect(41, "EVENT_DATETIME_CORRECT"), //

    SetMaxBasal(50, "EVENT_SET_MAX_BASAL"), //
    SetMaxBolus(51, "EVENT_SET_MAX_BOLUS"), //

    BatteryRemoved(55, "EVENT_BATERRY_REMOVED"), //
    BatteryReplaced(56, "EVENT_BATERRY_REPLACED"), //
    BatteryLow(57, "EVENT_BATERRY_LOW"), //
    BatteryLowDesc(58, "EVENT_BATERRY_LOW_DESC"), //

    BgFromMeter(70, "EVENT_BG_FROM_METER"), //
    BolusCancelled(80, "ALARM_BOLUS_CANCELED"), //

    BolusWizard(81, "EVENT_BOLUS_WIZARD", "BG=%s;CH=%s;CH_UNIT=%s;" +
            "CH_INS_RATIO=%s;BG_INS_RATIO=%s;BG_TARGET_LOW=%s;BG_TARGET_HIGH=%s;BOLUS_TOTAL=%s;" +
            "BOLUS_CORRECTION=%s;BOLUS_FOOD=%s;UNABSORBED_INSULIN=%s"), //

    ;



    static String[] descriptions;

    static Hashtable<String, PumpEvents> translationMapping = new Hashtable<String, PumpEvents>();
    static Hashtable<Integer, PumpEvents> codeMapping = new Hashtable<Integer, PumpEvents>();

    static
    {
        I18nControlAbstract ic = DataAccessPump.getInstance().getI18nControlInstance();

        for (PumpEvents pbt : values())
        {
            pbt.setTranslation(ic.getMessage(pbt.i18nKey));
            translationMapping.put(pbt.getTranslation(), pbt);
            codeMapping.put(pbt.code, pbt);
        }

        String[] descriptions_lcl = {
                ic.getMessage("SELECT_SUBTYPE"),
                ic.getMessage("EVENT_PRIME_INFUSION_SET"),
                ic.getMessage("EVENT_CARTRIDGE_CHANGED"),
                ic.getMessage("EVENT_REWIND_INFUSION_SET"),
                ic.getMessage("EVENT_RESERVOIR_LOW"),
                ic.getMessage("EVENT_RESERVOIR_LOW_DESC"),
                ic.getMessage("EVENT_FILL_CANNULA"),
                ic.getMessage("EVENT_SET_TEMPORARY_BASAL_RATE_TYPE"),
                ic.getMessage("EVENT_SET_BASAL_PATTERN"),
                ic.getMessage("EVENT_BASAL_RUN"),

                ic.getMessage("EVENT_BASAL_STOP"),
                ic.getMessage("EVENT_POWER_DOWN"),
                ic.getMessage("EVENT_POWER_UP"),
                ic.getMessage("EVENT_SELF_TEST"),
                ic.getMessage("EVENT_DOWNLOAD"),
                ic.getMessage("EVENT_DATETIME_SET"),
                ic.getMessage("EVENT_DATETIME_CORRECT"),

                ic.getMessage("EVENT_SET_MAX_BASAL"),
                ic.getMessage("EVENT_SET_MAX_BOLUS"),
                ic.getMessage("EVENT_BATERRY_REMOVED"),

                ic.getMessage("EVENT_BATERRY_REPLACED"),
                ic.getMessage("EVENT_BATERRY_LOW"),
                ic.getMessage("EVENT_BATERRY_LOW_DESC"),
                ic.getMessage("EVENT_BG_FROM_METER"),
                ic.getMessage("ALARM_BOLUS_CANCELED"),
                ic.getMessage("EVENT_BOLUS_WIZARD")
        };

        descriptions = descriptions_lcl;
    }


    int code;
    String i18nKey;
    String translation;
    private String valueTemplate;

    private PumpEvents(int code, String i18nKey)
    {
        this.code = code;
        this.i18nKey = i18nKey;
    }


    private PumpEvents(int code, String i18nKey, String valueTemplate)
    {
        this.code = code;
        this.i18nKey = i18nKey;
        this.setValueTemplate(valueTemplate);
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

    public static PumpEvents getByCode(int code)
    {
        if (codeMapping.containsKey(code))
        {
            return codeMapping.get(code);
        }
        else
        {
            return PumpEvents.None;
        }
    }


    /**
     * Shows if this type of event has value associated with it (most don't)
     * @param type
     * @return
     */
    public boolean hasValue(PumpEvents type)
    {
        switch (type)
        {
            case SetMaxBasal:
            case SetMaxBolus:
            case BatteryLowDesc:
                return true;

            default:
                return false;
        }
    }

    public String getValueTemplate()
    {
        return valueTemplate;
    }

    public void setValueTemplate(String valueTemplate)
    {
        this.valueTemplate = valueTemplate;
    }

    public static String[] getDescriptions()
    {
        return descriptions;
    }

}
