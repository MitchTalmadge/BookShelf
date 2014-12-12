package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelf;
import me.MitchT.BookShelf.Towny.TownyCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BSC_Towny extends BSCommand
{
    
    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        if(BookShelf.useTowny)
            TownyCommands.onCommand(sender, args, plugin);
        else
            sender.sendMessage("§cTowny Support is not enabled on this server.");
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
