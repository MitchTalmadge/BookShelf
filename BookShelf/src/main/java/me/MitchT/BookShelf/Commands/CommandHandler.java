package me.MitchT.BookShelf.Commands;

import java.lang.reflect.InvocationTargetException;

import me.MitchT.BookShelf.BookShelfPlugin;

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
public class CommandHandler
{
    private BookShelfPlugin plugin;

    public CommandHandler(BookShelfPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public void onCommand(CommandSender sender, Command command, String label,
            String[] args)
    {
        BSCommandEnum enumVal = BSCommandEnum.getEnumByCommandName(command
                .getName());
        if(enumVal != null)
        {
            if(!sender
                    .hasPermission("bookshelf." + enumVal.getPermissionName()))
            {
                sender.sendMessage("§cYou don't have permission to use this command here!");
            }
            
            try
            {
                BSCommand cmd;
                try
                {
                    cmd = enumVal.getCommandClass().getDeclaredConstructor(BookShelfPlugin.class).newInstance(plugin);
                    if(sender instanceof Player)
                        cmd.onPlayerCommand((Player) sender, command, args);
                    else if(sender instanceof ConsoleCommandSender)
                        cmd.onConsoleCommand((ConsoleCommandSender) sender,
                                command, args);
                    else if(sender.getClass().getSimpleName()
                            .equals("CraftBlockCommandSender"))
                        cmd.onCommandBlockCommand(sender, command, args);
                }
                catch(IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch(SecurityException e)
                {
                    e.printStackTrace();
                }
                catch(InvocationTargetException e)
                {
                    e.printStackTrace();
                }
                catch(NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
            catch(InstantiationException e)
            {
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }
}
