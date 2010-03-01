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

        // gzip level
        ByteArrayOutputStream baos;
        GZIPOutputStream gzos;
        DataOutputStream daos;
        byte[] level = null;
        try {
            daos = new DataOutputStream((gzos = new GZIPOutputStream(
                    (baos = new ByteArrayOutputStream()))));
            daos.writeInt(MCRPServer.level.blocks.length);
            daos.write(MCRPServer.level.blocks);
            daos.close();
            gzos.close();
            level = baos.toByteArray();
        } catch (IOException ex) {
            return;
        }

        // send level
        int sent = 0;
        int total = level.length;
        int left = 0;
        short chunksize = 0;
        while(sent < total) {
            left = total - sent;
            chunksize = (left < 1024 ? (short) left : 1024);

            MCRPServer.log(LogLevel.DEBUG, "Sending level chunk size "
                    + chunksize + ", " + sent + "/" + total);
            cls.sendServerLevelDataChunk(chunksize, Arrays.copyOfRange(
                    level, sent, sent+chunksize), (byte) ((sent * 100)/total));
            sent += chunksize;
        }

        // send finalize
        MCRPServer.log(LogLevel.DEBUG, "D: " + MCRPServer.level.width + "x"
                + MCRPServer.level.depth + "x" + MCRPServer.level.height);
        MCRPServer.log(LogLevel.DEBUG, "Total: " + MCRPServer.level.width*
                MCRPServer.level.depth*MCRPServer.level.height);
        MCRPServer.log(LogLevel.DEBUG, "Size: "
                + MCRPServer.level.blocks.length);
        cls.sendServerLevelFinalize((short)MCRPServer.level.width,
                (short)MCRPServer.level.depth, (short)MCRPServer.level.height);

        // send spawn
        cls.sendServerPlayerSpawn(cls.user.getID(), cls.user.getUsername(),
                (short)MCRPServer.level.xSpawn, (short)MCRPServer.level.ySpawn,
                (short)MCRPServer.level.zSpawn, (byte)MCRPServer.level.rotSpawn,
                (byte)0);
    }
}
