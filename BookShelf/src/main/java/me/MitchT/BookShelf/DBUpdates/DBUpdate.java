package me.MitchT.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import me.MitchT.BookShelf.BookShelf;

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
public class DBUpdate
{
    
    private Logger logger;
    private ResultSet r;
    private Version version;
    private BookShelf plugin;
    
    public DBUpdate(Logger logger, ResultSet r, BookShelf plugin)
    {
        this.logger = logger;
        this.r = r;
        this.plugin = plugin;
    }
    
    public void close(ResultSet r) throws SQLException
    {
        plugin.close(r);
    }
    
    public void doUpdate(int currentVersion)
    {
        switch(currentVersion)
        {
            case 0:
                version = new Version0To1(logger, r, plugin);
                version.doUpdate();
                break;
            case 1:
                version = new Version1To2(logger, r, plugin);
                version.doUpdate();
                break;
            case 2:
                version = new Version2To3(logger, r, plugin);
                version.doUpdate();
                break;
        }
    }
    
}
