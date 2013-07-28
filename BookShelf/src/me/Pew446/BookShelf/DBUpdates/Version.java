package me.Pew446.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import me.Pew446.BookShelf.BookShelf;

public abstract class Version {
	private ResultSet r;
	private Logger logger;

	public Version(Logger logger, ResultSet r)
	{
		this.logger = logger;
		this.r = r;
	}
	
	private void close(ResultSet r) throws SQLException
	{
		BookShelf.close(r);
	}
	
	public abstract void doUpdate();
}
