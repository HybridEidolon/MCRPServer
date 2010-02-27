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
package mcrpserver.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Furyhunter
 */
public class ServerPlayerPositionOrient extends Packet {

    private byte playerid;
    private byte xdelta;
    private byte ydelta;
    private byte zdelta;
    private byte heading;
    private byte pitch;

    public ServerPlayerPositionOrient(byte playerid, byte x, byte y, byte z,
            byte heading, byte pitch) {
        this.id = OpCode.SERVER_PLAYER_POSITIONORIENT;
        this.playerid = playerid;
        this.xdelta = x;
        this.ydelta = y;
        this.zdelta = z;
        this.heading = heading;
        this.pitch = pitch;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(1024);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte) id.id);

        // put player id
        pkt.put(playerid);

        // put x y z
        pkt.put(xdelta);
        pkt.put(ydelta);
        pkt.put(zdelta);

        // put heading, pitch
        pkt.put(heading);
        pkt.put(pitch);

        pkt.put((byte) 0x0A);

        return pkt.array();
    }
}
