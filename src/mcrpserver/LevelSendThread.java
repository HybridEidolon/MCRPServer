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
package mcrpserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Furyhunter
 */
public class LevelSendThread extends Thread {

    private ClientSession cls;

    public LevelSendThread(ClientSession cls) {
        this.cls = cls;
        this.start();
    }

    @Override
    public void run() {
        // send the client the init
        cls.sendServerLevelInitialize();
        MCRPServer.log(LogLevel.DEBUG, "Sent level init");

        // send the level
        MCRPServer.log(LogLevel.DEBUG, "Sending level");
        byte[] level;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzos = new GZIPOutputStream(baos);
            DataOutputStream out = new DataOutputStream(gzos);
            out.writeInt(MCRPServer.level.blocks.length);
            out.write(MCRPServer.level.blocks);
            out.flush();
            gzos.close();
            out.close();
            level = baos.toByteArray();
            baos.close();
        } catch (IOException ex) {
            return;
        }
        ArrayList<byte[]> chunks = new ArrayList<byte[]>();
        int num = 0;
        boolean done = false;
        try {
            while (!done) {
                chunks.add(num,Arrays.copyOfRange(level, 1024*num,
                        (1024*(num+1))-1));
                num++;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            done = true;
        }

        int pos = 0;
        try {
            for (int i=0; pos<=num; i++) {
                if (chunks.get(i) != null) {
                    byte[] chunk = chunks.get(i);
                    pos++;
                    MCRPServer.log(LogLevel.DEBUG, "Send chunk: "
                            + (pos/num)*100 + "%");
                    cls.sendServerLevelDataChunk((short)chunk.length, chunk,
                            (byte)Math.round((pos/num)*100));
                    chunks.remove(i);
                }
            }
        } catch (IndexOutOfBoundsException ex) {
        }

        // send finalize
        cls.sendServerLevelFinalize((short)MCRPServer.level.width,
                (short)MCRPServer.level.height, (short)MCRPServer.level.depth);
        MCRPServer.log(LogLevel.DEBUG, "Sent finalize");

        // send spawn
        cls.sendServerPlayerSpawn(cls.user.getID(), cls.user.getUsername(),
                (short)MCRPServer.level.xSpawn, (short)MCRPServer.level.ySpawn,
                (short)MCRPServer.level.zSpawn, (byte)MCRPServer.level.rotSpawn,
                (byte)0);
        MCRPServer.log(LogLevel.DEBUG, "Player spawned");
    }
}
