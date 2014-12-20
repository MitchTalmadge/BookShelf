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
import me.MitchT.BookShelf.Shelves.ShelfManager;
import me.MitchT.BookShelf.Shelves.ShelfScheduler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
public class BookShelfPlugin extends JavaPlugin
{
    private static BookShelfPlugin instance;
    
    private final Logger logger = Logger.getLogger("Minecraft");
    private CommandHandler commandHandler;
    private ExternalPluginManager externalPluginManager;
    private ShelfManager shelfManager;
    private ShelfScheduler shelfScheduler;
    
    public final static ArrayList<String> records = new ArrayList<String>(
            Arrays.asList(Material.RECORD_3.name(), Material.RECORD_4.name(),
                    Material.RECORD_5.name(), Material.RECORD_6.name(),
                    Material.RECORD_7.name(), Material.RECORD_8.name(),
                    Material.RECORD_9.name(), Material.RECORD_10.name(),
                    Material.RECORD_11.name(), Material.RECORD_12.name(),
                    Material.GOLD_RECORD.name(), Material.GREEN_RECORD.name()));
    public final static ArrayList<String> allowedItems = new ArrayList<String>(
            Arrays.asList(Material.BOOK.name(), Material.BOOK_AND_QUILL.name(),
                    Material.WRITTEN_BOOK.name(),
                    Material.ENCHANTED_BOOK.name(), Material.PAPER.name(),
                    Material.MAP.name(), Material.EMPTY_MAP.name()));
    
    /* AUTO TOGGLE (For shaythegoon) */
    public boolean autoToggle = false;
    int autoToggleFreq = 10;
    boolean autoToggleServerWide = false;
    boolean autoToggleDiffPlayers = false;
    public HashMap<Location, Integer> autoToggleMap1 = new HashMap<Location, Integer>();
    public HashMap<Location, List<Player>> autoToggleMap2 = new HashMap<Location, List<Player>>();
    List<?> autoToggleNameList = null;
    
    /* DATABASE */
    private SQLManager sqlManager;
    static ResultSet r;
    
    @Override
    public void onEnable()
    {
        instance = this;
        allowedItems.addAll(records);
        saveDefaultConfig();
        
        this.sqlManager = new SQLManager(this, logger);
        this.shelfManager = new ShelfManager(this);
        this.externalPluginManager = new ExternalPluginManager(this, logger);
        this.commandHandler = new CommandHandler(this);
        this.shelfScheduler = new ShelfScheduler(this);
        
        setupAutoToggle();
        
        getServer().getPluginManager().registerEvents(new BookListener(this),
                this);
        PluginDescriptionFile pdfFile = this.getDescription();
        
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
    }
    
    public static BookShelfPlugin getInstance()
    {
        return instance;
    }
    
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args)
    {
        commandHandler.onCommand(sender, command, label, args);
        return true;
    }
    
    public ExternalPluginManager getExternalPluginManager()
    {
        return externalPluginManager;
    }
    
    public SQLManager getSQLManager()
    {
        return sqlManager;
    }
    
    public ShelfManager getShelfManager()
    {
        return shelfManager;
    }
    
    public ShelfScheduler getShelfScheduler()
    {
        return shelfScheduler;
    }
    
    public ResultSet runQuery(String query)
    {
        return sqlManager.runQuery(query);
    }
    
    public void close(ResultSet r) throws SQLException
    {
        sqlManager.close(r);
    }
    
    private void setupAutoToggle()
    {
        if(getConfig().get("auto_toggle.enabled") != null)
        {
            this.autoToggle = getConfig().getBoolean("auto_toggle.enabled");
        }
        
        if(getConfig().get("auto_toggle.frequency") != null)
        {
            this.autoToggleFreq = getConfig().getInt("auto_toggle.frequency");
        }
        
        if(getConfig().get("auto_toggle.server_wide") != null)
        {
            this.autoToggleServerWide = getConfig().getBoolean(
                    "auto_toggle.server_wide");
        }
        
        if(getConfig().get("auto_toggle.different_players") != null)
        {
            this.autoToggleDiffPlayers = getConfig().getBoolean(
                    "auto_toggle.different_players");
        }
        
        if(getConfig().get("auto_toggle.name_list") != null)
        {
            this.autoToggleNameList = getConfig().getList(
                    "auto_toggle.name_list");
        }
    }
    
    public Block getTargetBlock(Player player, int range)
    {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        
        Block block = null;
        
        for(int i = 0; i <= range; i++)
        {
            block = loc.add(dir).getBlock();
            if(block.getType() != Material.AIR)
                break;
        }
        
        return block;
    }
    
    public void reloadBookShelfConfig()
    {
        reloadConfig();
        saveDefaultConfig();
        setupAutoToggle();
        
        this.externalPluginManager.setupPlugins();
    }
    
}
