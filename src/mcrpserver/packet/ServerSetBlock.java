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
public class ServerSetBlock extends Packet {

    private short xpos;
    private short ypos;
    private short zpos;
    private byte blocktype;

    public ServerSetBlock(short x, short y, short z, byte type) {
        this.id = OpCode.SERVER_SET_BLOCK;
        this.xpos = x;
        this.ypos = y;
        this.zpos = z;
        this.blocktype = type;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(8);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        // put x
        pkt.putShort(xpos);

        // put y
        pkt.putShort(ypos);

        // put z
        pkt.putShort(zpos);

        // put blocktype
        pkt.put(blocktype);

        return pkt.array();
    }
}
