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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BookShelf extends JavaPlugin{
	static FileConfiguration config;
	public static BookShelf plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final BookListener BookListener = new BookListener(this);
	public static SQLite mysql;
	static ResultSet r;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		try {
			if(me.Pew446.BookShelf.BookListener.r != null)
				me.Pew446.BookShelf.BookListener.r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysql.close();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}
	@Override
	public void onEnable() {
		config = getConfig();
		saveDefaultConfig();
		sqlConnection();
		sqlDoesDatabaseExist();
		getServer().getPluginManager().registerEvents(this.BookListener, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
		
	}
	public void sqlConnection() 
	{
		mysql = new SQLite(logger, "BookShelf", this.getDataFolder().getAbsolutePath(), "Shelves");
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
	public void sqlDoesDatabaseExist()
	{

        	try {
        		mysql.query("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, x INT, y INT, z INT, title STRING, author STRING, type INT, loc INT, amt INT);");
				mysql.query("CREATE TABLE IF NOT EXISTS pages (id INT, text STRING);");
				mysql.query("CREATE TABLE IF NOT EXISTS copy (x INT, y INT, z INT, bool INT);");
				mysql.query("CREATE TABLE IF NOT EXISTS enable (x INT, y INT, z INT, bool INT);");
				mysql.query("CREATE TABLE IF NOT EXISTS enchant (id INT, type STRING, level INT);");
				mysql.query("CREATE TABLE IF NOT EXISTS maps (id INT, durability SMALLINT);");
        	} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        System.out.println("BookShelf Database Loaded.");
	}	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("unlimited") || cmd.getName().equalsIgnoreCase("bsu"))
		{
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.unlimited"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
					ResultSet re = mysql.query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(re.getInt("bool") == 1)
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now limited.");
							mysql.query("UPDATE copy SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now unlimited.");
							mysql.query("UPDATE copy SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
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
		else if(cmd.getName().equalsIgnoreCase("toggle") || cmd.getName().equalsIgnoreCase("bst"))
		{
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf.toggle"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					try {
					ResultSet re = mysql.query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						if(re.getInt("bool") == 1)
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now disabled.");
							mysql.query("UPDATE enable SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
						}
						else
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now enabled.");
							mysql.query("UPDATE enable SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
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
		return false; 
	}
}
