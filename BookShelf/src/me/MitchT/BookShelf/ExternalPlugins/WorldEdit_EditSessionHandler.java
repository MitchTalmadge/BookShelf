/**
 * @author Mitch Talmadge
 *         Date Created:
 *         Jul 26, 2013
 */

package me.MitchT.BookShelf.ExternalPlugins;

import me.MitchT.BookShelf.BookListener;
import me.MitchT.BookShelf.BookShelf;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * 
 * BookShelf - A Bukkit & Spigot mod allowing the placement of items
 * into BookShelves. <br>
 * Copyright (C) 2012-2014 Mitch Talmadge (mitcht@aptitekk.com)<br>
 * <br>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.<br>
 * <br>
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * @author Mitch Talmadge (mitcht@aptitekk.com)
 */
public class WorldEdit_EditSessionHandler extends EditSession
{
    
    public WorldEdit_EditSessionHandler(LocalWorld world, int maxBlocks)
    {
        super(world, maxBlocks);
    }
    
    public WorldEdit_EditSessionHandler(LocalWorld world, int maxBlocks,
            BlockBag blockBag)
    {
        super(world, maxBlocks, blockBag);
    }
    
    @Override
    public boolean setBlock(Vector pt, BaseBlock block)
            throws MaxChangedBlocksException
    {
        
        BaseBlock originalB = getBlock(pt);
        BaseBlock currentB = block;
        
        if(super.setBlock(pt, block))
        {
            if(originalB.getId() == Material.BOOKSHELF.getId())
            {
                if(currentB.getId() != Material.BOOKSHELF.getId())
                {
                    BookListener.instance.breakShelf(new Location(null, pt
                            .toBlockVector().getBlockX(), pt.toBlockVector()
                            .getBlockY(), pt.toBlockVector().getBlockZ()),
                            false);
                }
            }
            return true;
        }
        return false;
    }
    
}
