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
public class ServerMessage extends Packet {

    private byte playerid;
    private String message;

    public ServerMessage(byte playerid, String message) {
        this.id = OpCode.SERVER_MESSAGE;
        this.playerid = playerid;
        this.message = message.substring(0,63);
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(1024);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // insert id
        pkt.put((byte)id.id);

        // insert playerid
        pkt.put(playerid);

        // insert message
        byte[] msg = message.getBytes(Charset.forName("US-ASCII"));
        int filler = 64-msg.length;
        pkt.put(msg);
        byte[] fill = new byte[filler];
        Arrays.fill(fill, (byte)0x20);
        pkt.put(fill);

        pkt.put((byte)0x0A);

        return pkt.array();
    }
}
