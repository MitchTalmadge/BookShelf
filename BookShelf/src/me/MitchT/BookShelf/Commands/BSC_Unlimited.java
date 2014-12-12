package me.MitchT.BookShelf.Commands;

import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.Towny.TownyHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;

public class BSC_Unlimited extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        Location loc = plugin.getTargetBlock(sender, 10).getLocation();
        if(loc.getBlock().getType() == Material.BOOKSHELF)
        {
            if(plugin.isOwner(loc, sender))
            {
                if(BookShelf.useTowny)
                {
                    Resident res = TownyHandler.convertToResident(sender);
                    if(!TownyHandler.checkCanDoAction(loc.getBlock(), res,
                            TownyHandler.UNLIMITED))
                    {
                        sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                        return;
                    }
                }
                if(plugin.isShelfUnlimited(loc))
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
