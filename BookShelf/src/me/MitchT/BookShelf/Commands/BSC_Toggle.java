package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;

public class BSC_Toggle extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        if(args.length == 0)
        {
            Location loc = plugin.getTargetBlock(sender, 10).getLocation();
            if(loc.getBlock().getType() == Material.BOOKSHELF)
            {
                if(plugin.isOwner(loc, sender))
                {
                    if(BookShelf.getExternalPluginManager().usingTowny())
                    {
                        Resident res = TownyHandler.convertToResident(sender);
                        if(!TownyHandler.checkCanDoAction(loc.getBlock(), res,
                                TownyHandler.TOGGLE))
                        {
                            sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                            return;
                        }
                    }
                    int result = plugin.toggleBookShelf(loc);
                    if(result == -1)
                        sender.sendMessage("§cAn error occured while processing this command. Check server logs.");
                    if(result == 0)
                        sender.sendMessage("The bookshelf you are looking at is now §cdisabled.");
                    else
                        sender.sendMessage("The bookshelf you are looking at is now §aenabled.");
                }
                else
                {
                    sender.sendMessage("§cYou are not an owner of this shelf!");
                }
            }
            else
            {
                sender.sendMessage("§cPlease look at a bookshelf when using this command.");
            }
        }
        else
        {
            if(!sender.isOp())
            {
                sender.sendMessage("§cYou must be an OP to toggle shelves by name.");
                return;
            }
            String name = "";
            for(int i = 0; i < args.length; i++)
            {
                name += args[i] + " ";
            }
            
            plugin.toggleBookShelvesByName(name);
            sender.sendMessage("All bookshelves with the name §6" + name
                    + "§fhave been toggled.");
        }
    }
    
    @Override
    public void onConsoleCommand(ConsoleCommandSender sender, Command command,
            String[] args)
    {
        if(!(args.length >= 1))
        {
            sender.sendMessage("§cMust include a shelf name!");
        }
        else
        {
            String name = "";
            for(int i = 0; i < args.length; i++)
            {
                name += args[i] + " ";
            }
            
            plugin.toggleBookShelvesByName(name);
            sender.sendMessage("All bookshelves with the name §6" + name
                    + "§fhave been toggled.");
        }
    }
    
    @Override
    public void onCommandBlockCommand(CommandSender sender, Command command,
            String[] args)
    {
        if(!(args.length >= 1))
        {
            sender.sendMessage("§cMust include a shelf name!");
        }
        else
        {
            String name = "";
            for(int i = 0; i < args.length; i++)
            {
                name += args[i] + " ";
            }
            
            plugin.toggleBookShelvesByName(name);
            sender.sendMessage("All bookshelves with the name §6" + name
                    + "§fhave been toggled.");
        }
    }
    
}
