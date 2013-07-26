/**
@author	Mitch Talmadge
Date Created:
	Jul 26, 2013
*/

package me.Pew446.BookShelf.WorldEdit;

import me.Pew446.BookShelf.BookShelf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;

public class WorldEdit_EditSessionHandler extends EditSession{

	public WorldEdit_EditSessionHandler(LocalWorld world, int maxBlocks) {
		super(world, maxBlocks);
	}
	
	public WorldEdit_EditSessionHandler(LocalWorld world, int maxBlocks, BlockBag blockBag) {
		super(world, maxBlocks, blockBag);
	}

	@Override
	public boolean setBlock(Vector pt, BaseBlock block)
			throws MaxChangedBlocksException {

		BaseBlock originalB = getBlock(pt);
		BaseBlock currentB = block;
		
		if(super.setBlock(pt, block))
		{
			if(originalB.getId() == Material.BOOKSHELF.getId())
			{
				if(currentB.getId() != Material.BOOKSHELF.getId())
				{
					BookShelf.BookListener.breakShelf(new Location(null, pt.toBlockVector().getBlockX(), pt.toBlockVector().getBlockY(), pt.toBlockVector().getBlockZ()), false);
				}
			}
			return true;
		}
		return false;
	}
	
}
