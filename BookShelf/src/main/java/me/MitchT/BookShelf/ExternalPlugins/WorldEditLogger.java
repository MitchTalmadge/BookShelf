package me.MitchT.BookShelf.ExternalPlugins;

import me.MitchT.BookShelf.BookListener;

import org.bukkit.Location;
import org.bukkit.Material;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;

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
        
            if(oldBlock.getId() == Material.BOOKSHELF.getId() && newBlock.getId() != Material.BOOKSHELF.getId())
            {
                    BookListener.instance.breakShelf(new Location(null, location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                            false);
            }
    }
    
}
