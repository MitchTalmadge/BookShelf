package me.MitchT.BookShelf.Commands;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BSC_Towny extends BSCommand
{
    
    public BSC_Towny(BookShelfPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public void onPlayerCommand(Player sender, Command command, String[] args)
    {
        if(plugin.getExternalPluginManager().usingTowny())
            plugin.getExternalPluginManager().getTownyCommandHandler().onCommand(sender, args, plugin);
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
