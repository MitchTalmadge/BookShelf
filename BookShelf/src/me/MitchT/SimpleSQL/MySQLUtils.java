package me.MitchT.SimpleSQL;

/**
 * 
 * BookShelf - A Bukkit & Spigot mod allowing the placement of items
 * into BookShelves. <br>
 * Copyright (C) 2012-2014 Mitch Talmadge (mitcht@aptitekk.com)<br>
 * <br>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.<br>
 * <br>
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * @author Mitch Talmadge (mitcht@aptitekk.com)
 */
public class MySQLUtils
{
    private String hostname = "localhost";
    private int port = 0;
    private String username = "minecraft";
    private String password = "";
    private String database = "minecraft";
    private Database db;
    
    public MySQLUtils(Database db)
    {
        this.db = db;
    }
    
    public String getHostname()
    {
        return hostname;
    }
    
    public void setHostname(String hostname)
    {
        if(hostname == null || hostname.length() == 0)
            db.printError("Hostname cannot be null or empty.");
        this.hostname = hostname;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        if(port < 0 || 65535 < port)
            db.printError("Port number cannot be below 0 or greater than 65535.");
        this.port = port;
    }
    
    public String getUsername()
    {
        return this.username;
    }
    
    public void setUsername(String username)
    {
        if(username == null || username.length() == 0)
            db.printError("Username cannot be null or empty.");
        this.username = username;
    }
    
    public String getPassword()
    {
        return this.password;
    }
    
    public void setPassword(String password)
    {
        if(password == null || password.length() == 0)
            db.printError("Password cannot be null or empty.");
        this.password = password;
    }
    
    public String getDatabase()
    {
        return this.database;
    }
    
    public void setDatabase(String database)
    {
        if(database == null || database.length() == 0)
            db.printError("Database cannot be null or empty.");
        this.database = database;
    }
}
