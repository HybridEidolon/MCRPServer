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

import mcrpserver.packet.server.ServerPlayerPosition;
import mcrpserver.packet.server.ServerPlayerSpawn;
import mcrpserver.packet.server.ServerPlayerDisconnect;
import mcrpserver.packet.server.ServerPing;
import mcrpserver.packet.server.ServerPlayerPositionOrient;
import mcrpserver.packet.server.ServerLevelInitialize;
import mcrpserver.packet.server.ServerLevelDataChunk;
import mcrpserver.packet.server.ServerPlayerTeleport;
import mcrpserver.packet.server.ServerSetBlock;
import mcrpserver.packet.server.ServerLevelFinalize;
import mcrpserver.packet.server.ServerMessage;
import mcrpserver.packet.server.ServerIdent;
import mcrpserver.packet.server.ServerPlayerDespawn;
import mcrpserver.packet.server.ServerPlayerOrient;
import mcrpserver.util.LogLevel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import mcrpserver.packet.*;

/**
 *
 * @author Furyhunter
 */
public class ClientSession extends Thread {

    private Socket sock;
    private String username;
    private String passhash;
    private boolean running = true;
    private BufferedInputStream sockIn;
    private BufferedOutputStream sockOut;
    public User user;
    private boolean[] playeridslist = new boolean[256];

    public ClientSession(Socket sock, String name) {
        this.sock = sock;
        this.setName(name);
        this.start();
    }

    @Override
    public void run() {
        // check properties
        if (sock == null) {
            MCRPServer.log(LogLevel.ERROR, getName() + ": Bad socket, stopping");
            return;
        }

        MCRPServer.log(LogLevel.VERBOSE, getName() + ": "
                + sock.getInetAddress().toString() + " connected");

        try {
            sockIn = new BufferedInputStream(sock.getInputStream());
            sockOut = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.ERROR, getName() + ": IOException setting up "
                    + "streams: " + ex.getMessage());
        }

        // enter infinite loop
        byte[] bfr = new byte[2048];
        int value = 0;
        while (running) {
            try {
                // parse incoming packet
                if ((value = sockIn.read(bfr)) > -1) {
                    Packet pkt = getPacket(bfr);

                    if (pkt instanceof ClientPlayerIdent) {
                        ClientPlayerIdent pkt2 = (ClientPlayerIdent) pkt;
                        int i;
                        // find unused player id
                        for (i = 0; i < 256; i++) {
                            if (!playeridslist[i]) {
                                break;
                            }
                        }
                        playeridslist[i] = true;

                        // tell other players of their existance
                        user = new User((byte) i, pkt2.getUsername(),
                                pkt2.getVerificationKey());
                        if (!user.verify()) {
                            // TODO: disconnect the user
                            running = false;
                            break;
                        } else {
                            sendServerIdent(user.getID(),
                                    MCRPServer.config.getProperty("server.name",
                                    "default"),
                                    MCRPServer.config.getProperty("server.motd",
                                    "default"),user.getType());
                            // send the map
                            new LevelSendThread(this);
                        }
                    }

                    if (pkt instanceof ClientMessage) {
                        ClientMessage pkt2 = (ClientMessage)pkt;

                        MCRPServer.broadcastMessage(user.getID(),
                                pkt2.getMessage());
                    }
                } else {
                    running = false;
                    break;
                }
            } catch (SocketException ex) {
                running = false;
            } catch (IOException ex) {
                MCRPServer.log(LogLevel.ERROR, getName() + ": socket fail: "
                        + ex.getMessage());
                running = false;
            }
        }

        MCRPServer.log(LogLevel.VERBOSE, getName() + ": "
                + sock.getInetAddress().toString() + " disconnected");
        end();
    }

    public synchronized void end() {
        try {
            sock.close();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.ERROR, getName() + ": failed IOException "
                    + "on end(): " + ex.getMessage());
        }
        playeridslist[user.getID()] = false;
    }

    public static Packet getPacket(byte[] bfr) {
        int id = (int) bfr[0];
        Packet returnvalue = null;

        if (id == OpCode.CLIENT_PLAYER_IDENT.id) {
            returnvalue = new ClientPlayerIdent(bfr);
        }
        if (id == OpCode.CLIENT_MESSAGE.id) {
            returnvalue = new ClientMessage(bfr);
        }
        if (id == OpCode.CLIENT_SET_BLOCK.id) {
            returnvalue = new ClientSetBlock(bfr);
        }
        if (id == OpCode.CLIENT_SET_POSITION.id) {
            returnvalue = new ClientPositionOrientation(bfr);
        }
        return returnvalue;
    }

    /* BEGIN PACKET SEND METHODS */
    /**
     * Sends a chat message to the socket.
     * @param playerid the playerid to send it to, or 0 for server message
     * @param message the message to be sent, max 64 characters
     */
    public synchronized void sendServerMessage(byte playerid, String message) {
        // create packet
        ServerMessage pkt = new ServerMessage(playerid, message);

        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerMessage: " + ex.getMessage());
        }
    }


    /**
     * Sends the server identification info to the socket.
     * @param playerid the player's unique identifier
     * @param servername the name of the server
     * @param servermotd the message of the day
     * @param playertype the type of player, 0x00 for regular 0x64 for admin
     */
    public synchronized void sendServerIdent(byte playerid, String servername,
            String servermotd, byte playertype) {
        ServerIdent pkt = new ServerIdent(playerid, servername, servermotd,
                playertype);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerIdent: " + ex.getMessage());
        }
    }

    /**
     * Sends a ping to the socket.
     */
    public synchronized void sendServerPing() {
        try {
            sockOut.write(new ServerPing().build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPing: " + ex.getMessage());
        }
    }

    /**
     * Sends the level initialize notice to the socket.
     */
    public synchronized void sendServerLevelInitialize() {
        try {
            sockOut.write(new ServerLevelInitialize().build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerLevelInitialize: " + ex.getMessage());
        }
    }

    public synchronized void sendServerLevelDataChunk(short chunksize,
            byte[] chunk, byte pctcompl) {
        ServerLevelDataChunk pkt = new ServerLevelDataChunk(chunksize,
                chunk, pctcompl);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerLevelDataChunk: " + ex.getMessage());
        }
    }

    public synchronized void sendServerLevelFinalize(short x, short y,
            short z) {
        ServerLevelFinalize pkt = new ServerLevelFinalize(x, y, z);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerLevelFinalize: " + ex.getMessage());
        }
    }

    public synchronized void sendServerSetBlock(short x, short y, short z,
            byte type) {
        ServerSetBlock pkt = new ServerSetBlock(x, y, z, type);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerSetBlock: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerSpawn(byte plrid, String name,
            short x, short y, short z, byte heading, byte pitch) {
        ServerPlayerSpawn pkt = new ServerPlayerSpawn(plrid, name, x, y, z,
                heading, pitch);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerSpawn: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerTeleport(byte plrid, short x,
            short y, short z, byte heading, byte pitch) {
        ServerPlayerTeleport pkt = new ServerPlayerTeleport(plrid, x, y, z,
                heading, pitch);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerTeleport: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerPositionOrient(byte plrid, byte x,
            byte y, byte z, byte heading, byte pitch) {
        ServerPlayerPositionOrient pkt = new ServerPlayerPositionOrient(plrid,
                x, y, z, heading, pitch);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerPositionOrient: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerPosition(byte plrid, byte x,
            byte y, byte z) {
        ServerPlayerPosition pkt = new ServerPlayerPosition(plrid, x, y, z);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerPosition: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerOrient(byte plrid, byte heading,
            byte pitch) {
        ServerPlayerOrient pkt = new ServerPlayerOrient(plrid, heading, pitch);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerOrient: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerDespawn(byte plrid) {
        ServerPlayerDespawn pkt = new ServerPlayerDespawn(plrid);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerDespawn: " + ex.getMessage());
        }
    }

    public synchronized void sendServerPlayerDisconnect(String reason) {
        ServerPlayerDisconnect pkt = new ServerPlayerDisconnect(reason);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.VERBOSE, getName() + ": failed to send "
                    + "ServerPlayerDisconnect: " + ex.getMessage());
        }
    }
}
