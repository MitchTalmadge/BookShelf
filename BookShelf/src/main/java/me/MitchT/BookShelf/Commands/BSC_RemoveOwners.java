package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BSC_RemoveOwners extends BSCommand
{
    
    public BSC_RemoveOwners(BookShelfPlugin plugin)
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
                if(!config.getBoolean("use_built_in_ownership"))
                    return;
                if(args.length >= 1)
                {
                    plugin.getShelfManager().removeOwners(loc, args);
                    String ownerString = "";
                    for(String name : plugin.getShelfManager().getOwners(loc))
                    {
                        ownerString += name + ", ";
                    }
                    ownerString = ownerString.substring(0,
                            ownerString.length() - 2);
                    sender.sendMessage("Current Shelf Owners: §6" + ownerString);
                }
                else
                {
                    return;
                }
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
