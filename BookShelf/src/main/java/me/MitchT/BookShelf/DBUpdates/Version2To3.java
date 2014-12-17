package me.MitchT.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.OldIDEnum;

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
public class Version2To3 extends Version
{
    
    public Version2To3(Logger logger, ResultSet r, BookShelf plugin)
    {
        super(logger, r, plugin);
    }
    
    public void close(ResultSet r) throws SQLException
    {
        plugin.close(r);
    }
    
    @Override
    public void doUpdate()
    {
        try
        {
            logger.info("[BookShelf] Updating Database to Version 3.");
            Map<Integer, Integer> typeID = new HashMap<Integer, Integer>();
            logger.info("[BookShelf] Copying item types...");
            r = plugin.runQuery("SELECT * FROM items");
            while(r.next())
            {
                typeID.put(r.getInt("id"), r.getInt("type"));
            }
            close(r);
            
            logger.info("[BookShelf] Altering table 'items'...");
            
            plugin.runQuery("ALTER TABLE items ADD enumType TEXT;");
            
            logger.info("[BookShelf] Updating table 'items'...");
            plugin.getSQLManager().setAutoCommit(false);
            for(int i = 0; i < typeID.size(); i++)
            {
                for(Map.Entry<Integer, Integer> entry : typeID.entrySet())
                {
                    plugin.runQuery("UPDATE items SET enumType='"
                            + OldIDEnum.getMaterialById(entry.getValue())
                                    .name() + "' WHERE id=" + entry.getKey());
                }
            }
            plugin.getSQLManager().commit();
            plugin.getSQLManager().setAutoCommit(true);
            
            plugin.runQuery("UPDATE version SET version=3");
            logger.info("[BookShelf] Update to Version 3 Complete.");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
}
