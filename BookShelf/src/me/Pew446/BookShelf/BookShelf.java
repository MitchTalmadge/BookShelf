package me.Pew446.BookShelf;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import me.Pew446.BookShelf.BookListener;
import me.Pew446.BookShelf.DBUpdates.DBUpdate;
import me.Pew446.BookShelf.LWC.LWCPluginHandler;
import me.Pew446.BookShelf.Towny.TownyCommands;
import me.Pew446.BookShelf.Towny.TownyHandler;
import me.Pew446.BookShelf.WorldEdit.WorldEdit_EditSessionFactoryHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_6_R2.command.CraftBlockCommandSender;
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

import me.Pew446.SimpleSQL.Database;
import me.Pew446.SimpleSQL.MySQL;
import me.Pew446.SimpleSQL.SQLite;

import net.milkbowl.vault.economy.Economy;

public class BookShelf extends JavaPlugin{

	/* SETUP */
	static FileConfiguration config;
	public static BookShelf plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static BookListener BookListener;
	public static final int currentDatabaseVersion = 2;

	public static ArrayList<Integer> records = new ArrayList<Integer>(Arrays.asList(
			Material.RECORD_3.getId(),
			Material.RECORD_4.getId(),
			Material.RECORD_5.getId(),
			Material.RECORD_6.getId(),
			Material.RECORD_7.getId(),
			Material.RECORD_8.getId(),
			Material.RECORD_9.getId(),
			Material.RECORD_10.getId(),
			Material.RECORD_11.getId(),
			Material.RECORD_12.getId(),
			Material.GOLD_RECORD.getId(),
			Material.GREEN_RECORD.getId()));
	public static ArrayList<Integer> allowedItems = new ArrayList<Integer>(Arrays.asList(
			Material.BOOK.getId(), 
			Material.BOOK_AND_QUILL.getId(), 
			Material.WRITTEN_BOOK.getId(),
			Material.ENCHANTED_BOOK.getId(),
			Material.PAPER.getId(),
			Material.MAP.getId(),
			Material.EMPTY_MAP.getId()));

	/* ECONOMY */
	static Economy economy;

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

	/* TOWNY */
	static Towny towny;
	public boolean useTowny = false;
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
	public void onDisable() {

		try {
			if(me.Pew446.BookShelf.BookListener.r != null)
				close(me.Pew446.BookShelf.BookListener.r);
			if(r != null)
				close(r);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		getdb().close();

		if(this.useTowny)
			TownyHandler.saveConfig();

	}
	@Override
	public void onEnable() {
		allowedItems.addAll(records);
		BookListener = new BookListener(this);
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
				LWCPluginHandler = new LWCPluginHandler(LWC);
				LWCPluginHandler.init();
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
		}
		
		/*if(setupWorldEdit())
		{
			logger.info("[BookShelf] WorldEdit found and hooked.");
		    worldEdit.getWorldEdit().setEditSessionFactory(new WorldEdit_EditSessionFactoryHandler());
		}*/
		
		getServer().getPluginManager().registerEvents(BookListener, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("["+pdfFile.getName() + "] Enabled BookShelf V" + pdfFile.getVersion());

	}

	public static void close(ResultSet r) throws SQLException
	{
		r.close();
		getdb().setShouldWait(false);
		synchronized (getdb().getSynchronized())
		{
			getdb().getSynchronized().notify();
		}
	}

	private void setupAutoToggle() {
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
			this.autoToggleServerWide = config.getBoolean("auto_toggle.server_wide");
		}

		if(config.get("auto_toggle.different_players") != null)
		{
			this.autoToggleDiffPlayers = config.getBoolean("auto_toggle.different_players");
		}

		if(config.get("auto_toggle.name_list") != null)
		{
			this.autoToggleNameList = config.getList("auto_toggle.name_list");
		}
	}
	private void loadTownyConfig() {
		if(!townyConfigPath.exists())
			saveResource("towny.yml", false);
		townyConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "towny.yml"));
	}

	private boolean setupEconomy()
	{
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");

		if (plugin != null)
		{
			@SuppressWarnings("rawtypes")
			RegisteredServiceProvider economyProvider = getServer()
			.getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null)
			{
				economy = (Economy) economyProvider.getProvider();
			}
		}
		return (economy != null);
	}
	private boolean setupLWC()
	{
		LWC = (LWCPlugin) getServer().getPluginManager().getPlugin("LWC");
		return (plugin != null);
	}

	private boolean setupTowny() {
		towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
		return towny != null;
	}

	private boolean setupWorldEdit() {
		worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
		return worldEdit != null;
	}
	
	private boolean setupWorldGuard() {
		worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
		return worldGuard != null;
	}

	public boolean isUsingTowny() {
		return this.useTowny;
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
			catch (Exception e) 
			{
				logger.info(e.getMessage());
				getPluginLoader().disablePlugin(this);
			}
		}
		else
		{
			sqlite = new SQLite(logger, "BookShelf", getDataFolder().getAbsolutePath(), "Shelves");
			try 
			{
				sqlite.open();
			} 
			catch (Exception e) 
			{
				logger.info(e.getMessage());
				getPluginLoader().disablePlugin(this);
			}
		}
	}
	
	private int getDbVersion()
	{
		try {
			sqlDoesVersionExist();
			r = getdb().query("SELECT * FROM version");
			r.next();
			int version = r.getInt("version");
			close(r);
			return version;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public void sqlDoesDatabaseExist()
	{
		try {
			sqlDoesVersionExist();
			if(getDbVersion() == 1)
				doDelimiterFix();
			updateDb();
			logger.info("[BookShelf] Current Database Version: "+getDbVersion());
			boolean enable = config.getBoolean("database.mysql_enabled");
			if(enable) //MYSQL
			{
				getdb().query("CREATE TABLE IF NOT EXISTS items (id INT NOT NULL AUTO_INCREMENT, x INT, y INT, z INT, title VARCHAR(128), author VARCHAR(128), lore TEXT, damage INT, type INT, loc INT, amt INT, pages TEXT, PRIMARY KEY (id));");
				getdb().query("CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS enchant (x INT, y INT, z INT, loc INT, type VARCHAR(64), level INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS maps (x INT, y INT, z INT, loc INT, durability SMALLINT);");
				getdb().query("CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name VARCHAR(64));");
			}
			else //SQLITE
			{
				getdb().query("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, x INT, y INT, z INT, title TEXT, author TEXT, lore TEXT, damage INT, type INT, loc INT, amt INT, pages TEXT);");
				getdb().query("CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS enchant (x INT, y INT, z INT, loc INT, type STRING, level INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS maps (x INT, y INT, z INT, loc INT, durability SMALLINT);");
				getdb().query("CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
				getdb().query("CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name TEXT);");	
			}
			logger.info("[BookShelf] Database Loaded.");
		} catch (SQLException e) {
			System.out.println("[BookShelf] Database could not load! Check server log.");
			e.printStackTrace();
		}

	}	

	private void doDelimiterFix() {
		try {
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
					BookShelf.getdb().query("UPDATE items SET pages='"+pages+"' WHERE id="+ids.get(i)+";");
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void updateDb() {

		try {
			r = getdb().query("SELECT * FROM version");
			r.next();
			int version = r.getInt("version");
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
			default:
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void sqlDoesVersionExist()
	{
		if(usingMySQL())
		{
			try {
				r = getdb().query("SHOW TABLES LIKE 'items';");
				if(!r.next())
				{
					close(r); //Looks like we are making a new database.
					logger.info("[BookShelf] Creating Database...");
					getdb().query("CREATE TABLE IF NOT EXISTS version (version INT);");
					getdb().query("INSERT INTO version (version) VALUES("+currentDatabaseVersion+");");
				}
				else
				{ //We aren't making a new database, but version doesn't exist.... Let's add it.
					close(r);
					logger.info("[BookShelf] Adding version to Database...");
					r = getdb().query("SHOW TABLES LIKE 'version';");
					if(!r.next())
					{
						close(r);
						getdb().query("CREATE TABLE IF NOT EXISTS version (version INT);");
						getdb().query("INSERT INTO version (version) VALUES(0);");
					}
					else
					{
						close(r);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				r = getdb().query("SELECT name FROM sqlite_master WHERE type='table' AND name='items';");
				if(!r.next())
				{
					close(r); //Looks like we are making a new database.
					logger.info("[BookShelf] Creating Database...");
					getdb().query("CREATE TABLE IF NOT EXISTS version (version INT);");
					getdb().query("INSERT INTO version (version) VALUES("+currentDatabaseVersion+");");
				}
				else
				{ //We aren't making a new database, but version doesn't exist.... Let's add it.
					close(r);
					r = getdb().query("SELECT name FROM sqlite_master WHERE type='table' AND name='version';");
					if(!r.next())
					{
						close(r);
						logger.info("[BookShelf] Adding version to Database...");
						getdb().query("CREATE TABLE IF NOT EXISTS version (version INT);");
						getdb().query("INSERT INTO version (version) VALUES(0);");
					}
					else
					{
						close(r);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConsole(CommandSender sender)
	{
		return sender instanceof ConsoleCommandSender;
	}

	public boolean isCommandBlock(CommandSender sender)
	{
		return sender instanceof CraftBlockCommandSender;
	}

	public boolean isPlayer(CommandSender sender)
	{
		return sender instanceof Player;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("bsunlimited") || cmd.getName().equalsIgnoreCase("bsu"))
		{
			if(!this.isPlayer(sender))
			{
				sender.sendMessage("This command may only be used by players.");
				return true;
			}
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.unlimited"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					if(useTowny)
					{
						Resident res = TownyHandler.convertToResident(p);
						if(!TownyHandler.checkCanDoAction(loc.getBlock(), res, TownyHandler.UNLIMITED))
						{
							sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
							return true;
						}
					}
					try {
						r = getdb().query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!r.next())
						{
							close(r);
							BookShelf.getdb().query("INSERT INTO copy (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0);");
						}
						else
						{
							close(r);
						}
						r = getdb().query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						r.next();
						if(r.getInt("bool") == 1)
						{
							close(r);
							p.sendMessage("The bookshelf you are looking at is now §6limited.");
							getdb().query("UPDATE copy SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							close(r);
							p.sendMessage("The bookshelf you are looking at is now §6unlimited.");
							getdb().query("UPDATE copy SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else
				{
					p.sendMessage("§cPlease look at a bookshelf when using this command");
				}
			}
			else
			{
				p.sendMessage("§cYou don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bstoggle") || cmd.getName().equalsIgnoreCase("bst"))
		{
			if(this.isConsole(sender) || this.isCommandBlock(sender))
			{
				if(!(args.length >= 1))
				{
					sender.sendMessage("§cMust include a shelf name!");
					return false;
				}
				else
				{
					String name = "";
					for(int i = 0;i<args.length;i++)
					{
						name += args[i]+" ";
					}

					toggleBookShelvesByName(name);
					sender.sendMessage("All bookshelves with the name §6"+name+"§fhave been toggled.");
				}
			}
			else
			{
				Player p = Bukkit.getPlayer(sender.getName());
				if(p.hasPermission("bookshelf.toggle"))
				{
					if(!(args.length >= 1))
					{
						Location loc = p.getTargetBlock(null, 10).getLocation();
						if(loc.getBlock().getType() == Material.BOOKSHELF)
						{
							if(useTowny)
							{
								Resident res = TownyHandler.convertToResident(p);
								if(!TownyHandler.checkCanDoAction(loc.getBlock(), res, TownyHandler.TOGGLE))
								{
									sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
									return true;
								}
							}
							int result = toggleBookShelf(loc);
							if(result == -1)
								p.sendMessage("§cAn error occured while processing this command. Check server logs.");
							if(result == 0)
								p.sendMessage("The bookshelf you are looking at is now §cdisabled.");
							else
								p.sendMessage("The bookshelf you are looking at is now §aenabled.");
						}
						else
						{
							p.sendMessage("§cPlease look at a bookshelf when using this command.");
						}
					}
					else
					{
						if(!p.isOp())
						{
							p.sendMessage("§cYou must be an op to toggle shelves by name.");
							return true;
						}
						String name = "";
						for(int i = 0;i<args.length;i++)
						{
							name += args[i]+" ";
						}

						toggleBookShelvesByName(name);
						sender.sendMessage("All bookshelves with the name §6"+name+"§fhave been toggled.");
					}
				}
				else
				{
					p.sendMessage("§cYou don't have permission to use this command!");
				}
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsreload") || cmd.getName().equalsIgnoreCase("bsr"))
		{
			CommandSender p = sender;
			if(p.hasPermission("bookshelf.reload"))
			{
				this.reloadConfig();
				config = getConfig();
				saveDefaultConfig();
				this.loadTownyConfig();
				this.setupAutoToggle();

				if(config.getBoolean("lwc_support.enabled"))
				{
					if(LWCPluginHandler == null)
					{
						LWCEnabled = true;
						LWCPluginHandler = new LWCPluginHandler(LWC);
						LWCPluginHandler.init();
					}
					else if(LWCEnabled == false)
						LWCEnabled = true;
				}
				else
					if(LWCPluginHandler != null)
						LWCEnabled = false;

				p.sendMessage("§aBookShelf config successfully reloaded.");
			}
			else
			{
				p.sendMessage("§cYou don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsshop") || cmd.getName().equalsIgnoreCase("bss"))
		{
			if(!this.isPlayer(sender))
			{
				sender.sendMessage("This command may only be used by players.");
				return true;
			}
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.shop"))
			{
				Integer price;
				if(!(args.length >= 1))
				{
					price = config.getInt("economy.default_price");
				}
				else
				{
					if(args[0].length() > 9)
						price = config.getInt("economy.default_price");
					else
						price = Integer.parseInt(args[0]);
				}
				if(economy == null)
				{
					p.sendMessage("§cVault is not installed! Aborting...");
					return true;
				}
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					if(useTowny)
					{
						Resident res = TownyHandler.convertToResident(p);
						if(!TownyHandler.checkCanDoAction(loc.getBlock(), res, TownyHandler.SHOP))
						{
							sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
							return true;
						}
					}
					try {
						r = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!r.next())
						{
							close(r);
							getdb().query("INSERT INTO shop (x,y,z,bool,price) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0, "+config.getInt("economy.default_price")+");");
						}
						else
						{
							close(r);
						}
						r = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						r.next();
						if(r.getInt("bool") == 1 & !(args.length >= 1))
						{
							close(r);
							r = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!r.next())
							{
								close(r);
								getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+config.getString("default_shelf_name")+"');");
							}
							else
							{
								close(r);
								getdb().query("UPDATE names SET name='"+config.getString("default_shelf_name")+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
							p.sendMessage("The bookshelf you are looking at is no longer a shop.");
							getdb().query("UPDATE shop SET bool=0, price="+price+" WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							close(r);
							r = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!r.next())
							{
								close(r);
								getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+config.getString("default_shop_name").replace("%$", price+" "+BookShelf.economy.currencyNamePlural())+"');");
							}
							else
							{
								close(r);
								getdb().query("UPDATE names SET name='"+config.getString("default_shop_name").replace("%$", price+" "+BookShelf.economy.currencyNamePlural())+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
							p.sendMessage("The bookshelf you are looking at is now a shop selling at §6"+price+" "+economy.currencyNamePlural()+" §feach.");
							getdb().query("UPDATE shop SET bool=1, price="+price+" WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else
				{
					p.sendMessage("§cPlease look at a bookshelf when using this command");
				}
			}
			else
			{
				p.sendMessage("§cYou don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsname") || cmd.getName().equalsIgnoreCase("bsn"))
		{
			if(!this.isPlayer(sender))
			{
				sender.sendMessage("This command may only be used by players.");
				return true;
			}
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.name"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				String name;
				if(!(args.length >= 1))
				{
					name = config.getString("default_shelf_name");
				}
				else
				{
					int price = 0;
					try
					{
						r = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						r.next();
						price = r.getInt("price");
						close(r);
					} catch (SQLException e)
					{
						e.printStackTrace();
					}
					String name1 = "";
					if(economy != null)
					{
						for(int i = 0;i<args.length;i++)
						{
							name1 += args[i].replace("%$", price+" "+BookShelf.economy.currencyNamePlural())+" ";
						}
					}
					else
					{
						for(int i = 0;i<args.length;i++)
						{
							name1 += args[i]+" ";
						}
					}
					name1.trim();
					if(name1.length() > 32)
					{
						name = name1.substring(0, 31);
					}
					else
					{
						name = name1;
					}
					name = ChatColor.translateAlternateColorCodes('&', name);
				}
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					if(useTowny)
					{
						Resident res = TownyHandler.convertToResident(p);
						if(!TownyHandler.checkCanDoAction(loc.getBlock(), res, TownyHandler.NAME))
						{
							sender.sendMessage("§cYou do not have permissions to use that command for this plot.");
							return true;
						}
					}
					try {
						r = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!r.next())
						{
							close(r);
							getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+name+"');");
							p.sendMessage("The name of the bookshelf you are looking at has been changed to "+name);
						}
						else
						{
							close(r);
							getdb().query("UPDATE names SET name='"+name+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							p.sendMessage("The name of the bookshelf you are looking at has been changed to "+name);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else
				{
					p.sendMessage("§cPlease look at a bookshelf when using this command");
				}
			}
			else
			{
				p.sendMessage("§cYou don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bstowny"))
		{
			if(!this.isPlayer(sender))
			{
				sender.sendMessage("This command may only be used by players.");
				return true;
			}
			if(useTowny)
				if(((Player)sender).hasPermission("bookshelf.towny"))
					return TownyCommands.onCommand(sender, label, args, this);
				else
				{
					sender.sendMessage("§cYou don't have permission to use this command!");
					return true;
				}

			sender.sendMessage("§cTowny Support is not enabled on this server.");
			return true;
		}
		/*		else if(cmd.getName().equalsIgnoreCase("bsdisplay") || cmd.getName().equalsIgnoreCase("bsd"))
		{
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.display"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
						r = getdb().query("SELECT * FROM display WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!r.next())
						{
							getdb().query("INSERT INTO display (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 1);");
							p.sendMessage("The name of the bookshelf you are looking at has been changed.");
							close(r);
						}
						else
						{
							if(r.getInt("bool") == 1)
							{
								close(r);
								p.sendMessage("The bookshelf you are looking at is no longer a display.");
								getdb().query("UPDATE display SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
							else
							{
								close(r);
								p.sendMessage("The bookshelf you are looking at is now a display.");
								getdb().query("UPDATE display SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else
				{
					p.sendMessage("Please look at a bookshelf when using this command");
				}
			}
			else
			{
				p.sendMessage("§cYou don't have permission to use this command!");
			}
			return true;
		}*/
		return false; 
	}

	public static int toggleBookShelf(Location loc) {
		try 
		{
			r = getdb().query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
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
				getdb().query("INSERT INTO enable (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", "+def+");");
			}
			else
			{
				close(r);
			}
			r = getdb().query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
			r.next();
			if(r.getInt("bool") == 1)
			{
				close(r);
				getdb().query("UPDATE enable SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
				return 0;
			}
			else
			{
				close(r);
				getdb().query("UPDATE enable SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
				return 1;
			}
		} catch (SQLException e) {
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
			if(name.equals(config.getString("default_shelf_name")+" "))
				name = name.substring(0, name.length()-1);
		}

		try 
		{
			r = getdb().query("SELECT * FROM names WHERE name='"+name+"';");
			List<Vector> vecs = new ArrayList<Vector>();
			HashMap<Vector, Boolean> selmap = new HashMap<Vector, Boolean>();

			while(r.next())
			{
				Vector loc = new Vector(r.getInt("x"), r.getInt("y"), r.getInt("z"));
				vecs.add(loc);
			}
			close(r);
			for(Vector loc : vecs)
			{
				r = getdb().query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
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
				getdb().query("UPDATE enable SET bool="+bool2+" WHERE x="+vec.getX()+" AND y="+vec.getY()+" AND z="+vec.getZ()+";");
			}
			getdb().getConnection().commit();
			getdb().getConnection().setAutoCommit(true);

		} catch (SQLException e){
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
	public static void eraseData(Location location) {

	}
}