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
import java.util.Arrays;
import mcrpserver.packet.OpCode;
import mcrpserver.packet.Packet;

/**
 *
 * @author Furyhunter
 */
public class ServerLevelDataChunk extends Packet {

    private short chunklength;
    private byte[] chunkdata;
    private byte percentcomplete;

    public ServerLevelDataChunk(short chunklength, byte[] chunkdata,
            byte percentcomplete) {
        this.id = OpCode.SERVER_LEVEL_CHUNK;
        this.chunkdata = chunkdata;
        this.percentcomplete = percentcomplete;
    }

    @Override
    public byte[] build() {
        ByteBuffer pkt = ByteBuffer.allocate(1028);
        pkt.order(ByteOrder.BIG_ENDIAN);

        // put id
        pkt.put((byte)id.id);

        // put chunklength
        pkt.putShort(chunklength);

        // put chunkdata
        pkt.put(Arrays.copyOf(chunkdata, 1024));
        
        // put percentcomplete
        pkt.put(percentcomplete);
        
        return pkt.array();
    }
}
