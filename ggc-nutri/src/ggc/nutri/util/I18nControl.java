package ggc.nutri.util;


import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atech.i18n.I18nControlAbstract;

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       Meter Tool (support for Meter devices)
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
 *  Filename:     I18nControl
 *  Description:  Used for internationalization
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public class I18nControl extends I18nControlAbstract
{

    
    private static Log s_logger = LogFactory.getLog(I18nControl.class); 
    
    


    static I18nControl s_i18n = null;   // This is handle to unique 
                                                    // singelton instance

/*
    private final static Locale defaultLocale = Locale.ENGLISH;

    private String selected_language = "en";
*/
    //   Constructor:  I18nControl
    /**
     *
     *  This is I18nControl constructor; Since classes use Singleton Pattern,
     *  constructor is protected and can be accessed only with getInstance() 
     *  method.<br><br>
     *
     */ 
    private I18nControl()
    {
        init();
        getSelectedLanguage();
        setLanguage();
    } 


    /**
     * Init library
     */
    public void init()
    {
    	def_language = "en";
		lang_file_root = "GGC_Nutrition";
    }


    private void getSelectedLanguage()
    {

    	try
    	{
    	    Properties props = new Properties();
    
    	    FileInputStream in = new FileInputStream("../data/GGC_Config.properties");
    	    props.load(in);
    
    	    String tempLang = (String)props.get("SELECTED_LANG");
    
    	    if (tempLang != null)
    		this.selected_language = tempLang;
    	}
    	catch(Exception ex)
    	{
    	    System.out.println("I18nControl: Configuration file not found. Using default langauge ('en')");
    	    s_logger.warn("Configuration file not found. Using default langauge ('en')");
    	}
        
    }

    
    //  Method:       getInstance
    //  Author:       Andy
    /**
     *
     *  This method returns reference to I18nControl object created, or if no 
     *  object was created yet, it creates one.<br><br>
     *
     *  @return Reference to I18nControl object
     * 
     */ 
    public static I18nControl getInstance()
    {
        if (s_i18n == null)
            s_i18n = new I18nControl();
        return s_i18n;
    }

    //  Method:       deleteInstance
    /**
     *
     *  This method sets handle to I18NControl to null and deletes the instance. <br><br>
     *
     */ 
    public static void deleteInstance()
    {
        s_i18n = null;
    }


    /**
     * This method sets the language according to the preferences.<br>
     */
    public void setLanguage() 
    {
        //GGCProperties props = GGCProperties.getInstance();
        if (selected_language!=null)
            setLanguage(selected_language);
        else
            setLanguage(def_language);
        //props.getLanguage());
    }


    /**
     * Get Partitial Translation
     * 
     * @param keyword
     * @param default_delim
     * @return
     */
    public String getPartitialTranslation(String keyword, String default_delim)
    {
    
        StringTokenizer strtok = new StringTokenizer(keyword, default_delim);
        StringBuffer sb = new StringBuffer();
        
        
        while(strtok.hasMoreTokens())
        {
            String k = strtok.nextToken();
            
            char[] chars = { ',', '(', ')' };
            boolean checked = false;
            int i =0;
            
            while (!checked)
            {
                checked = checkStartEnd(k, chars[i], sb);
                i++;
                
                if (i == chars.length)
                    break;
            }
            
            //boolean found = checkStartEnd(k, ',', sb);
            
            //if (!found
            
            if (!checked)
            sb.append(this.getMessage(k));
            
            sb.append(" ");
        }
    
        return sb.toString();
    
    }
    
    
    private boolean checkStartEnd(String key, char char_el, StringBuffer sb)
    {
        if (key.charAt(0)==char_el)
        {
            sb.append(char_el);
            
            key = key.substring(1,key.length());
            
            sb.append(this.getMessage(key));
            return true;
        }
        else if (key.charAt(key.length()-1)==char_el)
        {
            
            key = key.substring(0,key.length()-1);
            
            sb.append(this.getMessage(key));
            sb.append(char_el);
            return true;
            
        }
        
        return false;
    
    }

    
}
