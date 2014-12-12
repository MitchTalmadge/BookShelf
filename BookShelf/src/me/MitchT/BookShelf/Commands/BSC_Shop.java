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

public class BSC_Shop extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        Location loc = plugin.getTargetBlock(sender, 10).getLocation();
        if(loc.getBlock().getType() == Material.BOOKSHELF)
        {
            if(plugin.isOwner(loc, sender))
            {
                Integer price;
                if(!(args.length >= 1))
                {
                    price = config.getInt("economy.default_price");
                }
                else
                {
                    if(args[0].length() > 9)
                        price = config.getInt("economy.default_price");
                    else
                        price = Integer.parseInt(args[0]);
                }
                if(BookShelf.economy == null)
                {
                    sender.sendMessage("§cVault is not installed! Aborting...");
                }
                if(BookShelf.useTowny)
                {
                    Resident res = TownyHandler.convertToResident(sender);
                    if(!TownyHandler.checkCanDoAction(loc.getBlock(), res,
                            TownyHandler.SHOP))
                    {
                        sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
                    }
                }
                try
                {
                    if(plugin.isShelfShop(loc) & !(args.length >= 1))
                    {
                        r = plugin.runQuery("SELECT * FROM names WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                        if(!r.next())
                        {
                            close(r);
                            plugin
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
                            plugin
                                    .runQuery("UPDATE names SET name='"
                                            + plugin.getConfig().getString(
                                                    "default_shelf_name")
                                            + "' WHERE x=" + loc.getX()
                                            + " AND y=" + loc.getY()
                                            + " AND z=" + loc.getZ() + ";");
                        }
                        sender.sendMessage("The bookshelf you are looking at is no longer a shop.");
                        plugin.runQuery("UPDATE shop SET bool=0, price="
                                + price + " WHERE x=" + loc.getX() + " AND y="
                                + loc.getY() + " AND z=" + loc.getZ() + ";");
                    }
                    else
                    {
                        r = plugin.runQuery("SELECT * FROM names WHERE x="
                                + loc.getX() + " AND y=" + loc.getY()
                                + " AND z=" + loc.getZ() + ";");
                        if(!r.next())
                        {
                            close(r);
                            plugin
                                    .runQuery("INSERT INTO names (x,y,z,name) VALUES ("
                                            + loc.getX()
                                            + ","
                                            + loc.getY()
                                            + ","
                                            + loc.getZ()
                                            + ", '"
                                            + config.getString(
                                                    "default_shop_name")
                                                    .replace(
                                                            "%$",
                                                            price
                                                                    + " "
                                                                    + BookShelf.economy
                                                                            .currencyNamePlural())
                                            + "');");
                        }
                        else
                        {
                            close(r);
                            plugin
                                    .runQuery("UPDATE names SET name='"
                                            + config.getString(
                                                    "default_shop_name")
                                                    .replace(
                                                            "%$",
                                                            price
                                                                    + " "
                                                                    + BookShelf.economy
                                                                            .currencyNamePlural())
                                            + "' WHERE x=" + loc.getX()
                                            + " AND y=" + loc.getY()
                                            + " AND z=" + loc.getZ() + ";");
                        }
                        sender.sendMessage("The bookshelf you are looking at is now a shop selling at §6"
                                + price
                                + " "
                                + BookShelf.economy.currencyNamePlural()
                                + " §feach.");
                        plugin.runQuery("UPDATE shop SET bool=1, price="
                                + price + " WHERE x=" + loc.getX() + " AND y="
                                + loc.getY() + " AND z=" + loc.getZ() + ";");
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
