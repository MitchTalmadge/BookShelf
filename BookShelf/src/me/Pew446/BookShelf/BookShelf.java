package me.Pew446.BookShelf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.*;
import me.Pew446.BookShelf.BookListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.griefcraft.lwc.LWCPlugin;

import net.milkbowl.vault.economy.Economy;

public class BookShelf extends JavaPlugin{
	static FileConfiguration config;
	public static BookShelf plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final BookListener BookListener = new BookListener(this);
	public static MySQL mysql;
	public static SQLite sqlite;
	public static Economy economy;
	public static LWCPlugin LWC;
	static ResultSet r;
	
	@Override
	public void onDisable() {
		try {
			if(me.Pew446.BookShelf.BookListener.r != null)
				me.Pew446.BookShelf.BookListener.r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean enable = config.getBoolean("database.mysql_enabled");
		if(enable)
		{
			try 
			{
				mysql.close();
		    } 
			catch (Exception e) 
		    {
				logger.info(e.getMessage());
				getPluginLoader().disablePlugin(this);
		    }
		}
		else
		{
			try 
			{
				sqlite.close();
		    } 
			catch (Exception e) 
		    {
				logger.info(e.getMessage());
				getPluginLoader().disablePlugin(this);
		    }
		}
	}
	@Override
	public void onEnable() {
		config = getConfig();
		saveDefaultConfig();
		sqlConnection();
		sqlDoesDatabaseExist();
		if(setupEconomy())
		{
			this.logger.info("[BookShelf] Vault found and hooked.");
		}
		if(setupLWC())
		{
			this.logger.info("[BookShelf] LWC found and hooked.");
		}
		getServer().getPluginManager().registerEvents(this.BookListener, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("["+pdfFile.getName() + "] Enabled BookShelf v" + pdfFile.getVersion());
		
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
		Plugin plugin = getServer().getPluginManager().getPlugin("LWC");
		LWC = (LWCPlugin) plugin;
		return (plugin != null);
	}
	public void sqlConnection() 
	{
		boolean enable = config.getBoolean("database.mysql_enabled");
		String host = config.getString("database.hostname");
		int port = config.getInt("database.port");
		String dbname = config.getString("database.database");
		String user = config.getString("database.username");
		String pass = config.getString("database.password");
		if(enable)
		{
			mysql = new MySQL(logger, "BookShelf", host, port, dbname, user, pass);
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
			sqlite = new SQLite(logger, "BookShelf", this.getDataFolder().getAbsolutePath(), "Shelves");
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
	public void sqlDoesDatabaseExist()
	{

        	try {
        		boolean enable = config.getBoolean("database.mysql_enabled");
        		if(enable)
        		{
        			getdb().query("CREATE TABLE IF NOT EXISTS items (id INT NOT NULL AUTO_INCREMENT, x INT, y INT, z INT, title VARCHAR(32), author VARCHAR(32), type INT, loc INT, amt INT, primary key (id));");
    				getdb().query("CREATE TABLE IF NOT EXISTS pages (id INT, text VARCHAR(1000));");
    				getdb().query("CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS enchant (id INT, type VARCHAR(64), level INT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS maps (id INT, durability SMALLINT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
    				getdb().query("CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name VARCHAR(64));");
        		}
        		else
        		{
	        		getdb().query("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, x INT, y INT, z INT, title STRING, author STRING, type INT, loc INT, amt INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS pages (id INT, text STRING);");
					getdb().query("CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS enchant (id INT, type STRING, level INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS maps (id INT, durability SMALLINT);");
					getdb().query("CREATE TABLE IF NOT EXISTS shop (x INT, y INT, z INT, bool INT, price INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS display (x INT, y INT, z INT, bool INT);");
					getdb().query("CREATE TABLE IF NOT EXISTS names (x INT, y INT, z INT, name STRING);");	
        		}
        		
        	} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        System.out.println("[BookShelf] Database Loaded.");
	}	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("bsunlimited") || cmd.getName().equalsIgnoreCase("bsu"))
		{
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.unlimited"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
						ResultSet re = getdb().query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!re.next())
						{
							re.close();
							BookShelf.getdb().query("INSERT INTO copy (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0);");
						}
						else
						{
							re.close();
						}
						re = getdb().query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						re.next();
						if(re.getInt("bool") == 1)
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now limited.");
							getdb().query("UPDATE copy SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now unlimited.");
							getdb().query("UPDATE copy SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
				p.sendMessage("You don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bstoggle") || cmd.getName().equalsIgnoreCase("bst"))
		{
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.toggle"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try 
					{
						ResultSet re = getdb().query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!re.next())
						{
							int def = 1;
							if(getConfig().getBoolean("default_openable"))
							{
								def = 1;
							}
							else
							{
								def = 0;
							}
							re.close();
							getdb().query("INSERT INTO enable (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", "+def+");");
						}
						else
						{
							re.close();
						}
						re = getdb().query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						re.next();
						if(re.getInt("bool") == 1)
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now disabled.");
							getdb().query("UPDATE enable SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now enabled.");
							getdb().query("UPDATE enable SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
				p.sendMessage("You don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsreload") || cmd.getName().equalsIgnoreCase("bsr"))
		{
			CommandSender p = sender;
			if(p.hasPermission("bookshelf.reload"))
			{
				this.reloadConfig();
				p.sendMessage("BookShelf config successfully reloaded.");
			}
			else
			{
				p.sendMessage("You don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsshop") || cmd.getName().equalsIgnoreCase("bss"))
		{
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
					p.sendMessage("Vault is not installed! Aborting...");
					return true;
				}
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
						ResultSet re = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!re.next())
						{
							re.close();
							getdb().query("INSERT INTO shop (x,y,z,bool,price) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0, "+config.getInt("economy.default_price")+");");
						}
						else
						{
							re.close();
						}
						re = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						re.next();
						if(re.getInt("bool") == 1 & !(args.length >= 1))
						{
							re.close();
							re = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!re.next())
							{
								re.close();
								getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+config.getString("default_shelf_name")+"');");
							}
							else
							{
								re.close();
								getdb().query("UPDATE names SET name='"+config.getString("default_shelf_name")+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
							p.sendMessage("The bookshelf you are looking at is no longer a shop.");
							getdb().query("UPDATE shop SET bool=0, price="+price+" WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							re.close();
							re = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!re.next())
							{
								re.close();
								getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+config.getString("default_shop_name").replace("%$", price+" "+BookShelf.economy.currencyNamePlural())+"');");
							}
							else
							{
								re.close();
								getdb().query("UPDATE names SET name='"+config.getString("default_shop_name").replace("%$", price+" "+BookShelf.economy.currencyNamePlural())+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							}
							p.sendMessage("The bookshelf you are looking at is now a shop selling at "+price+" "+economy.currencyNamePlural()+" each.");
							getdb().query("UPDATE shop SET bool=1, price="+price+" WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
				p.sendMessage("You don't have permission to use this command!");
			}
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("bsname") || cmd.getName().equalsIgnoreCase("bsn"))
		{
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
						ResultSet re = getdb().query("SELECT * FROM shop WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						re.next();
						price = re.getInt("price");
						re.close();
					} catch (SQLException e)
					{
						// TODO Auto-generated catch block
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
				}
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
						ResultSet re = getdb().query("SELECT * FROM names WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(!re.next())
						{
							re.close();
							getdb().query("INSERT INTO names (x,y,z,name) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", '"+name+"');");
							p.sendMessage("The name of the bookshelf you are looking at has been changed.");
						}
						else
						{
							re.close();
							getdb().query("UPDATE names SET name='"+name+"' WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							p.sendMessage("The name of the bookshelf you are looking at has been changed.");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
				p.sendMessage("You don't have permission to use this command!");
			}
			return true;
		}
//		else if(cmd.getName().equalsIgnoreCase("bsdisplay") || cmd.getName().equalsIgnoreCase("bsd"))
//		{
//			Player p = Bukkit.getPlayer(sender.getName());
//			if(p.hasPermission("bookshelf.display"))
//			{
//				Location loc = p.getTargetBlock(null, 10).getLocation();
//				if(loc.getBlock().getType() == Material.BOOKSHELF)
//				{
//					try {
//						ResultSet re = getdb().query("SELECT * FROM display WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
//						if(!re.next())
//						{
//							getdb().query("INSERT INTO display (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 1);");
//							p.sendMessage("The name of the bookshelf you are looking at has been changed.");
//							re.close();
//						}
//						else
//						{
//							if(re.getInt("bool") == 1)
//							{
//								re.close();
//								p.sendMessage("The bookshelf you are looking at is no longer a display.");
//								getdb().query("UPDATE display SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
//							}
//							else
//							{
//								re.close();
//								p.sendMessage("The bookshelf you are looking at is now a display.");
//								getdb().query("UPDATE display SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
//							}
//						}
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else
//				{
//					p.sendMessage("Please look at a bookshelf when using this command");
//				}
//			}
//			else
//			{
//				p.sendMessage("You don't have permission to use this command!");
//			}
//			return true;
//		}
		return false; 
	}

	public static Database getdb()
	{
		boolean enable = config.getBoolean("database.mysql_enabled");
		if(enable)
		{
			if(mysql.checkConnection())
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