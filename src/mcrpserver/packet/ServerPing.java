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
public class ServerPing extends Packet {

    public ServerPing() {
        this.id = OpCode.SERVER_PING;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(2);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        pkt.put((byte)0x0A);

        return pkt.array();
    }
}
