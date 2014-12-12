package me.MitchT.BookShelf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import me.MitchT.BookShelf.Towny.TownyHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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
    public static BookShelf plugin;
    public static BookListener instance;
    
    public BookListener(BookShelf plugin)
    {
        BookListener.plugin = plugin;
        BookListener.instance = this;
    }
    
    private String author;
    private String title;
    private String[] pages;
    private Enchantment etype;
    private short mapdur = 0;
    private int elvl = 0;
    HashMap<Location, Inventory> map = new HashMap<Location, Inventory>();
    HashMap<Location, InventoryHolder> map2 = new HashMap<Location, InventoryHolder>();
    HashMap<Player, Location> map3 = new HashMap<Player, Location>();
    private String lore;
    private int damage;
    static ResultSet r;
    
    private void close(ResultSet r) throws SQLException
    {
        BookShelf.close(r);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(PlayerInteractEvent j)
    {
        Player p = j.getPlayer();
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
                        
                        Location loc = j.getClickedBlock().getLocation();
                        if(!plugin.getConfig().getBoolean("top-bottom_access"))
                        {
                            if(j.getBlockFace() == BlockFace.UP
                                    | j.getBlockFace() == BlockFace.DOWN)
                            {
                                return;
                            }
                        }
                        
                        if(!plugin.isShelfShop(loc)
                                && !plugin.isShelfUnlimited(loc)
                                && !plugin.isShelfDonate(loc))
                        {
                            if(!plugin.isOwner(loc, p)
                                    && !p.hasPermission("bookshelf.openshelf"))
                            {
                                p.sendMessage("§cYou are not allowed to open this shelf!");
                                return;
                            }
                        }
                        
                        try
                        {
                            if(plugin.isShelfShop(loc)
                                    && BookShelf.economy != null)
                            { //Is a shop and economy is enabled
                                if(BookShelf.useTowny)
                                {
                                    if(!TownyHandler.checkCanDoAction(j
                                            .getClickedBlock(), TownyHandler
                                            .convertToResident(j.getPlayer()),
                                            TownyHandler.OPEN_SHOP))
                                    {
                                        j.getPlayer()
                                                .sendMessage(
                                                        "§cYou are not allowed to open BookShops here!");
                                        j.setCancelled(true);
                                        return;
                                    }
                                }
                                if(BookShelf.worldGuard != null)
                                {
                                    RegionManager regionManager = BookShelf.worldGuard
                                            .getRegionManager(j.getPlayer()
                                                    .getWorld());
                                    if(regionManager != null)
                                    {
                                        ApplicableRegionSet set = regionManager
                                                .getApplicableRegions(j
                                                        .getClickedBlock()
                                                        .getLocation());
                                        if(set.size() > 0)
                                            if(!set.allows(
                                                    DefaultFlag.ENABLE_SHOP,
                                                    BookShelf.worldGuard.wrapPlayer(j
                                                            .getPlayer()))
                                                    && !set.isOwnerOfAll(BookShelf.worldGuard.wrapPlayer(j
                                                            .getPlayer()))
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
                                if(BookShelf.useTowny)
                                {
                                    if(!TownyHandler.checkCanDoAction(j
                                            .getClickedBlock(), TownyHandler
                                            .convertToResident(j.getPlayer()),
                                            TownyHandler.OPEN_SHELF))
                                    {
                                        j.getPlayer()
                                                .sendMessage(
                                                        "§cYou are not allowed to open BookShelves here!");
                                        j.setCancelled(true);
                                        return;
                                    }
                                }
                                if(BookShelf.useWorldGuard)
                                {
                                    RegionManager regionManager = BookShelf.worldGuard
                                            .getRegionManager(j.getPlayer()
                                                    .getWorld());
                                    if(regionManager != null)
                                    {
                                        ApplicableRegionSet set = regionManager
                                                .getApplicableRegions(j
                                                        .getClickedBlock()
                                                        .getLocation());
                                        if(set.size() > 0)
                                            if(!set.allows(
                                                    DefaultFlag.CHEST_ACCESS,
                                                    BookShelf.worldGuard.wrapPlayer(j
                                                            .getPlayer()))
                                                    && !set.isOwnerOfAll(BookShelf.worldGuard.wrapPlayer(j
                                                            .getPlayer()))
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
                            //							r = BookShelf.runQuery("SELECT * FROM display WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
                            //							if(!r.next())
                            //							{
                            //								close(r);
                            //								BookShelf.runQuery("INSERT INTO display (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+",0);");
                            //							}
                            //							else
                            //							{
                            //								if(r.getInt("bool") == 1)
                            //								{
                            //									close(r);
                            //									j.getPlayer().setItemInHand(new ItemStack(Material.WATER_BUCKET, 1));
                            //									j.useItemInHand();
                            //									return;	
                            //								}
                            //								else
                            //									close(r);
                            //							}
                            r = plugin.runQuery("SELECT * FROM enable WHERE x="
                                    + loc.getX() + " AND y=" + loc.getY()
                                    + " AND z=" + loc.getZ() + ";");
                            if(!r.next())
                            {
                                int def = 1;
                                close(r);
                                if(plugin.getConfig().getBoolean(
                                        "default_openable"))
                                {
                                    def = 1;
                                }
                                else
                                {
                                    def = 0;
                                }
                                plugin.runQuery("INSERT INTO enable (x,y,z,bool) VALUES ("
                                        + loc.getX()
                                        + ","
                                        + loc.getY()
                                        + ","
                                        + loc.getZ() + ", " + def + ");");
                                if(def == 0)
                                    return;
                            }
                            else
                            {
                                boolean open = r.getBoolean("bool");
                                close(r);
                                if(!open)
                                {
                                    return;
                                }
                            }
                        }
                        catch(SQLException e1)
                        {
                            e1.printStackTrace();
                        }
                        if(!map.containsKey(j.getClickedBlock().getLocation()))
                        {
                            String name = plugin.getConfig().getString(
                                    "default_shelf_name");
                            try
                            {
                                r = plugin
                                        .runQuery("SELECT * FROM names WHERE x="
                                                + loc.getX()
                                                + " AND y="
                                                + loc.getY()
                                                + " AND z="
                                                + loc.getZ() + ";");
                                if(r.next())
                                    name = r.getString("name");
                                close(r);
                            }
                            catch(SQLException e1)
                            {
                                e1.printStackTrace();
                            }
                            Inventory inv = Bukkit.createInventory(p, plugin
                                    .getConfig().getInt("rows") * 9, name);
                            Block cl = j.getClickedBlock();
                            int x = cl.getX();
                            int y = cl.getY();
                            int z = cl.getZ();
                            map3.put(j.getPlayer(), loc);
                            
                            try
                            {
                                boolean isOwner = plugin.isOwner(loc,
                                        j.getPlayer());
                                boolean isOwnerEditing = (isOwner && BookShelf.editingPlayers
                                        .contains(j.getPlayer()));
                                if(!plugin.isShelfUnlimited(loc)
                                        || isOwnerEditing)
                                {
                                    map.put(cl.getLocation(), inv);
                                    map2.put(cl.getLocation(), inv.getHolder());
                                }
                                r = plugin
                                        .runQuery("SELECT COUNT(*) FROM items WHERE x="
                                                + x
                                                + " AND y="
                                                + y
                                                + " AND z="
                                                + z + ";");
                                if(!r.next())
                                {
                                    close(r);
                                    p.openInventory(inv);
                                    return;
                                }
                                else
                                {
                                    close(r);
                                    r = plugin
                                            .runQuery("SELECT * FROM items WHERE x="
                                                    + x
                                                    + " AND y="
                                                    + y
                                                    + " AND z=" + z + ";");
                                    ArrayList<String> auth = new ArrayList<String>();
                                    ArrayList<String> titl = new ArrayList<String>();
                                    ArrayList<String> type = new ArrayList<String>();
                                    ArrayList<Integer> id = new ArrayList<Integer>();
                                    ArrayList<Integer> loca = new ArrayList<Integer>();
                                    ArrayList<Integer> amt = new ArrayList<Integer>();
                                    ArrayList<String> lore = new ArrayList<String>();
                                    ArrayList<Integer> dmg = new ArrayList<Integer>();
                                    ArrayList<String> pages = new ArrayList<String>();
                                    
                                    while(r.next())
                                    {
                                        auth.add(r.getString("author"));
                                        titl.add(r.getString("title"));
                                        id.add(r.getInt("id"));
                                        type.add(r.getString("enumType"));
                                        loca.add(r.getInt("loc"));
                                        amt.add(r.getInt("amt"));
                                        lore.add(r.getString("lore"));
                                        dmg.add(r.getInt("damage"));
                                        pages.add(r.getString("pages"));
                                    }
                                    close(r);
                                    for(int i = 0; i < id.size(); i++)
                                    {
                                        if(type.get(i).equals(
                                                Material.MAP.name()))
                                        {
                                            r = plugin
                                                    .runQuery("SELECT * FROM maps WHERE x="
                                                            + x
                                                            + " AND y="
                                                            + y
                                                            + " AND z="
                                                            + z
                                                            + " AND loc="
                                                            + loca.get(i) + ";");
                                            while(r.next())
                                            {
                                                mapdur = r
                                                        .getShort("durability");
                                            }
                                            close(r);
                                            inv.setItem(loca.get(i),
                                                    generateItemStack(3));
                                        }
                                        else if(type.get(i).equals(
                                                Material.ENCHANTED_BOOK.name()))
                                        {
                                            r = plugin
                                                    .runQuery("SELECT * FROM enchant WHERE x="
                                                            + x
                                                            + " AND y="
                                                            + y
                                                            + " AND z="
                                                            + z
                                                            + " AND loc="
                                                            + loca.get(i) + ";");
                                            String enchant = "";
                                            while(r.next())
                                            {
                                                enchant = r.getString("type");
                                                elvl = r.getInt("level");
                                            }
                                            close(r);
                                            etype = Enchantment
                                                    .getByName(enchant);
                                            inv.setItem(loca.get(i),
                                                    generateItemStack(2));
                                        }
                                        else if(type.get(i).equals(
                                                Material.WRITTEN_BOOK.name())
                                                || type.get(i).equals(
                                                        Material.BOOK_AND_QUILL
                                                                .name()))
                                        {
                                            String[] thepages = pages.get(i)
                                                    .split("¬");
                                            if(type.get(i).equals(
                                                    Material.WRITTEN_BOOK
                                                            .name()))
                                            {
                                                Book(titl.get(i), auth.get(i),
                                                        thepages, lore.get(i),
                                                        dmg.get(i));
                                                inv.setItem(loca.get(i),
                                                        generateItemStack(0));
                                            }
                                            else if(type.get(i).equals(
                                                    Material.BOOK_AND_QUILL
                                                            .name()))
                                            {
                                                Book(titl.get(i), auth.get(i),
                                                        thepages, lore.get(i),
                                                        dmg.get(i));
                                                inv.setItem(loca.get(i),
                                                        generateItemStack(1));
                                            }
                                        }
                                        else if(BookShelf.allowedItems
                                                .contains(type.get(i)))
                                        {
                                            inv.setItem(
                                                    loca.get(i),
                                                    new ItemStack(Material
                                                            .getMaterial(type
                                                                    .get(i)),
                                                            amt.get(i)));
                                        }
                                    }
                                    auth.clear();
                                    titl.clear();
                                    type.clear();
                                    id.clear();
                                    loca.clear();
                                    amt.clear();
                                    p.openInventory(inv);
                                    
                                    if(plugin.autoToggle)
                                    {
                                        String shelfName = name;
                                        if(shelfName.endsWith(" "))
                                            shelfName = shelfName.substring(0,
                                                    shelfName.length() - 1);
                                        if(plugin.autoToggleNameList == null
                                                || plugin.autoToggleNameList
                                                        .contains(shelfName))
                                        {
                                            if(!plugin.autoToggleMap1
                                                    .containsKey(loc))
                                            {
                                                plugin.autoToggleMap1.put(loc,
                                                        1);
                                                List<Player> list = new ArrayList<Player>();
                                                list.add(p);
                                                plugin.autoToggleMap2.put(loc,
                                                        list);
                                            }
                                            else
                                            {
                                                if(!plugin.autoToggleDiffPlayers)
                                                {
                                                    int old = plugin.autoToggleMap1
                                                            .get(loc);
                                                    plugin.autoToggleMap1
                                                            .remove(loc);
                                                    plugin.autoToggleMap1.put(
                                                            loc, old + 1);
                                                }
                                                else if(!plugin.autoToggleMap2
                                                        .get(loc).contains(p))
                                                {
                                                    int old = plugin.autoToggleMap1
                                                            .get(loc);
                                                    plugin.autoToggleMap1
                                                            .remove(loc);
                                                    plugin.autoToggleMap1.put(
                                                            loc, old + 1);
                                                    plugin.autoToggleMap2.get(
                                                            loc).add(p);
                                                }
                                            }
                                            if(plugin.autoToggleMap1.get(loc) >= plugin.autoToggleFreq)
                                            {
                                                plugin.autoToggleMap1
                                                        .remove(loc);
                                                plugin.autoToggleMap2
                                                        .remove(loc);
                                                if(plugin.autoToggleServerWide)
                                                {
                                                    plugin
                                                            .toggleBookShelvesByName(name);
                                                    if(!name.endsWith(" "))
                                                        name += " ";
                                                    System.out
                                                            .println("(Auto Toggle) All bookshelves with the name "
                                                                    + name
                                                                    + "have been toggled.");
                                                }
                                                else
                                                {
                                                    plugin
                                                            .toggleBookShelf(loc);
                                                    System.out
                                                            .println("(Auto Toggle) The bookshelf at ("
                                                                    + loc.getBlockX()
                                                                    + ", "
                                                                    + loc.getBlockY()
                                                                    + ", "
                                                                    + loc.getBlockZ()
                                                                    + ") has been toggled.");
                                                }
                                            }
                                        }
                                    }
                                    
                                }
                            }
                            catch(SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Inventory inv = map.get(j.getClickedBlock()
                                    .getLocation());
                            if(inv.getViewers().isEmpty())
                                return;
                            Player player = (Player) inv.getViewers().get(0);
                            map3.put(j.getPlayer(), loc);
                            if(player.getName() == p.getName())
                            {
                                j.setCancelled(true);
                            }
                            else
                            {
                                p.openInventory(inv);
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
    public void onAdd(InventoryCloseEvent u)
    {
        
        final InventoryCloseEvent j = u;
        if(!map2.containsValue(j.getInventory().getHolder()))
        {
            return;
        }
        if(j.getViewers().size() > 1)
        {
            return;
        }
        if(j.getViewers().get(0) != j.getPlayer())
        {
            return;
        }
        Location loc = getKey(map2, j.getInventory().getHolder());
        map.remove(loc);
        map2.remove(loc);
        map3.remove(j.getPlayer());
        ItemStack[] cont = j.getInventory().getContents();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        boolean isOwner = plugin.isOwner(loc, (Player) j.getPlayer());
        boolean isOwnerEditing = (isOwner && BookShelf.editingPlayers
                .contains(j.getPlayer()));
        if(!plugin.isShelfUnlimited(loc) || isOwnerEditing)
        {
            BookShelf.getSQLManager().setAutoCommit(false);
            plugin.runQuery("DELETE FROM items WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            plugin.runQuery("DELETE FROM enchant WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            plugin.runQuery("DELETE FROM maps WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            for(int i = 0; i < cont.length; i++)
            {
                if(cont[i] != null)
                {
                    String type = cont[i].getType().name();
                    if(cont[i].getType() == Material.BOOK_AND_QUILL
                            || cont[i].getType() == Material.WRITTEN_BOOK)
                    {
                        Book(cont[i]);
                        String title = getTitle().replaceAll("'", "''");
                        String author = getAuthor().replaceAll("'", "''");
                        String lore = "";
                        if(getLore() != null)
                            lore = getLore().replaceAll("'", "''");
                        int damage = getDamage();
                        String pageString = "";
                        if(getPages() != null)
                        {
                            for(int k = 0; k < getPages().length; k++)
                            {
                                pageString += getPages()[k].replaceAll("'",
                                        "''") + "¬";
                            }
                            if(pageString.endsWith("¬"))
                                pageString = pageString.substring(0,
                                        pageString.length() - 1);
                        }
                        plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt,lore,damage,pages) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ",'"
                                + author
                                + "','"
                                + title
                                + "','"
                                + type
                                + "',"
                                + i
                                + ",1,'"
                                + lore
                                + "', "
                                + damage
                                + ", '"
                                + pageString
                                + "');");
                    }
                    else if(cont[i].getType() == Material.ENCHANTED_BOOK)
                    {
                        plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ", 'null', 'null','"
                                + type
                                + "',"
                                + i
                                + ","
                                + cont[i].getAmount()
                                + ");");
                        EnchantmentStorageMeta book = (EnchantmentStorageMeta) cont[i]
                                .getItemMeta();
                        Map<Enchantment, Integer> enchants = book
                                .getStoredEnchants();
                        Enchantment enchant = null;
                        for(Enchantment key : enchants.keySet())
                        {
                            enchant = key;
                        }
                        Integer lvl = book.getStoredEnchantLevel(enchant);
                        String type2 = enchant.getName();
                        plugin.runQuery("INSERT INTO enchant (x,y,z,loc,type,level) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ","
                                + i
                                + ",'"
                                + type2 + "','" + lvl + "');");
                    }
                    else if(cont[i].getType() == Material.MAP)
                    {
                        ItemStack mapp = cont[i];
                        int dur = mapp.getDurability();
                        plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ", 'null', 'null','"
                                + type
                                + "',"
                                + i
                                + ","
                                + cont[i].getAmount()
                                + ");");
                        plugin.runQuery("INSERT INTO maps (x,y,z,loc,durability) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ","
                                + i
                                + ",'"
                                + dur
                                + "');");
                    }
                    else if(BookShelf.allowedItems.contains(cont[i].getType()
                            .name()))
                    {
                        plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                                + x
                                + ","
                                + y
                                + ","
                                + z
                                + ", 'null', 'null','"
                                + type
                                + "',"
                                + i
                                + ","
                                + cont[i].getAmount()
                                + ");");
                    }
                }
            }
            BookShelf.getSQLManager().commit();
            BookShelf.getSQLManager().setAutoCommit(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent j)
    {
        if(j.isCancelled())
            return;
        if(j.getBlock().getType() == Material.BOOKSHELF)
            breakShelf(j.getBlock().getLocation(), true);
    }
    
    @EventHandler
    public void onBurn(BlockBurnEvent j)
    {
        if(j.isCancelled())
            return;
        if(j.getBlock().getType() == Material.BOOKSHELF)
        {
            if(!BookShelf.config.getBoolean("shelves_can_burn"))
            {
                j.setCancelled(true);
                return;
            }
            breakShelf(j.getBlock().getLocation(), true);
        }
    }
    
    public void breakShelf(Location loc, boolean dropItems)
    {
        if(map.containsKey(loc))
        {
            Inventory inv = map.get(loc);
            List<HumanEntity> viewers = inv.getViewers();
            for(int i = 0; i < viewers.size(); i++)
            {
                viewers.get(i).closeInventory();
            }
        }
        try
        {
            r = plugin.runQuery("SELECT * FROM items WHERE x="
                    + loc.getBlockX() + " AND y=" + loc.getBlockY() + " AND z="
                    + loc.getBlockZ() + ";");
            ArrayList<String> auth = new ArrayList<String>();
            ArrayList<String> titl = new ArrayList<String>();
            ArrayList<String> type = new ArrayList<String>();
            ArrayList<Integer> id = new ArrayList<Integer>();
            ArrayList<Integer> amt = new ArrayList<Integer>();
            ArrayList<Integer> loca = new ArrayList<Integer>();
            ArrayList<String> lore = new ArrayList<String>();
            ArrayList<Integer> dmg = new ArrayList<Integer>();
            ArrayList<String> pages = new ArrayList<String>();
            while(r.next())
            {
                auth.add(r.getString("author"));
                titl.add(r.getString("title"));
                id.add(r.getInt("id"));
                type.add(r.getString("enumType"));
                amt.add(r.getInt("amt"));
                loca.add(r.getInt("loc"));
                lore.add(r.getString("lore"));
                dmg.add(r.getInt("damage"));
                pages.add(r.getString("pages"));
            }
            close(r);
            String enchant = "";
            BookShelf.getSQLManager().setAutoCommit(false);
            for(int i = 0; i < id.size(); i++)
            {
                if(type.get(i).equals(Material.ENCHANTED_BOOK.name()))
                {
                    r = plugin.runQuery("SELECT * FROM enchant WHERE x="
                            + loc.getBlockX() + " AND y=" + loc.getBlockY()
                            + " AND z=" + loc.getBlockZ() + " AND loc="
                            + loca.get(i) + ";");
                    while(r.next())
                    {
                        enchant = r.getString("type");
                        elvl = r.getInt("level");
                    }
                    close(r);
                    plugin.runQuery("DELETE FROM items WHERE id=" + id.get(i)
                            + ";");
                    etype = Enchantment.getByName(enchant);
                    if(dropItems)
                        dropItem(generateItemStack(2).clone(), loc);
                }
                else if(type.get(i).equals(Material.MAP.name()))
                {
                    r = plugin.runQuery("SELECT * FROM maps WHERE x="
                            + loc.getBlockX() + " AND y=" + loc.getBlockY()
                            + " AND z=" + loc.getBlockZ() + " AND loc="
                            + loca.get(i) + ";");
                    while(r.next())
                    {
                        mapdur = r.getShort("durability");
                    }
                    close(r);
                    plugin.runQuery("DELETE FROM items WHERE id=" + id.get(i)
                            + ";");
                    if(dropItems)
                        dropItem(generateItemStack(3).clone(), loc);
                }
                else if(type.get(i).equals(Material.WRITTEN_BOOK.name())
                        || type.get(i).equals(Material.BOOK_AND_QUILL.name()))
                {
                    String[] thepages = pages.get(i).split("¬");
                    
                    if(type.get(i).equals(Material.WRITTEN_BOOK.name()))
                    {
                        Book(titl.get(i), auth.get(i), thepages, lore.get(i),
                                dmg.get(i));
                        if(dropItems)
                            dropItem(generateItemStack(0).clone(), loc);
                    }
                    else if(type.get(i).equals(Material.BOOK_AND_QUILL.name()))
                    {
                        Book("null", "null", thepages, lore.get(i), dmg.get(i));
                        if(dropItems)
                            dropItem(generateItemStack(1).clone(), loc);
                    }
                    plugin.runQuery("DELETE FROM items WHERE id=" + id.get(i)
                            + ";");
                }
                else if(BookShelf.allowedItems.contains(type.get(i)))
                {
                    plugin.runQuery("DELETE FROM items WHERE id=" + id.get(i)
                            + ";");
                    ItemStack stack = new ItemStack(Material.getMaterial(type
                            .get(i)));
                    stack.setAmount(amt.get(i));
                    if(dropItems)
                        dropItem(stack, loc);
                }
            }
            plugin.runQuery("DELETE FROM copy WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM shop WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM donate WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM names WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM enable WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM enchant WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM maps WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            plugin.runQuery("DELETE FROM owners WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            //BookShelf.runQuery("DELETE FROM display WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
            BookShelf.getSQLManager().commit();
            BookShelf.getSQLManager().setAutoCommit(true);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        if(plugin.autoToggle)
        {
            if(plugin.autoToggleMap1.containsKey(loc))
            {
                plugin.autoToggleMap1.remove(loc);
                plugin.autoToggleMap2.remove(loc);
            }
        }
    }
    
    @EventHandler
    public void onInv(InventoryClickEvent j)
    {
        if((j.getInventory().getType() == InventoryType.CHEST || j
                .getInventory().getType() == InventoryType.ENDER_CHEST)
                && !map3.containsKey((Player) j.getWhoClicked()))
        {
            if(j.getCurrentItem() != null)
            {
                String prefix = "shelf_only_items.";
                Player p = (Player) j.getWhoClicked();
                if(BookShelf.config.getBoolean(prefix + "book"))
                {
                    if(j.getCurrentItem().getType() == Material.BOOK
                            || j.getCursor().getType() == Material.BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cBooks may only be stored in bookshelves.");
                        return;
                    }
                }
                if(BookShelf.config.getBoolean(prefix + "book_and_quill"))
                {
                    if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL
                            || j.getCursor().getType() == Material.BOOK_AND_QUILL)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cBook and Quills may only be stored in bookshelves.");
                        return;
                    }
                }
                if(BookShelf.config.getBoolean(prefix + "signed"))
                {
                    if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK
                            || j.getCursor().getType() == Material.WRITTEN_BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cSigned Books may only be stored in bookshelves.");
                        return;
                    }
                }
                if(BookShelf.config.getBoolean(prefix + "maps"))
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
                if(BookShelf.config.getBoolean(prefix + "enchanted_book"))
                {
                    if(j.getCurrentItem().getType() == Material.ENCHANTED_BOOK
                            || j.getCursor().getType() == Material.ENCHANTED_BOOK)
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cEnchanted Books may only be stored in bookshelves.");
                        return;
                    }
                }
                if(BookShelf.config.getBoolean(prefix + "records"))
                {
                    if(BookShelf.records.contains(j.getCurrentItem().getType())
                            || BookShelf.records.contains(j.getCursor()
                                    .getType()))
                    {
                        j.setCancelled(true);
                        p.sendMessage("§cRecords may only be stored in bookshelves.");
                        return;
                    }
                }
                if(BookShelf.config.getBoolean(prefix + "paper"))
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
        String name = null;
        if(!map3.containsKey((Player) j.getWhoClicked()))
            return;
        Location loc = map3.get((Player) j.getWhoClicked());
        try
        {
            r = plugin.runQuery("SELECT * FROM names WHERE x=" + loc.getX()
                    + " AND y=" + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(r.next())
                name = r.getString("name");
            close(r);
        }
        catch(SQLException e1)
        {
            e1.printStackTrace();
        }
        if(j.getInventory().getTitle().equals(name))
        {
            boolean isOwner = plugin
                    .isOwner(loc, (Player) j.getWhoClicked());
            boolean isOwnerEditing = (plugin.isOwner(loc,
                    (Player) j.getWhoClicked()) && BookShelf.editingPlayers
                    .contains((Player) j.getWhoClicked()));
            if((isOwner && !plugin.isShelfShop(loc) && !plugin
                    .isShelfUnlimited(loc)) || isOwnerEditing)
            {
                this.checkAllowed(j);
                return;
            }
            if(!plugin.isShelfShop(loc) || BookShelf.economy == null)
            {
                if(!plugin.isShelfUnlimited(loc))
                {
                    if(plugin.isShelfDonate(loc))
                    {
                        int slotamt = (plugin.getConfig().getInt("rows") * 9) - 1;
                        if(j.getRawSlot() > slotamt)
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
                    int slotamt = (plugin.getConfig().getInt("rows") * 9) - 1;
                    if(j.getRawSlot() <= slotamt)
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
                int price = plugin.getShopPrice(loc);
                int slotamt = (plugin.getConfig().getInt("rows") * 9) - 1;
                if(j.getRawSlot() <= slotamt)
                {
                    if(j.getCurrentItem().getType() == Material.AIR)
                    {
                        j.setCancelled(true);
                        return;
                    }
                    Player p = (Player) j.getWhoClicked();
                    
                    double money = BookShelf.economy.getBalance(p);
                    if(money >= price)
                    {
                        BookShelf.economy.withdrawPlayer(p, price);
                        p.sendMessage("New balance: §6"
                                + BookShelf.economy.getBalance(p) + " "
                                + BookShelf.economy.currencyNamePlural());
                        return;
                    }
                    p.sendMessage("§cInsufficient funds! Current balance: §6"
                            + BookShelf.economy.getBalance(p) + " "
                            + BookShelf.economy.currencyNamePlural());
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
            if(BookShelf.records.contains(j.getCurrentItem().getType())
                    || BookShelf.records.contains(j.getCursor().getType()))
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
        if(BookShelf.allowedItems.contains(j.getCurrentItem().getType().name())
                || BookShelf.allowedItems.contains(j.getCursor().getType()
                        .name()))
        {
            return;
        }
        j.setCancelled(true);
    }
    
    private void dropItem(ItemStack item, Location loc)
    {
        Random gen = new Random();
        double xs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        loc.getWorld().dropItem(
                new Location(loc.getWorld(), loc.getX() + xs, loc.getY() + ys,
                        loc.getZ() + zs), item);
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent j)
    {
        if(j.getBlock().getType() == Material.BOOKSHELF)
        {
            Location loc = j.getBlock().getLocation();
            BookShelf.getSQLManager().setAutoCommit(false);
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
            //BookShelf.runQuery("INSERT INTO display (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0);");
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
            BookShelf.getSQLManager().commit();
            BookShelf.getSQLManager().setAutoCommit(true);
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
            if(BookShelf.allowedItems.contains(j.getItemDrop().getItemStack()
                    .getType().name()))
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
    
    public static <T, E> T getKeyByValue(Map<T, E> map, E value)
    {
        for(Entry<T, E> entry : map.entrySet())
        {
            if(value.equals(entry.getValue()))
            {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void Book(ItemStack bookItem)
    {
        BookMeta bookData = (BookMeta) bookItem.getItemMeta();
        if(bookItem.getType() == Material.WRITTEN_BOOK)
        {
            this.author = bookData.getAuthor();
            if(bookData.hasDisplayName())
            {
                this.title = bookData.getDisplayName();
            }
            else
            {
                this.title = bookData.getTitle();
            }
        }
        else
        {
            this.author = "null";
            if(bookData.hasDisplayName())
            {
                this.title = bookData.getDisplayName();
            }
            else
            {
                this.title = "null";
            }
        }
        List<String> nPages;
        nPages = bookData.getPages();
        String[] sPages = null;
        if(nPages.size() > 0)
        {
            sPages = new String[nPages.size()];
            for(int i = 0; i < nPages.size(); i++)
            {
                sPages[i] = nPages.get(i).toString();
            }
        }
        else
        {
            sPages = new String[1];
            sPages[0] = "";
        }
        
        this.pages = sPages;
        
        if(bookData.getLore() != null)
            this.lore = bookData.getLore().get(0);
        else
            this.lore = null;
        this.damage = bookItem.getDurability();
        
    }
    
    void Book(String title, String author, String[] pages, String lore,
            int damage)
    {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.lore = lore;
        this.damage = damage;
    }
    
    public String getAuthor()
    {
        return author;
    }
    
    public void setAuthor(String sAuthor)
    {
        author = sAuthor;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String[] getPages()
    {
        return pages;
    }
    
    public String getLore()
    {
        return lore;
    }
    
    public int getDamage()
    {
        return damage;
    }
    
    public ItemStack generateItemStack(int type)
    {
        switch(type)
        {
            case 0:
                ItemStack written_book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta new_written_book = (BookMeta) written_book
                        .getItemMeta();
                
                new_written_book.setAuthor(author);
                new_written_book.setTitle(title);
                new_written_book.setDisplayName(title);
                if(lore != null && !lore.equals(""))
                    new_written_book.setLore(Arrays.asList(lore));
                if(pages != null)
                {
                    for(int i = 0; i < pages.length; i++)
                    {
                        new_written_book.addPage(pages[i]);
                    }
                }
                else
                {
                    new_written_book.addPage("");
                }
                written_book.setItemMeta(new_written_book);
                written_book.setDurability((short) damage);
                return written_book;
            case 1:
                ItemStack baq = new ItemStack(Material.BOOK_AND_QUILL);
                BookMeta newbaq = (BookMeta) baq.getItemMeta();
                
                newbaq.setAuthor(author);
                newbaq.setTitle(title);
                if(lore != null && !lore.equals(""))
                    newbaq.setLore(Arrays.asList(lore));
                if(!title.equals("null"))
                {
                    newbaq.setDisplayName(title);
                }
                if(pages != null)
                {
                    for(int i = 0; i < pages.length; i++)
                    {
                        newbaq.addPage(pages[i]);
                    }
                }
                else
                {
                    newbaq.addPage("");
                }
                
                baq.setItemMeta(newbaq);
                baq.setDurability((short) damage);
                return baq;
            case 2:
                ItemStack enchanted_book = new ItemStack(
                        Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta new_enchanted_book = (EnchantmentStorageMeta) enchanted_book
                        .getItemMeta();
                
                new_enchanted_book.addStoredEnchant(etype, elvl, false);
                enchanted_book.setItemMeta(new_enchanted_book);
                return enchanted_book;
            case 3:
                ItemStack map = new ItemStack(Material.MAP);
                
                map.setDurability(mapdur);
                return map;
        }
        return null;
    }
    
}
