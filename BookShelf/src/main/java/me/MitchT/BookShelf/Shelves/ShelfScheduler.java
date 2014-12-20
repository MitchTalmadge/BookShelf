package me.MitchT.BookShelf.Shelves;

import java.util.ArrayList;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.scheduler.BukkitScheduler;

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
public class ShelfScheduler
{
    private final ArrayList<Location> shelvesToPurge;
    private final BookShelfPlugin plugin;
    private final BukkitScheduler scheduler;
    private int taskID = 0;
    
    public ShelfScheduler(BookShelfPlugin plugin)
    {
        this.plugin = plugin;
        this.shelvesToPurge = new ArrayList<Location>();
        this.scheduler = plugin.getServer().getScheduler();
        startScheduler();
    }
    
    private void startScheduler()
    {
        this.taskID = this.scheduler.scheduleSyncRepeatingTask(plugin,
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(shelvesToPurge.size() > 0)
                        {
                            System.out.println("Purging "
                                    + shelvesToPurge.size() + " shelves.");
                            plugin.getSQLManager().setAutoCommit(false);
                            for(Location shelfLocation : shelvesToPurge)
                            {
                                plugin.runQuery("DELETE FROM copy WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM shop WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM donate WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM names WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM enable WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM enchant WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM maps WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM owners WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                                plugin.runQuery("DELETE FROM items WHERE x="
                                        + shelfLocation.getX() + " AND y="
                                        + shelfLocation.getY() + " AND z="
                                        + shelfLocation.getZ() + ";");
                            }
                            plugin.getSQLManager().commit();
                            plugin.getSQLManager().setAutoCommit(true);
                            shelvesToPurge.clear();
                            System.out.println("Shelves purged successfully.");
                        }
                    }
                }, 0L, 20L);
    }
    
    public void stopScheduler()
    {
        if(this.taskID > 0)
        {
            this.scheduler.cancelTask(taskID);
        }
    }
    
    public void addShelfToPurgationQueue(BookShelf shelf)
    {
        shelf.closeInventories();
        addShelfToPurgationQueue(shelf.getLocation());
    }
    
    public void removeShelfFromPurgationQueue(BookShelf shelf)
    {
        removeShelfFromPurgationQueue(shelf.getLocation());
    }
    
    public void addShelfToPurgationQueue(Location shelfLocation)
    {
        if(!this.shelvesToPurge.contains(shelfLocation))
            this.shelvesToPurge.add(shelfLocation);
    }
    
    public void removeShelfFromPurgationQueue(Location shelfLocation)
    {
        if(this.shelvesToPurge.contains(shelfLocation))
            this.shelvesToPurge.remove(shelfLocation);
    }
}
