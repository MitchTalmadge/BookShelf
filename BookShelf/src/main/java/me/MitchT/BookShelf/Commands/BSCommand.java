package me.MitchT.BookShelf.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class BSCommand
{
    
    public BookShelfPlugin plugin;
    protected ResultSet r;
    protected FileConfiguration config;
    
    public BSCommand(BookShelfPlugin plugin)
    {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    protected void close(ResultSet r)
    {
        try
        {
            plugin.close(r);
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
