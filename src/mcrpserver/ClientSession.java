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
    private User user;
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
                                    user.getVerificationKey(),user.getType());
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
            } catch (IOException ex) {
                MCRPServer.log(LogLevel.ERROR, getName() + ": socket fail: "
                        + ex.getMessage());
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
            MCRPServer.log(LogLevel.ERROR, getName() + ": failed to send "
                    + "ServerMessage: " + ex.getMessage());
        }
    }

    public synchronized void sendServerIdent(byte playerid, String servername,
            String servermotd, byte playertype) {
        ServerIdent pkt = new ServerIdent(playerid, servername, servermotd,
                playertype);
        try {
            // send it
            sockOut.write(pkt.build());
            sockOut.flush();
        } catch (IOException ex) {
            MCRPServer.log(LogLevel.ERROR, getName() + ": failed to send "
                    + "ServerIdent: " + ex.getMessage());
        }
    }
}
