package me.MitchT.BookShelf.Commands;

import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Resident;

public class BSC_Display extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        Location loc = plugin.getTargetBlock(sender, 10).getLocation();
        if(loc.getBlock().getType() == Material.BOOKSHELF)
        {
            if(plugin.isOwner(loc, sender))
            {
                try
                {
                    r = plugin.runQuery("SELECT * FROM display WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
                    if(!r.next())
                    {
                        plugin
                                .runQuery("INSERT INTO display (x,y,z,bool) VALUES ("
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
                            plugin
                                    .runQuery("UPDATE display SET bool=0 WHERE x="
                                            + loc.getX()
                                            + " AND y="
                                            + loc.getY()
                                            + " AND z="
                                            + loc.getZ() + ";");
                        }
                        else
                        {
                            close(r);
                            sender.sendMessage("The bookshelf you are looking at is now a display.");
                            plugin
                                    .runQuery("UPDATE display SET bool=1 WHERE x="
                                            + loc.getX()
                                            + " AND y="
                                            + loc.getY()
                                            + " AND z="
                                            + loc.getZ() + ";");
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
