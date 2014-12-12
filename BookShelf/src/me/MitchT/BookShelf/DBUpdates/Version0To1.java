package me.MitchT.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.MitchT.BookShelf.BookShelf;

import org.bukkit.util.Vector;

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
public class Version0To1 extends Version
{
    
    public Version0To1(Logger logger, ResultSet r)
    {
        super(logger, r);
    }
    
    public void close(ResultSet r) throws SQLException
    {
        BookShelf.close(r);
    }
    
    @Override
    public void doUpdate()
    {
        try
        {
            logger.info("[BookShelf] Updating Database to Version 1.");
            BookShelf.getdb().query("ALTER TABLE items ADD lore TEXT;");
            BookShelf.getdb().query("ALTER TABLE items ADD damage INT;");
            BookShelf.getdb().query("ALTER TABLE items ADD pages TEXT;");
            
            if(BookShelf.usingMySQL())
            {
                BookShelf.getdb().query(
                        "ALTER TABLE items MODIFY title VARCHAR(128);");
                BookShelf.getdb().query(
                        "ALTER TABLE items MODIFY author VARCHAR(128);");
            }
            
            BookShelf.getdb().query("UPDATE version SET version=1");
            
            /* CONVERT PAGES SYSTEM */
            logger.info("[BookShelf] Converting pages.");
            ArrayList<Integer> idlist = new ArrayList<Integer>();
            r = BookShelf.getdb().query(
                    "SELECT * FROM items WHERE type=386 OR type=387;");
            while(r.next())
            {
                idlist.add(r.getInt("id"));
            }
            close(r);
            if(idlist.size() > 0)
            {
                for(int id : idlist)
                {
                    ArrayList<String> pagelist = new ArrayList<String>();
                    r = BookShelf.getdb().query(
                            "SELECT * FROM pages WHERE id=" + id + ";");
                    while(r.next())
                    {
                        pagelist.add(r.getString("text"));
                    }
                    close(r);
                    String pageString = "";
                    for(String page : pagelist)
                    {
                        pageString += page + "¬";
                    }
                    if(pageString.endsWith("¬"))
                        pageString = pageString.substring(0,
                                pageString.length() - 1);
                    pageString = pageString.replaceAll("'", "''");
                    BookShelf.getdb().query(
                            "UPDATE items SET pages='" + pageString
                                    + "' WHERE id=" + id + ";");
                }
            }
            
            /* CONVERT ENCHANTMENT SYSTEM */
            logger.info("[BookShelf] Converting enchanted books.");
            BookShelf.getdb().query("ALTER TABLE enchant ADD x INT;");
            BookShelf.getdb().query("ALTER TABLE enchant ADD y INT;");
            BookShelf.getdb().query("ALTER TABLE enchant ADD z INT;");
            BookShelf.getdb().query("ALTER TABLE enchant ADD loc INT;");
            
            idlist = new ArrayList<Integer>();
            ArrayList<Integer> loclist = new ArrayList<Integer>();
            ArrayList<Vector> locationlist = new ArrayList<Vector>();
            
            r = BookShelf.getdb().query("SELECT * FROM items WHERE type=403;");
            while(r.next())
            {
                idlist.add(r.getInt("id"));
                loclist.add(r.getInt("loc"));
                locationlist.add(new Vector(r.getInt("x"), r.getInt("y"), r
                        .getInt("z")));
            }
            close(r);
            ArrayList<Integer> loctokeep = new ArrayList<Integer>();
            ArrayList<Integer> leveltokeep = new ArrayList<Integer>();
            ArrayList<String> typetokeep = new ArrayList<String>();
            ArrayList<Vector> locationtokeep = new ArrayList<Vector>();
            if(idlist.size() > 0)
            {
                for(int i = 0; i < idlist.size(); i++)
                {
                    BookShelf.getdb().query(
                            "UPDATE enchant SET x="
                                    + locationlist.get(i).getBlockX() + ", y="
                                    + locationlist.get(i).getBlockY() + ", z="
                                    + locationlist.get(i).getBlockZ()
                                    + ", loc=" + loclist.get(i) + " WHERE id="
                                    + idlist.get(i) + ";");
                    r = BookShelf.getdb().query(
                            "SELECT * FROM enchant WHERE x="
                                    + locationlist.get(i).getBlockX()
                                    + " AND y="
                                    + locationlist.get(i).getBlockY()
                                    + " AND z="
                                    + locationlist.get(i).getBlockZ()
                                    + " AND loc=" + loclist.get(i) + ";");
                    int currloctokeep = 0;
                    int currleveltokeep = 0;
                    String currtypetokeep = null;
                    Vector currlocationtokeep = null;
                    
                    while(r.next())
                    {
                        currloctokeep = r.getInt("loc");
                        currleveltokeep = r.getInt("level");
                        currtypetokeep = r.getString("type");
                        currlocationtokeep = new Vector(r.getInt("x"),
                                r.getInt("y"), r.getInt("z"));
                    }
                    close(r);
                    if(currlocationtokeep != null)
                    {
                        loctokeep.add(currloctokeep);
                        leveltokeep.add(currleveltokeep);
                        typetokeep.add(currtypetokeep);
                        locationtokeep.add(currlocationtokeep);
                    }
                }
            }
            
            BookShelf.getdb().query("DELETE FROM enchant;");
            
            if(idlist.size() > 0)
            {
                for(int i = 0; i < loctokeep.size(); i++)
                {
                    BookShelf.getdb().query(
                            "INSERT INTO enchant (x,y,z,loc,type,level) VALUES("
                                    + locationtokeep.get(i).getBlockX() + ","
                                    + locationtokeep.get(i).getBlockY() + ","
                                    + locationtokeep.get(i).getBlockZ() + ","
                                    + loctokeep.get(i) + ",'"
                                    + typetokeep.get(i) + "',"
                                    + leveltokeep.get(i) + ");");
                }
            }
            
            /* CONVERT MAPS SYSTEM */
            logger.info("[BookShelf] Converting maps.");
            BookShelf.getdb().query("ALTER TABLE maps ADD x INT;");
            BookShelf.getdb().query("ALTER TABLE maps ADD y INT;");
            BookShelf.getdb().query("ALTER TABLE maps ADD z INT;");
            BookShelf.getdb().query("ALTER TABLE maps ADD loc INT;");
            
            idlist = new ArrayList<Integer>();
            loclist = new ArrayList<Integer>();
            locationlist = new ArrayList<Vector>();
            
            r = BookShelf.getdb().query("SELECT * FROM items WHERE type=358;");
            while(r.next())
            {
                idlist.add(r.getInt("id"));
                loclist.add(r.getInt("loc"));
                locationlist.add(new Vector(r.getInt("x"), r.getInt("y"), r
                        .getInt("z")));
            }
            close(r);
            loctokeep = new ArrayList<Integer>();
            ArrayList<Short> durabilitytokeep = new ArrayList<Short>();
            locationtokeep = new ArrayList<Vector>();
            if(idlist.size() > 0)
            {
                for(int i = 0; i < idlist.size(); i++)
                {
                    BookShelf.getdb().query(
                            "UPDATE maps SET x="
                                    + locationlist.get(i).getBlockX() + ", y="
                                    + locationlist.get(i).getBlockY() + ", z="
                                    + locationlist.get(i).getBlockZ()
                                    + ", loc=" + loclist.get(i) + " WHERE id="
                                    + idlist.get(i) + ";");
                    r = BookShelf.getdb().query(
                            "SELECT * FROM maps WHERE x="
                                    + locationlist.get(i).getBlockX()
                                    + " AND y="
                                    + locationlist.get(i).getBlockY()
                                    + " AND z="
                                    + locationlist.get(i).getBlockZ()
                                    + " AND loc=" + loclist.get(i) + ";");
                    int currloctokeep = 0;
                    short currdurabilitytokeep = 0;
                    Vector currlocationtokeep = null;
                    while(r.next())
                    {
                        currloctokeep = r.getInt("loc");
                        currdurabilitytokeep = r.getShort("durability");
                        currlocationtokeep = new Vector(r.getInt("x"),
                                r.getInt("y"), r.getInt("z"));
                    }
                    close(r);
                    if(currlocationtokeep != null)
                    {
                        loctokeep.add(currloctokeep);
                        durabilitytokeep.add(currdurabilitytokeep);
                        locationtokeep.add(currlocationtokeep);
                    }
                }
            }
            BookShelf.getdb().query("DELETE FROM maps;");
            
            if(idlist.size() > 0)
            {
                for(int i = 0; i < loctokeep.size(); i++)
                {
                    BookShelf.getdb().query(
                            "INSERT INTO maps (x,y,z,loc,durability) VALUES("
                                    + locationtokeep.get(i).getBlockX() + ","
                                    + locationtokeep.get(i).getBlockY() + ","
                                    + locationtokeep.get(i).getBlockZ() + ","
                                    + loctokeep.get(i) + ","
                                    + durabilitytokeep.get(i) + ");");
                }
            }
            
            logger.info("[BookShelf] Update to Version 1 Complete.");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
}
