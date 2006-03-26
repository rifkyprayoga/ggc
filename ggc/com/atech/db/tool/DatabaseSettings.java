package com.atech.db.tool;

import java.util.Hashtable;

public class DatabaseSettings 
{
    public int number = 0;

    public String db_name = null;
    public String name = null;
    public String driver = null;
    public String url = null;
    public String port = null;
    public String dialect = null;

    public String username = null;
    public String password = null;

    public boolean isDefault = false;

    public Hashtable settings = null;

/*
    public String hostname = null;
    public String url = null;
    public String port = null;
    public String database = null;
*/

    public DatabaseSettings()
    {
	settings = new Hashtable();
    }

    public DatabaseSettings(String name, String driver, String url, String port, String dialect)
    {

	this.name = name;
	this.driver = driver;
	this.url = url;
	this.port = port;
	this.dialect = dialect;

	settings = new Hashtable();

    }


    public String toString()
    {
	String def = "";

	if (this.isDefault)
	    def=" (Selected)";

	return number + " - " + name + def;
    }

}
