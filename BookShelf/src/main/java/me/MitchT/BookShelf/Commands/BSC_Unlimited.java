package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelfPlugin;
import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;

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
public class BSC_Unlimited extends BSCommand
{
    
    public BSC_Unlimited(BookShelfPlugin plugin)
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
                if(plugin.getExternalPluginManager().usingTowny())
                {
                    Resident res = plugin.getExternalPluginManager()
                            .getTownyHandler().convertToResident(sender);
                    if(!plugin
                            .getExternalPluginManager()
                            .getTownyHandler()
                            .checkCanDoAction(loc.getBlock(), res,
                                    TownyHandler.UNLIMITED))
                    {
                        sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                        return;
                    }
                }
                if(plugin.getShelfManager().isShelfUnlimited(loc))
                {
                    sender.sendMessage("The bookshelf you are looking at is now §6limited.");
                    plugin.runQuery("UPDATE copy SET bool=0 WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
                }
                else
                {
                    sender.sendMessage("The bookshelf you are looking at is now §6unlimited.");
                    plugin.runQuery("UPDATE copy SET bool=1 WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
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
        return;
    }
    
}
