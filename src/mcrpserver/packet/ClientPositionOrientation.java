/*
 *  Copyright (C) 2010 Tak
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
 * @author Tak
 */
public class ClientPositionOrientation extends Packet {

    private byte playerid;
    private short xpos;
    private short ypos;
    private short zpos;
    private byte heading;
    private byte pitch;

    public ClientPositionOrientation(byte[] bfr) {
        ByteBuffer pkt = ByteBuffer.wrap(bfr);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // skip the first byte id
        pkt.get();

        // get playerid
        this.playerid = pkt.get();

        // get xpos
        this.xpos = pkt.getShort();

        // get ypos
        this.ypos = pkt.getShort();

        // get zpos
        this.zpos = pkt.getShort();

        // get heading
        this.heading = pkt.get();

        // get pitch
        this.pitch = pkt.get();
    }

    public byte getPlayerID() {
        return playerid;
    }

    public short getXPos() {
        return xpos;
    }

    public short getYPos() {
        return ypos;
    }

    public short getZPos() {
        return zpos;
    }

    public byte getHeading() {
        return heading;
    }

    public byte getPitch() {
        return pitch;
    }
}
