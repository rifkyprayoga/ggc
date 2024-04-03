package ggc.cgms.data.defs;

import java.util.HashMap;

/**
 *  Application: GGC - GNU Gluco Control
 *  Plug-in: CGMS Tool (support for CGMS devices)
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
 *  Filename: CGMDataType
 *  Description: CGMS Data types, as used in database (undefined at this time)
 *
 *  Author: Andy {andy@atech-software.com}
 */

public enum CGMSTrendArrow
{

    DoubleUp(1), //
    SingleUp(2), //
    FortyFiveUp(3), //
    Flat(4), //
    FortyFiveDown(5), //
    SingleDown(6), //
    DoubleDown(7), //

    None(0), //
    NotComputable(8), //
    RateOutOfRange(9), ;

    private int value;
    private static HashMap<Integer, CGMSTrendArrow> map = new HashMap<Integer, CGMSTrendArrow>();

    static
    {
        for (CGMSTrendArrow el : values())
        {
            map.put(el.getValue(), el);
        }
    }


    CGMSTrendArrow(int value)
    {
        this.value = value;
    }


    public int getValue()
    {
        return value;
    }


    public void setValue(int value)
    {
        this.value = value;
    }


    public static CGMSTrendArrow getEnum(int value)
    {
        return map.get(value);
    }

}
