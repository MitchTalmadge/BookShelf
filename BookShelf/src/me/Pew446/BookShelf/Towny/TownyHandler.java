package me.Pew446.BookShelf.Towny;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import me.Pew446.BookShelf.BookShelf;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * Handles all the Towny related checks.
 * 
 * @author graywolf336, Pew446
 *
 */
public class TownyHandler {	

	public static final int RESIDENT = 0;
	public static final int ALLY = 1;
	public static final int OUTSIDER = 2;
	public static final int FRIEND = 3;

	public static final int BUILD = 0;
	public static final int DESTROY = 1;
	public static final int SWITCH = 2;
	public static final int ITEM = 3;

	public static final int UNLIMITED = 0;
	public static final int TOGGLE = 1;
	public static final int SHOP = 2;
	public static final int NAME = 3;
	public static final int OPEN_SHELF = 4;
	public static final int OPEN_SHOP = 5;
	public static final int DONATE = 6;

	public static Resident convertToResident(Player p)
	{
		try {
			return TownyUniverse.getDataSource().getResident(p.getName());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean checkPlotOwnedByResident(TownBlock b, Resident r)
	{
		try {
			if(b.getResident() == r)
				return true;
			else
				return false;
		} catch (NotRegisteredException e) {
			return false;
		}
	}

	public static boolean checkCanDoAction(Block b, Resident r, int type)
	{
		Player p = Bukkit.getPlayer(r.getName());
		String typeString = null;
		switch(type)
		{
		case UNLIMITED:
			typeString = "unlimited.";
			break;
		case TOGGLE:
			typeString = "toggle.";
			break;
		case SHOP:
			typeString = "shop.";
			break;
		case NAME:
			typeString = "name.";
			break;
		case OPEN_SHELF:
			typeString = "open_shelf.";
			break;
		case OPEN_SHOP:
			typeString = "open_shop.";
			break;
		case DONATE:
			typeString = "donate.";
			break;
		}
		TownBlock plot = TownyUniverse.getTownBlock(b.getLocation());
		if(plot == null)
			if(!p.isOp())
				return false;
			else
				return true;
		if(!plot.hasTown())
			if(!p.isOp())
				return false;
			else
				return true;
		try {
			Town town = plot.getTown();
			if(hasPrivileges(r, town))
				return true;
			if(checkPlotIsOwned(plot))
			{
				if(checkPlotOwnedByResident(plot, r))
					return true;
				switch(getRelation(plot.getResident(), r))
				{
				case FRIEND:
					return (Boolean) getTownPermission(town, "plot."+getPlotStringFromCoords(plot.getCoord())+"."+typeString+
							"friend");
				case ALLY:
					return (Boolean) getTownPermission(town, "plot."+getPlotStringFromCoords(plot.getCoord())+"."+typeString+
							"ally");
				case OUTSIDER:
					return (Boolean) getTownPermission(town, "plot."+getPlotStringFromCoords(plot.getCoord())+"."+typeString+
							"outsider");
				}
			}
			else
			{
				switch(getRelation(r, town))
				{
				case RESIDENT:
					return (Boolean) getTownPermission(town, "town."+typeString+
							"resident");
				case ALLY:
					return (Boolean) getTownPermission(town, "town."+typeString+
							"ally");
				case OUTSIDER:
					return (Boolean) getTownPermission(town, "town."+typeString+
							"outsider");
				}
			}
		} catch (NotRegisteredException e) {
			return false;
		}
		return false;
	}

	public static boolean checkCanPerformAction(Block b, Resident r, int action)
	{
		Town t = null;

		try {
			t = TownyUniverse.getTownBlock(b.getLocation()).getTown();

			int relation = getRelation(r, t);
			switch(relation)
			{
			case RESIDENT:
				switch(action)
				{
				case BUILD:
					return t.getPermissions().residentBuild;
				case DESTROY:
					return t.getPermissions().residentDestroy;
				case SWITCH:
					return t.getPermissions().residentSwitch;
				case ITEM:
					return t.getPermissions().residentItemUse;
				}
			case ALLY:
				switch(action)
				{
				case BUILD:
					return t.getPermissions().allyBuild;
				case DESTROY:
					return t.getPermissions().allyDestroy;
				case SWITCH:
					return t.getPermissions().allySwitch;
				case ITEM:
					return t.getPermissions().allyItemUse;
				}
			case OUTSIDER:
				switch(action)
				{
				case BUILD:
					return t.getPermissions().outsiderBuild;
				case DESTROY:
					return t.getPermissions().outsiderDestroy;
				case SWITCH:
					return t.getPermissions().outsiderSwitch;
				case ITEM:
					return t.getPermissions().outsiderItemUse;
				}
			}

		} catch (NotRegisteredException e) {
			return false;
		}
		return false;
	}

	public static boolean hasPrivileges(Resident r, Town t)
	{
		Player p = Bukkit.getPlayer(r.getName());
		if(t.hasMayor())
			if(t.getMayor() == r)
				return true;
		if(t.hasAssistant(r))
			return true;
		if(p.isOp())
			return true;
		return false;
	}

	public static TownBlock getPlotFromResidentCoords(Resident r)
	{
		Player p = Bukkit.getPlayer(r.getName());
		Location loc = p.getLocation();
		return TownyUniverse.getTownBlock(loc);
	}

	public static String getPlotStringFromResidentCoords(Resident r)
	{
		Player p = Bukkit.getPlayer(r.getName());
		Location loc = p.getLocation();
		TownBlock block = TownyUniverse.getTownBlock(loc);
		Coord c = block.getCoord();
		return c.getX()+"_"+c.getZ();
	}
	
	public static String getPlotStringFromCoords(Coord c)
	{
		return c.getX()+"_"+c.getZ();
	}

	/**
	 * Returns the relation of the resident to the town.
	 * @param r Resident
	 * @param t Town
	 */
	public static int getRelation(Resident r, Town t)
	{
		try {
			if(!r.hasTown())
				return OUTSIDER;
			else if(r.getTown() == t)
			{
				return RESIDENT;
			}
			else if(t.hasNation())
			{
				if(r.hasNation())
				{
					if(r.getTown().getNation().hasAlly(t.getNation()))
						return ALLY;
					else if(t.getNation().hasTown(r.getTown()))
						return ALLY;
				}
			}
		} catch (NotRegisteredException e) {
			return OUTSIDER;
		}
		return OUTSIDER;
	}

	/**
	 * Returns the relation of the two residents
	 * @param r Resident one
	 * @param r2 Resident two
	 */
	public static int getRelation(Resident r, Resident r2)
	{
		try {
			if(r.hasFriend(r2))
			{
				return FRIEND;
			}

			if(r.hasTown() && r2.hasTown())
			{
				if(r.getTown() == r2.getTown())
				{
					return ALLY;
				}
				else if(r.getTown().hasNation() && r2.getTown().hasNation())
				{
					if(r.getTown().getNation().hasTown(r2.getTown()))
						return ALLY;
					else if(r.getTown().getNation().hasAlly(r2.getTown().getNation()))
						return ALLY;
				}
			}
		} catch (NotRegisteredException e) {
			return OUTSIDER;
		}
		return OUTSIDER;
	}

	public static void setTownPermission(Town t, String permission, Object value)
	{
		String townName = t.getName();
		if(!BookShelf.townyConfig.contains("towns."+townName))
		{
			saveDefaultConfig(t);
		}

		BookShelf.townyConfig.set("towns."+townName+"."+permission, value);
		saveConfig();
	}

	public static Object getTownPermission(Town t, String permission)
	{
		String townName = t.getName();
		if(!BookShelf.townyConfig.contains("towns."+townName))
		{
			saveDefaultConfig(t);
		}

		Object result = BookShelf.townyConfig.get("towns."+townName+"."+permission);
		if(result == null)
		{
			return getDefaultConfigValue(t, permission);
		}
		
		return BookShelf.townyConfig.get("towns."+townName+"."+permission);
	}
	
	public static void setDefaultConfigValue(Town t, String location)
	{
		if(location.startsWith("plot"))
		{
			setTownPermission(t, location, BookShelf.townyConfig.get("defaults."+"resident"+location.substring(location.split("_")[1].indexOf(".")+location.split("_")[0].length()+1)));
		}
		else
		{
			setTownPermission(t, location, BookShelf.townyConfig.get("defaults."+location));
		}
	}
	
	public static Object getDefaultConfigValue(Town t, String location)
	{
		if(location.startsWith("plot"))
		{
			return getTownPermission(t, "resident"+location.substring(location.split("_")[1].indexOf(".")+location.split("_")[0].length()+1));
		}
		else
		{
			return BookShelf.townyConfig.get("defaults."+location);
		}
	}

	public static void removeTownFromConfig(Town t)
	{
		saveDefaultConfig(t);
	}

	private static void saveDefaultConfig(Town t) {
		ArrayList<String> level1 = new ArrayList<String>(Arrays.asList("town.", "resident."));
		ArrayList<String> level2 = new ArrayList<String>(Arrays.asList("unlimited.", "toggle.", "shop.", "name."));
		ArrayList<String> level3a = new ArrayList<String>(Arrays.asList("resident", "ally", "outsider"));
		ArrayList<String> level3b = new ArrayList<String>(Arrays.asList("friend", "ally", "outsider"));

		String prefix = "towns."+t.getName()+".";
		String prefixDef = "defaults.";
		for(int i = 0; i<level1.size(); i++)
		{
			for(String j:level2)
			{
				if(i == 0)
				{
					for(String k:level3a)
					{
						BookShelf.townyConfig.set(prefix+level1.get(i)+j+k, BookShelf.townyConfig.get(prefixDef+level1.get(i)+j+k));
					}
				}
				else
				{
					for(String k:level3b)
					{
						BookShelf.townyConfig.set(prefix+level1.get(i)+j+k, BookShelf.townyConfig.get(prefixDef+level1.get(i)+j+k));
					}
				}
			}
		}
		saveConfig();
	}

	public static void saveConfig()
	{
		try {
			BookShelf.townyConfig.save(BookShelf.townyConfigPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkPlotInAnyTown(TownBlock plot) {
		if(plot == null)
			return false;
		if(!plot.hasTown())
			return false;
		
		return true;
	}
	
	public static boolean checkPlotInResidentsTown(TownBlock plot, Resident res) {
		if(plot == null)
			return false;
		if(!plot.hasTown())
			return false;
		if(!res.hasTown())
			return false;
		try {
			if(res.getTown() == plot.getTown())
				return true;
		} catch (NotRegisteredException e) {
			return false;
		}
		return false;
	}
	
	public static boolean checkPlotIsOwned(TownBlock plot) {
		if(plot == null)
			return false;
		if(!plot.hasTown())
			return false;
		if(plot.hasResident())
			return true;
		
		return false;
	}

}
