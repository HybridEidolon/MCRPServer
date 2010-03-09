/*
 *  Copyright (C) 2010 Furyhunter
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcrpserver.packet.server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import mcrpserver.packet.OpCode;
import mcrpserver.packet.Packet;

/**
 *
 * @author Furyhunter
 */
public class ServerPlayerSpawn extends Packet {

    private byte playerid;
    private String playername;
    private short xpos;
    private short ypos;
    private short zpos;
    private byte heading;
    private byte pitch;

    public ServerPlayerSpawn(byte playerid, String playername, short x,
            short y, short z, byte heading, byte pitch) {
        this.id = OpCode.SERVER_PLAYER_SPAWN;
        this.playerid = playerid;
        this.playername = playername;
        this.xpos = x;
        this.ypos = y;
        this.zpos = z;
        this.heading = heading;
        this.pitch = pitch;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(74);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        // put player id
        pkt.put(playerid);

        // put player name
        byte[] msg;
        try {
            msg = playername.substring(0,63)
                    .getBytes(Charset.forName("US-ASCII"));
        } catch (StringIndexOutOfBoundsException ex) {
            msg = playername.getBytes(Charset.forName("US-ASCII"));
        }
        byte[] fill = new byte[64-msg.length];
        Arrays.fill(fill, (byte)0x20);
        pkt.put(msg);
        pkt.put(fill);

        // put x y z
        pkt.putShort(xpos);
        pkt.putShort(ypos);
        pkt.putShort(zpos);

        // put heading, pitch
        pkt.put(heading);
        pkt.put(pitch);

        return pkt.array();
    }
}
