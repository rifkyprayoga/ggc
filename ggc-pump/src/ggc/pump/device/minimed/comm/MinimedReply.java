package ggc.pump.device.minimed.comm;

import ggc.plugin.device.impl.minimed.MinimedDeviceUtil;

public class MinimedReply
{
    public int msg_header = 0;
    public int[] serial_number = new int[3];

    public int msg_command_reply = 0; // 80
    public int msg_packet_nr = 0;

    public int msg_body[] = null;

    public int msg_footer = 0;

    MinimedDeviceUtil mu = MinimedDeviceUtil.getInstance();

    // HexUtils hu = new HexUtils();

    public MinimedReply(int[] reply)
    {
        mu.getHexUtils().setUseShortHex(true);
        unpack(reply);
    }

    public void unpack(int[] reply)
    {
        this.msg_header = reply[0];
        this.serial_number = mu.getHexUtils().getIntSubArray(reply, 1, reply.length - 3, reply.length);
        this.msg_command_reply = reply[4];
        this.msg_packet_nr = reply[5];
        this.msg_body = mu.getHexUtils().getIntSubArray(reply, 6, 1, reply.length);
        this.msg_footer = reply[reply.length - 1];
    }

    @Override
    public String toString()
    {
        return " Packet [header=" + mu.getHexUtils().getHex(this.msg_header) + ",serial="
                + mu.getHexUtils().getHex(this.serial_number) + ",command_reply="
                + mu.getHexUtils().getHex(this.msg_command_reply) + ",packet_nr="
                + mu.getHexUtils().getHex(this.msg_packet_nr) + "\n" + ",body="
                + mu.getHexUtils().getHex(this.msg_body) + "\n" + ",end=" + mu.getHexUtils().getHex(this.msg_footer)
                + "]";
    }

    // 14 0A 55 90 0E 28 32 07 3C 00 00 0D 00 3C 30 08 28 10 00 0C 42 00 01 3C
    // 3C 00 7B CE 49 14 0A

    // 17:05:25,296 $RS232Command-readDeviceDataPage: just read packet 0, bytes
    // = 64, total bytes = 64
    /*
     * A7 31 65 51 80 01 01 3C 3C 00 7A FA 4D 13 0A 39 0F 62 F1 8F 73 0A 89 59
     * 43 39 27 74 E6 D1 73 0A 89 59 43 2F B1 67 E7 11 13 0A 00 90 0F 28 32 1F
     * 00 00 00 15 00 0A 30 11 F0 DD 00 30 37 10 18 9B 10 A0 A5 10 0C 13 72>
     * A7 31 65 51 80 02 20 01 1F 1F 00 67 E7 51 13 0A 39 1B 6C E4 B2 73 0A 89
     * 59 43 2F 00 70 DE 13 13 0A 0F 90 0F 28 32 00 0A 00 00 00 00 0A 30 11 7C
     * 70 00 F0 4C 10 30 A6 10 18 0A 20 A0 14 20 01 0A 0A 00 70 DE 53 13 12>
     * A7 31 65 51 80 03 0A 2F 00 68 F7 13 13 0A 0F 90 0F 28 32 00 0A 00 00 00
     * 00 0A 30 14 28 1B 00 7C 89 00 F0 65 10 30 BF 10 18 23 20 A0 2D 20 01 0A
     * 0A 00 68 F7 53 13 0A 2F 00 57 C9 14 13 0A 0F 90 0F 28 33 00 0A 00 7C>
     * A7 31 65 51 80 04 00 00 00 0A 30 17 28 15 00 28 29 00 7C 97 00 F0 73 10
     * 30 CD 10 18 31 20 A0 3B 20 01 0A 0A 00 58 C9 54 13 0A 2F 00 52 E2 15 13
     * 0A 0C 90 0F 28 34 00 08 00 00 00 00 08 30 14 28 56 00 28 6A 00 28 00>
     * A7 31 65 51 80 05 7E 00 7C EC 00 F0 C8 10 30 22 20 01 08 08 00 53 E2 55
     * 13 0A 2F 00 64 C0 16 13 0A 0F 90 0F 22 34 00 0A 00 00 00 00 0A 30 17 20
     * 20 00 28 70 00 28 84 00 28 98 00 7C 06 10 F0 E2 10 30 3C 20 01 0A C8>
     * A7 31 65 51 80 06 0A 00 65 C0 56 13 0A 2F 00 4D CA 16 13 0A 0C 90 0F 22
     * 34 00 08 00 00 00 00 08 30 1A 28 0C 00 20 2A 00 28 7A 00 28 8E 00 28 A2
     * 00 7C 10 10 F0 EC 10 30 46 20 01 08 08 00 4D CA 56 13 0A 39 10 42 14>
     * A7 31 65 51 80 07 C3 F7 73 0A 89 59 43 07 00 00 07 12 73 8A 39 15 74 C8
     * A8 74 0A 89 59 43 2F 60 69 C9 08 14 0A 00 90 0E 2A 32 0A 00 00 00 00 00
     * 0A 01 03 03 00 6A C9 48 14 0A 1E 00 5E DE 08 14 0A 1F 00 7A C5 09 1B>
     * A7 31 65 51 80 08 14 0A 01 0A 0A 00 5D C6 49 14 0A 39 11 56 C7 89 74 0A
     * 89 59 43 2F 4E 7B CE 09 14 0A 55 90 0E 28 32 07 3C 00 00 0D 00 3C 30 08
     * 28 10 00 0C 42 00 01 3C 3C 00 7B CE 49 14 0A 39 16 52 D2 6B 74 0A 8D>
     * A7 31 65 51 80 09 89 59 43 2F 63 44 D3 0B 14 0A 00 90 0F 28 32 0C 00 00
     * 00 2E 00 00 30 0E 3C 79 00 B4 83 00 28 8D 00 0C BF 00 01 03 03 00 44 D3
     * 4B 14 0A 1E 00 76 E4 0B 14 0A 1F 00 64 C4 0D 14 0A 2F 00 77 C4 0D E0>
     * A7 31 65 51 80 0A 14 0A 0C 90 0F 28 32 00 08 00 00 00 00 08 30 11 0C 6A
     * 00 3C E2 00 B4 EC 00 28 F6 00 0C 28 10 01 0F 0F 00 78 C4 4D 14 0A 2F 00
     * 6F D7 0D 14 0A 0C 90 0F 28 32 00 08 00 00 00 00 08 30 14 3C 19 00 B5>
     * A7 31 65 51 80 0B 0C 7D 00 3C F5 00 B4 FF 00 28 09 10 0C 3B 10 01 08 08
     * 00 6F D7 4D 14 0A 39 0E 56 F5 4D 74 0A 89 59 43 2F 00 60 DE 0E 14 0A 3C
     * 90 0F 28 32 00 28 00 00 00 00 28 30 17 20 48 00 3C 5C 00 0C C0 00 DB>
     * A7 31 65 51 80 0C 3C 38 10 B4 42 10 28 4C 10 0C 7E 10 01 28 28 00 60 DE
     * 4E 14 0A 39 0C 6A C5 31 74 0A 89 59 43 2F 00 79 F8 11 14 0A 0F 90 0F 28
     * 32 00 0A 00 00 00 00 0A 30 1A A0 D0 00 20 16 10 3C 2A 10 0C 8E 10 A6>
     * A7 31 65 51 80 0D 3C 06 20 B4 10 20 28 1A 20 0C 4C 20 01 0A 0A 00 79 F8
     * 51 14 0A 39 12 4A C0 33 74 0A 89 59 43 2F 00 43 C0 14 14 0A 37 90 0F 28
     * 32 00 24 00 00 00 00 24 30 11 28 84 00 A0 4C 10 20 92 10 3C A6 10 70>
     * A7 31 65 51 80 0E 0C 0A 20 01 24 24 00 43 C0 54 14 0A 2F 00 50 CE 14 14
     * 0A 19 90 0F 28 33 00 10 00 00 00 00 10 30 14 90 10 00 28 92 00 A0 5A 10
     * 20 A0 10 3C B4 10 0C 18 20 34 C8 74 CE 14 14 0A 01 10 10 00 50 CE 7D>
     * A7 31 65 51 80 0F 54 14 0A 39 0B 6A D8 F5 74 0A 89 59 43 2F 35 6D DD 15
     * 14 0A 08 90 0F 28 34 00 05 00 00 2F 00 05 30 14 40 51 00 90 5B 00 28 DD
     * 00 A0 A5 10 20 EB 10 3C FF 10 01 05 05 00 6D DD 55 14 0A 39 0D 40 AE>
     * A7 31 65 51 80 90 E7 16 74 0A 89 59 43 2F 00 44 C2 17 14 0A 0F 90 0F 22
     * 34 00 0A 00 00 00 00 0A 30 14 14 5E 00 40 AE 00 90 B8 00 28 3A 10 A0 02
     * 20 20 48 20 01 0A 0A 00 44 C2 57 14 0A 07 00 00 06 06 74 8A 55 94 2B>
     */

    /*
     * 14 0A 00 90 0E 2A 32 0A 00 00 00 00 00 0A 01 03 03 00 6A C9 48
     * 14 0A 1E 00 5E DE 08
     * 14 0A 1F 00 7A C5 09 1B
     * 14 0A 01 0A 0A 00 5D C6 49
     * 14 0A 39 11 56 C7 89 74 0A 89 59 43 2F 4E 7B CE 09
     * 14 0A 55 90 0E 28 32 07 3C 00 00 0D 00 3C 30 08 28 10 00 0C 42 00 01 3C
     * 3C 00 7B CE 49
     * 14 0A 39 16 52 D2 6B 74 0A 89 59 43 2F 63 44 D3 0B
     * 14 0A 00 90 0F 28 32 0C 00 00 00 2E 00 00 30 0E 3C 79 00 B4 83 00 28 8D
     * 00 0C BF 00 01 03 03 00 44 D3 4B
     * 14 0A 1E 00 76 E4 0B
     * 14 0A 1F 00 64 C4 0D
     * 14 0A 2F 00 77 C4 0D
     * 14 0A 0C 90 0F 28 32 00 08 00 00 00 00 08 30 11 0C 6A 00 3C E2 00 B4 EC
     * 00 28 F6 00 0C 28 10 01 0F 0F 00 78 C4 4D
     * 14 0A 2F 00 6F D7 0D
     * 14 0A 0C 90 0F 28 32 00 08 00 00 00 00 08 30 14 3C 19 00 0C 7D 00 3C F5
     * 00 B4 FF 00 28 09 10 0C 3B 10 01 08 08 00 6F D7 4D 14 0A 39 0E 56 F5 4D
     * 74 0A 89 59 43 2F 00 60 DE 0E
     * 14 0A 3C 90 0F 28 32 00 28 00 00 00 00 28 30 17 20 48 00 3C 5C 00 0C C0
     * 00
     * 2010 0 144 14 42 50 10 0 0 0 0 0 10 1 3 3 0 106 201 72 20 10 30 0 94 222
     * 8
     * 2010 31
     * 0 122 197 9 27 20 10 1 10 10 0 93 198 73 20 10 57 17 86 199 137 116 10
     * 137 89 67 47 78 123 206
     * 9 20 10 85 144 14 40 50 7 60 0 0 13 0 60 48 8 40 16 0 12 66 0 1 60 60 0
     * 123 206 73
     * 20 10 57 22 82 210 107 116 10 137 89 67 47 99 68 211 11 20 10 0 144 15 40
     * 50 12 0 0 0 46 0
     * 0 48 14 60 121 0 180 131 0 40 141 0 12 191 0 1 3 3 0 68 211 75 20 10 30 0
     * 118 228 11 20
     * 10 31 0 100 196 13 20 10 47 0 119 196 13 20 10 12 144 15 40 50 0 8 0 0 0
     * 0 8 48 17 12
     * 106 0 60 226 0 180 236 0 40 246 0 12 40 16 1 15 15 0 120 196 77 20 10 47
     * 0 111 215 13 20 10
     * 12 144 15 40 50 0 8 0 0 0 0 8 48 20 60 25 0 12 125 0 60 245 0 180 255 0
     * 40 9 16 12
     * 59 16 1 8 8 0 111 215 77 20 10 57 14 86 245 77 116 10 137 89 67 47 0 96
     * 222 14 20 10 60 144
     * 15 40 50 0 40 0 0 0 0 40 48 23 32 72 0 60 92 0 12 192 0
     */

    // 14 0A 00 90 0E 2A 32 0A 00 00 00 00 00 0A 01 03 03 00 6A C9 48 14 0A 1E
    // 00 5E DE 08 14 0A 1F 00 7A C5 09 1B 14 0A 01 0A 0A 00 5D C6 49 14 0A 39
    // 11 56 C7 89 74 0A 89 59 43 2F 4E 7B CE 09 14 0A 55 90 0E 28 32 07 3C 00
    // 00 0D 00 3C 30 08 28 10 00 0C 42 00 01 3C 3C 00 7B CE 49 14 0A 39 16 52
    // D2 6B 74 0A 89 59 43 2F 63 44 D3 0B 14 0A 00 90 0F 28 32 0C 00 00 00 2E
    // 00 00 30 0E 3C 79 00 B4 83 00 28 8D 00 0C BF 00 01 03 03 00 44 D3 4B 14
    // 0A 1E 00 76 E4 0B 14 0A 1F 00 64 C4 0D 14 0A 2F 00 77 C4 0D 14 0A 0C 90
    // 0F 28 32 00 08 00 00 00 00 08 30 11 0C 6A 00 3C E2 00 B4 EC 00 28 F6 00
    // 0C 28 10 01 0F 0F 00 78 C4 4D 14 0A 2F 00 6F D7 0D 14 0A 0C 90 0F 28 32
    // 00 08 00 00 00 00 08 30 14 3C 19 00 0C 7D 00 3C F5 00 B4 FF 00 28 09 10
    // 0C 3B 10 01 08 08 00 6F D7 4D 14 0A 39 0E 56 F5 4D 74 0A 89 59 43 2F 00
    // 60 DE 0E 14 0A 3C 90 0F 28 32 00 28 00 00 00 00 28 30 17 20 48 00 3C 5C
    // 00 0C C0 00

}
