package ggc.cgms.device.animas.impl.converter;

import java.math.BigDecimal;
import java.util.*;

import ggc.cgms.device.animas.impl.data.AnimasCGMSDeviceData;

import ggc.cgms.device.animas.impl.data.dto.AnimasDexcomHistoryEntry;
import ggc.cgms.device.animas.impl.data.dto.AnimasDexcomWarning;
import ggc.cgms.device.animas.impl.data.dto.CGMSSettings;
import ggc.cgms.device.animas.impl.data.enums.AnimasCGMSWarningType;
import ggc.plugin.device.impl.animas.enums.AnimasSoundType;
import ggc.plugin.device.impl.animas.enums.advsett.SoundValueType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atech.utils.data.ATechDate;

import ggc.plugin.device.impl.animas.AnimasDeviceReader;
import ggc.plugin.device.impl.animas.converter.AnimasAbstractDataConverter;
import ggc.plugin.device.impl.animas.data.AnimasDeviceData;
import ggc.plugin.device.impl.animas.data.AnimasDevicePacket;
import ggc.plugin.device.impl.animas.util.AnimasUtils;


/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       CGMS Tool (support for Pump devices)
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
 *  Filename:     FRC_MinimedCarelink
 *  Description:  Minimed Carelink File Handler
 *
 *  Author: Andy {andy@atech-software.com}
 */

/**
 *  Application:   GGC - GNU Gluco Control
 *  Plug-in:       CGMS Tool (support for CGMS devices)
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
 *  Filename:     AnimasDexcomDataConverter
 *  Description:  Converter for Dexcom Animas Data
 *
 *  Author: Andy Rozman {andy@atech-software.com}
 */

public class AnimasDexcomDataConverter extends AnimasAbstractDataConverter
{

    public static final Log LOG = LogFactory.getLog(AnimasDexcomDataConverter.class);

    private static final boolean DEBUG = true;

    AnimasCGMSDeviceData data;
    boolean inDataProcessing = false;

    HashMap<String, BigDecimal> bigDecimals = new HashMap<String, BigDecimal>();

    SimpleTimeZone timezoneUTC;


    public AnimasDexcomDataConverter(AnimasDeviceReader deviceReader, AnimasDeviceData data)
    {
        super(deviceReader, data);
        //super(portName, deviceType, deviceReader);

        this.data = (AnimasCGMSDeviceData)data;
        this.createNeededBigDecimals();
        timezoneUTC = new SimpleTimeZone(0, "UTC");
    }

    @Override
    public AnimasDeviceData getData()
    {
        return this.data;
    }


    private void createNeededBigDecimals()
    {
        //bigDecimals.put("BIG_DECIMAL_100f", new BigDecimal(100.0f));
//        bigDecimals.put("BIG_DECIMAL_0", new BigDecimal(0));
//        bigDecimals.put("BIG_DECIMAL_6", new BigDecimal(6));
//        bigDecimals.put("BIG_DECIMAL_10f", new BigDecimal(10.0f));
//        bigDecimals.put("BIG_DECIMAL_18f", new BigDecimal(18.0f));
//        bigDecimals.put("BIG_DECIMAL_100", new BigDecimal(100));
        bigDecimals.put("BIG_DECIMAL_1000", new BigDecimal(1000));
//        bigDecimals.put("BIG_DECIMAL_10000f", new BigDecimal(10000.0f));
//        bigDecimals.put("BIG_DECIMAL_1000f", new BigDecimal(1000.0f));

    }




    @Override
    public void processCustomReturnedRawData(AnimasDevicePacket animasDevicePacket, ATechDate dt)
    {
        switch (animasDevicePacket.dataTypeObject)
        {
            case DexcomSettings:
                decodeDexcomSettings(animasDevicePacket);
                break;

            case DexcomWarnings: // 43
                decodeDexcomWarnings(animasDevicePacket, dt);
                break;

            case Dexcom_C3:
                decodeDexcomC3(animasDevicePacket);
                break;

            case DexcomBgHistory: // 45
                decodeDexcomHistory(animasDevicePacket);
                break;

            case Dexcom_C5:
                decodeDexcomC5(animasDevicePacket);
                break;

            case Dexcom_C6:
                decodeDexcomC6(animasDevicePacket); // ?
                break;

            case Dexcom_C7:
                decodeDexcomDataSpecific(animasDevicePacket, "Dexcom-C7");
                break;

            default:
                LOG.warn(String
                        .format(
                                "This type (code=%s) is not available/supported.",
                                animasDevicePacket.dataTypeObject.getCode()));


        }
    }



    // NO GCC
    private void decodeDexcomWarnings(AnimasDevicePacket packet, ATechDate dt)
    {
        //AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), "Dexcom-C2: %s [%s]", LOG);

        for(int i=0; i<8;i++)
        {
            int start = 6 + (i * 14);

            AnimasDexcomWarning warn = new AnimasDexcomWarning();

            warn.dateTime = this.decodeDateTimeFromRawComponents(packet.getReceivedDataBit(start+1),
                    packet.getReceivedDataBit(start),
                    packet.getReceivedDataBit(start+2),
                    packet.getReceivedDataBit(start+3));

            warn.warningCode = packet.getReceivedDataBit(start+4);

            warn.warningType = AnimasCGMSWarningType.getWarningType(warn.warningCode);

            short[] dataBits = { packet.getReceivedDataBit(start+5),
                    packet.getReceivedDataBit(start+6),
                    packet.getReceivedDataBit(start+7),
                    packet.getReceivedDataBit(start+8),
                    packet.getReceivedDataBit(start+9)};

            warn.dataBits = dataBits;

            data.addDexcomWarning(warn);
        }

    }

























    private void decodeDexcomC6(AnimasDevicePacket packet)
    {
        //        12:14:30,951 DEBUG [AnimasCommProtocolV2:166] - 02 f1  [2 241 ]
//        12:14:31,062 DEBUG [AnimasCommProtocolV2:166] - 02 11  [2 17 ]
//        12:14:31,274 DEBUG [AnimasCommProtocolV2:166] - 02 12 44 49 00 00 00  [2 18 68 73 0 0 0 ]
//        12:14:31,274 DEBUG [AnimasCommProtocolV2:890] - DataType: Dexcom_C6, quantity=1, dataPresent=false

        decodeDexcomDataSpecific(packet, "Dexcom-C6");

    }


//        12:14:29,607 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C1: 02 b2 44 49 00 00 04 04 04 04 02 02 f0 00 46 00 03 03 00 00 00 00 1e 00 01 01 01 01 01 36 36 35 36 4d ef 76 83 3a  [2 178 68 73 0 0 4 4 4 4 2 2 240 0 70 0 3 3 0 0 0 0 30 0 1 1 1 1 1 54 54 53 54 77 239 118 131 58 ]
//        12:14:30,162 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C3: 02 d2 44 49 00 00 07 05 0d 32 e2 21 31 0d 01 00 00 00 00 00 17 5a 60 3d  [2 210 68 73 0 0 7 5 13 50 226 33 49 13 1 0 0 0 0 0 23 90 96 61 ]
//        12:14:30,719 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C5: 02 f2 44 49 00 00 a6 1b 0c 2e 4b f2 ff ff 00 00 00 00 00 00 40 85 9d df  [2 242 68 73 0 0 166 27 12 46 75 242 255 255 0 0 0 0 0 0 64 133 157 223 ]


//        12:14:30,719 DEBUG [AnimasCommProtocolV2:890] - DataType: Dexcom_C5, quantity=1, dataPresent=true
//        12:14:30,940 DEBUG [AnimasCommProtocolV2:129] - Downloading - Dexcom C6 - 46
//        12:14:30,951 DEBUG [AnimasCommProtocolV2:166] - 02 f1  [2 241 ]
//        12:14:31,062 DEBUG [AnimasCommProtocolV2:166] - 02 11  [2 17 ]
//        12:14:31,274 DEBUG [AnimasCommProtocolV2:166] - 02 12 44 49 00 00 00  [2 18 68 73 0 0 0 ]
//        12:14:31,274 DEBUG [AnimasCommProtocolV2:890] - DataType: Dexcom_C6, quantity=1, dataPresent=false
//        12:14:32,395 DEBUG [AnimasCommProtocolV2:256] - Pump is disconnected.


    private void decodeDexcomSettings(AnimasDevicePacket packet)
    {



        //Settings: 4(H) : High Alert (V), LowAlert(M), RiseRate, FallRate
        //2(L):  Range, Others


        CGMSSettings settings = this.data.cgmsSettings;

//
//        6= High Alert  [1=Vibrate,2=Low,3=Medium,4=High]
//        7= LowAlert
//        8=
//                9=
//                        10=
//                                11=

        // OK
        settings.soundVolumes.put(AnimasSoundType.CGMS_HighAlert, SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(6)));
        settings.soundVolumes.put(AnimasSoundType.CGMS_LowAlert, SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(7)));

        // Not checked
        settings.soundVolumes.put(AnimasSoundType.CGMS_RiseRate, SoundValueType.Unknown); //SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(6)
        settings.soundVolumes.put(AnimasSoundType.CGMS_FallRate, SoundValueType.Unknown); //SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(6)
        settings.soundVolumes.put(AnimasSoundType.CGMS_Range, SoundValueType.Unknown); //SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(6)
        settings.soundVolumes.put(AnimasSoundType.CGMS_Others, SoundValueType.Unknown); //SoundValueType.getSoundValueTypeByCode(packet.getReceivedDataBit(6)


        settings.highAlertWarnAbove = AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(12),
                packet.getReceivedDataBit(13));

        settings.lowAlertWarnBelow = AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(14),
                packet.getReceivedDataBit(15));




        settings.riseRateWarnAbove = packet.getReceivedDataBit(16);

        settings.fallRateWarnAbove = packet.getReceivedDataBit(17);

        settings.highAlertSnoozeTime = AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(18),
                packet.getReceivedDataBit(19));
        settings.lowAlertSnoozeTime = AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(20),
                packet.getReceivedDataBit(21));
        settings.transmiterOutOfRangeSnoozeTime = packet.getReceivedDataBit(22);

        // bit 23, could be part of transmiterOutOfRangeSnoozeTime, but since that is limited to 201, it might not be

        settings.highAlertWarningEnabled = false;
        settings.lowAlertWarningEnabled = false;
        settings.riseRateWarningEnabled = false;
        settings.fallRateWarningEnabled = false;
        settings.transmiterOutOfRangeWarningEnabled = false;

        settings.transmiterSerialNumber = "";





//        24=E
//        25=E
//        26=E
//        27=E
//        28=E
//        29=
//                30=
//                        31=
//                                32=
//                                        33=
//                                                34=
//                                                        35=






//        12:14:29,607  Dexcom-C1:
// 02 b2 44 49 00 00 04 04 04 04 02 02 f0 00 46 00 03 03 00 00 00 00 1e 00 01 01 01 01 01 36 36 35 36 4d ef 76 83 3a
// [2 178 68 73 0 0
// 4 4 4 4 2 2 240 0 70 0 3 3 0 0 0 0 30 0 1 1 1 1 1 54 54 53 54 77 239 118
// 131 58 ]
// TT: Settings ??

        //Dexcom-C1:
        // 02 b2 44 49 00 00 04 04 04 04 02 02 f0 00 46 00 03 03 00 00 00 00 1e 00 01 01 01 01 01 36 36 35 36 4d ef 76 83 3a
        // [2 178 68 73 0 0 4 4 4 4 2 2 240 0 70 0 3 3 0 0 0 0 30 0 1 1 1 1 1 54 54 53 54 77 239 118 131 58 ]
        AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), "Dexcom-C1: %s [%s]", LOG);
    }



    private void decodeDexcomC3(AnimasDevicePacket packet)
    {

//        12:14:30,162 Dexcom-C3:
// 02 d2 44 49 00 00 07 05 0d 32 e2 21 31 0d 01 00 00 00 00 00 17 5a 60 3d
// [2 210 68 73 0 0
// 7 5 13 50 226 33 49 13 1 0 0 0 0 0 23 90
// 96 61 ]


        // Dexcom-C3:
        // 02 d2 44 49 00 00     07 05 0d 32 e2 21 31 0d 01 00 00 00 00 00 17 5a 60 3d
        // [2 210 68 73 0 0      7 5 13 50 226 33 49 13 1 0 0 0 0 0 23 90 96 61 ]
        AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), "Dexcom-C3: %s [%s]", LOG);

    }





    private void decodeDexcomC5(AnimasDevicePacket packet)
    {
//        12:14:30,719 Dexcom-C5:
// 02 f2 44 49 00 00 a6 1b 0c 2e 4b f2 ff ff 00 00 00 00 00 00 40 85 9d df
// [2 242 68 73 0 0
// 166 27 12 46 75 242 255 255 0 0 0 0 0 0 64 133
// 157 223 ]


        // Dexcom-C5:
        // 02 f2 44 49 00 00 a6 1b 0c 2e 4b f2 ff ff 00 00 00 00 00 00 40 85 9d df
        // [2 242 68 73 0 0 166 27 12 46 75 242 255 255 0 0 0 0 0 0 64 133 157 223 ]
        AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), "Dexcom-C5: %s [%s]", LOG);
    }


    private void decodeDexcomC7()
    {

//        Dexcom-C7: 02 b2 44 49 00 00 01 00 00 00 b4 76 04 00 13 05 34 37 31 36 35 2d 31 36 31 33 38 34 32 00 00 00 00 01 2c eb ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff 26 96
// [2 178 68 73 0 0
// 1 0 0 0 180 118 4 0 19 5 52 55 49 54 53 45 49 54 49 51 56 52 50 0 0 0 0 1 44 235 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 38 150 ]
//
//        Dexcom-C7: 02 b2 44 49 01 00 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff 86 36  [2 178 68 73 1 0 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 134 54 ]
//        Dexcom-C7: 02 b2 44 49 02 00 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff 9d 27 00 00 4f fc 28 52 9f 80 33 00 00 00 e0 17 03 00 c0 17 03 00 02 2d 08 e6 a3 80 33 00 00 00 60 19 03 00 c0 18 03 00 e6 d9 e8 79 a8 80 33 00 00 00 20 1e 03 00 40 19 03 00 b5 3d c8 0d ad 80 33 00 00 00 80 22 03 00 20 1b 03 00 fd 25 a8 a1 b1 80 33 00 00 00 e0 24 03 00 a0 1e 03 00 5c 01 14  [2 178 68 73 2 0 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 157 39 0 0 79 252 40 82 159 128 51 0 0 0 224 23 3 0 192 23 3 0 2 45 8 230 163 128 51 0 0 0 96 25 3 0 192 24 3 0 230 217 232 121 168 128 51 0 0 0 32 30 3 0 64 25 3 0 181 61 200 13 173 128 51 0 0 0 128 34 3 0 32 27 3 0 253 37 168 161 177 128 51 0 0 0 224 36 3 0 160 30 3 0 92 1 20 ]
//        Dexcom-C7: 02 b2 44 49 03 00 50 88 35 b6 80 33 00 00 00 e0 25 03 00 a0 22 03 00 c1 ef 68 c9 ba 80 33 00 00 00 e0 27 03 00 c0 25 03 00 91 96 48 5d bf 80 33 00 00 00 60 29 03 00 80 27 03 00 9e 18 28 f1 c3 80 33 00 00 00 a0 24 03 00 e0 27 03 00 93 79 08 85 c8 80 33 00 00 00 20 1e 03 00 60 26 03 00 20 86 e8 18 cd 80 33 00 00 00 40 18 03 00 80 22 03 00 8d be c8 ac d1 80 33 00 00 00 ce 77  [2 178 68 73 3 0 80 136 53 182 128 51 0 0 0 224 37 3 0 160 34 3 0 193 239 104 201 186 128 51 0 0 0 224 39 3 0 192 37 3 0 145 150 72 93 191 128 51 0 0 0 96 41 3 0 128 39 3 0 158 24 40 241 195 128 51 0 0 0 160 36 3 0 224 39 3 0 147 121 8 133 200 128 51 0 0 0 32 30 3 0 96 38 3 0 32 134 232 24 205 128 51 0 0 0 64 24 3 0 128 34 3 0 141 190 200 172 209 128 51 0 0 0 206 119 ]
//        Dexcom-C7: 02 b2 44 49 04 00 a0 12 03 00 80 1c 03 00 4d 69 a8 40 d6 80 33 00 00 00 40 0c 03 00 a0 15 03 00 9a 50 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff aa 27 00 00 8b 81 88 d4 da 80 33 00 00 00 a0 07 03 00 40 0f 03 00 34 ba 68 68 df 80 33 00 00 00 c0 04 03 00 00 0a 03 00 90 15 48 fc e3 80 33 00 00 00 c0 02 03 00 00 06 03 00 1c 4b 28 90 e8 80 33 00 00 00 20 03 03 00 80 4e 54  [2 178 68 73 4 0 160 18 3 0 128 28 3 0 77 105 168 64 214 128 51 0 0 0 64 12 3 0 160 21 3 0 154 80 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 170 39 0 0 139 129 136 212 218 128 51 0 0 0 160 7 3 0 64 15 3 0 52 186 104 104 223 128 51 0 0 0 192 4 3 0 0 10 3 0 144 21 72 252 227 128 51 0 0 0 192 2 3 0 0 6 3 0 28 75 40 144 232 128 51 0 0 0 32 3 3 0 128 78 84 ]
//        Dexcom-C7: 02 b2 44 49 05 00 03 03 00 92 12 08 24 ed 80 33 00 00 00 00 02 03 00 40 02 03 00 e1 89 e8 b7 f1 80 33 00 00 00 80 fd 02 00 80 01 03 00 8c 95 c8 4b f6 80 33 00 00 00 00 fb 02 00 e0 ff 02 00 f8 7c a8 df fa 80 33 00 00 00 80 f1 02 00 60 fc 02 00 87 ab 88 73 ff 80 33 00 00 00 00 e7 02 00 40 f6 02 00 f6 85 38 0f 04 81 33 00 00 00 60 e7 02 00 a0 ee 02 00 3b 4a 18 a3 08 81 05 ce  [2 178 68 73 5 0 3 3 0 146 18 8 36 237 128 51 0 0 0 0 2 3 0 64 2 3 0 225 137 232 183 241 128 51 0 0 0 128 253 2 0 128 1 3 0 140 149 200 75 246 128 51 0 0 0 0 251 2 0 224 255 2 0 248 124 168 223 250 128 51 0 0 0 128 241 2 0 96 252 2 0 135 171 136 115 255 128 51 0 0 0 0 231 2 0 64 246 2 0 246 133 56 15 4 129 51 0 0 0 96 231 2 0 160 238 2 0 59 74 24 163 8 129 5 206 ]
//        Dexcom-C7: 02 b2 44 49 06 00 33 00 00 00 40 ee 02 00 00 e9 02 00 52 50 28 2f 0d 81 33 00 00 00 40 ef 02 00 00 e8 02 00 3c d4 08 c3 11 81 33 00 00 00 e0 ee 02 00 20 eb 02 00 7f 4d ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff b7 27 00 00 aa a3 b8 5e 16 81 33 00 00 00 80 ee 02 00 c0 ee 02 00 27 ba c8 ea 1a 81 33 00 00 00 20 ef 02 00 40 f0 02 00 33 e2 a8 7e 1f 81 33 00 00 00 80 0c c5  [2 178 68 73 6 0 51 0 0 0 64 238 2 0 0 233 2 0 82 80 40 47 13 129 51 0 0 0 64 239 2 0 0 232 2 0 60 212 8 195 17 129 51 0 0 0 224 238 2 0 32 235 2 0 127 77 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 183 39 0 0 170 163 184 94 22 129 51 0 0 0 128 238 2 0 192 238 2 0 39 186 200 234 26 129 51 0 0 0 32 239 2 0 64 240 2 0 51 226 168 126 31 129 51 0 0 0 128 12 197 ]
//        Dexcom-C7: 02 b2 44 49 07 00 f0 02 00 e0 ef 02 00 be 91 88 12 24 81 33 00 00 00 40 f2 02 00 a0 ef 02 00 9e e4 68 a6 28 81 33 00 00 00 60 f4 02 00 80 f0 02 00 72 ae 48 3a 2d 81 33 00 00 00 e0 f5 02 00 80 f2 02 00 4d e3 28 ce 31 81 33 00 00 00 40 f6 02 00 a0 f4 02 00 fa 14 08 62 36 81 33 00 00 00 a0 ee 02 00 40 f5 02 00 2c 30 e8 f5 3a 81 33 00 00 00 60 db 02 00 60 f1 02 00 55 46 29 c8  [2 178 68 73 7 0 240 2 0 224 239 2 0 190 145 136 18 36 129 51 0 0 0 64 242 2 0 160 239 2 0 158 228 104 166 40 129 51 0 0 0 96 244 2 0 128 240 2 0 114 174 72 58 45 129 51 0 0 0 224 245 2 0 128 242 2 0 77 227 40 206 49 129 51 0 0 0 64 246 2 0 160 244 2 0 250 20 8 98 54 129 51 0 0 0 160 238 2 0 64 245 2 0 44 48 232 245 58 129 51 0 0 0 96 219 2 0 96 241 2 0 85 70 41 200 ]
//        Dexcom-C7: 02 b2 44 49 08 00 c8 89 3f 81 33 00 00 00 a0 cc 02 00 60 e7 02 00 48 17 a8 1d 44 81 33 00 00 00 40 d4 02 00 60 da 02 00 0a 19 88 b1 48 81 33 00 00 00 00 db 02 00 60 d1 02 00 e9 b0 80 41 4d 81 33 00 00 00 60 d5 02 00 60 d0 02 00 2d 02 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff c4 27 00 00 03 7a 60 d5 51 81 33 00 00 00 40 d2 02 00 20 d4 02 00 03 41 40 69 56 81 33 a1 91  [2 178 68 73 8 0 200 137 63 129 51 0 0 0 160 204 2 0 96 231 2 0 72 23 168 29 68 129 51 0 0 0 64 212 2 0 96 218 2 0 10 25 136 177 72 129 51 0 0 0 0 219 2 0 96 209 2 0 233 176 128 65 77 129 51 0 0 0 96 213 2 0 96 208 2 0 45 2 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 196 39 0 0 3 122 96 213 81 129 51 0 0 0 64 210 2 0 32 212 2 0 3 65 64 105 86 129 51 161 145 ]
//        Dexcom-C7: 02 b2 44 49 09 00 00 00 00 00 d1 02 00 40 d6 02 00 8a 55 20 fd 5a 81 33 00 00 00 e0 cc 02 00 00 d4 02 00 e6 a5 00 91 5f 81 33 00 00 00 c0 c1 02 00 a0 ce 02 00 6a 91 e0 24 64 81 33 00 00 00 a0 c1 02 00 40 c8 02 00 e7 67 c0 b8 68 81 33 00 00 00 60 c1 02 00 e0 c2 02 00 b4 b7 a0 4c 6d 81 33 00 00 00 40 c1 02 00 00 c0 02 00 99 5c 80 e0 71 81 33 00 00 00 e0 c0 02 00 c0 bf ad 41  [2 178 68 73 9 0 0 0 0 0 209 2 0 64 214 2 0 138 85 32 253 90 129 51 0 0 0 224 204 2 0 0 212 2 0 230 165 0 145 95 129 51 0 0 0 192 193 2 0 160 206 2 0 106 145 224 36 100 129 51 0 0 0 160 193 2 0 64 200 2 0 231 103 192 184 104 129 51 0 0 0 96 193 2 0 224 194 2 0 180 183 160 76 109 129 51 0 0 0 64 193 2 0 0 192 2 0 153 92 128 224 113 129 51 0 0 0 224 192 2 0 192 191 173 65 ]
//        Dexcom-C7: 02 b2 44 49 0a 00 02 00 50 c6 30 7c 76 81 33 00 00 00 c0 c0 02 00 80 c0 02 00 9b 9c 40 08 7b 81 33 00 00 00 40 c0 02 00 00 c1 02 00 ae 6d 20 9c 7f 81 33 00 00 00 20 c0 02 00 c0 c0 02 00 31 d2 00 30 84 81 33 00 00 00 80 bf 02 00 20 c0 02 00 6e ac e0 c3 88 81 33 00 00 00 a0 bd 02 00 60 bf 02 00 63 b7 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff d1 27 00 00 e1 dd c0 91 d8  [2 178 68 73 10 0 2 0 80 198 48 124 118 129 51 0 0 0 192 192 2 0 128 192 2 0 155 156 64 8 123 129 51 0 0 0 64 192 2 0 0 193 2 0 174 109 32 156 127 129 51 0 0 0 32 192 2 0 192 192 2 0 49 210 0 48 132 129 51 0 0 0 128 191 2 0 32 192 2 0 110 172 224 195 136 129 51 0 0 0 160 189 2 0 96 191 2 0 99 183 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 209 39 0 0 225 221 192 145 216 ]
//        22:50:07,001 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 0b 00 57 8d 81 33 00 00 00 00 bc 02 00 60 be 02 00 21 ff a0 eb 91 81 33 00 00 00 20 bb 02 00 00 bd 02 00 f1 bc 80 7f 96 81 33 00 00 00 20 bb 02 00 c0 bb 02 00 cd 0a 60 13 9b 81 33 00 00 00 00 bb 02 00 e0 ba 02 00 d3 d5 40 a7 9f 81 33 00 00 00 40 b8 02 00 40 ba 02 00 78 b6 20 3b a4 81 33 00 00 00 e0 b1 02 00 00 b9 02 00 32 7d 00 cf a8 81 33 00 00 00 20 bd 9d ef  [2 178 68 73 11 0 87 141 129 51 0 0 0 0 188 2 0 96 190 2 0 33 255 160 235 145 129 51 0 0 0 32 187 2 0 0 189 2 0 241 188 128 127 150 129 51 0 0 0 32 187 2 0 192 187 2 0 205 10 96 19 155 129 51 0 0 0 0 187 2 0 224 186 2 0 211 213 64 167 159 129 51 0 0 0 64 184 2 0 64 186 2 0 120 182 32 59 164 129 51 0 0 0 224 177 2 0 0 185 2 0 50 125 0 207 168 129 51 0 0 0 32 189 157 239 ]
//        22:50:07,326 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 0c 00 02 00 80 b7 02 00 dc 84 e0 62 ad 81 33 00 00 00 a0 c1 02 00 00 b8 02 00 da c1 c0 f6 b1 81 33 00 00 00 20 bd 02 00 e0 ba 02 00 dc 17 a0 8a b6 81 33 00 00 00 00 a7 02 00 e0 bb 02 00 eb 60 80 1e bb 81 33 00 00 00 00 99 02 00 20 b5 02 00 da 09 60 b2 bf 81 33 00 00 00 e0 8e 02 00 a0 a6 02 00 ce da 40 46 c4 81 33 00 00 00 60 89 02 00 20 96 02 00 fa 40 ff 50 75  [2 178 68 73 12 0 2 0 128 183 2 0 220 132 224 98 173 129 51 0 0 0 160 193 2 0 0 184 2 0 218 193 192 246 177 129 51 0 0 0 32 189 2 0 224 186 2 0 220 23 160 138 182 129 51 0 0 0 0 167 2 0 224 187 2 0 235 96 128 30 187 129 51 0 0 0 0 153 2 0 32 181 2 0 218 9 96 178 191 129 51 0 0 0 224 142 2 0 160 166 2 0 206 218 64 70 196 129 51 0 0 0 96 137 2 0 32 150 2 0 250 64 255 80 117 ]
//        22:50:07,650 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 0d 00 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff de 27 00 00 0f 09 20 da c8 81 33 00 00 00 a0 84 02 00 00 8a 02 00 70 da 00 6e cd 81 33 00 00 00 40 9a 02 00 e0 86 02 00 df 82 e0 01 d2 81 33 00 00 00 60 6d 02 00 e0 86 02 00 73 b8 c0 95 d6 81 33 00 00 00 c0 82 02 00 40 85 02 00 2d 25 a0 29 db 81 33 00 00 00 60 94 02 00 80 83 02 00 76 a8 80 bd df 81 33 00 60 c7  [2 178 68 73 13 0 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 222 39 0 0 15 9 32 218 200 129 51 0 0 0 160 132 2 0 0 138 2 0 112 218 0 110 205 129 51 0 0 0 64 154 2 0 224 134 2 0 223 130 224 1 210 129 51 0 0 0 96 109 2 0 224 134 2 0 115 184 192 149 214 129 51 0 0 0 192 130 2 0 64 133 2 0 45 37 160 41 219 129 51 0 0 0 96 148 2 0 128 131 2 0 118 168 128 189 223 129 51 0 96 199 ]
//        22:50:07,976 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 0e 00 00 00 c0 8e 02 00 80 84 02 00 a1 55 60 51 e4 81 33 00 00 00 e0 8a 02 00 40 8a 02 00 ef e7 40 e5 e8 81 33 00 00 00 60 88 02 00 c0 8e 02 00 91 37 20 79 ed 81 33 00 00 00 60 86 02 00 80 8d 02 00 42 d8 00 0d f2 81 33 00 00 00 a0 84 02 00 e0 88 02 00 79 78 e0 a0 f6 81 33 00 00 00 a0 80 02 00 80 84 02 00 e9 71 c0 34 fb 81 33 00 00 00 a0 7c 02 00 60 81 02 6f e5  [2 178 68 73 14 0 0 0 192 142 2 0 128 132 2 0 161 85 96 81 228 129 51 0 0 0 224 138 2 0 64 138 2 0 239 231 64 229 232 129 51 0 0 0 96 136 2 0 192 142 2 0 145 55 32 121 237 129 51 0 0 0 96 134 2 0 128 141 2 0 66 216 0 13 242 129 51 0 0 0 160 132 2 0 224 136 2 0 121 120 224 160 246 129 51 0 0 0 160 128 2 0 128 132 2 0 233 113 192 52 251 129 51 0 0 0 160 124 2 0 96 129 2 111 229 ]
//        22:50:08,306 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 0f 00 00 91 c9 a0 c8 ff 81 33 00 00 00 60 79 02 00 a0 7e 02 00 4b e1 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff eb 27 00 00 a3 99 80 5c 04 82 33 00 00 00 40 75 02 00 60 7b 02 00 b6 d9 60 f0 08 82 33 00 00 00 20 71 02 00 80 77 02 00 31 c5 40 84 0d 82 33 00 00 00 20 6e 02 00 60 73 02 00 7c 82 20 18 12 82 33 00 00 00 c0 69 02 00 80 6f 02 00 20 68 00 ac 41 e8  [2 178 68 73 15 0 0 145 201 160 200 255 129 51 0 0 0 96 121 2 0 160 126 2 0 75 225 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 235 39 0 0 163 153 128 92 4 130 51 0 0 0 64 117 2 0 96 123 2 0 182 217 96 240 8 130 51 0 0 0 32 113 2 0 128 119 2 0 49 197 64 132 13 130 51 0 0 0 32 110 2 0 96 115 2 0 124 130 32 24 18 130 51 0 0 0 192 105 2 0 128 111 2 0 32 104 0 172 65 232 ]
//        22:50:08,630 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 10 00 16 82 33 00 00 00 60 65 02 00 a0 6b 02 00 a3 f0 e0 3f 1b 82 33 00 00 00 e0 60 02 00 a0 67 02 00 bb 57 c0 d3 1f 82 33 00 00 00 c0 5b 02 00 40 63 02 00 12 82 a0 67 24 82 33 00 00 00 a0 56 02 00 60 5e 02 00 e8 be 80 fb 28 82 33 00 00 00 a0 51 02 00 60 59 02 00 89 d4 60 8f 2d 82 33 00 00 00 c0 47 02 00 a0 53 02 00 a6 9d 40 23 32 82 33 00 00 00 c0 33 02 e4 7b  [2 178 68 73 16 0 22 130 51 0 0 0 96 101 2 0 160 107 2 0 163 240 224 63 27 130 51 0 0 0 224 96 2 0 160 103 2 0 187 87 192 211 31 130 51 0 0 0 192 91 2 0 64 99 2 0 18 130 160 103 36 130 51 0 0 0 160 86 2 0 96 94 2 0 232 190 128 251 40 130 51 0 0 0 160 81 2 0 96 89 2 0 137 212 96 143 45 130 51 0 0 0 192 71 2 0 160 83 2 0 166 157 64 35 50 130 51 0 0 0 192 51 2 228 123 ]
//        22:50:08,954 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 11 00 00 80 4b 02 00 f2 6b 20 b7 36 82 33 00 00 00 60 28 02 00 c0 3f 02 00 a8 4d 00 4b 3b 82 33 00 00 00 40 1a 02 00 00 31 02 00 c2 9d ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff f8 27 00 00 d8 19 e0 de 3f 82 33 00 00 00 20 11 02 00 e0 21 02 00 e2 8c c0 72 44 82 33 00 00 00 80 0a 02 00 80 15 02 00 42 13 a0 06 49 82 33 00 00 00 60 08 02 00 00 0d 02 00 99 41  [2 178 68 73 17 0 0 128 75 2 0 242 107 32 183 54 130 51 0 0 0 96 40 2 0 192 63 2 0 168 77 0 75 59 130 51 0 0 0 64 26 2 0 0 49 2 0 194 157 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 255 248 39 0 0 216 25 224 222 63 130 51 0 0 0 32 17 2 0 224 33 2 0 226 140 192 114 68 130 51 0 0 0 128 10 2 0 128 21 2 0 66 19 160 6 73 130 51 0 0 0 96 8 2 0 0 13 2 0 153 65 ]
//        22:50:09,281 DEBUG [AnimasDexcomDataConverter:166] - Dexcom-C7: 02 b2 44 49 12 00 d4 f1 80 9a 4d 82 33 00 00 00 00 02 02 00 e0 07 02 00 d7 d1 60 2e 52 82 33 00 00 00 c0 fd 01 00 40 04 02 00 d8 81 40 c2 56 82 33 00 00 00 30 fa 01 00 a0 00 02 00 72 69 20 56 5b 82 33 00 00 00 f0 f5 01 00 60 fc 01 00 b7 be 00 ea 5f 82 33 00 00 00 40 f4 01 00 40 f8 01 00 cf d6 e0 7d 64 82 33 00 00 00 70 fd 01 00 30 f6 01 00 19 7f c0 11 69 82 33 00 00 69 20  [2 178 68 73 18 0 212 241 128 154 77 130 51 0 0 0 0 2 2 0 224 7 2 0 215 209 96 46 82 130 51 0 0 0 192 253 1 0 64 4 2 0 216 129 64 194 86 130 51 0 0 0 48 250 1 0 160 0 2 0 114 105 32 86 91 130 51 0 0 0 240 245 1 0 96 252 1 0 183 190 0 234 95 130 51 0 0 0 64 244 1 0 64 248 1 0 207 214 224 125 100 130 51 0 0 0 112 253 1 0 48 246 1 0 25 127 192 17 105 130 51 0 0 105 32 ]


    }



    private void decodeDexcomDataSpecific(AnimasDevicePacket packet, String name)
    {
        AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), name + ": %s [%s]", LOG);
    }



    // UTILS

    // DATA METHODS

    // FIXME need to be fixed ?? - hour_of_day (system_date - display_date)
    private ATechDate convertDexcomDateTimeToATechDate(short b1, short b2, short b3, short b4)
    {
        BigDecimal dec = AnimasUtils.createBigDecimalValueThroughMoreBits(b1, b2, b3, b4). //
                multiply(bigDecimals.get("BIG_DECIMAL_1000"));

        Calendar c = Calendar.getInstance(this.timezoneUTC);
        c.setTimeInMillis(dec.longValue());

        c.add(Calendar.YEAR, 38);
        c.add(Calendar.HOUR_OF_DAY, -1);

        return new ATechDate(ATechDate.FORMAT_DATE_AND_TIME_S, c);
    }


    public void decodeDexcomHistory(AnimasDevicePacket packet)
    {
        AnimasUtils.debugHexData(true, packet.dataReceived, packet.dataReceived.size(), "Dexcom-History: %s [%s]", LOG);

        for(int i = 6; i<packet.dataReceived.size()-2; i+=6)
        {
            AnimasDexcomHistoryEntry entry = new AnimasDexcomHistoryEntry();

            int val = AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(i), packet.getReceivedDataBit(i+1));
            System.out.println("Val: " + val);

            entry.glucoseValueWithFlags = (short)AnimasUtils.createIntValueThroughMoreBits(packet.getReceivedDataBit(i), packet.getReceivedDataBit(i+1));

            entry.dateTime = convertDexcomDateTimeToATechDate(packet.getReceivedDataBit(i+2),
                    packet.getReceivedDataBit(i+3),
                    packet.getReceivedDataBit(i+4),
                    packet.getReceivedDataBit(i+5));


            data.addDexcomHistory(entry);
        }

    }


    // SETTINGS












    // UTILS







}
