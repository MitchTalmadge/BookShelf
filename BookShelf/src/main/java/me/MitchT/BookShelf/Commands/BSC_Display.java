package me.MitchT.BookShelf.Commands;

import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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
public class BSC_Display extends BSCommand
{
    
    public BSC_Display(BookShelfPlugin plugin)
    {
        super(plugin);
    }
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        Location loc = plugin.getTargetBlock(sender, 10).getLocation();
        if(loc.getBlock().getType() == Material.BOOKSHELF)
        {
            if(plugin.getShelfManager().isOwner(loc, sender))
            {
                try
                {
                    r = plugin.runQuery("SELECT * FROM display WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
                    if(!r.next())
                    {
                        plugin.runQuery("INSERT INTO display (x,y,z,bool) VALUES ("
                                + loc.getX()
                                + ","
                                + loc.getY()
                                + ","
                                + loc.getZ() + ", 1);");
                        sender.sendMessage("The bookshelf you are looking at is now a display.");
                        close(r);
                    }
                    else
                    {
                        if(r.getInt("bool") == 1)
                        {
                            close(r);
                            sender.sendMessage("The bookshelf you are looking at is no longer a display.");
                            plugin.runQuery("UPDATE display SET bool=0 WHERE x="
                                    + loc.getX()
                                    + " AND y="
                                    + loc.getY()
                                    + " AND z=" + loc.getZ() + ";");
                        }
                        else
                        {
                            close(r);
                            sender.sendMessage("The bookshelf you are looking at is now a display.");
                            plugin.runQuery("UPDATE display SET bool=1 WHERE x="
                                    + loc.getX()
                                    + " AND y="
                                    + loc.getY()
                                    + " AND z=" + loc.getZ() + ";");
                        }
                    }
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                sender.sendMessage("§cYou are not an owner of this shelf!");
            }
        }
        else
        {
            sender.sendMessage("Please look at a bookshelf when using this command");
        }
    }
    
    @Override
    public void onConsoleCommand(ConsoleCommandSender sender, Command command,
            String[] args)
    {
        sender.sendMessage("This command may only be used by players.");
    }
    
    @Override
    public void onCommandBlockCommand(CommandSender sender, Command command,
            String[] args)
    {
        return;
    }
    
}
