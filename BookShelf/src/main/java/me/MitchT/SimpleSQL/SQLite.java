/**
 * @author Mitch Talmadge (AKA Pew446)
 * 
 *         Date Created:
 *         May 12, 2013
 */

package me.MitchT.SimpleSQL;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

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
public class SQLite extends Database
{
    
    private enum Statements implements StatementsList
    {
        SELECT("SELECT"),
        INSERT("INSERT"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        REPLACE("REPLACE"),
        CREATE("CREATE"),
        ALTER("ALTER"),
        DROP("DROP"),
        ANALYZE("ANALYZE"),
        ATTACH("ATTACH"),
        BEGIN("BEGIN"),
        DETACH("DETACH"),
        END("END"),
        EXPLAIN("EXPLAIN"),
        INDEXED("INDEXED"),
        PRAGMA("PRAGMA"),
        REINDEX("REINDEX"),
        RELEASE("RELEASE"),
        SAVEPOINT("SAVEPOINT"),
        VACUUM("VACUUM"),
        
        LINE_COMMENT("--"),
        BLOCK_COMMENT("/*");
        
        private String string;
        
        private Statements(String string)
        {
            this.string = string;
        }
        
        public String toString()
        {
            return string;
        }
    }
    
    private SQLiteUtils utils;
    
    public SQLite(Logger logger, String prefix, String directory,
            String filename)
    {
        super(prefix, "[SQLite] ", logger);
        this.utils = new SQLiteUtils(this);
        this.driver = DBList.SQLite;
        setFile(directory, filename);
    }
    
    private File getFile()
    {
        return utils.getFile();
    }
    
    private void setFile(String directory, String filename)
    {
        utils.setFile(directory, filename);
    }
    
    private void setFile(String directory, String filename, String extension)
    {
        utils.setFile(directory, filename, extension);
    }
    
    protected boolean initialize()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            return true;
        }
        catch(ClassNotFoundException e)
        {
            this.printError("Class not found in initialize(): " + e);
            return false;
        }
    }
    
    @Override
    public boolean open()
    {
        if(initialize())
        {
            try
            {
                this.connection = DriverManager.getConnection("jdbc:sqlite:"
                        + this.getFile().getAbsolutePath());
                return true;
            }
            catch(SQLException e)
            {
                this.printError("Could not establish an SQLite connection, SQLException: "
                        + e.getMessage());
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    @Override
    protected void queryValidation(StatementsList statement)
            throws SQLException
    {
    }
    
    @Override
    public Statements getStatement(String query) throws SQLException
    {
        String[] statement = query.trim().split(" ", 2);
        try
        {
            Statements converted = Statements.valueOf(statement[0]
                    .toUpperCase());
            return converted;
        }
        catch(IllegalArgumentException e)
        {
            throw new SQLException("Unknown statement: \"" + statement[0]
                    + "\".");
        }
    }
}
