package me.MitchT.BookShelf.Commands;

import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelfPlugin;
import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;

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
public class BSC_Name extends BSCommand
{
    
    public BSC_Name(BookShelfPlugin plugin)
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
                String name;
                String queryName;
                if(!(args.length >= 1))
                {
                    name = config.getString("default_shelf_name");
                    queryName = name.replaceAll("'", "''");
                }
                else
                {
                    int price = 0;
                    try
                    {
                        r = plugin.runQuery("SELECT * FROM shop WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                        if(r.next())
                            price = r.getInt("price");
                        close(r);
                    }
                    catch(SQLException e)
                    {
                        e.printStackTrace();
                    }
                    String name1 = "";
                    if(plugin.getExternalPluginManager().usingVaultEconomy())
                    {
                        for(int i = 0; i < args.length; i++)
                        {
                            name1 += args[i].replace("%$", price
                                    + " "
                                    + plugin.getExternalPluginManager()
                                            .getVaultEconomy()
                                            .currencyNamePlural())
                                    + " ";
                        }
                    }
                    else
                    {
                        for(int i = 0; i < args.length; i++)
                        {
                            name1 += args[i] + " ";
                        }
                    }
                    name1.trim();
                    if(name1.length() > 32)
                    {
                        name = name1.substring(0, 31);
                    }
                    else
                    {
                        name = name1;
                    }
                    name = ChatColor.translateAlternateColorCodes('&', name);
                    queryName = name.replaceAll("'", "''");
                }
                if(plugin.getExternalPluginManager().usingTowny())
                {
                    Resident res = plugin.getExternalPluginManager()
                            .getTownyHandler().convertToResident(sender);
                    if(!plugin
                            .getExternalPluginManager()
                            .getTownyHandler()
                            .checkCanDoAction(loc.getBlock(), res,
                                    TownyHandler.NAME))
                    {
                        sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                    }
                }
                try
                {
                    r = plugin.runQuery("SELECT * FROM names WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
                    if(!r.next())
                    {
                        close(r);
                        plugin.runQuery("INSERT INTO names (x,y,z,name) VALUES ("
                                + loc.getX()
                                + ","
                                + loc.getY()
                                + ","
                                + loc.getZ() + ", '" + queryName + "');");
                        sender.sendMessage("The name of the bookshelf you are looking at has been changed to §6"
                                + name);
                    }
                    else
                    {
                        close(r);
                        plugin.runQuery("UPDATE names SET name='" + queryName
                                + "' WHERE x=" + loc.getX() + " AND y="
                                + loc.getY() + " AND z=" + loc.getZ() + ";");
                        sender.sendMessage("The name of the bookshelf you are looking at has been changed to §6"
                                + name);
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
            sender.sendMessage("§cPlease look at a bookshelf when using this command");
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
        sender.sendMessage("This command may only be used by players.");
    }
    
}
