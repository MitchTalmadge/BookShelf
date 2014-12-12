package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.LWC.LWCPluginHandler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BSC_Reload extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        BookShelf.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
    @Override
    public void onConsoleCommand(ConsoleCommandSender sender, Command command,
            String[] args)
    {
        BookShelf.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
    @Override
    public void onCommandBlockCommand(CommandSender sender, Command command,
            String[] args)
    {
        BookShelf.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
}
