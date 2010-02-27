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

/**
 *
 * @author Furyhunter
 */
public class ClientMessage extends Packet {

    private byte unused; // Unused byte in the packet...
    private String message; // Message

    public ClientMessage(byte[] bfr) {
        ByteBuffer pkt = ByteBuffer.wrap(bfr);

        pkt.get();
        unused = pkt.get();
        byte[] strbfr = new byte[64];
        pkt.get(strbfr);
        message = new String(strbfr);
    }

    public byte getUnused() {
        return unused;
    }

    public String getMessage() {
        return message;
    }
}
