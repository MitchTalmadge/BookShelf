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

public class BSC_Donate extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        Location loc = BookShelf.getTargetBlock(sender, 10).getLocation();
        if(loc.getBlock().getType() == Material.BOOKSHELF)
        {
            if(BookShelf.isOwner(loc, sender))
            {
                if(BookShelf.useTowny)
                {
                    Resident res = TownyHandler.convertToResident(sender);
                    if(!TownyHandler.checkCanDoAction(loc.getBlock(), res,
                            TownyHandler.DONATE))
                    {
                        sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                    }
                }
                try
                {
                    if(BookShelf.isShelfDonate(loc))
                    {
                        r = BookShelf.runQuery("SELECT * FROM names WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                        if(!r.next())
                        {
                            close(r);
                            BookShelf
                                    .runQuery("INSERT INTO names (x,y,z,name) VALUES ("
                                            + loc.getX()
                                            + ","
                                            + loc.getY()
                                            + ","
                                            + loc.getZ()
                                            + ", '"
                                            + config.getString("default_shelf_name")
                                            + "');");
                        }
                        else
                        {
                            close(r);
                            BookShelf
                                    .runQuery("UPDATE names SET name='"
                                            + config.getString("default_shelf_name")
                                            + "' WHERE x=" + loc.getX()
                                            + " AND y=" + loc.getY()
                                            + " AND z=" + loc.getZ() + ";");
                        }
                        sender.sendMessage("The bookshelf you are looking at is no longer set up for donations.");
                        BookShelf.runQuery("UPDATE donate SET bool=0 WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                    }
                    else
                    {
                        r = BookShelf.runQuery("SELECT * FROM names WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                        if(!r.next())
                        {
                            close(r);
                            BookShelf
                                    .runQuery("INSERT INTO names (x,y,z,name) VALUES ("
                                            + loc.getX()
                                            + ","
                                            + loc.getY()
                                            + ","
                                            + loc.getZ()
                                            + ", 'Donation "
                                            + config.getString("default_shelf_name")
                                            + "');");
                        }
                        else
                        {
                            close(r);
                            BookShelf
                                    .runQuery("UPDATE names SET name='Donation "
                                            + config.getString("default_shelf_name")
                                            + "' WHERE x="
                                            + loc.getX()
                                            + " AND y="
                                            + loc.getY()
                                            + " AND z=" + loc.getZ() + ";");
                        }
                        sender.sendMessage("The bookshelf you are looking at is now set up for donations.");
                        BookShelf.runQuery("UPDATE donate SET bool=1 WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
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
