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
 *  Filename: AbstractPrefOptionsPanel.java
 *  Purpose:  A abstract OptionsPanel to easily change the option panels on
 *            the preferences Frame.
 *
 *  Author:   schultd
 */

package ggc.gui.prefPane;


import ggc.util.GGCProperties;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public abstract class AbstractPrefOptionsPanel extends JPanel implements DocumentListener, ItemListener, ActionListener
{
    protected GGCProperties props = GGCProperties.getInstance();
    protected boolean changed = false;

    public abstract void saveProps();

    public boolean hasChanged()
    {
        return changed;
    }

    public void insertUpdate(DocumentEvent e)
    {
        changed = true;
    }

    public void removeUpdate(DocumentEvent e)
    {
        changed = true;
    }

    public void changedUpdate(DocumentEvent e)
    {
    };

    public void actionPerformed(ActionEvent e)
    {
        changed = true;
    }

    public void itemStateChanged(ItemEvent e)
    {
        changed = true;
    }

    public void kill()
    {

        if (changed) {
            int res = JOptionPane.showConfirmDialog(null, "Some values have changed. Would you like to safe them?", "Values Changed", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION)
                saveProps();
        }
    }
}
