package me.MitchT.BookShelf;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import me.MitchT.BookShelf.Commands.CommandHandler;
import me.MitchT.BookShelf.DBUpdates.DBUpdate;
import me.MitchT.BookShelf.LWC.LWCPluginHandler;
import me.MitchT.BookShelf.Towny.TownyCommands;
import me.MitchT.BookShelf.Towny.TownyHandler;
import me.MitchT.BookShelf.WorldEdit.WorldEdit_EditSessionFactoryHandler;
import me.MitchT.BookShelf.BookListener;
import me.MitchT.SimpleSQL.Database;
import me.MitchT.SimpleSQL.MySQL;
import me.MitchT.SimpleSQL.SQLite;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.griefcraft.lwc.LWCPlugin;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
    public static final int currentDatabaseVersion = 3;
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
    
    /* ECONOMY */
    public static Economy economy;
    
    /* LWC */
    static LWCPlugin LWC;
    static LWCPluginHandler LWCPluginHandler;
    public static boolean LWCEnabled;
    
    /* AUTO TOGGLE (For shaythegoon) */
    boolean autoToggle = false;
    int autoToggleFreq = 10;
    boolean autoToggleServerWide = false;
    boolean autoToggleDiffPlayers = false;
    HashMap<Location, Integer> autoToggleMap1 = new HashMap<Location, Integer>();
    HashMap<Location, List<Player>> autoToggleMap2 = new HashMap<Location, List<Player>>();
    List<?> autoToggleNameList = null;
    private CommandHandler commandHandler;
    
    /* TOWNY */
    static Towny towny;
    public static boolean useTowny = false;
    public static boolean useWorldGuard = false;
    public static File townyConfigPath;
    public static FileConfiguration townyConfig;
    
    /* WORLD EDIT */
    static WorldEditPlugin worldEdit = null;
    
    /* WORLD GUARD */
    static WorldGuardPlugin worldGuard;
    
    /* DATABASE */
    static MySQL mysql;
    static SQLite sqlite;
    static ResultSet r;
    
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
        
        getdb().close();
        
        if(BookShelf.useTowny)
            TownyHandler.saveConfig();
        
    }
    
    @Override
    public void onEnable()
    {
        allowedItems.addAll(records);
        config = getConfig();
        saveDefaultConfig();
        sqlConnection();
        sqlDoesDatabaseExist();
        
        setupAutoToggle();
        
        if(setupEconomy())
        {
            this.logger.info("[BookShelf] Vault found and hooked.");
        }
        
        if(setupLWC())
        {
            this.logger.info("[BookShelf] LWC found and hooked.");
            if(config.getBoolean("lwc_support.enabled"))
            {
                LWCEnabled = true;
                LWCPluginHandler = new LWCPluginHandler(LWC);
            }
        }
        
        townyConfigPath = new File(getDataFolder(), "towny.yml");
        
        if(setupTowny())
        {
            logger.info("[BookShelf] Towny found and hooked.");
            useTowny = config.getBoolean("towny_support.enabled");
            if(useTowny)
            {
                loadTownyConfig();
            }
        }
        
        if(setupWorldGuard())
        {
            logger.info("[BookShelf] WorldGuard found and hooked.");
            useWorldGuard = BookShelf.worldGuard != null
                    && config.getBoolean("worldguard_support.enabled");
        }
        
        if(setupWorldEdit())
        {
            logger.info("[BookShelf] WorldEdit found and hooked.");
            worldEdit.getWorldEdit().setEditSessionFactory(
                    new WorldEdit_EditSessionFactoryHandler());
        }
        
        getServer().getPluginManager().registerEvents(new BookListener(this), this);
        PluginDescriptionFile pdfFile = this.getDescription();

        this.commandHandler = new CommandHandler();
        
        this.logger.info("[" + pdfFile.getName() + "] Enabled BookShelf V"
                + pdfFile.getVersion());
        
    }
    
    public static ResultSet runQuery(String query)
    {
        try
        {
            return getdb().query(query);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void close(ResultSet r) throws SQLException
    {
        r.close();
        getdb().setShouldWait(false);
        synchronized(getdb().getSynchronized())
        {
            getdb().getSynchronized().notify();
        }
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
    
    private void loadTownyConfig()
    {
        if(!townyConfigPath.exists())
            saveResource("towny.yml", false);
        townyConfig = YamlConfiguration.loadConfiguration(new File(
                getDataFolder(), "towny.yml"));
    }
    
    private boolean setupEconomy()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        
        if(plugin != null)
        {
            @SuppressWarnings("rawtypes")
            RegisteredServiceProvider economyProvider = getServer()
                    .getServicesManager().getRegistration(Economy.class);
            if(economyProvider != null)
            {
                economy = (Economy) economyProvider.getProvider();
            }
        }
        return economy != null;
    }
    
    private boolean setupLWC()
    {
        LWC = (LWCPlugin) getServer().getPluginManager().getPlugin("LWC");
        return LWC != null;
    }
    
    private boolean setupTowny()
    {
        towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
        return towny != null;
    }
    
    private boolean setupWorldEdit()
    {
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin(
                "WorldEdit");
        return worldEdit != null;
    }
    
    private boolean setupWorldGuard()
    {
        worldGuard = (WorldGuardPlugin) getServer().getPluginManager()
                .getPlugin("WorldGuard");
        return worldGuard != null;
    }
    
    public boolean isUsingTowny()
    {
        return BookShelf.useTowny;
    }
    
    public static boolean usingMySQL()
    {
        return getdb() instanceof MySQL;
    }
    
    public void sqlConnection()
    {
        boolean enable = config.getBoolean("database.mysql_enabled");
        String host = config.getString("database.hostname");
        int port = config.getInt("database.port");
        String dbname = config.getString("database.database");
        String user = config.getString("database.username");
        String pass = config.getString("database.password");
        String prefix = config.getString("database.prefix");
        if(enable)
        {
            mysql = new MySQL(logger, prefix, host, port, dbname, user, pass);
            try
            {
                mysql.open();
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
                getPluginLoader().disablePlugin(this);
            }
        }
        else
        {
            sqlite = new SQLite(logger, "BookShelf", getDataFolder()
                    .getAbsolutePath(), "Shelves");
            try
            {
                sqlite.open();
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
                getPluginLoader().disablePlugin(this);
            }
        }
    }
    
    private int getDbVersion()
    {
        int version = -1;
        try
        {
            sqlDoesVersionExist();
            r = getdb().query("SELECT * FROM version");
            if(r.next())
                version = r.getInt("version");
            close(r);
            return version;
        }
        catch(SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
    
    public void sqlDoesDatabaseExist()
    {
        try
        {
            sqlDoesVersionExist();
            if(getDbVersion() == 1)
                doDelimiterFix();
            updateDb();
            logger.info("[BookShelf] Current Database Version: "
                    + getDbVersion());
            boolean enable = config.getBoolean("database.mysql_enabled");
            if(enable) //MYSQL
            {
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS items (id INT NOT NULL AUTO_INCREMENT, x INT, y INT, z INT, title VARCHAR(128), author VARCHAR(128), lore TEXT, damage INT, enumType TEXT, loc INT, amt INT, pages TEXT, PRIMARY KEY (id));");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS enchant (x INT, y INT, z INT, loc INT, type VARCHAR(64), level INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS maps (x INT, y INT, z INT, loc INT, durability SMALLINT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name VARCHAR(64));");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS donate (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS owners (x INT, y INT, z INT, ownerString TEXT);");
            }
            else
            //SQLITE
            {
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, x INT, y INT, z INT, title TEXT, author TEXT, lore TEXT, damage INT, enumType TEXT, loc INT, amt INT, pages TEXT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS enchant (x INT, y INT, z INT, loc INT, type STRING, level INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS maps (x INT, y INT, z INT, loc INT, durability SMALLINT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name TEXT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS donate (x INT, y INT, z INT, bool INT);");
                getdb().query(
                        "CREATE TABLE IF NOT EXISTS owners (x INT, y INT, z INT, ownerString TEXT);");
            }
            logger.info("[BookShelf] Database Loaded.");
        }
        catch(SQLException e)
        {
            System.out
                    .println("[BookShelf] Database could not load! Check server log.");
            e.printStackTrace();
        }
        
    }
    
    private void doDelimiterFix()
    {
        try
        {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            ArrayList<String> pageStrings = new ArrayList<String>();
            r = BookShelf.getdb().query("SELECT * FROM items;");
            while(r.next())
            {
                ids.add(r.getInt("id"));
                pageStrings.add(r.getString("pages"));
            }
            close(r);
            for(int i = 0; i < ids.size(); i++)
            {
                if(pageStrings.get(i) != null)
                {
                    String pages = pageStrings.get(i).replaceAll(":", "¬");
                    pages = pages.replaceAll("'", "''");
                    BookShelf.getdb().query(
                            "UPDATE items SET pages='" + pages + "' WHERE id="
                                    + ids.get(i) + ";");
                }
            }
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    private void updateDb()
    {
        
        //Note to self: Update currentDatabaseVersion!! :)
        int version = -1;
        try
        {
            r = getdb().query("SELECT * FROM version");
            if(r.next())
                version = r.getInt("version");
            close(r);
            DBUpdate updater = new DBUpdate(logger, r);
            switch(version)
            {
                case 0:
                    updater.doUpdate(version);
                    updateDb();
                    break;
                case 1:
                    updater.doUpdate(version);
                    updateDb();
                    break;
                case 2:
                    updater.doUpdate(version);
                    updateDb();
                    break;
                default:
                    break;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    private void sqlDoesVersionExist()
    {
        if(usingMySQL())
        {
            try
            {
                r = getdb().query("SHOW TABLES LIKE 'version';");
                if(r.next())
                {
                    close(r);
                    return;
                }
                else
                    close(r);
                
                r = getdb().query("SHOW TABLES LIKE 'items';");
                if(!r.next())
                {
                    close(r); //Looks like we are making a new database.
                    logger.info("[BookShelf] Creating Database...");
                    getdb().query(
                            "CREATE TABLE IF NOT EXISTS version (version INT);");
                    getdb().query(
                            "INSERT INTO version (version) VALUES("
                                    + currentDatabaseVersion + ");");
                }
                else
                { //We aren't making a new database, but version doesn't exist.... Let's add it.
                    close(r);
                    logger.info("[BookShelf] Adding version to Database...");
                    r = getdb().query("SHOW TABLES LIKE 'version';");
                    if(!r.next())
                    {
                        close(r);
                        getdb().query(
                                "CREATE TABLE IF NOT EXISTS version (version INT);");
                        getdb().query(
                                "INSERT INTO version (version) VALUES(0);");
                    }
                    else
                    {
                        close(r);
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
            try
            {
                
                r = getdb()
                        .query("SELECT name FROM sqlite_master WHERE type='table' AND name='version';");
                if(r.next())
                {
                    close(r);
                    return;
                }
                else
                    close(r);
                
                r = getdb()
                        .query("SELECT name FROM sqlite_master WHERE type='table' AND name='items';");
                if(!r.next())
                {
                    close(r); //Looks like we are making a new database.
                    logger.info("[BookShelf] Creating Database...");
                    getdb().query(
                            "CREATE TABLE IF NOT EXISTS version (version INT);");
                    getdb().query(
                            "INSERT INTO version (version) VALUES("
                                    + currentDatabaseVersion + ");");
                }
                else
                { //We aren't making a new database, but version doesn't exist.... Let's add it.
                    close(r);
                    r = getdb()
                            .query("SELECT name FROM sqlite_master WHERE type='table' AND name='version';");
                    if(!r.next())
                    {
                        close(r);
                        logger.info("[BookShelf] Adding version to Database...");
                        getdb().query(
                                "CREATE TABLE IF NOT EXISTS version (version INT);");
                        getdb().query(
                                "INSERT INTO version (version) VALUES(0);");
                    }
                    else
                    {
                        close(r);
                    }
                }
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static Block getTargetBlock(Player player, int range)
    {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        
        Block b = null;
        
        for(int i = 0; i <= range; i++)
        {
            b = loc.add(dir).getBlock();
        }
        
        return b;
    }
    
    public static void reloadBookShelfConfig()
    {
        instance.reloadConfig();
        config = instance.getConfig();
        instance.saveDefaultConfig();
        instance.loadTownyConfig();
        instance.setupAutoToggle();
        
        if(config.getBoolean("lwc_support.enabled"))
        {
            if(LWCPluginHandler == null)
            {
                LWCEnabled = true;
                LWCPluginHandler = new LWCPluginHandler(LWC);
            }
            else if(LWCEnabled == false)
                LWCEnabled = true;
        }
        else if(LWCPluginHandler != null)
            LWCEnabled = false;
        
        if(config.getBoolean("worldguard_support.enabled"))
        {
            if(worldGuard != null)
                useWorldGuard = true;
            else
                useWorldGuard = false;
        }
        else
            useWorldGuard = false;
        
        if(config.getBoolean("towny_support.enabled"))
        {
            if(towny != null)
                useTowny = true;
            else
                useTowny = false;
        }
        else
            useTowny = false;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args)
    {
        commandHandler.onCommand(sender, command, label, args);
        return false;
    }
    
    public static boolean isOwner(Location loc, Player p)
    {
        if(loc.getBlock().getType() != Material.BOOKSHELF)
            return false;
        return isOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), p);
    }
    
    public static boolean isOwner(int x, int y, int z, Player p)
    {
        if(p.isOp())
            return true;
        if(!config.getBoolean("use_built_in_ownership"))
            return true;
        try
        {
            r = getdb().query(
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
    
    public static void setOwners(Location loc, String[] pList)
    {
        setOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public static void setOwners(int x, int y, int z, String[] pList)
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
            r = getdb().query(
                    "SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                            + " AND z=" + z + ";");
            if(!r.next())
            {
                close(r);
                getdb().query(
                        "INSERT INTO owners (x, y, z, ownerString) VALUES ("
                                + x + ", +" + y + ", " + z + ", '"
                                + ownerString + "');");
            }
            else
            {
                close(r);
                getdb().query(
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
    
    public static void addOwners(Location loc, String[] pList)
    {
        addOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public static void addOwners(int x, int y, int z, String[] pList)
    {
        try
        {
            r = getdb().query(
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
                
                getdb().query(
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
                
                getdb().query(
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
    
    public static void removeOwners(Location loc, String[] pList)
    {
        removeOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public static void removeOwners(int x, int y, int z, String[] pList)
    {
        for(int i = 0; i < pList.length; i++)
        {
            pList[i] = pList[i].toLowerCase();
        }
        try
        {
            r = getdb().query(
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
                
                getdb().query(
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
    
    public static String[] getOwners(Location loc)
    {
        return getOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public static String[] getOwners(int x, int y, int z)
    {
        try
        {
            r = getdb().query(
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
    
    public static boolean isShelfUnlimited(Location loc)
    {
        try
        {
            r = getdb().query(
                    "SELECT * FROM copy WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                getdb().query(
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
    
    public static boolean isShelfDonate(Location loc)
    {
        try
        {
            r = getdb().query(
                    "SELECT * FROM donate WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                getdb().query(
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
    
    public static boolean isShelfShop(Location loc)
    {
        try
        {
            r = BookShelf.getdb().query(
                    "SELECT * FROM shop WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                BookShelf.getdb().query(
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
    
    public static int getShopPrice(Location loc)
    {
        try
        {
            r = BookShelf.getdb().query(
                    "SELECT * FROM shop WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(!r.next())
            {
                close(r);
                BookShelf.getdb().query(
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
    
    public static boolean isShelfEnabled(Location loc)
    {
        try
        {
            r = BookShelf.getdb().query(
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
                BookShelf.getdb().query(
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
    
    public static int toggleBookShelf(Location loc)
    {
        try
        {
            r = getdb().query(
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
                getdb().query(
                        "INSERT INTO enable (x,y,z,bool) VALUES (" + loc.getX()
                                + "," + loc.getY() + "," + loc.getZ() + ", "
                                + def + ");");
            }
            else
            {
                close(r);
            }
            r = getdb().query(
                    "SELECT * FROM enable WHERE x=" + loc.getX() + " AND y="
                            + loc.getY() + " AND z=" + loc.getZ() + ";");
            if(r.next())
                if(r.getInt("bool") == 1)
                {
                    close(r);
                    getdb().query(
                            "UPDATE enable SET bool=0 WHERE x=" + loc.getX()
                                    + " AND y=" + loc.getY() + " AND z="
                                    + loc.getZ() + ";");
                    return 0;
                }
                else
                {
                    close(r);
                    getdb().query(
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
    
    public static void toggleBookShelvesByName(String name)
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
            r = getdb().query("SELECT * FROM names WHERE name='" + name + "';");
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
                r = getdb().query(
                        "SELECT * FROM enable WHERE x=" + loc.getX()
                                + " AND y=" + loc.getY() + " AND z="
                                + loc.getZ() + ";");
                if(r.next())
                {
                    selmap.put(loc, r.getInt("bool") == 1 ? true : false);
                }
                close(r);
            }
            getdb().getConnection().setAutoCommit(false);
            for(Vector vec : selmap.keySet())
            {
                boolean bool = selmap.get(vec);
                int bool2 = bool == true ? 0 : 1;
                getdb().query(
                        "UPDATE enable SET bool=" + bool2 + " WHERE x="
                                + vec.getX() + " AND y=" + vec.getY()
                                + " AND z=" + vec.getZ() + ";");
            }
            getdb().getConnection().commit();
            getdb().getConnection().setAutoCommit(true);
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static Database getdb()
    {
        boolean enable = config.getBoolean("database.mysql_enabled");
        if(enable)
        {
            if(mysql.isOpen())
                return mysql;
            else
            {
                mysql.open();
                return mysql;
            }
        }
        else
        {
            return sqlite;
        }
    }
}
