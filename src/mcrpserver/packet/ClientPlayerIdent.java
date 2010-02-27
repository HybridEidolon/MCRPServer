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
public class ClientPlayerIdent extends Packet {

    private byte version;
    private String username;
    private String verificationkey;
    private byte unused;

    public ClientPlayerIdent(byte[] bfr) {
        super(bfr);
        ByteBuffer pkt = ByteBuffer.wrap(bfr);

        this.version = pkt.get();
        byte[] strbfr = new byte[64];
        pkt.get(strbfr);
        username = new String(strbfr);
        pkt.get(strbfr);
        verificationkey = new String(strbfr);
        unused = pkt.get();
    }

    public ClientPlayerIdent(byte version, String username,
            String verificationkey) {
        this.id = OpCode.CLIENT_PLAYER_IDENT;
        this.username = username;
        this.verificationkey = verificationkey;
        this.unused = 0x00;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(2048);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // build up
        pkt.put((byte)id.id);
        pkt.put(version);
        
        // insert username
        int filler = 64-username.getBytes(Charset.forName("UTF-8")).length;
        pkt.put(username.getBytes());
        byte[] arrfill = new byte[filler];
        Arrays.fill(arrfill, (byte)0x20);
        pkt.put(arrfill);

        // insert verification key
        pkt.put(verificationkey.getBytes(Charset.forName("UTF-8")));
        filler = 64-verificationkey.getBytes().length;
        arrfill = new byte[filler];
        Arrays.fill(arrfill, (byte)0x20);
        pkt.put(arrfill);

        // unused byte
        pkt.put(unused);

        return pkt.array();
    }
}
