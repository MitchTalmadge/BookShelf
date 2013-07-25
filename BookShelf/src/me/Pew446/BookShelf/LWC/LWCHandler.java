/**
@author	Mitch Talmadge
Date Created:
	Jul 25, 2013
*/

package me.Pew446.BookShelf.LWC;

import me.Pew446.BookShelf.BookShelf;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;

public class LWCHandler extends LWC{

	public LWCHandler(LWCPlugin plugin) {
		super(plugin);
		super.load();
	}
	
	@Override
	public boolean isProtectable(Block block)
	{
		if(block.getType() == Material.BOOKSHELF && BookShelf.LWCEnabled)
			return true;
		else
			return super.isProtectable(block.getType());	
	}
	
}
