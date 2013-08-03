/**
@author	Mitch Talmadge
Date Created:
	Jul 26, 2013
*/

package me.Pew446.BookShelf.WorldEdit;

import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bags.BlockBag;

public class WorldEdit_EditSessionFactoryHandler extends EditSessionFactory{
	
	@Override
	public EditSession getEditSession(LocalWorld world, int maxBlocks) 
	{
		return new WorldEdit_EditSessionHandler(world, maxBlocks);
	}
	
	@Override
	public EditSession getEditSession(LocalWorld world, int maxBlocks,
			BlockBag blockBag) 
	{
		return new WorldEdit_EditSessionHandler(world, maxBlocks, blockBag);
	}
}
