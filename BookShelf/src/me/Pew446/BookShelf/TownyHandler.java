package me.Pew446.BookShelf;

import org.bukkit.block.Block;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * Handles all the Towny related checks.
 * 
 * @author graywolf336
 *
 */
public class TownyHandler {	
	/**
	 * Checks if the block the player is placing at is located in their town.
	 * 
	 * @param block The block to check which they are placing down
	 * @param player The player to check
	 * @return True if the player can build it, false if not.
	 */
	public static boolean checkForPlayersTown(Block block, String player) {
		Town playersTown = null, blocksTown = null;
		
		try {
			playersTown = TownyUniverse.getDataSource().getResident(player).getTown();
			
			TownBlock tb = TownyUniverse.getTownBlock(block.getLocation());
			if(tb == null)
				return false; //wilderness
			
			blocksTown = TownyUniverse.getTownBlock(block.getLocation()).getTown();
		} catch (NotRegisteredException e) {
			return false;
		}
		
		return playersTown.equals(blocksTown);
	}
	
	/**
	 * Checks if the player is the owner of the block/plot they are building at.
	 * 
	 * @param check The block to check which they are placing down
	 * @param player The name of the player to check, normal case.
	 * @return True if the player can build, false if not.
	 */
	public static boolean checkSingleTownyBlock(Block check, String player) {
		Resident res = null, blockOwner = null;
		TownBlock tbC = TownyUniverse.getTownBlock(check.getLocation());
		
		if(tbC == null)
			return false;
		
		try {
			res = TownyUniverse.getDataSource().getResident(player);
			blockOwner = tbC.getResident();
			
			//if the town's mayor is this player, then allow it
			if(tbC.getTown().getMayor().equals(res)) return true;
			
		} catch (NotRegisteredException e) {return false;}
		
		return res.equals(blockOwner);
	}
}
