package me.MitchT.BookShelf.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class BSCommand
{
    
    public BookShelf plugin = BookShelf.instance;
    protected ResultSet r;
    protected FileConfiguration config = plugin.getConfig();
    
    protected void close(ResultSet r)
    {
        try
        {
            BookShelf.close(r);
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
