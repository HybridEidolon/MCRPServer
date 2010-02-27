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
public class ServerIdent extends Packet {

    private byte version;
    private String servername;
    private String servermotd;
    private byte playertype;

    public ServerIdent(byte version, String servername, String servermotd,
            byte playertype) {
        this.id = OpCode.SERVER_IDENT;
        this.version = version;
        this.servername = servername;
        this.servermotd = servermotd;
        this.playertype = playertype;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(1024);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // insert id
        pkt.put((byte) id.id);

        // insert version
        pkt.put(version);

        // insert servername
        byte[] msg = servername.substring(0, 63)
                .getBytes(Charset.forName("US-ASCII"));
        int filler = 64-msg.length;
        byte[] fill = new byte[filler];
        Arrays.fill(fill, (byte)0x20);
        pkt.put(msg);
        pkt.put(fill);

        // insert servermotd
        msg = servermotd.substring(0, 63)
                .getBytes(Charset.forName("US_ASCII"));
        filler = 64-msg.length;
        fill = new byte[filler];
        Arrays.fill(fill, (byte)0x20);
        pkt.put(msg);
        pkt.put(fill);

        // insert playertype
        pkt.put(playertype);

        pkt.put((byte)0x0A);
        
        return pkt.array();
    }
}
