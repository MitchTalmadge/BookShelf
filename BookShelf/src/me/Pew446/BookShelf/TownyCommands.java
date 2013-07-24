/**
@author	Mitch Talmadge
Date Created:
	Jul 24, 2013
 */

package me.Pew446.BookShelf;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;

public class TownyCommands {

	public static boolean onCommand(CommandSender sender, String label, String[] args, BookShelf bs)
	{
		if(args.length == 0)
		{
			sender.sendMessage(new String[] {
					"Help for /bstowny",
					"Use these commands to change BookShelf permissions for Towny",
					"The commands are set up just like Towny commands for ease of use.",
					"/bstowny town",
					"/bstowny resident",
					"/bstowny plot" });
		}
		else if(args.length > 3)
		{
			ArrayList<String> level0 = new ArrayList<String>(Arrays.asList("town", "resident", "plot"));
			ArrayList<String> level1 = new ArrayList<String>(Arrays.asList("unlimited", "toggle", "shop", "name"));
			ArrayList<String> level2a = new ArrayList<String>(Arrays.asList("resident", "ally", "outsider"));
			ArrayList<String> level2b = new ArrayList<String>(Arrays.asList("friend", "ally", "outsider"));
			ArrayList<String> level3 = new ArrayList<String>(Arrays.asList("on", "off"));
			if(level0.contains(args[0]))
			{
				Resident res = TownyHandler.convertToResident((Player)sender);
				if(res.hasTown())
				{
					try {
						if(!TownyHandler.hasPriviledges(res, res.getTown()) && (args[0].equals("town") || args[0].equals("resident")))
						{
							sender.sendMessage("You do not have the priveledges for this town!");
							return true;
						}
						if(args[1].equals("set"))
						{	
							String plotString = "";
							if(args[0].equals("plot"))
							{
								TownBlock currentPlot = TownyHandler.getPlotFromResidentCoords(res);
								if(!TownyHandler.checkPlotInResidentsTown(currentPlot, res))
								{
									sender.sendMessage("This plot does not belong to "+res.getTown().getFormattedName()+".");
									return true;
								}
								if(!TownyHandler.checkPlotOwnedByResident(currentPlot, res) && !TownyHandler.hasPriviledges(res, res.getTown()))
								{
									sender.sendMessage("You do not have the priveledges for this plot!");
									return true;
								}
								plotString = TownyHandler.getPlotStringFromResidentCoords(res)+".";
							}
							if(level1.contains(args[2]))
							{
								ArrayList<String> chosen = args[0].equals("town") ? level2a : level2b; 
								if(chosen.contains(args[3]) && args.length > 4)
								{
									if(args[4].equals("on"))
										TownyHandler.setTownPermission(res.getTown(), args[0]+"."+plotString+args[2]+"."+args[3], true);
									else if(args[4].equals("off"))
										TownyHandler.setTownPermission(res.getTown(), args[0]+"."+plotString+args[2]+"."+args[3], false);
								}
								else if(level3.contains(args[3]))
								{
									for(String s : chosen)
									{
										TownyHandler.setTownPermission(res.getTown(), args[0]+"."+plotString+args[2]+"."+s, args[3].equals("on") ? true : false);
									}
								}
							}
						}
					} catch (NotRegisteredException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
}
