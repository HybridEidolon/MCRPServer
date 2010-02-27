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
public class ClientPlayerIdent extends Packet {

    private byte version;
    private String username;
    private String verificationkey;
    private byte unused;

    public ClientPlayerIdent(byte[] bfr) {
        ByteBuffer pkt = ByteBuffer.wrap(bfr);

        this.version = pkt.get();
        byte[] strbfr = new byte[64];
        pkt.get(strbfr);
        username = new String(strbfr);
        pkt.get(strbfr);
        verificationkey = new String(strbfr);
        unused = pkt.get();
    }

    public byte getVersion() {
        return version;
    }

    public String getUsername() {
        return username;
    }

    public String getVerificationKey() {
        return verificationkey;
    }

    public byte getUnused() {
        return unused;
    }
}
