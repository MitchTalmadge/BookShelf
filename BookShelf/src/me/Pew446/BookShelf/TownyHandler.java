package me.Pew446.BookShelf;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.listeners.TownyBlockListener;
import com.palmergames.bukkit.towny.listeners.TownyCustomListener;
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
	
	public static Resident convertToResident(Player p)
	{
		try {
			return TownyUniverse.getDataSource().getResident(p.getName());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean checkPlotOwnedByResident(TownBlock p, Resident r)
	{
		if(r.hasTownBlock(p))
			return true;
		else
			return false;
	}
	
	public static boolean checkCanUseCommand(Block b, Resident r, int action)
	{
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
			e.printStackTrace();
		}
		
		return false;
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return OUTSIDER;
	}
	
	public static void setTownPermission(Town t, String permission, boolean bool)
	{
		
	}
}
