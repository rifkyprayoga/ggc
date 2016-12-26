package ggc.pump.gui.manual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import com.atech.graphics.components.JDecimalTextField;
import com.atech.graphics.components.TimeComponent;
import com.atech.i18n.I18nControlAbstract;
import com.atech.utils.ATSwingUtils;

import ggc.core.data.defs.GlucoseUnitType;
import ggc.core.util.DataAccess;
import ggc.shared.bolushelper.BolusHelper;
import ggc.pump.data.PumpValuesEntry;
import ggc.pump.data.PumpValuesEntryExt;
import ggc.pump.data.defs.*;
import ggc.pump.gui.pdtc.TemporaryBasalRateComponent;
import ggc.pump.gui.profile.ProfileSelectorPump;
import ggc.pump.util.DataAccessPump;

/**
 * Application: GGC - GNU Gluco Control
 * Plug-in: Pump Tool (support for Pump devices)
 *
 * See AUTHORS for copyright information.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Filename:     PumpDataTypeComponent
 * Description:  Selection for different base selections and settings
 *
 * Author: Andy {andy@atech-software.com}
 */

// NOTE: This class should replace currenr PumpDataTypeComponent, but it's still
// beeing developed

@Deprecated
public class PumpDataTypeComponent2 extends JPanel implements ActionListener
{

    private static final long serialVersionUID = -4449947661003378689L;

    TemporaryBasalRateComponent temporaryBasalRateComponent = null;
    SquareBolusComponent squareBolusComponent = null;
    JLabel label1, label2, label3, label4;
    JTextField text1, text2;
    JComboBox combo1, combo2;
    JDecimalTextField numericDecimal2Field1, numericDecimal2Field2;
    JRadioButton radioButton1, radioButton2, radioButton3;
    ButtonGroup buttonGroup;
    ProfileComponent profileComponent;
    JButton button1;
    TimeComponent timeComponent;

    PumpBaseType type = PumpBaseType.None;
    int height = 0;
    int width = 400;

    private PumpDataRowDialog m_parent = null;

    private DataAccessPump m_da = DataAccessPump.getInstance();
    private I18nControlAbstract ic = m_da.getI18nControlInstance();

    private Object[] type_items = { ic.getMessage("SELECT_ITEM"), //
                                    ic.getMessage("BASAL_DOSE"), //
                                    ic.getMessage("BOLUS_DOSE"), //
                                    ic.getMessage("EVENT"), //
                                    ic.getMessage("ALARM"), //
                                    ic.getMessage("ERROR"), //
                                    ic.getMessage("REPORT"), //
                                    ic.getMessage("PEN_INJECTION_BASAL"), //
                                    ic.getMessage("PEN_INJECTION_BOLUS"), //
                                    ic.getMessage("ADDITIONAL_DATA") };

    private Map<String, JComponent> componentMap;


    /**
     * Constructor
     *
     * @param parent
     * @param startx
     */
    public PumpDataTypeComponent2(PumpDataRowDialog parent, int startx)
    {
        super();
        this.m_parent = parent;
        this.setLayout(null);
        init();
        this.setBounds(30, startx, width, height);
    }


    /**
     * Init
     */
    public void init()
    {
        ATSwingUtils.initLibrary();

        componentMap = new HashMap<String, JComponent>();

        temporaryBasalRateComponent = new TemporaryBasalRateComponent();
        addToComponents(temporaryBasalRateComponent, "temporaryBasalRateComponent");

        label1 = new JLabel();
        label1.setFont(ATSwingUtils.getFont(ATSwingUtils.FONT_NORMAL_BOLD));
        this.add(label1);
        label2 = new JLabel();
        label2.setFont(ATSwingUtils.getFont(ATSwingUtils.FONT_NORMAL_BOLD));
        this.add(label2);
        label3 = new JLabel();
        label3.setFont(ATSwingUtils.getFont(ATSwingUtils.FONT_NORMAL_BOLD));
        this.add(label3);
        label4 = new JLabel();
        label4.setFont(ATSwingUtils.getFont(ATSwingUtils.FONT_NORMAL_BOLD));
        this.add(label4);

        text1 = new JTextField();
        this.add(text1);
        text2 = new JTextField();
        this.add(text2);

        combo1 = new JComboBox();
        this.add(combo1);
        combo2 = new JComboBox();
        this.add(combo2);

        numericDecimal2Field1 = new JDecimalTextField(new Float(0.0f), 2);
        this.add(numericDecimal2Field1);
        numericDecimal2Field2 = new JDecimalTextField(new Float(0.0f), 2);
        this.add(numericDecimal2Field2);

        combo1.addActionListener(this);
        combo2.addActionListener(this);

        // JRadioButton radioButton1, radioButton2;

        this.radioButton1 = new JRadioButton();
        this.add(radioButton1);
        this.radioButton2 = new JRadioButton();
        this.add(radioButton2);
        this.radioButton3 = new JRadioButton();
        this.add(radioButton3);

        this.buttonGroup = new ButtonGroup();

        profileComponent = new ProfileComponent();
        this.add(profileComponent);

        this.timeComponent = new TimeComponent();
        this.add(timeComponent);

        this.button1 = ATSwingUtils.getButton("", 0, 0, 0, 0, this, ATSwingUtils.FONT_NORMAL, "magic-wand.png",
            "bolus_helper", this, m_da);
        componentMap.put("button1", this.button1);

        this.squareBolusComponent = new SquareBolusComponent();
        addToComponents(this.squareBolusComponent, "squareBolusComponent");

    }


    public void addToComponents(JComponent component, String name)
    {
        this.componentMap.put(name, component);
        this.add(component);
    }


    /**
     * Set Type
     *
     * @param type
     */
    public void setType(PumpBaseType type)
    {
        if (this.type == type)
            return;

        this.type = type;

        switch (this.type)
        {

            case Event:
            case Alarm:
            case Error:
                {
                    this.setComboAndText();
                }
                break;

            case Basal:
                {
                    this.setBasal();
                }
                break;

            case Bolus:
                {
                    this.setBolus();
                }
                break;

            case Report:
                {
                    this.setReport();
                }
                break;

            case PenInjectionBasal:
            case PenInjectionBolus:
                {
                    this.setNumericTextAndText();
                }
                break;

            case None:
            case AdditionalData:
            default:
                {
                    this.setEmpty();
                }
                break;
        }

    }


    /**
     * Get Items
     *
     * @return
     */
    public Object[] getItems()
    {
        return this.type_items;
    }


    private void hideAll()
    {
        for (JComponent component : componentMap.values())
        {
            component.setVisible(false);
        }

        temporaryBasalRateComponent.setVisible(false);
        label1.setVisible(false);
        // label1.setHorizontalAlignment(JLabel.LEFT);
        label2.setVisible(false);
        label3.setVisible(false);
        label4.setVisible(false);
        text1.setVisible(false);
        text2.setVisible(false);
        combo1.setVisible(false);
        combo2.setVisible(false);
        numericDecimal2Field1.setVisible(false);
        numericDecimal2Field2.setVisible(false);
        combo1.setActionCommand("");
        combo2.setActionCommand("");
        radioButton1.setVisible(false);
        radioButton2.setVisible(false);
        radioButton3.setVisible(false);
        profileComponent.setVisible(false);
        this.button1.setVisible(false);
        this.timeComponent.setVisible(false);
        this.squareBolusComponent.setVisible(false);

        this.repaint();
    }


    private void setEmpty()
    {
        this.hideAll();
        setHeight(0);
    }

    /*
     * private void setUnsupported()
     * {
     * this.hideAll();
     * label1.setBounds(0, 20, 370, 25);
     * label1.setText("Component doesn't support this type: " +
     * this.type_items[type]);
     * label1.setVisible(true);
     * this.setHeight(40);
     * }
     */


    // type: event, alarm, error
    private void setComboAndText()
    {
        this.hideAll();

        this.label1.setBounds(0, 22, 150, 25);
        this.label1.setVisible(true);

        this.combo1.setBounds(150, 20, 180, 25);
        this.combo1.setVisible(true);

        this.label2.setBounds(0, 57, 150, 25);
        this.label2.setVisible(true);

        this.text1.setBounds(150, 55, 180, 25);
        this.text1.setVisible(true);

        this.label2.setText(ic.getMessage("COMMENT") + ":");

        this.setHeight(85);

        if (this.type == PumpBaseType.Event)
        {
            this.label1.setText(ic.getMessage("EVENT_TYPE") + ":");
            addAllItems(this.combo1, PumpEventType.getDescriptions());
        }
        else if (this.type == PumpBaseType.Alarm)
        {
            this.label1.setText(ic.getMessage("ALARM_TYPE") + ":");
            addAllItems(this.combo1, PumpAlarms.getDescriptions());
        }
        else if (this.type == PumpBaseType.Error)
        {
            this.label1.setText(ic.getMessage("ERROR_TYPE") + ":");
            addAllItems(this.combo1, PumpErrors.getDescriptions());
            // this.dataAccess.getPumpErrorTypes().getDescriptions());
        }
        else
        {
            this.combo1.removeAllItems();
        }

    }


    // type: pen bolus, pen basal
    private void setNumericTextAndText()
    {
        this.hideAll();

        this.label1.setBounds(0, 22, 150, 25);
        this.label1.setVisible(true);

        this.numericDecimal2Field1.setBounds(150, 20, 180, 25);
        this.numericDecimal2Field1.setVisible(true);
        this.numericDecimal2Field1.setValue(new Float(0.0f));

        this.label2.setBounds(0, 57, 150, 25);
        this.label2.setVisible(true);
        this.label2.setText(ic.getMessage("COMMENT") + ":");

        this.text1.setBounds(150, 55, 180, 25);
        this.text1.setVisible(true);

        this.setHeight(85);

        if (this.type == PumpBaseType.PenInjectionBasal)
        {
            this.label1.setText(ic.getMessage("BASAL_INSULIN") + ":");
        }
        else if (this.type == PumpBaseType.PenInjectionBolus)
        {
            this.label1.setText(ic.getMessage("BOLUS_INSULIN") + ":");
        }

    }


    // type: report
    private void setReport()
    {
        this.hideAll();

        this.label1.setBounds(0, 22, 150, 25);
        this.label1.setVisible(true);
        this.label1.setText(ic.getMessage("REPORT_TYPE") + ":");

        this.combo1.setBounds(150, 20, 180, 25);
        this.combo1.setVisible(true);
        addAllItems(this.combo1, PumpReport.getDescriptions());

        // PumpReportTypes().getDescriptions());

        this.label2.setText(ic.getMessage("REPORT_TEXT") + ":");
        this.label2.setBounds(0, 57, 150, 25);
        this.label2.setVisible(true);

        this.text2.setBounds(150, 55, 180, 25);
        this.text2.setVisible(true);

        this.label3.setBounds(0, 92, 150, 25);
        this.label3.setVisible(true);
        this.label3.setText(ic.getMessage("COMMENT") + ":");

        this.text1.setBounds(150, 90, 180, 25);
        this.text1.setVisible(true);

        this.setHeight(115);

    }

    int sub_type = 0;


    // types: basal
    private void setBasal()
    {
        this.hideAll();
        this.sub_type = 0;

        this.label1.setBounds(0, 17, 150, 25);
        this.label1.setVisible(true);
        this.label1.setText(ic.getMessage("BASAL_TYPE") + ":");

        this.combo1.setBounds(110, 15, 220, 25);
        this.combo1.setVisible(true);
        this.combo1.setActionCommand("basal");
        addAllItems(this.combo1, PumpBasalType.basal_desc); // this.dataAccess.getBasalSubTypes().getDescriptions());

        this.label2.setBounds(0, 57, 150, 25);
        this.label2.setVisible(true);
        this.label2.setText(ic.getMessage("COMMENT") + ":");

        this.text1.setBounds(110, 55, 220, 25);
        this.text1.setVisible(true);

        this.setHeight(85);

    }


    /**
     * Set Basal Sub Type
     *
     * @param stype
     */
    public void setBasalSubType(int stype)
    {
        // 20 55
        if (this.sub_type == stype)
            return;
        else
        {
            this.sub_type = stype;
        }

        int index = m_da.getIndexOfElementInArray(PumpBasalType.getDescriptions(),
            PumpBasalType.getByCode(stype).getTranslation());

        this.combo1.setSelectedIndex(index);

        this.numericDecimal2Field1.setVisible(false);
        this.numericDecimal2Field2.setVisible(false);
        this.label3.setVisible(false);
        this.label4.setVisible(false);
        this.temporaryBasalRateComponent.setVisible(false);

        this.radioButton1.setVisible(false);
        this.radioButton2.setVisible(false);
        this.radioButton3.setVisible(false);

        this.buttonGroup.remove(radioButton1);
        this.buttonGroup.remove(radioButton2);
        this.buttonGroup.remove(radioButton3);

        profileComponent.setVisible(false);

        // comment
        this.label2.setVisible(true);
        this.text1.setVisible(true);

        switch (PumpBasalType.getByCode(this.sub_type))
        {
            case Value:
                {
                    this.label2.setBounds(0, 92, 150, 25);
                    this.text1.setBounds(150, 90, 180, 25);

                    this.numericDecimal2Field1.setBounds(150, 55, 180, 25);
                    this.numericDecimal2Field1.setVisible(true);
                    this.label3.setBounds(0, 55, 150, 25);
                    this.label3.setText(ic.getMessage("AMOUNT") + ":");
                    this.label3.setVisible(true);

                    this.setHeight(115);

                }
                break;

            case TemporaryBasalRate:
                {
                    this.label2.setBounds(0, 92, 150, 25);
                    this.text1.setBounds(150, 90, 180, 25);

                    this.temporaryBasalRateComponent.setBounds(0, 55, 180, 25);
                    this.temporaryBasalRateComponent.setVisible(true);

                    // this.numericDecimal2Field1.setVisible(true);
                    // this.label3.setBounds(0, 55, 150, 25);
                    // this.label3.setText(i18nControlAbstract.getMessage("AMOUNT")
                    // + ":");
                    // this.label3.setVisible(true);

                    this.setHeight(115);

                }
                break;

            case Profile:
                {
                    this.label2.setBounds(0, 90, 150, 25);
                    this.text1.setBounds(150, 90, 180, 25);

                    this.profileComponent.setBounds(0, 55, 180, 25);
                    this.profileComponent.setVisible(true);

                    this.setHeight(115);

                }
                break;

            case TemporaryBasalRateProfile:
                {
                    this.label2.setBounds(0, 127, 150, 25);
                    this.text1.setBounds(150, 125, 180, 25);

                    this.profileComponent.setBounds(0, 55, 180, 25);
                    this.profileComponent.setVisible(true);

                    this.temporaryBasalRateComponent.setBounds(0, 90, 180, 25);
                    this.temporaryBasalRateComponent.setVisible(true);

                    this.setHeight(150);

                }
                break;

            case PumpStatus:
                {
                    this.label2.setBounds(0, 142, 150, 25);
                    this.text1.setBounds(150, 140, 180, 25);

                    this.radioButton1.setText("  " + ic.getMessage("ON"));
                    this.radioButton1.setBounds(150, 55, 200, 25);
                    this.radioButton1.setVisible(true);
                    this.radioButton1.setSelected(true);
                    this.radioButton2.setText("  " + ic.getMessage("OFF"));
                    this.radioButton2.setBounds(150, 80, 200, 25);
                    this.radioButton2.setVisible(true);
                    this.radioButton3.setText("  " + ic.getMessage("SUSPENDED"));
                    this.radioButton3.setBounds(150, 105, 200, 25);
                    this.radioButton3.setVisible(true);

                    this.buttonGroup.add(radioButton1);
                    this.buttonGroup.add(radioButton2);
                    this.buttonGroup.add(radioButton3);

                    // this.numericDecimal2Field1.setBounds(150, 55, 180, 25);
                    // this.numericDecimal2Field1.setVisible(true);

                    this.label3.setBounds(0, 57, 150, 25);
                    this.label3.setText(ic.getMessage("PUMP_STATUS") + ":");
                    this.label3.setVisible(true);

                    this.setHeight(165);

                }
                break;

            default:
                {
                    this.label2.setVisible(false);
                    this.text1.setVisible(false);

                    this.setHeight(55);

                }
                break;
        }

        this.m_parent.realignComponents();

    }


    // types: bolus
    private void setBolus()
    {
        this.hideAll();
        this.sub_type = 0;

        this.label1.setBounds(0, 20, 150, 25);
        this.label1.setVisible(true);
        this.label1.setText(ic.getMessage("BOLUS_TYPE") + ":");

        // System.out.println("DHHHD" +
        // DataAccess.this.dataAccess.getBolusSubTypes().getStaticDescriptionArray());

        this.combo1.setBounds(150, 20, 180, 25);
        this.combo1.setVisible(true);
        this.combo1.setActionCommand("bolus");

        // System.out.println("Bolus: " + PumpBolusType.getDescriptions());
        addAllItems(this.combo1, PumpBolusType.getDescriptions());
        // this.combo1.setSelectedIndex(1);

        this.button1.setBounds(120, 20, 25, 25);
        this.button1.setVisible(true);

        this.label2.setBounds(0, 55, 150, 25);
        this.label2.setVisible(true);
        this.label2.setText(ic.getMessage("COMMENT") + ":");

        this.text1.setBounds(150, 55, 180, 25);
        this.text1.setVisible(true);

        this.sub_type = -1;
        this.setBolusSubType(PumpBolusType.Normal.getCode());
        // this.setHeight(85);

    }


    /**
     * Set Bolus Sub Type
     * @param stype
     */
    public void setBolusSubType(int stype)
    {
        // 20 55
        if (this.sub_type == stype)
            return;
        else
        {
            this.sub_type = stype;
        }

        // fix subtype
        // getIndexFromStaticDescriptionArrayWithID(stype)

        this.combo1.setSelectedIndex(m_da.getIndexOfElementInArray(PumpBolusType.getDescriptions(),
            PumpBolusType.getByCode(stype).getTranslation()));
        // this.dataAccess.getBolusSubTypes().getIndexFromStaticDescriptionArrayWithID(stype));

        this.numericDecimal2Field1.setVisible(false);
        this.numericDecimal2Field2.setVisible(false);
        this.label3.setVisible(false);
        this.label4.setVisible(false);
        this.timeComponent.setVisible(false);
        this.squareBolusComponent.setVisible(false);

        switch (PumpBolusType.getByCode(this.sub_type))
        {
            case Normal:
            case Audio:
                {
                    this.label2.setBounds(0, 90, 150, 25);
                    this.text1.setBounds(150, 90, 180, 25);

                    // this.text1.setVisible(true);
                    // this.label2.setVisible(true);

                    this.numericDecimal2Field1.setBounds(150, 55, 180, 25);
                    this.numericDecimal2Field1.setVisible(true);
                    this.label3.setBounds(0, 55, 150, 25);
                    this.label3.setText(ic.getMessage("AMOUNT") + ":");
                    this.label3.setVisible(true);

                    this.setHeight(115);
                }
                break;

            case Extended:
                {
                    // TODO
                    this.label2.setBounds(0, 90, 150, 25);
                    this.text1.setBounds(110, 90, 220, 25);

                    // this.numericDecimal2Field1.setBounds(150, 55, 180, 25);
                    // this.numericDecimal2Field1.setVisible(true);
                    // this.label3.setBounds(0, 55, 150, 25);
                    // this.label3.setText(i18nControlAbstract.getMessage("AMOUNT")
                    // + ":");
                    // this.label3.setVisible(true);

                    this.squareBolusComponent.setBounds(0, 55, 200, 25);
                    this.squareBolusComponent.setType(SquareBolusComponent.SQUARE_SINGLE);
                    this.squareBolusComponent.setVisible(true);

                    this.setHeight(115);
                }
                break;

            case Multiwave:
                {
                    this.label2.setBounds(0, 125, 150, 25);
                    this.text1.setBounds(150, 125, 180, 25);

                    this.numericDecimal2Field1.setBounds(150, 55, 180, 25);
                    this.numericDecimal2Field1.setVisible(true);
                    this.label3.setBounds(0, 55, 150, 25);
                    this.label3.setText(ic.getMessage("IMMEDIATE_AMOUNT") + ":");
                    this.label3.setVisible(true);

                    // 90
                    /*
                     * this.label4.setText(i18nControlAbstract.getMessage(
                     * "AMOUNT_MW_2") + ":");
                     * label4.setBounds(0, 90, 150, 25);
                     * this.label4.setVisible(true);
                     * this.numericDecimal2Field2.setBounds(150, 90, 180, 25);
                     * this.numericDecimal2Field2.setVisible(true);
                     */
                    // this.timeComponent.setVisible(false);

                    this.squareBolusComponent.setBounds(0, 90, 200, 25);
                    this.squareBolusComponent.setType(SquareBolusComponent.SQUARE_DUAL);
                    this.squareBolusComponent.setVisible(true);

                    this.setHeight(150);

                }
                break;

            case None:
                {
                    /*
                     * this.numericDecimal2Field1.setVisible(false);
                     * this.numericDecimal2Field2.setVisible(false);
                     * this.label3.setVisible(false);
                     * this.label4.setVisible(false);
                     */

                    this.label2.setBounds(0, 55, 150, 25);
                    this.text1.setBounds(150, 55, 180, 25);
                    this.setHeight(85);
                }
                break;

        }

        this.m_parent.realignComponents();

    }


    /**
     * Are Required Elements Set. For checking if elements are set.
     *
     * @return
     */
    public boolean areRequiredElementsSet()
    {
        // System.out.println("!!!! Are Elements Set - Not Implemented !!!!");

        switch (this.type)
        {

            case Basal:
                {
                    switch (PumpBasalType.getByCode(this.sub_type))
                    {
                        case Value:
                            {
                                return m_da.getFloatValue(this.numericDecimal2Field1.getCurrentValue()) >= 0;
                            }

                        case TemporaryBasalRate:
                            {
                                return this.temporaryBasalRateComponent.isValueSet();
                            }

                        case Profile:
                            {
                                return this.profileComponent.isValueSet();
                            }

                        case TemporaryBasalRateProfile:
                            {
                                return this.temporaryBasalRateComponent.isValueSet()
                                        && this.profileComponent.isValueSet();
                            }

                        case PumpStatus:
                            {
                                return this.radioButton1.isSelected() || this.radioButton2.isSelected()
                                        || this.radioButton3.isSelected();
                            }

                        default:
                            return false;
                    }
                }

            case Bolus:
                {
                    switch (PumpBolusType.getByCode(this.sub_type))
                    {
                        case Normal:
                        case Audio:
                            {
                                return this.numericDecimal2Field1.getText().length() > 0;
                            }
                        case Extended:
                            {
                                return this.squareBolusComponent.isValueSet();
                            }

                        case Multiwave:
                            {
                                return m_da.getFloatValue(this.numericDecimal2Field1.getCurrentValue()) > 0
                                        && this.squareBolusComponent.isValueSet();
                            }

                        default:
                            {
                                return false;

                            }

                    }
                }

            case Event:
            case Alarm:
            case Error:
                {
                    return this.combo1.getSelectedIndex() > 0;
                }

            case Report:
                {
                    return this.combo1.getSelectedIndex() > 0 && this.text2.getText().trim().length() != 0;
                }

            case PenInjectionBasal:
            case PenInjectionBolus:
                {
                    return m_da.getFloatValue(this.numericDecimal2Field1.getCurrentValue()) > 0;
                }

            case AdditionalData:
            default:
                return true;

        }

    }


    /**
     * Load Data
     *
     * @param data
     */
    public void loadData(PumpValuesEntry data)
    {
        // System.out.println("Load data not implemented yet !");
        setType(data.getBaseType());
        this.text1.setText(data.getComment());

        // this.combo1.setSelectedIndex(data.getBaseType());
        switch (this.type)
        {
            case Basal:
                {
                    this.setBasalSubType(data.getSubType());

                    switch (PumpBasalType.getByCode(this.sub_type))
                    {
                        case Value:
                            {
                                this.numericDecimal2Field1.setValue(m_da.getFloatValueFromString(data.getValue()));
                            }
                            break;

                        case TemporaryBasalRate:
                            {
                                this.temporaryBasalRateComponent.setValue(data.getValue());
                            }
                            break;

                        case Profile:
                            {
                                this.profileComponent.setValue(data.getValue());
                            }
                            break;

                        case TemporaryBasalRateProfile:
                            {
                                String s[] = this.getParsedValues(data.getValue());
                                this.profileComponent.setValue(s[0]);
                                this.temporaryBasalRateComponent.setValue(s[1]);
                            }
                            break;

                        case PumpStatus:
                            {
                                int i = m_da.getIntValueFromString(data.getValue(), 0);

                                if (i == 1)
                                {
                                    this.radioButton1.setSelected(true);
                                }
                                else if (i == 2)
                                {
                                    this.radioButton2.setSelected(true);
                                }
                                else if (i == 3)
                                {
                                    this.radioButton3.setSelected(true);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
                break;

            case Bolus:
                {
                    this.setBolusSubType(data.getSubType());

                    switch (PumpBolusType.getByCode(this.sub_type))
                    {
                        case Normal:
                        case Audio:
                            {
                                this.numericDecimal2Field1.setValue(m_da.getFloatValueFromString(data.getValue()));
                            }
                            break;

                        case Extended:
                            {
                                this.squareBolusComponent.setValue(data.getValue());
                            }
                            break;

                        case Multiwave:
                            {

                                String s[] = this.getParsedValues(data.getValue());
                                // String s[] = data.getValue().split(regex)
                                this.numericDecimal2Field1.setValue(m_da.getFloatValueFromString(s[0]));
                                // this.numericDecimal2Field2.setValue(dataAccess.getFloatValueFromString(s[1]));
                                this.squareBolusComponent.setValue(data.getValue());

                            }
                            break;

                        default:
                            {

                            }
                            break;

                    }
                    // TODO
                }
                break;

            case Event:
                {
                    int index = m_da.getIndexOfElementInArray(PumpEventType.getDescriptions(),
                        PumpEventType.getByCode(data.getSubType()).getTranslation());
                    this.combo1.setSelectedIndex(index);
                }
                break;
            case Alarm:
                {
                    int index = m_da.getIndexOfElementInArray(PumpAlarms.getDescriptions(),
                        PumpAlarms.getByCode(data.getSubType()).getTranslation());
                    this.combo1.setSelectedIndex(index);
                }
                break;
            case Error:
                {
                    int index = m_da.getIndexOfElementInArray(PumpErrors.getDescriptions(),
                        PumpErrors.getByCode(data.getSubType()).getTranslation());
                    this.combo1.setSelectedIndex(index);
                }
                break;

            case Report:
                {
                    int index = m_da.getIndexOfElementInArray(PumpReport.getDescriptions(),
                        PumpReport.getByCode(data.getSubType()).getTranslation());
                    this.combo1.setSelectedIndex(index);
                    this.text2.setText(data.getValue());
                }
                break;

            case PenInjectionBasal:
            case PenInjectionBolus:
                {
                    this.numericDecimal2Field1.setValue(m_da.getFloatValueFromString(data.getValue()));
                }
                break;

            case AdditionalData:
                break;

            default:
                System.out.println("Load not implemented for this type: " + data.getBaseType());
                break;

        }

    }


    /**
     * Save Data (we put in PumpValuesEntry, which is then set to right values)
     *
     * @param pve
     */
    public void saveData(PumpValuesEntry pve)
    {
        pve.setComment(this.text1.getText());

        switch (this.type)
        {
            case Basal:
                {
                    pve.setSubType(PumpBasalType.getTypeFromDescription((String) this.combo1.getSelectedItem()));
                    // pve.setSubType(PumpEvents.getTypeFromDescription((String)
                    // this.combo1.getSelectedItem()));

                    switch (PumpBasalType.getByCode(this.sub_type))
                    {
                        case Value:
                            {
                                pve.setValue("" + this.numericDecimal2Field1.getCurrentValue());
                            }
                            break;

                        case TemporaryBasalRate:
                            {
                                pve.setValue(this.temporaryBasalRateComponent.getValue());
                            }
                            break;

                        case Profile:
                            {
                                pve.setValue(this.profileComponent.getValue());
                            }
                            break;

                        case TemporaryBasalRateProfile:
                            {
                                pve.setValue("PROFILE_ID=" + this.profileComponent.getValue() + ";TBR="
                                        + this.temporaryBasalRateComponent.getValue());
                            }
                            break;

                        case PumpStatus:
                            {
                                if (this.radioButton1.isSelected())
                                {
                                    pve.setValue("1");
                                }
                                else if (this.radioButton2.isSelected())
                                {
                                    pve.setValue("2");
                                }
                                else if (this.radioButton3.isSelected())
                                {
                                    pve.setValue("3");
                                }
                                else
                                {
                                    pve.setValue("0");
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
                break;

            case Bolus:
                {
                    pve.setSubType(PumpBolusType.getTypeFromDescription((String) this.combo1.getSelectedItem()));
                    // pve.setSubType(sub_type);

                    switch (PumpBolusType.getByCode(this.sub_type))
                    {
                        case Normal:
                        case Audio:
                            {
                                pve.setValue("" + this.numericDecimal2Field1.getCurrentValue());

                            }
                            break;

                        // case PumpBolusType.PUMP_BOLUS_DUAL_SQUARE:
                        case Extended:
                            {
                                pve.setValue(this.squareBolusComponent.getValue());
                                // pve.setValue("AMOUNT_SQUARE=" + amount +
                                // ";DURATION=" + e);

                            }
                            break;

                        case Multiwave:
                            {
                                /*
                                 * pve.setValue(String.format(
                                 * "AMOUNT=%s;AMOUNT_SQUARE=%s;DURATION=%s",
                                 * str[0],
                                 * str[2],
                                 * str[3]));
                                 */

                                // pve.setValue("AMOUNT_1=" +
                                // this.numericDecimal2Field1.getCurrentValue()
                                // +
                                // ";AMOUNT_2=" +
                                // this.numericDecimal2Field2.getCurrentValue());

                                pve.setValue("IMMEDIATE_AMOUNT=" + this.numericDecimal2Field1.getCurrentValue() + ";"
                                        + this.squareBolusComponent.getValue());

                            }
                            break;

                        default:
                            {

                            }
                            break;

                    }
                    // TODO
                }
                break;

            case Event:
                {
                    pve.setSubType(PumpEventType.getTypeFromDescription((String) this.combo1.getSelectedItem()));
                }
                break;

            case Alarm:
                {
                    pve.setSubType(PumpAlarms.getTypeFromDescription((String) this.combo1.getSelectedItem()));
                }
                break;
            case Error:
                {
                    pve.setSubType(PumpErrors.getTypeFromDescription((String) this.combo1.getSelectedItem()));

                    // pve.setSubType(this.combo1.getSelectedIndex());
                }
                break;

            case Report:
                {
                    pve.setSubType(PumpReport.getTypeFromDescription((String) this.combo1.getSelectedItem()));
                    pve.setValue(this.text2.getText());
                }
                break;

            case PenInjectionBasal:
            case PenInjectionBolus:
                {
                    pve.setSubType(0);
                    pve.setValue("" + this.numericDecimal2Field1.getCurrentValue());
                }
                break;

            case AdditionalData:
            default:
                break;

        }

        // System.out.println("Save data not implemented yet !");

    }


    private String[] getParsedValues(String val)
    {
        ArrayList<String> lst = new ArrayList<String>();

        StringTokenizer strtok = new StringTokenizer(val, ";");

        while (strtok.hasMoreTokens())
        {
            String tk = strtok.nextToken();
            lst.add(tk.substring(tk.indexOf("=") + 1));
        }

        String ia[] = new String[lst.size()];
        return lst.toArray(ia);
    }


    private void addAllItems(JComboBox cb, String[] array)
    {
        cb.removeAllItems();

        for (String element : array)
        {
            // System.out.println("e: " + element);
            cb.addItem(element);
        }
    }


    private void addAllItems(JComboBox cb, Hashtable<Integer, String> ht)
    {
        cb.removeAllItems();

        for (Enumeration<Integer> en = ht.keys(); en.hasMoreElements();)
        {
            Integer i = en.nextElement();
            cb.addItem(ht.get(i));
        }
    }


    /**
     * Set Height
     *
     * @param height
     */
    public void setHeight(int height)
    {
        this.height = height;
        this.setSize(width, height);
    }


    /**
     * Get Height
     */
    @Override
    public int getHeight()
    {
        return this.height;
    }


    /**
     * Get Base Type
     *
     * @return
     */
    public PumpBaseType getBaseType()
    {
        return this.type;
    }


    /**
     * Action Performed
     */
    public void actionPerformed(ActionEvent ev)
    {
        String cmd = ev.getActionCommand();

        if (cmd.equals("bolus"))
        {
            setBolusSubType(PumpBolusType.getTypeFromDescription((String) this.combo1.getSelectedItem()));
        }
        else if (cmd.equals("basal"))
        {
            setBasalSubType(PumpBasalType.getTypeFromDescription((String) this.combo1.getSelectedItem()));
        }
        else if (cmd.equals("bolus_helper"))
        {
            System.out.println("Bolus Helper"); // Basal event: " +
            // this.combo1.getSelectedIndex());
            // setBasalSubType(this.combo1.getSelectedIndex());
            // m_parent.dtc

            float _bg = 0.0f;
            float ch = 0.0f;

            if (m_parent.ht_data.containsKey(PumpAdditionalDataType.BloodGlucose))
            {
                PumpValuesEntryExt pvex = m_parent.ht_data.get(PumpAdditionalDataType.BloodGlucose);
                _bg = m_da.getFloatValueFromString(pvex.getValue());

                if (m_da.getGlucoseUnitType() == GlucoseUnitType.mmol_L)
                {
                    _bg = m_da.getBGValueDifferent(GlucoseUnitType.mmol_L, _bg);
                }
            }

            if (m_parent.ht_data.containsKey(PumpAdditionalDataType.Carbohydrates))
            {
                PumpValuesEntryExt pvex = m_parent.ht_data.get(PumpAdditionalDataType.Carbohydrates);
                ch = m_da.getFloatValueFromString(pvex.getValue());
            }

            BolusHelper bh = new BolusHelper(m_parent, _bg, // dataAccess.getJFormatedTextValueFloat(ftf_bg2),
                    ch, // dataAccess.getJFormatedTextValueFloat(this.ftf_ch),
                    m_parent.dtc.getDateTime(), 2, DataAccess.INSULIN_PUMP);

            if (bh.hasResult())
            {
                this.numericDecimal2Field1.setValue(bh.getResult());
                // this.ftf_ins1.setValue(bh.getResult());
            }

        }
    }

    private class ProfileComponent extends JPanel implements ActionListener
    {

        private static final long serialVersionUID = 1195430308386555236L;
        JLabel label_1_1, label_2_1;
        JButton b_button_1;
        // PumpValuesEntryProfile profile = null;
        String profile;


        public ProfileComponent()
        {
            super();
            this.setLayout(null);
            this.init();
        }


        private void init()
        {
            label_1_1 = new JLabel(ic.getMessage("PROFILE") + ":");
            label_1_1.setBounds(0, 0, 140, 25);
            label_1_1.setFont(ATSwingUtils.getFont(ATSwingUtils.FONT_NORMAL_BOLD));
            this.add(label_1_1);

            label_2_1 = new JLabel(ic.getMessage("NOT_SELECTED"));
            label_2_1.setBounds(150, 0, 140, 25);
            this.add(label_2_1);

            b_button_1 = new JButton("...");
            b_button_1.setBounds(300, 0, 25, 25);
            b_button_1.addActionListener(this);
            this.add(b_button_1);

        }


        @Override
        public void setBounds(int x, int y, int width, int height)
        {
            super.setBounds(x, y, 350, 30);
        }


        public void actionPerformed(ActionEvent arg0)
        {

            if (!ProfileSelectorPump.isPrecheckForProfilesSucessful(m_parent))
            {
                System.out.println("Not Successfull !");
                return;
            }
            else
            {
                System.out.println("Successfull !");
            }

            ProfileSelectorPump psp = new ProfileSelectorPump(m_da, m_parent);

            if (psp.wasAction())
            {
                label_2_1.setText(psp.getSelectedObject().toString());
                this.profile = psp.getSelectedObject().toString();
            }
        }


        public boolean isSelected()
        {
            return profile != null;
        }


        public boolean isValueSet()
        {
            return isSelected();
        }


        public String getValue()
        {
            /*
             * if (this.profile==null)
             * return "0";
             * else
             * return "" + this.profile.getId();
             */
            return this.profile;
        }


        public void setValue(String val)
        {

            this.profile = val;
            label_2_1.setText(this.profile);
            /*
             * long id = dataAccess.getLongValueFromString(val, 0L);
             * if (id == 0)
             * {
             * this.profile = null;
             * }
             * else
             * {
             * this.profile = new PumpValuesEntryProfile();
             * this.profile.setId(id);
             * dataAccess.getDb().get(this.profile);
             * }
             */
        }
    }

    /**
     *  Square Bolus Component - Component used for setting Square Bolus
     */
    private class SquareBolusComponent extends JPanel
    {

        private static final long serialVersionUID = -1888760063675164725L;

        // String[] vals = { "-", "+" };

        JSpinner spinner = null;
        // JComboBox cb_sign = null;
        JLabel label_1_1, label_2_1;
        TimeComponent cmp_time = null;
        int m_type = 0;

        public static final int SQUARE_SINGLE = 1;
        public static final int SQUARE_DUAL = 2;
        DataAccessPump da = DataAccessPump.getInstance();


        public SquareBolusComponent()
        {
            super();
            ATSwingUtils.initLibrary();
            this.setLayout(null);
            this.init();
        }


        private void init()
        {
            label_1_1 = ATSwingUtils.getLabel(ic.getMessage("SQUARE_AMOUNT") + ":", 0, 0, 120, 25, this,
                ATSwingUtils.FONT_NORMAL_BOLD);
            this.add(label_1_1);

            spinner = new JSpinner();
            spinner.setModel(new SpinnerNumberModel(0.0f, 0.0f, da.getMaxBolusValue(), da.getBolusStep()));
            spinner.setBounds(110, 0, 50, 25);
            spinner.setValue(0.1f);
            this.add(spinner);

            label_2_1 = ATSwingUtils.getLabel(ic.getMessage("DURATION_SHORT") + ":", 175, 0, 120, 25, this,
                ATSwingUtils.FONT_NORMAL_BOLD);
            this.add(label_2_1);

            cmp_time = new TimeComponent();
            cmp_time.setBounds(245, 0, 50, 25);
            this.add(cmp_time);

        }


        // Type: 1 = AMOUNT_SQUARE=%s;DURATION=%s
        // 2 = AMOUNT=%s;AMOUNT_SQUARE=%s;DURATION=%s
        public void setType(int type_in)
        {
            this.m_type = type_in;
        }


        @Override
        public void setBounds(int x, int y, int width, int height)
        {
            super.setBounds(x, y, 350, 30);
        }


        public String getValue()
        {
            return String.format("AMOUNT_SQUARE=%s;DURATION=%s",
                "" + m_da.getFormatedBolusValue(m_da.getFloatValue(this.spinner.getValue())),
                this.cmp_time.getTimeString());
        }


        public void setValue(String val)
        {
            String s[] = getParsedValues(val);

            if (m_type == SquareBolusComponent.SQUARE_SINGLE)
            {
                this.spinner.setValue(m_da.getFloatValueFromString(s[0], 0.0f));
                this.setTime(s[1]);
            }
            else
            {
                this.spinner.setValue(m_da.getFloatValueFromString(s[1], 0.0f));

                if (s.length > 2)
                {
                    this.setTime(s[2]);
                }
                else
                {
                    this.cmp_time.setTime(0);
                }
            }

            // String s[] = data.getValue().split(regex)
            // this.numericDecimal2Field1.setValue(dataAccess.getFloatValueFromString(s[0]));
            // this.numericDecimal2Field2.setValue(dataAccess.getFloatValueFromString(s[1]));

            // System.out.println("setValue not implemented");
        }


        private void setTime(String val)
        {
            String s1, s2;

            s1 = val.substring(0, val.indexOf(":"));
            s2 = val.substring(val.indexOf(":") + 1);

            int tm = m_da.getIntValueFromString(s1, 0) * 100 + m_da.getIntValueFromString(s2, 0);

            this.cmp_time.setTime(tm);
        }


        public boolean isValueSet()
        {
            return true;
        }


        @Override
        public void setVisible(boolean vis)
        {
            // System.out.println("SquareBolusCompo: setVisible" + vis);
            super.setVisible(vis);

            spinner.setVisible(vis);
            label_1_1.setVisible(vis);
            label_2_1.setVisible(vis);
            cmp_time.setVisible(vis);
        }

    }

} // 1759
