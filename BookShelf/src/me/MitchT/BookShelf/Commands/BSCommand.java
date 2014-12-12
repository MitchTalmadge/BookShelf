package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class BSCommand
{
    
    public BookShelf plugin = BookShelf.instance;
    
    public abstract void onPlayerCommand(Player sender, Command command,
            String[] args);
    
    public abstract void onConsoleCommand(ConsoleCommandSender sender,
            Command command, String[] args);
    
    public abstract void onCommandBlockCommand(CommandSender sender,
            Command command, String[] args);
    
}
