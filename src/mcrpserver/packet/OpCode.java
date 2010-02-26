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

/**
 * Enumeration of server commands
 * @author Furyhunter
 */
public enum OpCode {
    CLIENT_PLAYER_IDENT(0x00),
    CLIENT_SET_BLOCK(0x05),
    CLIENT_SET_POSITION(0x08),
    CLIENT_MESSAGE(0x0d),
    SERVER_IDENT(0x00),
    SERVER_PING(0x01),
    SERVER_LEVEL_INIT(0x02),
    SERVER_LEVEL_CHUNK(0x03),
    SERVER_LEVEL_FINALIZE(0x04),
    SERVER_SET_BLOCK(0x06),
    SERVER_PLAYER_SPAWN(0x07),
    SERVER_PLAYER_TELEPORT(0x08),
    SERVER_PLAYER_POSITIONORIENT(0x09),
    SERVER_PLAYER_POSITION(0x0a),
    SERVER_PLAYER_ORIENT(0x0b),
    SERVER_PLAYER_DESPAWN(0x0c),
    SERVER_MESSAGE(0x0d),
    SERVER_PLAYER_DISCONNECT(0x0e);

    public int id;

    OpCode(int id) {
        this.id = id;
    }
}
