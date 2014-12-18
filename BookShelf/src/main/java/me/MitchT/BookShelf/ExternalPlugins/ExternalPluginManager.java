package me.MitchT.BookShelf.ExternalPlugins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import me.MitchT.BookShelf.BookShelf;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

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
public class ExternalPluginManager
{
    private BookShelf plugin;
    private Logger logger;
    
    private Economy vaultEconomy;
    private boolean usingVaultEconomy = false;
    
    private LWCPlugin lwcPlugin;
    private boolean usingLWC = false;
    
    private Towny townyPlugin;
    private File townyConfigFile;
    private YamlConfiguration townyConfig;
    private TownyHandler townyHandler;
    private TownyCommandHandler townyCommandHandler;
    private boolean usingTowny = false;
    
    private WorldGuardPlugin worldGuardPlugin;
    private boolean usingWorldGuard = false;
    
    private WorldEditPlugin worldEditPlugin;
    private boolean usingWorldEdit = false;
    
    public ExternalPluginManager(BookShelf plugin, Logger logger)
    {
        this.plugin = plugin;
        this.logger = logger;
        
        setupPlugins();
    }
    
    public void setupPlugins()
    {
        setupEconomy();
        setupLWC();
        setupTowny();
        setupWorldGuard();
        setupWorldEdit();
    }
    
    public void shutDown()
    {
        saveTownyConfig();
    }
    
    //SETUP AND INITIALIZERS ----------------------------------------------------------
    
    private void setupEconomy()
    {
        Plugin vaultPlugin = plugin.getServer().getPluginManager()
                .getPlugin("Vault");
        
        if(vaultPlugin != null)
        {
            @SuppressWarnings("rawtypes")
            RegisteredServiceProvider economyProvider = plugin.getServer()
                    .getServicesManager().getRegistration(Economy.class);
            if(economyProvider != null)
            {
                this.vaultEconomy = (Economy) economyProvider.getProvider();
                if(vaultEconomy != null)
                {
                    logger.info("[BookShelf] Vault found and hooked.");
                    this.usingVaultEconomy = true;
                }
            }
        }
    }
    
    private void setupLWC()
    {
        Plugin thePlugin = plugin.getServer().getPluginManager()
                .getPlugin("LWC");
        if(thePlugin != null)
        {
            logger.info("[BookShelf] LWC found and hooked.");
            if(plugin.getConfig().getBoolean("lwc_support.enabled"))
            {
                this.usingLWC = true;
                this.lwcPlugin = (LWCPlugin) thePlugin;
                injectLWCHandler(new LWCHandler(lwcPlugin, plugin));
            }
            else
                this.usingLWC = false;
        }
    }
    
    private void injectLWCHandler(LWCHandler lwcHandler)
    {
        try
        {
            Field lwc = lwcPlugin.getClass().getDeclaredField("lwc");
            lwc.setAccessible(true);
            lwc.set(lwcPlugin, lwcHandler);
            lwcHandler.load();
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
    
    private void setupTowny()
    {
        Plugin thePlugin = plugin.getServer().getPluginManager()
                .getPlugin("Towny");
        if(thePlugin != null)
        {
            this.townyPlugin = (Towny) thePlugin;
            logger.info("[BookShelf] Towny found and hooked.");
            if(plugin.getConfig().getBoolean("towny_support.enabled"))
            {
                this.usingTowny = true;
                townyConfigFile = new File(plugin.getDataFolder(), "towny.yml");
                this.townyHandler = new TownyHandler(plugin);
                this.townyCommandHandler = new TownyCommandHandler(plugin);
                loadTownyConfig();
            }
            else
                this.usingTowny = false;
        }
    }
    
    private void loadTownyConfig()
    {
        if(!townyConfigFile.exists())
            plugin.saveResource("towny.yml", false);
        townyConfig = YamlConfiguration.loadConfiguration(new File(plugin
                .getDataFolder(), "towny.yml"));
    }
    
    public void saveTownyConfig()
    {
        if(usingTowny)
            try
            {
                townyConfig.save(townyConfigFile);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
    }
    
    private boolean setupWorldGuard()
    {
        this.worldGuardPlugin = (WorldGuardPlugin) plugin.getServer()
                .getPluginManager().getPlugin("WorldGuard");
        if(worldGuardPlugin != null)
        {
            if(worldGuardPlugin.getDescription().getVersion().startsWith("6."))
            {
                logger.info("[BookShelf] WorldGuard found and hooked.");
                this.usingWorldGuard = plugin.getConfig().getBoolean(
                        "worldguard_support.enabled");
            }
            else
            {
                logger.info("[BookShelf] WorldGuard is outdated! You must use WorldGuard version 6.0 or later for BookShelf support.");
                usingWorldGuard = false;
            }
        }
        return worldGuardPlugin != null;
    }
    
    private boolean setupWorldEdit()
    {
        this.worldEditPlugin = (WorldEditPlugin) plugin.getServer()
                .getPluginManager().getPlugin("WorldEdit");
        if(worldEditPlugin != null)
        {
            if(worldEditPlugin.getDescription().getVersion().startsWith("6."))
            {
                logger.info("[BookShelf] WorldEdit found and hooked.");
                this.usingWorldEdit = true;
                WorldEdit.getInstance().getEventBus()
                        .register(new WorldEditHandler());
            }
            else
            {
                logger.info("[BookShelf] WorldEdit is outdated! You must use WorldEdit version 6.0 or later for BookShelf support.");
                usingWorldEdit = false;
            }
        }
        else
            this.usingWorldEdit = false;
        return worldEditPlugin != null;
    }
    
    //GETTERS AND SETTERS ----------------------------------------------------------
    
    public boolean usingVaultEconomy()
    {
        return this.usingVaultEconomy;
    }
    
    public Economy getVaultEconomy()
    {
        return this.vaultEconomy;
    }
    
    public boolean usingTowny()
    {
        return this.usingTowny;
    }
    
    public Towny getTownyPlugin()
    {
        return this.townyPlugin;
    }
    
    public YamlConfiguration getTownyConfig()
    {
        return this.townyConfig;
    }
    
    public TownyHandler getTownyHandler()
    {
        return this.townyHandler;
    }
    
    public TownyCommandHandler getTownyCommandHandler()
    {
        return this.townyCommandHandler;
    }
    
    public boolean usingLWC()
    {
        return this.usingLWC;
    }
    
    public LWCPlugin getLWCPlugin()
    {
        return this.lwcPlugin;
    }
    
    public boolean usingWorldGuard()
    {
        return this.usingWorldGuard;
    }
    
    public WorldGuardPlugin getWorldGuardPlugin()
    {
        return this.worldGuardPlugin;
    }
    
    public boolean usingWorldEdit()
    {
        return this.usingWorldEdit;
    }
    
    public WorldEditPlugin getWorldEditPlugin()
    {
        return this.worldEditPlugin;
    }
    
}
