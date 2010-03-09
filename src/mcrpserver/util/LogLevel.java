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

package mcrpserver.util;

/**
 * Log levels
 * @author Furyhunter
 */
public enum LogLevel {

    MINIMAL(0,"M"),
    ERROR(1,"E"),
    VERBOSE(2,"V"),
    DEBUG(3,"D");
    public final int level;
    public final String str;

    LogLevel(int level, String str) {
        this.level = level;
        this.str = str;
    }
}
