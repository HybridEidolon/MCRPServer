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
import mcrpserver.packet.Packet;

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

    public ClientSession(Socket sock, String name) {
        this.sock = sock;
        this.setName(name);
    }

    @Override
    public void run() {
        // check properties
        if (sock == null) {
            Main.log(LogLevel.ERROR, getName() + ": Bad socket, stopping");
            return;
        }

        Main.log(LogLevel.VERBOSE, getName() + ": "
                + sock.getInetAddress().toString() + " connected");

        try {
            sockIn = new BufferedInputStream(sock.getInputStream());
            sockOut = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException ex) {
            Main.log(LogLevel.ERROR, getName() + ": IOException setting up "
                    + "streams: " + ex.getMessage());
        }

        // enter infinite loop
        byte[] bfr = new byte[2048];
        int value = 0;
        while (running) {
            try {
                // parse incoming packet
                if ((value = sockIn.read(bfr)) > -1) {
                    // TODO: parse incoming packet
                } else {
                    running = false;
                    break;
                }
            } catch (IOException ex) {
                Main.log(LogLevel.ERROR, getName() + ": socket fail: "
                        + ex.getMessage());
            }
        }

        Main.log(LogLevel.VERBOSE, getName() + ": "
                + sock.getInetAddress().toString() + " disconnected");
    }

    public Packet parse() {

        return null;
    }
}
