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
 *  Filename: DateRangeSelectionPanel.java
 *  Purpose:  Panel for selecting a date range.
 *
 *  Author:   schutld
 */

package gui.calendar;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateRangeSelectionPanel extends JPanel
{
    private JTextField fieldStartDate;
    private JTextField fieldEndDate;
    private Date endDate;
    private Date startDate;

    private int iRadioGroupState = 0;

    public static final int ONE_WEEK = 0;
    public static final int ONE_MONTH = 1;
    public static final int THREE_MONTHS = 2;
    public static final int CUSTOM = 3;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public DateRangeSelectionPanel()
    {
        endDate = new Date(System.currentTimeMillis());
        iRadioGroupState = ONE_MONTH;
        calcStartDate();
        init();
    }

    public DateRangeSelectionPanel(int flag)
    {
        endDate = new Date(System.currentTimeMillis());
        iRadioGroupState = flag;
        calcStartDate();
        init();
    }

    public DateRangeSelectionPanel(Date endDate)
    {
        this.endDate = endDate;
        iRadioGroupState = ONE_MONTH;
        calcStartDate();
        init();
    }

    public DateRangeSelectionPanel(Date endDate, int flag)
    {
        this.endDate = endDate;
        iRadioGroupState = flag;
        calcStartDate();
        init();
    }

    public DateRangeSelectionPanel(Date endDate, Date startDate)
    {
        this.endDate = endDate;
        this.startDate = startDate;
        iRadioGroupState = CUSTOM;
    }


    private void init()
    {
        Box a = Box.createVerticalBox();
        a.add(new JLabel("Ending Date:"));
        a.add(fieldEndDate = new JTextField());
        a.add(new JLabel("Starting Date"));
        a.add(fieldStartDate = new JTextField());

        fieldEndDate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                calcDateAndUpdateFields();
            }
        });
        fieldEndDate.addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
            }

            public void focusLost(FocusEvent e)
            {
                calcDateAndUpdateFields();
            }
        });

        fieldStartDate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    startDate = sdf.parse(fieldStartDate.getText());
                } catch (ParseException e1) {
                    System.out.println(e1);
                }
            }
        });
        fieldStartDate.addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
            }

            public void focusLost(FocusEvent e)
            {
                try {
                    startDate = sdf.parse(fieldStartDate.getText());
                } catch (ParseException e1) {
                    System.out.println(e1);
                }
            }
        });

        setTextFields();

        JRadioButton rbOneWeek = new JRadioButton("1 Week", iRadioGroupState == ONE_WEEK);
        JRadioButton rbOneMonth = new JRadioButton("1 Month", iRadioGroupState == ONE_MONTH);
        JRadioButton rbThreeMonths = new JRadioButton("3 Months", iRadioGroupState == THREE_MONTHS);
        JRadioButton rbCustom = new JRadioButton("custom", iRadioGroupState == CUSTOM);

        if(iRadioGroupState != CUSTOM)
            fieldStartDate.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbOneWeek);
        group.add(rbOneMonth);
        group.add(rbThreeMonths);
        group.add(rbCustom);

        rbOneWeek.addActionListener(new RadioListener(ONE_WEEK));
        rbOneMonth.addActionListener(new RadioListener(ONE_MONTH));
        rbThreeMonths.addActionListener(new RadioListener(THREE_MONTHS));
        rbCustom.addActionListener(new RadioListener(CUSTOM));

        Box b = Box.createVerticalBox();
        b.add(rbOneWeek);
        b.add(rbOneMonth);
        b.add(rbThreeMonths);
        b.add(rbCustom);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("DateRange Selector"));

        add(a, BorderLayout.WEST);
        add(b, BorderLayout.CENTER);
    }

    private void calcDateAndUpdateFields()
    {
        try {
            endDate = sdf.parse(fieldEndDate.getText());
        } catch (ParseException e) {
            System.out.println(e);
        }
        calcStartDate();
        setTextFields();
    }

    private void setTextFields()
    {
        fieldEndDate.setText(sdf.format(endDate));
        fieldStartDate.setText(sdf.format(startDate));
    }

    private void calcStartDate()
    {
        if (iRadioGroupState == 3)
            return;

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(endDate);

        switch (iRadioGroupState) {
            case ONE_WEEK:
                gc.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case ONE_MONTH:
                gc.add(Calendar.MONTH, -1);
                break;
            case THREE_MONTHS:
                gc.add(Calendar.MONTH, -3);
        }

        startDate = gc.getTime();
    }

    private class RadioListener extends AbstractAction
    {
        private int stat = 1;

        public RadioListener(int flag)
        {
            this.stat = flag;
        }

        public void actionPerformed(ActionEvent e)
        {
            iRadioGroupState = stat;

            if (stat == CUSTOM)
                fieldStartDate.setEnabled(true);
            else
                fieldStartDate.setEnabled(false);

            calcStartDate();
            setTextFields();
        }
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }
}
