/**
 * @author Mitch Talmadge (AKA Pew446)
 * 
 *         Date Created:
 *         May 12, 2013
 */

package me.MitchT.SimpleSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public abstract class Database
{
    
    protected Logger logger;
    protected Connection connection;
    protected DBList driver;
    protected String prefix;
    protected String dbprefix;
    private int lastUpdate;
    private volatile Object syncObject = new Object();
    private volatile boolean shouldWait = false;
    
    /**
     * Used for child class super
     * 
     * @param prefix
     * @param dbprefix
     * @param logger
     */
    public Database(String prefix, String dbprefix, Logger logger)
    {
        if(logger == null)
        {
            Logger.getLogger("SimpleSQL").severe("logger cannot be null!");
            return;
        }
        if(prefix == null)
        {
            Logger.getLogger("SimpleSQL").severe("prefix cannot be null!");
            return;
        }
        this.prefix = prefix;
        this.dbprefix = dbprefix;
        this.logger = logger;
    }
    
    public abstract boolean open();
    
    public final boolean close()
    {
        if(connection != null)
        {
            try
            {
                connection.close();
                return true;
            }
            catch(SQLException e)
            {
                this.printError("Could not close connection, SQLException: "
                        + e.getMessage());
                return false;
            }
        }
        else
        {
            this.printError("Could not close connection, it is null.");
            return false;
        }
    }
    
    public final Connection getConnection()
    {
        return this.connection;
    }
    
    public final boolean isOpen()
    {
        if(connection != null)
            try
            {
                if(connection.isValid(1))
                    return true;
            }
            catch(SQLException e)
            {
            }
        return false;
    }
    
    public final boolean isOpen(int seconds)
    {
        if(connection != null)
            try
            {
                if(connection.isValid(seconds))
                    return true;
            }
            catch(SQLException e)
            {
            }
        return false;
    }
    
    protected void printError(String error)
    {
        logger.severe(this.prefix + " " + this.dbprefix + " " + error);
    }
    
    public abstract StatementsList getStatement(String query)
            throws SQLException;
    
    protected abstract void queryValidation(StatementsList statement)
            throws SQLException;
    
    public final ResultSet query(String query) throws SQLException
    {
        doWait();
        queryValidation(this.getStatement(query));
        Statement statement = this.getConnection().createStatement();
        if(statement.execute(query))
        {
            this.shouldWait = true;
            return statement.getResultSet();
        }
        else
        {
            int uc = statement.getUpdateCount();
            this.lastUpdate = uc;
            return this.getConnection().createStatement()
                    .executeQuery("SELECT " + uc);
        }
    }
    
    public Object getSynchronized()
    {
        return this.syncObject;
    }
    
    public boolean shouldWait()
    {
        return this.shouldWait;
    }
    
    public void doWait()
    {
        if(shouldWait())
        {
            try
            {
                synchronized(getSynchronized())
                {
                    getSynchronized().wait();
                }
            }
            catch(InterruptedException e)
            {
                return;
            }
        }
        else
        {
            return;
        }
    }
    
    public void setShouldWait(boolean b)
    {
        this.shouldWait = b;
    }
    
}
