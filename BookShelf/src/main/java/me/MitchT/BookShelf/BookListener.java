package me.MitchT.BookShelf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;
import me.MitchT.BookShelf.Shelves.BookShelf;
import me.MitchT.BookShelf.Shelves.ShelfType;
import me.MitchT.BookShelf.Shelves.VirtualBookShelf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

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
public class BookListener implements Listener
{
    public static BookShelfPlugin plugin;
    public static BookListener instance;
    
    public BookListener(BookShelfPlugin plugin)
    {
        BookListener.plugin = plugin;
        BookListener.instance = this;
    }
    
    static ResultSet r;
    
    private void close(ResultSet r) throws SQLException
    {
        plugin.close(r);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent j)
    {
        Player player = j.getPlayer();
        if(j.isCancelled())
            return;
        
        if(j.getClickedBlock() != null)
        {
            if(j.getClickedBlock().getType() == Material.BOOKSHELF)
            {
                if(!j.getPlayer().isSneaking())
                {
                    if(j.getPlayer().getItemInHand().getType() == Material.BOOKSHELF)
                    {
                        return;
                    }
                    if(j.getAction() == Action.RIGHT_CLICK_BLOCK)
                    {
                        
                        Location shelfLocation = j.getClickedBlock()
                                .getLocation();
                        if(!plugin.getConfig().getBoolean("top-bottom_access"))
                        {
                            if(j.getBlockFace() == BlockFace.UP
                                    | j.getBlockFace() == BlockFace.DOWN)
                            {
                                return;
                            }
                        }
                        
                        BookShelf shelf = new BookShelf(shelfLocation);
                        
                        if(!shelf.isEnabled())
                            return;
                        
                        if(!shelf.isShelfType(ShelfType.UNLIMITED)
                                && !shelf.isShelfType(ShelfType.SHOP)
                                && !shelf.isShelfType(ShelfType.DONATION))
                        {
                            if(!shelf.isOwner(player)
                                    && !player
                                            .hasPermission("bookshelf.openshelf"))
                            {
                                player.sendMessage("§cYou are not allowed to open this shelf!");
                                return;
                            }
                        }
                        
                        if(shelf.isShelfType(ShelfType.SHOP)
                                && plugin.getExternalPluginManager()
                                        .usingVaultEconomy())
                        {
                            if(plugin.getExternalPluginManager().usingTowny())
                            {
                                if(!plugin
                                        .getExternalPluginManager()
                                        .getTownyHandler()
                                        .checkCanDoAction(
                                                j.getClickedBlock(),
                                                plugin.getExternalPluginManager()
                                                        .getTownyHandler()
                                                        .convertToResident(
                                                                j.getPlayer()),
                                                TownyHandler.OPEN_SHOP))
                                {
                                    j.getPlayer()
                                            .sendMessage(
                                                    "§cYou are not allowed to open BookShops here!");
                                    j.setCancelled(true);
                                    return;
                                }
                            }
                            if(plugin.getExternalPluginManager()
                                    .usingWorldGuard())
                            {
                                RegionManager regionManager = plugin
                                        .getExternalPluginManager()
                                        .getWorldGuardPlugin()
                                        .getRegionManager(
                                                j.getPlayer().getWorld());
                                if(regionManager != null)
                                {
                                    ApplicableRegionSet set = regionManager
                                            .getApplicableRegions(shelfLocation);
                                    if(set.size() > 0)
                                        if(!set.allows(
                                                DefaultFlag.ENABLE_SHOP,
                                                plugin.getExternalPluginManager()
                                                        .getWorldGuardPlugin()
                                                        .wrapPlayer(
                                                                j.getPlayer()))
                                                && !set.isOwnerOfAll(plugin
                                                        .getExternalPluginManager()
                                                        .getWorldGuardPlugin()
                                                        .wrapPlayer(
                                                                j.getPlayer()))
                                                && !j.getPlayer().isOp())
                                        {
                                            j.getPlayer()
                                                    .sendMessage(
                                                            "§cYou are not allowed to open BookShops here!");
                                            j.setCancelled(true);
                                            return;
                                        }
                                }
                            }
                        }
                        else
                        { //Not a shop or economy is disabled
                            if(plugin.getExternalPluginManager().usingTowny())
                            {
                                if(!plugin
                                        .getExternalPluginManager()
                                        .getTownyHandler()
                                        .checkCanDoAction(
                                                j.getClickedBlock(),
                                                plugin.getExternalPluginManager()
                                                        .getTownyHandler()
                                                        .convertToResident(
                                                                j.getPlayer()),
                                                TownyHandler.OPEN_SHELF))
                                {
                                    j.getPlayer()
                                            .sendMessage(
                                                    "§cYou are not allowed to open BookShelves here!");
                                    j.setCancelled(true);
                                    return;
                                }
                            }
                            if(plugin.getExternalPluginManager()
                                    .usingWorldGuard())
                            {
                                RegionManager regionManager = plugin
                                        .getExternalPluginManager()
                                        .getWorldGuardPlugin()
                                        .getRegionManager(
                                                j.getPlayer().getWorld());
                                if(regionManager != null)
                                {
                                    ApplicableRegionSet set = regionManager
                                            .getApplicableRegions(shelfLocation);
                                    if(set.size() > 0)
                                        if(!set.allows(
                                                DefaultFlag.CHEST_ACCESS,
                                                plugin.getExternalPluginManager()
                                                        .getWorldGuardPlugin()
                                                        .wrapPlayer(
                                                                j.getPlayer()))
                                                && !set.isOwnerOfAll(plugin
                                                        .getExternalPluginManager()
                                                        .getWorldGuardPlugin()
                                                        .wrapPlayer(
                                                                j.getPlayer()))
                                                && !j.getPlayer().isOp())
                                        {
                                            j.getPlayer()
                                                    .sendMessage(
                                                            "§cYou are not allowed to open BookShelves here!");
                                            j.setCancelled(true);
                                            return;
                                        }
                                }
                            }
                        }
                        
                        boolean isOwner = shelf.isOwner(player);
                        boolean isOwnerEditing = (isOwner && plugin
                                .getShelfManager().playerIsEditing(player));
                        
                        Inventory inv;
                        if(!isOwnerEditing
                                && shelf.isShelfType(ShelfType.UNLIMITED))
                            inv = shelf.generateVirtualInventory();
                        else
                            inv = shelf.getInventory();
                        
                        player.openInventory(inv);
                        
                        if(plugin.autoToggle)
                        {
                            String shelfName = shelf.getName();
                            if(shelfName.endsWith(" "))
                                shelfName = shelfName.substring(0,
                                        shelfName.length() - 1);
                            if(plugin.autoToggleNameList == null
                                    || plugin.autoToggleNameList
                                            .contains(shelfName))
                            {
                                if(!plugin.autoToggleMap1
                                        .containsKey(shelfLocation))
                                {
                                    plugin.autoToggleMap1.put(shelfLocation, 1);
                                    List<Player> list = new ArrayList<Player>();
                                    list.add(player);
                                    plugin.autoToggleMap2.put(shelfLocation,
                                            list);
                                }
                                else
                                {
                                    if(!plugin.autoToggleDiffPlayers)
                                    {
                                        int old = plugin.autoToggleMap1
                                                .get(shelfLocation);
                                        plugin.autoToggleMap1
                                                .remove(shelfLocation);
                                        plugin.autoToggleMap1.put(
                                                shelfLocation, old + 1);
                                    }
                                    else if(!plugin.autoToggleMap2.get(
                                            shelfLocation).contains(player))
                                    {
                                        int old = plugin.autoToggleMap1
                                                .get(shelfLocation);
                                        plugin.autoToggleMap1
                                                .remove(shelfLocation);
                                        plugin.autoToggleMap1.put(
                                                shelfLocation, old + 1);
                                        plugin.autoToggleMap2
                                                .get(shelfLocation).add(player);
                                    }
                                }
                                if(plugin.autoToggleMap1.get(shelfLocation) >= plugin.autoToggleFreq)
                                {
                                    plugin.autoToggleMap1.remove(shelfLocation);
                                    plugin.autoToggleMap2.remove(shelfLocation);
                                    if(plugin.autoToggleServerWide)
                                    {
                                        plugin.getShelfManager()
                                                .toggleShelvesByName(shelfName);
                                        if(!shelfName.endsWith(" "))
                                            shelfName += " ";
                                        System.out
                                                .println("(Auto Toggle) All bookshelves with the name "
                                                        + shelfName
                                                        + "have been toggled.");
                                    }
                                    else
                                    {
                                        plugin.getShelfManager().toggleShelf(
                                                shelfLocation);
                                        System.out
                                                .println("(Auto Toggle) The bookshelf at ("
                                                        + shelfLocation
                                                                .getBlockX()
                                                        + ", "
                                                        + shelfLocation
                                                                .getBlockY()
                                                        + ", "
                                                        + shelfLocation
                                                                .getBlockZ()
                                                        + ") has been toggled.");
                                    }
                                }
                            }
                        }
                        
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public Location getKey(HashMap map, InventoryHolder inventoryHolder)
    {
        Set key = map.keySet();
        for(Iterator i = key.iterator(); i.hasNext();)
        {
            Location next = (Location) i.next();
            if(map.get(next).equals(inventoryHolder))
            {
                return next;
            }
        }
        return null;
    }
    
    @EventHandler
    public void onAdd(InventoryCloseEvent j)
    {
        if(j.getInventory().getHolder() instanceof BookShelf
                && j.getInventory().getViewers().size() == 1)
        {
            BookShelf shelf = (BookShelf) j.getInventory().getHolder();
            shelf.saveInventory();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent j)
    {
        if(j.isCancelled())
            return;
        if(j.getBlock().getType() == Material.BOOKSHELF)
            new BookShelf(j.getBlock().getLocation()).breakShelf(false);
    }
    
    @EventHandler
    public void onBurn(BlockBurnEvent j)
    {
        if(j.isCancelled())
            return;
        if(j.getBlock().getType() == Material.BOOKSHELF)
        {
            if(!plugin.getConfig().getBoolean("shelves_can_burn"))
            {
                j.setCancelled(true);
                return;
            }
            new BookShelf(j.getBlock().getLocation()).breakShelf(false);
        }
    }
    
    @EventHandler
    public void onInv(InventoryClickEvent j)
    {
        if((j.getInventory().getType() == InventoryType.CHEST || j
                .getInventory().getType() == InventoryType.ENDER_CHEST)
                && !((j.getInventory().getHolder() instanceof BookShelf) || (j
                        .getInventory().getHolder() instanceof VirtualBookShelf)))
        {
            if(j.getCurrentItem() != null)
            {
                String prefix = "shelf_only_items.";
                Player p = (Player) j.getWhoClicked();
                if(plugin.getConfig().getBoolean(prefix + "book"))
                {
                    if(j.getCurrentItem().getType() == Material.BOOK
                            || j.getCursor().getType() == Material.BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cBooks may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "book_and_quill"))
                {
                    if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL
                            || j.getCursor().getType() == Material.BOOK_AND_QUILL)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cBook and Quills may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "signed"))
                {
                    if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK
                            || j.getCursor().getType() == Material.WRITTEN_BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cSigned Books may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "maps"))
                {
                    if(j.getCurrentItem().getType() == Material.MAP
                            || j.getCursor().getType() == Material.MAP
                            || j.getCurrentItem().getType() == Material.EMPTY_MAP
                            || j.getCursor().getType() == Material.EMPTY_MAP)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cMaps may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "enchanted_book"))
                {
                    if(j.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                            || j.getCursor().getType() == Material.ENCHANTED_BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cEnchanted Books may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "records"))
                {
                    if(BookShelfPlugin.records.contains(j.getCurrentItem()
                            .getType())
                            || BookShelfPlugin.records.contains(j.getCursor()
                                    .getType()))
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cRecords may only be stored in bookshelves.");
                        return;
                    }
                }
                if(plugin.getConfig().getBoolean(prefix + "paper"))
                {
                    if(j.getCurrentItem().getType() == Material.PAPER
                            || j.getCursor().getType() == Material.PAPER)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cPaper may only be stored in bookshelves.");
                        return;
                    }
                }
                return;
            }
        }
        
        if(j.getInventory().getType() == InventoryType.MERCHANT)
        {
            if(j.getCurrentItem() == null)
            {
                return;
            }
            if(plugin.getConfig().getBoolean("villager_trading.allow_book") == false)
            {
                if(j.getCurrentItem().getType() == Material.BOOK)
                {
                    j.setCancelled(true);
                    return;
                }
                else if(j.getCursor().getType() == Material.BOOK)
                {
                    j.setCancelled(true);
                    return;
                }
            }
            if(plugin.getConfig().getBoolean(
                    "villager_trading.allow_book_and_quill") == false)
            {
                if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL)
                {
                    j.setCancelled(true);
                    return;
                }
                else if(j.getCursor().getType() == Material.BOOK_AND_QUILL)
                {
                    j.setCancelled(true);
                    return;
                }
            }
            if(plugin.getConfig().getBoolean("villager_trading.allow_signed") == false)
            {
                if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK)
                {
                    j.setCancelled(true);
                    return;
                }
                else if(j.getCursor().getType() == Material.WRITTEN_BOOK)
                {
                    j.setCancelled(true);
                    return;
                }
            }
            if(plugin.getConfig().getBoolean("villager_trading.allow_paper") == false)
            {
                if(j.getCurrentItem().getType() == Material.PAPER)
                {
                    j.setCancelled(true);
                    return;
                }
                else if(j.getCursor().getType() == Material.PAPER)
                {
                    j.setCancelled(true);
                    return;
                }
            }
        }
        
        if(j.getInventory().getHolder() instanceof BookShelf
                || j.getInventory().getHolder() instanceof VirtualBookShelf)
        {
            BookShelf shelf;
            if(j.getInventory().getHolder() instanceof BookShelf)
            {
                shelf = (BookShelf) j.getInventory().getHolder();
            }
            else
            {
                shelf = ((VirtualBookShelf) j.getInventory().getHolder())
                        .getOriginShelf();
            }
            
            Player player = (Player) j.getWhoClicked();
            
            boolean isOwner = shelf.isOwner(player);
            boolean isOwnerEditing = (shelf.isOwner(player) && plugin
                    .getShelfManager().playerIsEditing(player));
            
            if((isOwner && !shelf.isShelfType(ShelfType.SHOP) && !shelf
                    .isShelfType(ShelfType.UNLIMITED)) || isOwnerEditing)
            {
                this.checkAllowed(j);
                return;
            }
            if(!shelf.isShelfType(ShelfType.SHOP)
                    || !plugin.getExternalPluginManager().usingVaultEconomy())
            {
                if(!shelf.isShelfType(ShelfType.UNLIMITED))
                {
                    if(shelf.isShelfType(ShelfType.DONATION))
                    {
                        int amountOfSlots = (plugin.getConfig().getInt("rows") * 9) - 1;
                        if(j.getRawSlot() > amountOfSlots)
                        {
                            this.checkAllowed(j);
                            return;
                        }
                        else
                        {
                            if(j.getCurrentItem().getType() == Material.AIR)
                                return;
                            j.setCancelled(true);
                        }
                    }
                }
                else
                {
                    int amountOfSlots = (plugin.getConfig().getInt("rows") * 9) - 1;
                    if(j.getRawSlot() <= amountOfSlots)
                    {
                        return;
                    }
                    else
                    {
                        if(j.getCurrentItem().getType() == Material.AIR)
                            return;
                        j.setCancelled(true);
                    }
                }
            }
            else
            {
                int shopPrice = shelf.getPrice();
                int amountOfSlots = (plugin.getConfig().getInt("rows") * 9) - 1;
                if(j.getRawSlot() <= amountOfSlots)
                {
                    if(j.getCurrentItem() == null
                            || j.getCurrentItem().getType() == null
                            || j.getCurrentItem().getType() == Material.AIR)
                    {
                        j.setCancelled(true);
                        return;
                    }
                    
                    double playerBalance = plugin.getExternalPluginManager()
                            .getVaultEconomy().getBalance(player);
                    
                    if(playerBalance >= shopPrice)
                    {
                        plugin.getExternalPluginManager().getVaultEconomy()
                                .withdrawPlayer(player, shopPrice);
                        player.sendMessage("New balance: §6"
                                + plugin.getExternalPluginManager()
                                        .getVaultEconomy().getBalance(player)
                                + " "
                                + plugin.getExternalPluginManager()
                                        .getVaultEconomy().currencyNamePlural());
                        return;
                    }
                    player.sendMessage("§cInsufficient funds! Current balance: §6"
                            + plugin.getExternalPluginManager()
                                    .getVaultEconomy().getBalance(player)
                            + " "
                            + plugin.getExternalPluginManager()
                                    .getVaultEconomy().currencyNamePlural());
                    j.setCancelled(true);
                }
                else
                {
                    if(j.getCurrentItem().getType() == Material.AIR)
                        return;
                    j.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    private void checkAllowed(InventoryClickEvent j)
    {
        if(j.getCurrentItem() == null)
        {
            return;
        }
        
        Player p = (Player) j.getWhoClicked();
        
        if(plugin.getConfig().getBoolean("permissions.allow_maps") == false
                || !p.hasPermission("bookshelf.maps"))
        {
            if(j.getCurrentItem().getType() == Material.MAP
                    || j.getCursor().getType() == Material.MAP
                    || j.getCurrentItem().getType() == Material.EMPTY_MAP
                    || j.getCursor().getType() == Material.EMPTY_MAP)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_book") == false
                || !p.hasPermission("bookshelf.book"))
        {
            if(j.getCurrentItem().getType() == Material.BOOK
                    || j.getCursor().getType() == Material.BOOK)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_enchanted_book") == false
                || !p.hasPermission("bookshelf.enchanted_book"))
        {
            if(j.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                    || j.getCursor().getType() == Material.ENCHANTED_BOOK)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_book_and_quill") == false
                || !p.hasPermission("bookshelf.baq"))
        {
            if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL
                    || j.getCursor().getType() == Material.BOOK_AND_QUILL)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_signed") == false
                || !p.hasPermission("bookshelf.signed"))
        {
            if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK
                    || j.getCursor().getType() == Material.WRITTEN_BOOK)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_records") == false
                || !p.hasPermission("bookshelf.records"))
        {
            if(BookShelfPlugin.records.contains(j.getCurrentItem().getType())
                    || BookShelfPlugin.records
                            .contains(j.getCursor().getType()))
            {
                j.setCancelled(true);
                return;
            }
        }
        if(plugin.getConfig().getBoolean("permissions.allow_paper") == false
                || !p.hasPermission("bookshelf.paper"))
        {
            if(j.getCurrentItem().getType() == Material.PAPER
                    || j.getCursor().getType() == Material.PAPER)
            {
                j.setCancelled(true);
                return;
            }
        }
        if(BookShelfPlugin.allowedItems.contains(j.getCurrentItem().getType()
                .name())
                || BookShelfPlugin.allowedItems.contains(j.getCursor()
                        .getType().name()))
        {
            return;
        }
        j.setCancelled(true);
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent j)
    {
        if(j.getBlock().getType() == Material.BOOKSHELF)
        {
            Location loc = j.getBlock().getLocation();
            plugin.getSQLManager().setAutoCommit(false);
            plugin.runQuery("INSERT INTO copy (x,y,z,bool) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                    + ", 0);");
            plugin.runQuery("INSERT INTO shop (x,y,z,bool,price) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                    + ", 0, "
                    + plugin.getConfig().getInt("economy.default_price") + ");");
            plugin.runQuery("INSERT INTO donate (x,y,z,bool) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ()
                    + ", 0);");
            plugin.runQuery("INSERT INTO names (x,y,z,name) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ", '"
                    + plugin.getConfig().getString("default_shelf_name")
                    + "');");
            plugin.runQuery("INSERT INTO owners (x,y,z,ownerString) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ", '"
                    + j.getPlayer().getName().toLowerCase() + "');");
            int def = 1;
            if(plugin.getConfig().getBoolean("default_openable"))
            {
                def = 1;
            }
            else
            {
                def = 0;
            }
            plugin.runQuery("INSERT INTO enable (x,y,z,bool) VALUES ("
                    + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ", "
                    + def + ");");
            plugin.getSQLManager().commit();
            plugin.getSQLManager().setAutoCommit(true);
            return;
        }
        if(j.getBlockAgainst().getType() == Material.BOOKSHELF)
        {
            if(j.isCancelled())
                return;
            if(j.getBlockAgainst().getFace(j.getBlock()) == BlockFace.UP
                    | j.getBlockAgainst().getFace(j.getBlock()) == BlockFace.DOWN)
            {
                if(!plugin.getConfig().getBoolean("top-bottom_access"))
                {
                    return;
                }
                else
                {
                    if(j.getPlayer().isSneaking())
                    {
                        return;
                    }
                    else
                    {
                        j.setCancelled(true);
                    }
                }
            }
            else
            {
                if(j.getPlayer().isSneaking())
                {
                    return;
                }
                else
                {
                    j.setCancelled(true);
                }
            }
            return;
        }
    }
    
    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent j)
    {
        if(j.getBlockClicked().getType() == Material.BOOKSHELF)
        {
            if(j.isCancelled())
                return;
            if(j.getBlockFace() == BlockFace.UP
                    | j.getBlockFace() == BlockFace.DOWN)
            {
                if(!plugin.getConfig().getBoolean("top-bottom_access"))
                {
                    return;
                }
                else
                {
                    if(j.getPlayer().isSneaking())
                    {
                        return;
                    }
                    else
                    {
                        j.setCancelled(true);
                    }
                }
            }
            else
            {
                if(j.getPlayer().isSneaking())
                {
                    return;
                }
                else
                {
                    j.setCancelled(true);
                }
            }
            return;
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent j)
    {
        Player p = j.getPlayer();
        if(plugin.getTargetBlock(p, 10).getType() == Material.BOOKSHELF)
        {
            if(j.isCancelled())
                return;
            if(BookShelfPlugin.allowedItems.contains(j.getItemDrop()
                    .getItemStack().getType().name()))
            {
                Location loc = plugin.getTargetBlock(p, 10).getLocation();
                
                try
                {
                    r = plugin.runQuery("SELECT * FROM copy WHERE x="
                            + loc.getX() + " AND y=" + loc.getY() + " AND z="
                            + loc.getZ() + ";");
                    if(r.next())
                        if(r.getInt("bool") == 1)
                        {
                            j.setCancelled(true);
                        }
                    close(r);
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
}
