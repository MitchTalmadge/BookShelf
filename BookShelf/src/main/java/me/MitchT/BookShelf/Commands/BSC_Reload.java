package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BSC_Reload extends BSCommand
{
    
    public BSC_Reload(BookShelfPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        plugin.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
    @Override
    public void onConsoleCommand(ConsoleCommandSender sender, Command command,
            String[] args)
    {
        plugin.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
    @Override
    public void onCommandBlockCommand(CommandSender sender, Command command,
            String[] args)
    {
        plugin.reloadBookShelfConfig();
        sender.sendMessage("§aBookShelf config successfully reloaded.");
    }
    
}
