/*
 *  MD5.java
 * 
 *  Created on Oct 27, 2009, 11:34:56 PM
 * 
 *  Copyright (c) 2010 Tak. All rights reserved.
 * 
 *  This file is part of MCRPServer.
 * 
 *  MCRPServer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  MCRPServer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with MCRPServer.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcrpserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author kyran
 */
public class MD5 {

    private MessageDigest md = null;
    static private MD5 md5 = null;
    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Constructor is private so you must use the getInstance method
     */
    private MD5() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * This returns the singleton instance
     * @return The instance of md5 - Singleton pattern
     * @throws NoSuchAlgorithmException If there is no such algorithm (MD5)
     */
    public static MD5 getInstance() throws NoSuchAlgorithmException {
        if (md5 == null) {
            md5 = new MD5();
        }
        return (md5);
    }

    public String hashData(byte[] dataToHash) {
        return hexStringFromBytes((calculateHash(dataToHash)));
    }

    private byte[] calculateHash(byte[] dataToHash) {
        md.update(dataToHash, 0, dataToHash.length);
        return (md.digest());
    }

    public String hexStringFromBytes(byte[] b) {
        String hex = "";

        int msb;

        int lsb = 0;
        int i;

        // MSB maps to idx 0

        for (i = 0; i < b.length; i++) {
            msb = ((int) b[i] & 0x000000FF) / 16;
            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex = hex + hexChars[msb] + hexChars[lsb];
        }
        return (hex);
    }
}
