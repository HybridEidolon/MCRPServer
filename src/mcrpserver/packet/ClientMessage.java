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
public class ClientMessage extends Packet {

    private byte unused; // Unused byte in the packet...
    private String message; // Message

    public ClientMessage(byte[] bfr) {
        super(bfr);
        ByteBuffer pkt = ByteBuffer.wrap(bfr);

        pkt.get();
        unused = pkt.get();
        byte[] strbfr = new byte[64];
        pkt.get(strbfr);
        message = new String(strbfr);
    }

    public ClientMessage(String message) {
        this.id = OpCode.CLIENT_MESSAGE;
        this.message = message;
        this.unused = 0x00;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(128);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // build up
        pkt.put((byte)id.id);
        pkt.put(unused);
        // need to trim message to 64 bytes...
        String newmessage = message.substring(0,63);
        pkt.put(newmessage.getBytes(Charset.forName("UTF-8")));

        byte[] blank = new byte[64-newmessage.length()];
        Arrays.fill(blank, (byte)0x20);
        pkt.put(blank);

        // done!
        return pkt.array();
    }
}
