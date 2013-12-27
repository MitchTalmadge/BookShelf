package me.Pew446.BookShelf.DBUpdates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.Pew446.BookShelf.BookShelf;
import me.Pew446.BookShelf.OldIDEnum;

public class Version2To3 extends Version{

	public Version2To3(Logger logger, ResultSet r)
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
		logger.info("[BookShelf] Updating Database to Version 3.");
		Map<Integer, Integer> typeID = new HashMap<Integer, Integer>();
		logger.info("[BookShelf] Copying item types...");
		r = BookShelf.getdb().query("SELECT * FROM items");
		while(r.next())
		{
			typeID.put(r.getInt("id"), r.getInt("type"));
		}
		r.close();
		
		logger.info("[BookShelf] Altering table 'items'...");
		if(BookShelf.usingMySQL())
		{
			BookShelf.getdb().query("ALTER TABLE items ADD enumType VARCHAR(64);");
		}
		else
		{
			BookShelf.getdb().query("ALTER TABLE items ADD enumType TEXT;");
		}
		
		logger.info("[BookShelf] Updating table 'items'...");
		for(int i=0; i<typeID.size(); i++)
		{
			for (Map.Entry<Integer,Integer> entry : typeID.entrySet()) {
			    BookShelf.getdb().query("UPDATE items SET enumType='"+OldIDEnum.getMaterialById(entry.getValue()).name()+"' WHERE id="+entry.getKey());
			}
		}
		
		BookShelf.getdb().query("UPDATE version SET version=3");
		logger.info("[BookShelf] Update to Version 3 Complete.");
		} 
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

}
