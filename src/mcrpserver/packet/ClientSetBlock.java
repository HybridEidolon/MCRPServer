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
public class ClientSetBlock extends Packet {
    private byte packetid;
    private short xpos;
    private short ypos;
    private short zpos;
    private byte mode;
    private byte blocktype;

    public ClientSetBlock(byte[] bfr) {
        ByteBuffer pkt = ByteBuffer.wrap(bfr);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // skip the first byte id
        pkt.get();

        // get playerid
        this.packetid = pkt.get();

        // get xpos
        this.xpos = pkt.getShort();

        // get ypos
        this.ypos = pkt.getShort();

        // get zpos
        this.zpos = pkt.getShort();

        // get heading
        this.mode = pkt.get();

        // get pitch
        this.blocktype = pkt.get();
    }

    public byte getPacketID() {
        return packetid;
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

    public byte getBlocktype() {
        return blocktype;
    }

    public boolean getDestroyed() {
        if ( mode == 0x01 ) {
            return false;
        } else {
            return true;
        }
    }


}
