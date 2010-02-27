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
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * @author Furyhunter
 */
public class ServerPlayerDisconnect extends Packet {

    private String reason;

    public ServerPlayerDisconnect(String reason) {
        this.id = OpCode.SERVER_PLAYER_DISCONNECT;
        this.reason = reason;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(65);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        // put reason
        byte[] msg;
        try {
            msg = reason.substring(0, 63).getBytes(Charset.forName("US-ASCII"));
        } catch (StringIndexOutOfBoundsException ex) {
            msg = reason.getBytes(Charset.forName("US-ASCII"));
        }
        byte[] fill = new byte[64-msg.length];
        Arrays.fill(fill, (byte)0x20);

        return pkt.array();
    }
}
