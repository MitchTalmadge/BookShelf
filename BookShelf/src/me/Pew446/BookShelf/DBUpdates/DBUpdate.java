package me.Pew446.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import me.Pew446.BookShelf.BookShelf;

public class DBUpdate {
	
	private Logger logger;
	private ResultSet r;
	private Version version;

	public DBUpdate(Logger logger, ResultSet r)
	{
		this.logger = logger;
		this.r = r;
	}
	
	public void close(ResultSet r) throws SQLException
	{
		BookShelf.close(r);
	}
	
	public void doUpdate(int currentVersion)
	{
		switch(currentVersion)
		{
		case 0:
			version = new Version0To1(logger, r);
			version.doUpdate();
			break;
		case 1:
			version = new Version1To2(logger, r);
			version.doUpdate();
			break;
		case 2:
			version = new Version2To3(logger, r);
			version.doUpdate();
			break;
		}
	}

}
