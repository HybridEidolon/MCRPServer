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
import mcrpserver.packet.OpCode;
import mcrpserver.packet.Packet;

/**
 *
 * @author Furyhunter
 */
public class ServerLevelFinalize extends Packet {

    private short xsize;
    private short ysize;
    private short zsize;

    public ServerLevelFinalize(short x, short y, short z) {
        this.id = OpCode.SERVER_LEVEL_FINALIZE;
        this.xsize = x;
        this.ysize = y;
        this.zsize = z;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(7);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        // put x size
        pkt.putShort(xsize);

        // put y size
        pkt.putShort(ysize);

        // put z size
        pkt.putShort(zsize);

        return pkt.array();
    }
}
