/**
@author	Mitch Talmadge
Date Created:
	Jul 24, 2013
 */

package me.Pew446.BookShelf;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

public class TownyCommands {

	public static boolean onCommand(CommandSender sender, String label, String[] args, BookShelf bs)
	{
		if(args.length == 0)
		{
			sender.sendMessage("Help will go here");
		}
		else if(args.length > 3)
		{
			if(args[0] == "town")
			{
				Resident res = TownyHandler.convertToResident((Player)sender);
				if(res.hasTown())
				{
					try {
						if(res.getTown().getMayor() == res)
						{
							if(args[1] == "set")
							{
								if(args[2] == "unlimited")
								{
									if(args.length > 4)
									{
										if(args[3] == "resident")
										{
											if(args[4] == "on")
											{
												
											}
											else if(args[4] == "off")
											{
												
											}
										}
										else if(args[3] == "ally")
										{
											
										}
										else if(args[3] == "outsider")
										{
											
										}
									}
								}
								else if(args[2] == "toggle")
								{

								}
								else if(args[2] == "shop")
								{

								}
								else if(args[2] == "name")
								{

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
