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
 *  Filename: MealValue
 *  Purpose:  This is datalayer file (data file). 
 *      This one is used for Meal (Nutrition) values.
 *
 *  Author:   andyrozman  {andy@atech-software.com}
 */


package ggc.core.db.datalayer;

import ggc.core.util.DataAccess;


public class MealNutrition
{

    public boolean debug = false;
    private int nutrition_type_id = 0;
    private String nutrition_desc;
    private float amount = 0.0f;
    
    //private boolean no_desc = false;
    
    // value pack: id=amount
    public MealNutrition(String packed, boolean load_description)
    {
	int index = packed.indexOf("=");
	
	this.nutrition_type_id = Integer.parseInt(packed.substring(0, index));
	this.amount = Float.parseFloat(packed.substring(index+1));
	
	if (load_description)
	    this.nutrition_desc = DataAccess.getInstance().getDb().nutrition_defs.get("" + this.nutrition_type_id).getName();
    }

    
    public MealNutrition(MealNutrition mn)
    {
	this.nutrition_type_id = mn.nutrition_type_id;
	this.amount = mn.amount;
	this.nutrition_desc = mn.nutrition_desc;
    }
    

    public MealNutrition(int id, float amount, String desc)
    {
	this.nutrition_type_id = id;
	this.amount = amount;
	this.nutrition_desc = desc;
	
	//this.no_desc = true;
    }
    
    
    
    public int getId()
    {
	return this.nutrition_type_id;
    }

    public float getAmount()
    {
	return this.amount;
    }
    
    
    public void setAmount(float val)
    {
	this.amount = val;
    }
    
    public void addToAmount(float val)
    {
	this.amount += val;
    }
    
    
    public String getDescription()
    {
	return this.nutrition_desc;
    }
    

    public void loadMealPart()
    {
	//DataAccess.getInstance().getDb().getMeals();
	
    }


    @Override
    public String toString()
    {
        return "MealPart";
    }


}


