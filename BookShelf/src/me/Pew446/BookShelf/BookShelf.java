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
		mysql = new SQLite(logger, "BookShelf", "Shelves", this.getDataFolder().getAbsolutePath());
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
        if(mysql.checkTable("items") == false)
        {
        	mysql.createTable("CREATE TABLE items (id INTEGER PRIMARY KEY, x INT, y INT, z INT, title STRING, author STRING, type INT, loc INT, amt INT);");
        	BookShelf.mysql.commit();
        }
        if(mysql.checkTable("pages") == false)
        {
        	mysql.createTable("CREATE TABLE pages (id INT, text STRING);");
        	BookShelf.mysql.commit();
        }
        if(mysql.checkTable("copy") == false)
        {
        	mysql.createTable("CREATE TABLE copy (x INT, y INT, z INT, bool INT);");
        	BookShelf.mysql.commit();
        }
        if(mysql.checkTable("enchant") == false)
        {
        	mysql.createTable("CREATE TABLE enchant (id INT, type STRING, level INT);");
        	BookShelf.mysql.commit();
        }
        if(mysql.checkTable("maps") == false)
        {
        	mysql.createTable("CREATE TABLE maps (id INT, durability SMALLINT);");
        	BookShelf.mysql.commit();
        }
        System.out.println("BookShelf Database Loaded.");
	}	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("bookshelf") || cmd.getName().equalsIgnoreCase("bs")){ // If the player typed /basic then do the following...
			Player p = Bukkit.getPlayer(sender.getName());
			if(p.hasPermission("bookshelf"))
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				if(loc.getBlock().getType() == Material.BOOKSHELF)
				{
					ResultSet re = mysql.query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
					try {
						if(re.getInt("bool") == 1)
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now limited.");
							mysql.query("UPDATE copy SET bool=0 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							BookShelf.mysql.commit();
						}
						else
						{
							re.close();
							p.sendMessage("The bookshelf you are looking at is now unlimited.");
							mysql.query("UPDATE copy SET bool=1 WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							BookShelf.mysql.commit();
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
