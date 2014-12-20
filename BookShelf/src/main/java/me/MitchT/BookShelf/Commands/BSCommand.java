package me.MitchT.BookShelf.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
public abstract class BSCommand
{
    
    public BookShelfPlugin plugin;
    protected ResultSet r;
    protected FileConfiguration config;
    
    public BSCommand(BookShelfPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    protected void close(ResultSet r)
    {
        try
        {
            plugin.close(r);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public abstract void onPlayerCommand(Player sender, Command command,
            String[] args);
    
    public abstract void onConsoleCommand(ConsoleCommandSender sender,
            Command command, String[] args);
    
    public abstract void onCommandBlockCommand(CommandSender sender,
            Command command, String[] args);
    
}
