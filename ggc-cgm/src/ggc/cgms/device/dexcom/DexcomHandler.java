package ggc.cgms.device.dexcom;

import ggc.cgms.device.CGMSDeviceHandler;
import ggc.cgms.device.dexcom.receivers.DexcomDevice;
import ggc.cgms.device.dexcom.receivers.DexcomDeviceReader;
import ggc.cgms.device.dexcom.receivers.data.output.GGCOutputParser;
import ggc.cgms.util.DataAccessCGMS;
import ggc.plugin.data.enums.DeviceHandlerType;
import ggc.plugin.device.PlugInBaseException;
import ggc.plugin.device.v2.DeviceDefinition;
import ggc.plugin.output.OutputWriter;

/**
 * Created by andy on 15.04.15.
 */
public class DexcomHandler extends CGMSDeviceHandler
{

    public DeviceHandlerType getDeviceHandlerKey()
    {
        return DeviceHandlerType.DexcomHandler;
    }


    public void readDeviceData(DeviceDefinition definition, Object connectionParameters, OutputWriter outputWriter)
            throws PlugInBaseException
    {
        DexcomDeviceReader ddr = null;
        try
        {
            prepareBaseDeviceIdentification(outputWriter);

            ddr = new DexcomDeviceReader(getCommunicationPort(connectionParameters), getDexcomDeviceType(definition));

            GGCOutputParser gop = new GGCOutputParser(outputWriter, getDexcomDeviceType(definition));
            ddr.setOutputWriter(outputWriter);
            ddr.setDataOutputParser(gop);

            ddr.downloadData();
        }
        finally
        {
            if (ddr != null)
            {
                ddr.dispose();
            }

            outputWriter.endOutput();
        }

    }


    public void readConfiguration(DeviceDefinition definition, Object connectionParameters, OutputWriter outputWriter)
            throws PlugInBaseException
    {
        DexcomDeviceReader ddr = null;

        try
        {
            prepareBaseDeviceIdentification(outputWriter);

            ddr = new DexcomDeviceReader(getCommunicationPort(connectionParameters), getDexcomDeviceType(definition));

            GGCOutputParser gop = new GGCOutputParser(outputWriter, getDexcomDeviceType(definition));
            ddr.setOutputWriter(outputWriter);
            ddr.setDataOutputParser(gop);

            ddr.downloadSettings();
        }
        finally
        {
            if (ddr != null)
            {
                ddr.dispose();
            }

            outputWriter.endOutput();
        }

    }


    private DexcomDevice getDexcomDeviceType(DeviceDefinition definition)
    {
        return (DexcomDevice) getDeviceDefinition(definition).getInternalDefintion();
    }


    private void prepareBaseDeviceIdentification(OutputWriter outputWriter)
    {
        DataAccessCGMS.getInstance().getPluginDeviceUtil().prepareDeviceIdentification(outputWriter);
    }
}
