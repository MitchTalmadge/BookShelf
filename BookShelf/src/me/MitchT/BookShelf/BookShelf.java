package me.MitchT.BookShelf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import me.MitchT.BookShelf.Commands.CommandHandler;
import me.MitchT.BookShelf.ExternalPlugins.ExternalPluginManager;
import me.MitchT.BookShelf.ExternalPlugins.TownyHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
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
public class BookShelf extends JavaPlugin
{
    
    /* SETUP */
    static FileConfiguration config;
    public static BookShelf instance;
    public final Logger logger = Logger.getLogger("Minecraft");
    public static ArrayList<Player> editingPlayers = new ArrayList<Player>();
    
    public static ArrayList<String> records = new ArrayList<String>(
            Arrays.asList(Material.RECORD_3.name(), Material.RECORD_4.name(),
                    Material.RECORD_5.name(), Material.RECORD_6.name(),
                    Material.RECORD_7.name(), Material.RECORD_8.name(),
                    Material.RECORD_9.name(), Material.RECORD_10.name(),
                    Material.RECORD_11.name(), Material.RECORD_12.name(),
                    Material.GOLD_RECORD.name(), Material.GREEN_RECORD.name()));
    public static ArrayList<String> allowedItems = new ArrayList<String>(
            Arrays.asList(Material.BOOK.name(), Material.BOOK_AND_QUILL.name(),
                    Material.WRITTEN_BOOK.name(),
                    Material.ENCHANTED_BOOK.name(), Material.PAPER.name(),
                    Material.MAP.name(), Material.EMPTY_MAP.name()));
    
    /* AUTO TOGGLE (For shaythegoon) */
    boolean autoToggle = false;
    int autoToggleFreq = 10;
    boolean autoToggleServerWide = false;
    boolean autoToggleDiffPlayers = false;
    HashMap<Location, Integer> autoToggleMap1 = new HashMap<Location, Integer>();
    HashMap<Location, List<Player>> autoToggleMap2 = new HashMap<Location, List<Player>>();
    List<?> autoToggleNameList = null;
    private CommandHandler commandHandler;
    private SQLManager sqlManager;
    private ExternalPluginManager externalPluginManager;
    
    /* DATABASE */
    static ResultSet r;
    
    @Override
    public void onEnable()
    {
        instance = this;
        allowedItems.addAll(records);
        config = getConfig();
        saveDefaultConfig();
        
        this.sqlManager = new SQLManager(this, logger);

        setupAutoToggle();

        this.externalPluginManager = new ExternalPluginManager(this, logger);
        
        getServer().getPluginManager().registerEvents(new BookListener(this), this);
        PluginDescriptionFile pdfFile = this.getDescription();

        this.commandHandler = new CommandHandler();
        
        this.logger.info("[" + pdfFile.getName() + "] Enabled BookShelf V"
                + pdfFile.getVersion());
        
    }
    
    @Override
    public void onDisable()
    {
        try
        {
            if(r != null)
                close(r);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        
        sqlManager.shutDown();
        
            TownyHandler.saveConfig();
        
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args)
    {
        commandHandler.onCommand(sender, command, label, args);
        return true;
    }
    
    public static ExternalPluginManager getExternalPluginManager()
    {
        return instance.externalPluginManager;
    }
    
    public ResultSet runQuery(String query)
    {
        return instance.sqlManager.runQuery(query);
    }
    
    public static void close(ResultSet r) throws SQLException
    {
        instance.sqlManager.close(r);
    }
    
    private void setupAutoToggle()
    {
        if(config.get("auto_toggle.enabled") != null)
        {
            this.autoToggle = config.getBoolean("auto_toggle.enabled");
        }
        
        if(config.get("auto_toggle.frequency") != null)
        {
            this.autoToggleFreq = config.getInt("auto_toggle.frequency");
        }
        
        if(config.get("auto_toggle.server_wide") != null)
        {
            this.autoToggleServerWide = config
                    .getBoolean("auto_toggle.server_wide");
        }
        
        if(config.get("auto_toggle.different_players") != null)
        {
            this.autoToggleDiffPlayers = config
                    .getBoolean("auto_toggle.different_players");
        }
        
        if(config.get("auto_toggle.name_list") != null)
        {
            this.autoToggleNameList = config.getList("auto_toggle.name_list");
        }
    }
    
    public Block getTargetBlock(Player player, int range)
    {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        
        Block b = null;
        
        for(int i = 0; i <= range; i++)
        {
            b = loc.add(dir).getBlock();
            if(b.getType() != Material.AIR)
                break;
        }
        
        return b;
    }
    
    public void reloadBookShelfConfig()
    {
        instance.reloadConfig();
        config = instance.getConfig();
        instance.saveDefaultConfig();
        instance.setupAutoToggle();
        
        this.externalPluginManager.setupPlugins();
    }
    
    public boolean isOwner(Location loc, Player p)
    {
        if(loc.getBlock().getType() != Material.BOOKSHELF)
            return false;
        return isOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), p);
    }
    
    public boolean isOwner(int x, int y, int z, Player p)
    {
        if(p.isOp())
            return true;
        if(!config.getBoolean("use_built_in_ownership"))
            return true;
        try
        {
            r = runQuery(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
                return false;
            }
            else
            {
                String owners = r.getString("ownerString").toLowerCase();
                close(r);
                
                String[] splitOwners = owners.split("§");
                if(Arrays.asList(splitOwners).contains(
                        p.getName().toLowerCase())
                        || Arrays.asList(splitOwners).contains("all"))
                    return true;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public void setOwners(Location loc, String[] pList)
    {
        setOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void setOwners(int x, int y, int z, String[] pList)
    {
        String ownerString = "";
        for(String p : pList)
        {
            p = p.toLowerCase();
            ownerString += p + "§";
        }
        if(ownerString.length() > 0)
            ownerString = ownerString.substring(0, ownerString.length() - 1);
        
        try
        {
            r = runQuery(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
                runQuery(
                        "INSERT INTO owners (x, y, z, ownerString) VALUES ("
                                + x + ", +" + y + ", " + z + ", '"
                                + ownerString + "');");
            }
            else
            {
                close(r);
                runQuery(
                        "UPDATE owners SET ownerString='" + ownerString
                                + "' WHERE x=" + x + " AND y=" + y + " AND z="
                                + z + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public void addOwners(Location loc, String[] pList)
    {
        addOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void addOwners(int x, int y, int z, String[] pList)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
                
                String ownerString = "";
                for(String p : pList)
                {
                    p = p.toLowerCase();
                    ownerString += p + "§";
                }
                ownerString = ownerString
                        .substring(0, ownerString.length() - 1);
                
                runQuery(
                        "INSERT INTO owners (x, y, z, ownerString) VALUES ("
                                + x + ", +" + y + ", " + z + ", '"
                                + ownerString + "');");
            }
            else
            {
                String currentOwnerString = r.getString("ownerString");
                close(r);
                
                String[] currentOwnerStringSplit = currentOwnerString
                        .split("§");
                String newOwnerString = "";
                
                for(String p : currentOwnerStringSplit)
                {
                    p = p.toLowerCase();
                    newOwnerString += p + "§";
                }
                for(String p : pList)
                {
                    p = p.toLowerCase();
                    if(Arrays.asList(currentOwnerStringSplit).contains(p))
                        continue;
                    newOwnerString += p + "§";
                }
                if(newOwnerString.length() > 0)
                    newOwnerString = newOwnerString.substring(0,
                            newOwnerString.length() - 1);
                
                runQuery(
                        "UPDATE owners SET ownerString='" + newOwnerString
                                + "' WHERE x=" + x + " AND y=" + y + " AND z="
                                + z + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public void removeOwners(Location loc, String[] pList)
    {
        removeOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void removeOwners(int x, int y, int z, String[] pList)
    {
        for(int i = 0; i < pList.length; i++)
        {
            pList[i] = pList[i].toLowerCase();
        }
        try
        {
            r = runQuery(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
            }
            else
            {
                String currentOwnerString = r.getString("ownerString");
                close(r);
                
                String[] currentOwnerStringSplit = currentOwnerString
                        .split("§");
                String newOwnerString = "";
                
                for(String p : currentOwnerStringSplit)
                {
                    p = p.toLowerCase();
                    if(Arrays.asList(pList).contains(p))
                        continue;
                    newOwnerString += p + "§";
                }
                if(newOwnerString.length() > 0)
                    newOwnerString = newOwnerString.substring(0,
                            newOwnerString.length() - 1);
                
                runQuery(
                        "UPDATE owners SET ownerString='" + newOwnerString
                                + "' WHERE x=" + x + " AND y=" + y + " AND z="
                                + z + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public String[] getOwners(Location loc)
    {
        return getOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public String[] getOwners(int x, int y, int z)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
                return new String[] { "No Owners!" };
            }
            else
            {
                String ownerString = r.getString("ownerString");
                close(r);
                
                String[] ownerStringSplit = ownerString.split("§");
                return ownerStringSplit;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return new String[] { "Unknown Owners!" };
    }
    
    public boolean isShelfUnlimited(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM copy WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                runQuery(
                        "INSERT INTO copy (x,y,z,bool) VALUES (" + loc.getX()
                                + "," + loc.getY() + "," + loc.getZ() + ",0);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isShelfDonate(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM donate WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                runQuery(
                        "INSERT INTO donate (x,y,z,bool) VALUES (" + loc.getX()
                                + "," + loc.getY() + "," + loc.getZ() + ",0);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isShelfShop(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM shop WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                runQuery(
                        "INSERT INTO shop (x,y,z,bool,price) VALUES ("
                                + loc.getX() + "," + loc.getY() + ","
                                + loc.getZ() + ",0,10);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getShopPrice(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM shop WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                runQuery(
                        "INSERT INTO shop (x,y,z,bool,price) VALUES ("
                                + loc.getX() + "," + loc.getY() + ","
                                + loc.getZ() + ",0,10);");
                return 10;
            }
            else
            {
                int price = r.getInt("price");
                close(r);
                
                return price;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return 10;
    }
    
    public boolean isShelfEnabled(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM enable WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                int def = 1;
                close(r);
                if(instance.getConfig().getBoolean("default_openable"))
                {
                    def = 1;
                }
                else
                {
                    def = 0;
                }
                runQuery(
                        "INSERT INTO enable (x,y,z,bool) VALUES (" + loc.getX()
                                + "," + loc.getY() + "," + loc.getZ() + ", "
                                + def + ");");
                return(def != 0);
            }
            else
            {
                int enabled = r.getInt("bool");
                close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public int toggleBookShelf(Location loc)
    {
        try
        {
            r = runQuery(
                    "SELECT * FROM enable WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                int def = 1;
                if(config.getBoolean("default_openable"))
                {
                    def = 1;
                }
                else
                {
                    def = 0;
                }
                close(r);
                runQuery(
                        "INSERT INTO enable (x,y,z,bool) VALUES (" + loc.getX()
                                + "," + loc.getY() + "," + loc.getZ() + ", "
                                + def + ");");
            }
            else
            {
                close(r);
            }
            r = runQuery(
                    "SELECT * FROM enable WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(r.next())
                if(r.getInt("bool") == 1)
                {
                    close(r);
                    runQuery(
                            "UPDATE enable SET bool=0 WHERE x=" + loc.getX()
                                    + " AND y=" + loc.getY() + " AND z="
                                    + loc.getZ() + ";");
                    return 0;
                }
                else
                {
                    close(r);
                    runQuery(
                            "UPDATE enable SET bool=1 WHERE x=" + loc.getX()
                                    + " AND y=" + loc.getY() + " AND z="
                                    + loc.getZ() + ";");
                    return 1;
                }
            return -1;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void toggleBookShelvesByName(String name)
    {
        
        if(!name.endsWith(" "))
        {
            if(!name.equals(config.getString("default_shelf_name")))
                name += " ";
        }
        else
        {
            if(name.equals(config.getString("default_shelf_name") + " "))
                name = name.substring(0, name.length() - 1);
        }
        
        try
        {
            r = runQuery("SELECT * FROM names WHERE name='" + name + "';");
            List<Vector> vecs = new ArrayList<Vector>();
            HashMap<Vector, Boolean> selmap = new HashMap<Vector, Boolean>();
            
            while(r.next())
            {
                Vector loc = new Vector(r.getInt("x"), r.getInt("y"),
                        r.getInt("z"));
                vecs.add(loc);
            }
            close(r);
            for(Vector loc : vecs)
            {
                r = runQuery(
                        "SELECT * FROM enable WHERE x=" + loc.getX()
                                + " AND y=" + loc.getY() + " AND z="
                                + loc.getZ() + ";");
                if(r.next())
                {
                    selmap.put(loc, r.getInt("bool") == 1 ? true : false);
                }
                close(r);
            }
            instance.sqlManager.setAutoCommit(false);
            for(Vector vec : selmap.keySet())
            {
                boolean bool = selmap.get(vec);
                int bool2 = bool == true ? 0 : 1;
                runQuery(
                        "UPDATE enable SET bool=" + bool2 + " WHERE x="
                                + vec.getX() + " AND y=" + vec.getY()
                                + " AND z=" + vec.getZ() + ";");
            }
            instance.sqlManager.commit();
            instance.sqlManager.setAutoCommit(true);
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static SQLManager getSQLManager()
    {
        return instance.sqlManager;
    }
    
    
}
