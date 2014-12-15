/**
 * @author Mitch Talmadge (AKA Pew446)
 *         Date Created:
 *         May 12, 2013
 */

package me.MitchT.SimpleSQL;

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
public class MySQL extends Database
{
    
    public enum Statements implements StatementsList
    {
        SELECT("SELECT"),
        INSERT("INSERT"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        DO("DO"),
        REPLACE("REPLACE"),
        LOAD("LOAD"),
        HANDLER("HANDLER"),
        CALL("CALL"),
        CREATE("CREATE"),
        ALTER("ALTER"),
        DROP("DROP"),
        TRUNCATE("TRUNCATE"),
        RENAME("RENAME"),
        START("START"),
        COMMIT("COMMIT"),
        SAVEPOINT("SAVEPOINT"),
        ROLLBACK("ROLLBACK"),
        RELEASE("RELEASE"),
        LOCK("LOCK"),
        UNLOCK("UNLOCK"),
        PREPARE("PREPARE"),
        EXECUTE("EXECUTE"),
        DEALLOCATE("DEALLOCATE"),
        SET("SET"),
        SHOW("SHOW"),
        DESCRIBE("DESCRIBE"),
        EXPLAIN("EXPLAIN"),
        HELP("HELP"),
        USE("USE");
        
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
    
    private MySQLUtils utils = new MySQLUtils(this);
    
    public MySQL(Logger logger, String prefix, String host, int port,
            String dbname, String user, String pass)
    {
        super(prefix, "[MySQL] ", logger);
        setHostname(host);
        setPort(port);
        setDatabase(dbname);
        setUsername(user);
        setPassword(pass);
        this.driver = DBList.MySQL;
    }
    
    public String getHostname()
    {
        return utils.getHostname();
    }
    
    private void setHostname(String hostname)
    {
        utils.setHostname(hostname);
    }
    
    public int getPort()
    {
        return utils.getPort();
    }
    
    private void setPort(int port)
    {
        utils.setPort(port);
    }
    
    public String getUsername()
    {
        return utils.getUsername();
    }
    
    private void setUsername(String username)
    {
        utils.setUsername(username);
    }
    
    private String getPassword()
    {
        return utils.getPassword();
    }
    
    private void setPassword(String password)
    {
        utils.setPassword(password);
    }
    
    public String getDatabase()
    {
        return utils.getDatabase();
    }
    
    private void setDatabase(String database)
    {
        utils.setDatabase(database);
    }
    
    protected boolean initialize()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
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
            String url = "jdbc:mysql://" + getHostname() + ":" + getPort()
                    + "/" + getDatabase();
            try
            {
                this.connection = DriverManager.getConnection(url,
                        getUsername(), getPassword());
                return true;
            }
            catch(SQLException e)
            {
                this.printError("Could not establish a MySQL connection, SQLException: "
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
    public StatementsList getStatement(String query) throws SQLException
    {
        return null;
    }
    
    @Override
    protected void queryValidation(StatementsList statement)
            throws SQLException
    {
        
    }
}
