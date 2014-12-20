package me.MitchT.BookShelf.ExternalPlugins;

import me.MitchT.BookShelf.BookShelfPlugin;
import me.MitchT.BookShelf.Shelves.BookShelf;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;

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
public class WorldEditLogger extends AbstractLoggingExtent
{
    private final Actor actor;
    
    protected WorldEditLogger(Actor actor, Extent extent)
    {
        super(extent);
        this.actor = actor;
    }
    
    @Override
    protected void onBlockChange(Vector position, BaseBlock newBlock)
    {
        BaseBlock oldBlock = getBlock(position);
        BlockVector location = position.toBlockVector();
        
        if(oldBlock.getId() == Material.BOOKSHELF.getId()
                && newBlock.getId() != Material.BOOKSHELF.getId())
        {
            BookShelfPlugin
                    .getInstance()
                    .getShelfScheduler()
                    .addShelfToPurgationQueue(
                            new Location(null, location.getBlockX(), location
                                    .getBlockY(), location.getBlockZ()));
        }
    }
    
}
