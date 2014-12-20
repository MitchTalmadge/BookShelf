package me.MitchT.BookShelf.Shelves;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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
public class BookShelf implements InventoryHolder
{
    private BookShelfPlugin plugin;
    
    private Location shelfLocation;
    
    private ArrayList<ShelfType> shelfTypes;
    private boolean shelfEnabled = false;
    private String shelfName = "";
    private Inventory shelfInventory;
    private int shopPrice = 0;
    private ArrayList<String> shelfOwners;
    
    public BookShelf(Location shelfLocation)
    {
        this.plugin = BookShelfPlugin.getInstance();
        this.shelfLocation = shelfLocation;
        loadShelf();
    }
    
    private void loadShelf()
    {
        shelfTypes = new ArrayList<ShelfType>();
        shelfOwners = new ArrayList<String>();
        try
        {
            //Check Unlimited
            ResultSet r = plugin.runQuery("SELECT bool FROM copy WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO copy (x,y,z,bool) VALUES ("
                        + shelfLocation.getX() + "," + shelfLocation.getY()
                        + "," + shelfLocation.getZ() + ",0);");
            }
            else
            {
                if(r.getInt("bool") == 1)
                    shelfTypes.add(ShelfType.UNLIMITED);
                plugin.close(r);
            }
            
            //Check Shop
            r = plugin.runQuery("SELECT * FROM shop WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO shop (x,y,z,bool,price) VALUES ("
                        + shelfLocation.getX() + "," + shelfLocation.getY()
                        + "," + shelfLocation.getZ() + ",0,10);");
            }
            else
            {
                if(r.getInt("bool") == 1)
                {
                    shelfTypes.add(ShelfType.SHOP);
                    shopPrice = r.getInt("price");
                }
                plugin.close(r);
            }
            
            //Check Donate
            r = plugin.runQuery("SELECT * FROM donate WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO donate (x,y,z,bool) VALUES ("
                        + shelfLocation.getX() + "," + shelfLocation.getY()
                        + "," + shelfLocation.getZ() + ",0);");
            }
            else
            {
                if(r.getInt("bool") == 1)
                    shelfTypes.add(ShelfType.DONATION);
                plugin.close(r);
            }
            
            //Check Enabled
            r = plugin.runQuery("SELECT bool FROM enable WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(!r.next())
            {
                int enabled = 1;
                plugin.close(r);
                if(plugin.getConfig().getBoolean("default_openable"))
                {
                    enabled = 1;
                }
                else
                {
                    enabled = 0;
                }
                plugin.runQuery("INSERT INTO enable (x,y,z,bool) VALUES ("
                        + shelfLocation.getX() + "," + shelfLocation.getY()
                        + "," + shelfLocation.getZ() + ", " + enabled + ");");
                this.shelfEnabled = (enabled == 1) ? true : false;
            }
            else
            {
                int enabled = r.getInt("bool");
                plugin.close(r);
                this.shelfEnabled = (enabled == 1) ? true : false;
            }
            
            //Check Name
            r = plugin.runQuery("SELECT name FROM names WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(!r.next())
            {
                this.shelfName = plugin.getConfig().getString(
                        "default_shelf_name");
            }
            else
            {
                this.shelfName = r.getString("name");
            }
            plugin.close(r);
            
            //Check Owners
            r = plugin.runQuery("SELECT * FROM owners WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            if(r.next())
            {
                String ownerString = r.getString("ownerString").toLowerCase();
                
                String[] splitOwners = ownerString.split("§");
                for(String owner : splitOwners)
                {
                    this.shelfOwners.add(owner);
                }
            }
            plugin.close(r);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean isEnabled()
    {
        return this.shelfEnabled;
    }
    
    public boolean isShelfType(ShelfType type)
    {
        return this.shelfTypes.contains(type);
    }
    
    public Location getLocation()
    {
        return this.shelfLocation;
    }
    
    public String getName()
    {
        return this.shelfName;
    }
    
    public int getPrice()
    {
        return this.shopPrice;
    }
    
    public Inventory generateVirtualInventory()
    {
        VirtualBookShelf owner = new VirtualBookShelf(this);
        owner.setInventory(getInventory(owner));
        return owner.getInventory();
    }
    
    public Inventory getInventory()
    {
        return getInventory(null);
    }
    
    private Inventory getInventory(VirtualBookShelf owner)
    {
        if(owner != null || this.shelfInventory == null)
        {
            Inventory inventory = Bukkit.createInventory(
                    (owner != null) ? owner : this,
                    plugin.getConfig().getInt("rows") * 9, this.shelfName);
            
            if(owner == null)
                this.shelfInventory = inventory;
            
            try
            {
                ResultSet r = plugin
                        .runQuery("SELECT COUNT(*) FROM items WHERE x="
                                + shelfLocation.getX() + " AND y="
                                + shelfLocation.getY() + " AND z="
                                + shelfLocation.getZ() + ";");
                if(!r.next())
                {
                    plugin.close(r);
                    return inventory;
                }
                else
                {
                    plugin.close(r);
                    r = plugin.runQuery("SELECT * FROM items WHERE x="
                            + shelfLocation.getX() + " AND y="
                            + shelfLocation.getY() + " AND z="
                            + shelfLocation.getZ() + ";");
                    ArrayList<String> itemAuthor = new ArrayList<String>();
                    ArrayList<String> itemTitle = new ArrayList<String>();
                    ArrayList<String> itemType = new ArrayList<String>();
                    ArrayList<Integer> itemID = new ArrayList<Integer>();
                    ArrayList<Integer> itemInvLocation = new ArrayList<Integer>();
                    ArrayList<Integer> itemAmount = new ArrayList<Integer>();
                    ArrayList<String> itemLore = new ArrayList<String>();
                    ArrayList<Integer> itemDamage = new ArrayList<Integer>();
                    ArrayList<String> itemPages = new ArrayList<String>();
                    
                    while(r.next())
                    {
                        itemAuthor.add(r.getString("author"));
                        itemTitle.add(r.getString("title"));
                        itemID.add(r.getInt("id"));
                        itemType.add(r.getString("enumType"));
                        itemInvLocation.add(r.getInt("loc"));
                        itemAmount.add(r.getInt("amt"));
                        itemLore.add(r.getString("lore"));
                        itemDamage.add(r.getInt("damage"));
                        itemPages.add(r.getString("pages"));
                    }
                    plugin.close(r);
                    for(int i = 0; i < itemID.size(); i++)
                    {
                        if(itemType.get(i).equals(Material.MAP.name()))
                        {
                            r = plugin.runQuery("SELECT * FROM maps WHERE x="
                                    + shelfLocation.getX() + " AND y="
                                    + shelfLocation.getY() + " AND z="
                                    + shelfLocation.getZ() + " AND loc="
                                    + itemInvLocation.get(i) + ";");
                            short mapDurability = 0;
                            while(r.next())
                            {
                                mapDurability = r.getShort("durability");
                            }
                            plugin.close(r);
                            inventory.setItem(itemInvLocation.get(i),
                                    ItemGenerator.generateMap(mapDurability));
                        }
                        else if(itemType.get(i).equals(
                                Material.ENCHANTED_BOOK.name()))
                        {
                            r = plugin
                                    .runQuery("SELECT * FROM enchant WHERE x="
                                            + shelfLocation.getX() + " AND y="
                                            + shelfLocation.getY() + " AND z="
                                            + shelfLocation.getZ()
                                            + " AND loc="
                                            + itemInvLocation.get(i) + ";");
                            String enchantType = "";
                            Enchantment enchantment;
                            int enchantLevel = 0;
                            while(r.next())
                            {
                                enchantType = r.getString("type");
                                enchantLevel = r.getInt("level");
                            }
                            plugin.close(r);
                            enchantment = Enchantment.getByName(enchantType);
                            inventory.setItem(itemInvLocation.get(i),
                                    ItemGenerator.generateEnchantedBook(
                                            enchantment, enchantLevel));
                        }
                        else if(itemType.get(i).equals(
                                Material.WRITTEN_BOOK.name())
                                || itemType.get(i).equals(
                                        Material.BOOK_AND_QUILL.name()))
                        {
                            String[] splitPages = itemPages.get(i).split("¬");
                            if(itemType.get(i).equals(
                                    Material.WRITTEN_BOOK.name()))
                            {
                                inventory.setItem(itemInvLocation.get(i),
                                        ItemGenerator.generateWrittenBook(
                                                itemAuthor.get(i),
                                                itemTitle.get(i),
                                                itemLore.get(i), splitPages,
                                                itemDamage.get(i)));
                            }
                            else if(itemType.get(i).equals(
                                    Material.BOOK_AND_QUILL.name()))
                            {
                                inventory.setItem(itemInvLocation.get(i),
                                        ItemGenerator.generateBookAndQuill(
                                                itemAuthor.get(i),
                                                itemTitle.get(i),
                                                itemLore.get(i), splitPages,
                                                itemDamage.get(i)));
                            }
                        }
                        else if(BookShelfPlugin.allowedItems.contains(itemType
                                .get(i)))
                        {
                            inventory.setItem(
                                    itemInvLocation.get(i),
                                    new ItemStack(Material.getMaterial(itemType
                                            .get(i)), itemAmount.get(i)));
                        }
                    }
                }
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
            return inventory;
        }
        return this.shelfInventory;
    }
    
    public void saveInventory()
    {
        ItemStack[] shelfContents = this.shelfInventory.getContents();
        
        plugin.getSQLManager().setAutoCommit(false);
        plugin.runQuery("DELETE FROM items WHERE x=" + shelfLocation.getX()
                + " AND y=" + shelfLocation.getY() + " AND z="
                + shelfLocation.getZ() + ";");
        plugin.runQuery("DELETE FROM enchant WHERE x=" + shelfLocation.getX()
                + " AND y=" + shelfLocation.getY() + " AND z="
                + shelfLocation.getZ() + ";");
        plugin.runQuery("DELETE FROM maps WHERE x=" + shelfLocation.getX()
                + " AND y=" + shelfLocation.getY() + " AND z="
                + shelfLocation.getZ() + ";");
        for(int i = 0; i < shelfContents.length; i++)
        {
            if(shelfContents[i] != null)
            {
                String type = shelfContents[i].getType().name();
                if(shelfContents[i].getType() == Material.BOOK_AND_QUILL
                        || shelfContents[i].getType() == Material.WRITTEN_BOOK)
                {
                    String title = "";
                    String author = "";
                    String lore;
                    List<String> pages;
                    int damage = 0;
                    
                    BookMeta bookMeta = (BookMeta) shelfContents[i]
                            .getItemMeta();
                    title = (bookMeta.getTitle() != null) ? ((bookMeta
                            .hasDisplayName()) ? bookMeta.getDisplayName()
                            : bookMeta.getTitle()) : "";
                    author = (bookMeta.getAuthor() != null) ? bookMeta
                            .getAuthor() : "";
                    lore = (bookMeta.getLore() != null) ? bookMeta.getLore()
                            .get(0) : null;
                    damage = shelfContents[i].getDurability();
                    pages = bookMeta.getPages();
                    
                    title = title.replaceAll("'", "''");
                    author = author.replaceAll("'", "''");
                    lore = (lore != null) ? lore.replaceAll("'", "''") : "";
                    String pageString = "";
                    
                    if(pages != null)
                    {
                        for(String page : pages)
                        {
                            pageString += page.replaceAll("'", "''") + "¬";
                        }
                        if(pageString.endsWith("¬"))
                            pageString = pageString.substring(0,
                                    pageString.length() - 1);
                    }
                    
                    plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt,lore,damage,pages) VALUES ("
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
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
                else if(shelfContents[i].getType() == Material.ENCHANTED_BOOK)
                {
                    plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
                            + ", 'null', 'null','"
                            + type
                            + "',"
                            + i
                            + ","
                            + shelfContents[i].getAmount() + ");");
                    EnchantmentStorageMeta book = (EnchantmentStorageMeta) shelfContents[i]
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
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
                            + ","
                            + i
                            + ",'"
                            + type2 + "','" + lvl + "');");
                }
                else if(shelfContents[i].getType() == Material.MAP)
                {
                    ItemStack mapp = shelfContents[i];
                    int dur = mapp.getDurability();
                    plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
                            + ", 'null', 'null','"
                            + type
                            + "',"
                            + i
                            + ","
                            + shelfContents[i].getAmount() + ");");
                    plugin.runQuery("INSERT INTO maps (x,y,z,loc,durability) VALUES ("
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
                            + ","
                            + i
                            + ",'"
                            + dur
                            + "');");
                }
                else if(BookShelfPlugin.allowedItems.contains(shelfContents[i]
                        .getType().name()))
                {
                    plugin.runQuery("INSERT INTO items (x,y,z,author,title,enumType,loc,amt) VALUES ("
                            + shelfLocation.getX()
                            + ","
                            + shelfLocation.getY()
                            + ","
                            + shelfLocation.getZ()
                            + ", 'null', 'null','"
                            + type
                            + "',"
                            + i
                            + ","
                            + shelfContents[i].getAmount() + ");");
                }
            }
        }
        plugin.getSQLManager().commit();
        plugin.getSQLManager().setAutoCommit(true);
    }
    
    public boolean isOwner(Player player)
    {
        if(player.isOp())
            return true;
        else if(!plugin.getConfig().getBoolean("use_built_in_ownership"))
            return true;
        else
            return this.shelfOwners.contains(player.getName().toLowerCase());
    }
    
    public ArrayList<String> getOwners()
    {
        return this.shelfOwners;
    }
    
    public void breakShelf()
    {
        closeInventories();
        
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM items WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            ArrayList<String> itemAuthor = new ArrayList<String>();
            ArrayList<String> itemTitle = new ArrayList<String>();
            ArrayList<String> itemType = new ArrayList<String>();
            ArrayList<Integer> itemID = new ArrayList<Integer>();
            ArrayList<Integer> itemAmount = new ArrayList<Integer>();
            ArrayList<Integer> itemInventoryLocation = new ArrayList<Integer>();
            ArrayList<String> itemLore = new ArrayList<String>();
            ArrayList<Integer> itemDamage = new ArrayList<Integer>();
            ArrayList<String> itemPages = new ArrayList<String>();
            while(r.next())
            {
                itemAuthor.add(r.getString("author"));
                itemTitle.add(r.getString("title"));
                itemID.add(r.getInt("id"));
                itemType.add(r.getString("enumType"));
                itemAmount.add(r.getInt("amt"));
                itemInventoryLocation.add(r.getInt("loc"));
                itemLore.add(r.getString("lore"));
                itemDamage.add(r.getInt("damage"));
                itemPages.add(r.getString("pages"));
            }
            plugin.close(r);
            plugin.getSQLManager().setAutoCommit(false);
            for(int i = 0; i < itemID.size(); i++)
            {
                if(itemType.get(i).equals(Material.ENCHANTED_BOOK.name()))
                {
                    r = plugin.runQuery("SELECT * FROM enchant WHERE x="
                            + shelfLocation.getX() + " AND y="
                            + shelfLocation.getY() + " AND z="
                            + shelfLocation.getZ() + " AND loc="
                            + itemInventoryLocation.get(i) + ";");
                    
                    String enchantType = "";
                    Enchantment enchantment;
                    int enchantLevel = 0;
                    while(r.next())
                    {
                        enchantType = r.getString("type");
                        enchantLevel = r.getInt("level");
                    }
                    plugin.close(r);
                    enchantment = Enchantment.getByName(enchantType);
                    dropItem(ItemGenerator.generateEnchantedBook(enchantment,
                            enchantLevel), shelfLocation);
                }
                else if(itemType.get(i).equals(Material.MAP.name()))
                {
                    r = plugin.runQuery("SELECT * FROM maps WHERE x="
                            + shelfLocation.getX() + " AND y="
                            + shelfLocation.getY() + " AND z="
                            + shelfLocation.getZ() + " AND loc="
                            + itemInventoryLocation.get(i) + ";");
                    
                    short mapDurability = 0;
                    while(r.next())
                    {
                        mapDurability = r.getShort("durability");
                    }
                    plugin.close(r);
                    dropItem(ItemGenerator.generateMap(mapDurability),
                            shelfLocation);
                }
                else if(itemType.get(i).equals(Material.WRITTEN_BOOK.name())
                        || itemType.get(i).equals(
                                Material.BOOK_AND_QUILL.name()))
                {
                    String[] itemPagesSplit = itemPages.get(i).split("¬");
                    
                    if(itemType.get(i).equals(Material.WRITTEN_BOOK.name()))
                    {
                        dropItem(ItemGenerator.generateWrittenBook(
                                itemAuthor.get(i), itemTitle.get(i),
                                itemLore.get(i), itemPagesSplit,
                                itemDamage.get(i)), shelfLocation);
                    }
                    else if(itemType.get(i).equals(
                            Material.BOOK_AND_QUILL.name()))
                    {
                        dropItem(ItemGenerator.generateBookAndQuill(
                                itemAuthor.get(i), itemTitle.get(i),
                                itemLore.get(i), itemPagesSplit,
                                itemDamage.get(i)), shelfLocation);
                    }
                }
                else if(BookShelfPlugin.allowedItems.contains(itemType.get(i)))
                {
                    ItemStack stack = new ItemStack(
                            Material.getMaterial(itemType.get(i)));
                    stack.setAmount(itemAmount.get(i));
                    dropItem(stack, shelfLocation);
                }
            }
            plugin.runQuery("DELETE FROM copy WHERE x=" + shelfLocation.getX()
                    + " AND y=" + shelfLocation.getY() + " AND z="
                    + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM shop WHERE x=" + shelfLocation.getX()
                    + " AND y=" + shelfLocation.getY() + " AND z="
                    + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM donate WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM names WHERE x=" + shelfLocation.getX()
                    + " AND y=" + shelfLocation.getY() + " AND z="
                    + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM enable WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM enchant WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM maps WHERE x=" + shelfLocation.getX()
                    + " AND y=" + shelfLocation.getY() + " AND z="
                    + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM owners WHERE x="
                    + shelfLocation.getX() + " AND y=" + shelfLocation.getY()
                    + " AND z=" + shelfLocation.getZ() + ";");
            plugin.runQuery("DELETE FROM items WHERE x=" + shelfLocation.getX()
                    + " AND y=" + shelfLocation.getY() + " AND z="
                    + shelfLocation.getZ() + ";");
            plugin.getSQLManager().commit();
            plugin.getSQLManager().setAutoCommit(true);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        if(plugin.autoToggle)
        {
            if(plugin.autoToggleMap1.containsKey(shelfLocation))
            {
                plugin.autoToggleMap1.remove(shelfLocation);
                plugin.autoToggleMap2.remove(shelfLocation);
            }
        }
    }
    
    public void closeInventories()
    {
        if(this.shelfInventory != null)
        {
            for(HumanEntity viewer : this.shelfInventory.getViewers())
            {
                viewer.closeInventory();
            }
        }
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
    
}
