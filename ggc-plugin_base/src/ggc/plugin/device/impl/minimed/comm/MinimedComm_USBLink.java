package ggc.plugin.device.impl.minimed.comm;

import ggc.plugin.device.PlugInBaseException;
import ggc.plugin.device.impl.minimed.MinimedDevice;
import ggc.plugin.device.impl.minimed.cmd.MinimedCommand;


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
 *  Filename:     MinimedComm_USBLink  
 *  Description:  Communication Protocol: USB Link
 * 
 *  Author: Andy {andy@atech-software.com}
 */


public class MinimedComm_USBLink implements MinimedComm_Interface
{

    /**
     * Constructor
     */
    public MinimedComm_USBLink(MinimedDevice mmd)
    {
        //super(port, serial_number);
    }

    public int[] decrypt(int[] input)
    {
        return null;
    }

    public int[] encrypt(int[] input)
    {
        return null;
    }


    



    public int closeCommunicationInterface() throws PlugInBaseException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int closeDevice() throws PlugInBaseException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int initializeCommunicationInterface() throws PlugInBaseException
    {
        // TODO Auto-generated method stub
        return 0;
    }


    

    public boolean hasEncryptionSupport()
    {
        return false;
    }

    public void dumpInterfaceStatus()
    {
        // TODO Auto-generated method stub
        
    }

    public int initDevice() throws PlugInBaseException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean executeCommandRetry(int commandId) throws PlugInBaseException
    {
        return false;
    }

    public boolean executeCommandRetry(MinimedCommand command) throws PlugInBaseException
    {
        return false;
    }

    public void sendCommandReadData(MinimedCommand command) throws PlugInBaseException
    {
        // TODO Auto-generated method stub
        
    }

 
    
    
    
}
