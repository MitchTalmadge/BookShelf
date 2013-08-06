package me.Pew446.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.Pew446.BookShelf.BookShelf;

import org.bukkit.util.Vector;

public class Version1To2 extends Version{

	public Version1To2(Logger logger, ResultSet r)
	{
		super(logger, r);
	}
	
	public void close(ResultSet r) throws SQLException
	{
		BookShelf.close(r);
	}
	
	@Override
	public void doUpdate() {
		try
		{
		logger.info("[BookShelf] Updating Database to Version 2.");
		BookShelf.getdb().query("UPDATE version SET version=2");
		logger.info("[BookShelf] Update to Version 2 Complete.");
		} 
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

}
