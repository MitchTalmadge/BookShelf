/**
 * @author Mitch Talmadge
 *         Date Created:
 *         Jul 24, 2013
 */

package me.MitchT.BookShelf.Towny;

import java.util.ArrayList;
import java.util.Arrays;

import me.MitchT.BookShelf.BookShelf;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;

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
public class TownyCommands
{
    
    static ArrayList<String> level0 = new ArrayList<String>(Arrays.asList(
            "town", "resident", "plot"));
    static ArrayList<String> level1 = new ArrayList<String>(Arrays.asList(
            "unlimited", "toggle", "shop", "name", "open_shelf", "open_shop"));
    static ArrayList<String> level2a = new ArrayList<String>(Arrays.asList(
            "resident", "ally", "outsider"));
    static ArrayList<String> level2b = new ArrayList<String>(Arrays.asList(
            "friend", "ally", "outsider"));
    static ArrayList<String> level3 = new ArrayList<String>(Arrays.asList("on",
            "off"));
    
    public static boolean onCommand(CommandSender sender, String label,
            String[] args, BookShelf bs)
    {
        if(args.length == 0)
        {
            showHelp(sender, 0, null);
            return true;
        }
        else if(args.length == 1)
        {
            showHelp(sender, 1, args[0]);
            return true;
        }
        else if(args.length >= 3)
        {
            if(level0.contains(args[0]))
            {
                Resident res = TownyHandler.convertToResident((Player) sender);
                if(res.hasTown())
                {
                    try
                    {
                        if(!TownyHandler.hasPrivileges(res, res.getTown())
                                && (args[0].equals("town") || args[0]
                                        .equals("resident")))
                        {
                            sender.sendMessage("You do not have the privileges for this town!");
                            return true;
                        }
                        if(args[1].equals("set"))
                        {
                            String plotString = "";
                            ArrayList<String> chosen = args[0].equals("town") ? level2a
                                    : level2b;
                            if(args[0].equals("plot"))
                            {
                                TownBlock currentPlot = TownyHandler
                                        .getPlotFromResidentCoords(res);
                                if(!TownyHandler.checkPlotInResidentsTown(
                                        currentPlot, res))
                                {
                                    sender.sendMessage("This plot does not belong to §6"
                                            + res.getTown().getName() + "§f.");
                                    return true;
                                }
                                if(!TownyHandler.checkPlotIsOwned(currentPlot))
                                {
                                    sender.sendMessage("This is not an owned plot.");
                                    return true;
                                }
                                if(!TownyHandler.checkPlotOwnedByResident(
                                        currentPlot, res)
                                        && !TownyHandler.hasPrivileges(res,
                                                res.getTown()))
                                {
                                    sender.sendMessage("You do not have the privileges for this plot!");
                                    return true;
                                }
                                
                                plotString = TownyHandler
                                        .getPlotStringFromResidentCoords(res)
                                        + ".";
                            }
                            int id = 0;
                            if(args[0].equals("town"))
                                id = 0;
                            else if(args[0].equals("resident"))
                                id = 1;
                            else if(args[0].equals("plot"))
                                id = 2;
                            if(chosen.contains(args[2]))
                            {
                                if(args.length > 4 && level1.contains(args[3])
                                        && level3.contains(args[4]))
                                { // town set ally unlimited on
                                    if(args[4].equals("on"))
                                        TownyHandler.setTownPermission(
                                                res.getTown(), args[0] + "."
                                                        + plotString + args[3]
                                                        + "." + args[2], true);
                                    else if(args[4].equals("off"))
                                        TownyHandler.setTownPermission(
                                                res.getTown(), args[0] + "."
                                                        + plotString + args[3]
                                                        + "." + args[2], false);
                                    showPermissions(res, id, sender);
                                }
                                else if(chosen.contains(args[2])
                                        && level3.contains(args[3]))
                                { // town set ally on
                                    for(String s : level1)
                                    {
                                        TownyHandler.setTownPermission(res
                                                .getTown(), args[0] + "."
                                                + plotString + s + "."
                                                + args[2],
                                                args[3].equals("on") ? true
                                                        : false);
                                    }
                                    showPermissions(res, id, sender);
                                }
                            }
                            else if(level3.contains(args[2]))
                            { //town set on
                                for(String s : chosen)
                                {
                                    for(String s2 : level1)
                                    {
                                        TownyHandler.setTownPermission(res
                                                .getTown(), args[0] + "."
                                                + plotString + s2 + "." + s,
                                                args[2].equals("on") ? true
                                                        : false);
                                    }
                                }
                                showPermissions(res, id, sender);
                            }
                            else if(level1.contains(args[2])
                                    && level3.contains(args[3]))
                            { //town set unlimited on
                                for(String s : chosen)
                                {
                                    TownyHandler
                                            .setTownPermission(
                                                    res.getTown(),
                                                    args[0] + "." + plotString
                                                            + args[2] + "." + s,
                                                    args[3].equals("on") ? true
                                                            : false);
                                }
                                showPermissions(res, id, sender);
                            }
                            else if(args[2].equals("defaults"))
                            {
                                for(String s : level1)
                                {
                                    for(String s2 : chosen)
                                    {
                                        TownyHandler.setDefaultConfigValue(
                                                res.getTown(), args[0] + "."
                                                        + plotString + s + "."
                                                        + s2);
                                    }
                                }
                                showPermissions(res, id, sender);
                            }
                            else
                            {
                                showHelp(sender, 1, args[0]);
                            }
                        }
                        else
                        {
                            showHelp(sender, 1, args[0]);
                        }
                    }
                    catch(NotRegisteredException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
    
    private static void showHelp(CommandSender sender, int helpType, String arg)
    {
        switch(helpType)
        {
            case 0:
                sender.sendMessage(new String[] {
                        "§6Help for §e/bstowny",
                        "Use these commands to change BookShelf permissions for Towny",
                        "§3/bstowny §btown", "§3/bstowny §bresident",
                        "§3/bstowny §bplot" });
                break;
            case 1:
                if(arg.equals("town"))
                {
                    String level = "§3[";
                    for(String s : level2a)
                    {
                        level += (s + "/");
                    }
                    level = level.substring(0, level.length() - 1);
                    level += "]";
                    
                    String type = "§3[";
                    for(String s : level1)
                    {
                        type += (s + "/");
                    }
                    type = type.substring(0, type.length() - 1);
                    type += "]";
                    
                    sender.sendMessage(new String[] {
                            "§6Help for §e/bstowny town",
                            "§cLevel: " + level,
                            "§cType: " + type,
                            "§3set §b[on/off] §7: Toggle all permissions on/off",
                            "§3set §b[level/type] [on/off]",
                            "§3set §b[level] [type] [on/off]",
                            "§3set §bdefaults" });
                    showPermissions(
                            TownyHandler.convertToResident((Player) sender), 0,
                            sender);
                }
                else if(arg.equals("resident"))
                {
                    String level = "§3[";
                    for(String s : level2b)
                    {
                        level += (s + "/");
                    }
                    level = level.substring(0, level.length() - 1);
                    level += "]";
                    
                    String type = "§3[";
                    for(String s : level1)
                    {
                        type += (s + "/");
                    }
                    type = type.substring(0, type.length() - 1);
                    type += "]";
                    
                    sender.sendMessage(new String[] {
                            "§6Help for §e/bstowny resident",
                            "§cLevel: " + level,
                            "§cType: " + type,
                            "§3set §b[on/off] §7: Toggle all permissions on/off",
                            "§3set §b[level/type] [on/off]",
                            "§3set §b[level] [type] [on/off]",
                            "§3set §bdefaults" });
                    showPermissions(
                            TownyHandler.convertToResident((Player) sender), 1,
                            sender);
                }
                else if(arg.equals("plot"))
                {
                    String level = "§3[";
                    for(String s : level2b)
                    {
                        level += (s + "/");
                    }
                    level = level.substring(0, level.length() - 1);
                    level += "]";
                    
                    String type = "§3[";
                    for(String s : level1)
                    {
                        type += (s + "/");
                    }
                    type = type.substring(0, type.length() - 1);
                    type += "]";
                    
                    sender.sendMessage(new String[] {
                            "§6Help for §e/bstowny plot",
                            "§cLevel: " + level,
                            "§cType: " + type,
                            "§3set §b[on/off] §7: Toggle all permissions on/off",
                            "§3set §b[level/type] [on/off]",
                            "§3set §b[level] [type] [on/off]",
                            "§3set §bdefaults" });
                    showPermissions(
                            TownyHandler.convertToResident((Player) sender), 2,
                            sender);
                }
                else
                {
                    showHelp(sender, 0, null);
                }
                break;
        }
    }
    
    private static void showPermissions(Resident res, int type,
            CommandSender sender)
    {
        TownBlock block = TownyHandler.getPlotFromResidentCoords(res);
        if(type == 2 && block == null)
        {
            sender.sendMessage("You are not standing on a plot!");
            return;
        }
        if(block != null)
        {
            if(!block.hasTown())
            {
                sender.sendMessage("You are not standing on a plot!");
                return;
            }
            if(!TownyHandler.checkPlotInAnyTown(block))
            {
                sender.sendMessage("You are not standing on a plot!");
                return;
            }
            if(type == 2)
            {
                if(TownyHandler.checkPlotIsOwned(block))
                {
                    String plotString = TownyHandler
                            .getPlotStringFromCoords(block.getCoord());
                    ArrayList<String> permStrings = new ArrayList<String>();
                    permStrings.add("§2Plot Permissions: ");
                    String currPermString = "";
                    for(int i = 0; i < level1.size(); i++)
                    {
                        currPermString += "§a" + level1.get(i) + " = §7";
                        for(String s2 : level2b)
                        {
                            String value = "-";
                            try
                            {
                                if((Boolean) TownyHandler.getTownPermission(
                                        block.getTown(), "plot." + plotString
                                                + "." + level1.get(i) + "."
                                                + s2))
                                {
                                    value = s2.substring(0, 1);
                                }
                            }
                            catch(NotRegisteredException e)
                            {
                                value = "?";
                            }
                            currPermString += value;
                        }
                        currPermString += " ";
                        if((i + 1) % 4 == 0)
                        {
                            permStrings.add(new String(currPermString));
                            currPermString = "";
                        }
                    }
                    permStrings.add(currPermString);
                    sender.sendMessage(permStrings
                            .toArray(new String[permStrings.size()]));
                }
                else
                {
                    sender.sendMessage("You are not standing on a plot!");
                }
            }
        }
        if(type == 0)
        {
            ArrayList<String> permStrings = new ArrayList<String>();
            String currPermString = "";
            permStrings.add("§2Town Permissions: ");
            for(int i = 0; i < level1.size(); i++)
            {
                currPermString += "§a" + level1.get(i) + " = §7";
                for(String s2 : level2a)
                {
                    String value = "-";
                    try
                    {
                        if((Boolean) TownyHandler.getTownPermission(
                                res.getTown(), "town." + level1.get(i) + "."
                                        + s2))
                        {
                            value = s2.substring(0, 1);
                        }
                    }
                    catch(NotRegisteredException e)
                    {
                        value = "?";
                    }
                    currPermString += value;
                }
                currPermString += " ";
                if((i + 1) % 4 == 0)
                {
                    permStrings.add(new String(currPermString));
                    currPermString = "";
                }
            }
            permStrings.add(currPermString);
            sender.sendMessage(permStrings.toArray(new String[permStrings
                    .size()]));
        }
        else if(type == 1)
        {
            ArrayList<String> permStrings = new ArrayList<String>();
            String currPermString = "";
            permStrings.add("§2Resident Permissions: ");
            for(int i = 0; i < level1.size(); i++)
            {
                currPermString += "§a" + level1.get(i) + " = §7";
                for(String s2 : level2b)
                {
                    String value = "-";
                    try
                    {
                        if((Boolean) TownyHandler.getTownPermission(
                                res.getTown(), "resident." + level1.get(i)
                                        + "." + s2))
                        {
                            value = s2.substring(0, 1);
                        }
                    }
                    catch(NotRegisteredException e)
                    {
                        value = "?";
                    }
                    currPermString += value;
                }
                currPermString += " ";
                if((i + 1) % 4 == 0)
                {
                    permStrings.add(new String(currPermString));
                    currPermString = "";
                }
            }
            permStrings.add(currPermString);
            sender.sendMessage(permStrings.toArray(new String[permStrings
                    .size()]));
        }
    }
}
