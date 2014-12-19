package me.MitchT.BookShelf.Shelves;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.MitchT.BookShelf.BookShelfPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ShelfManager
{
    private BookShelfPlugin plugin;
    
    public ShelfManager(BookShelfPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public boolean isShelfUnlimited(Location shelfLoc)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM copy WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO copy (x,y,z,bool) VALUES ("
                        + shelfLoc.getX() + "," + shelfLoc.getY() + ","
                        + shelfLoc.getZ() + ",0);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                plugin.close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isShelfShop(Location shelfLoc)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM shop WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO shop (x,y,z,bool,price) VALUES ("
                        + shelfLoc.getX() + "," + shelfLoc.getY() + ","
                        + shelfLoc.getZ() + ",0,10);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                plugin.close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getShopPrice(Location shelfLoc)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM shop WHERE x=" + shelfLoc.getX() + " AND y="
                    + shelfLoc.getY() + " AND z=" + shelfLoc.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO shop (x,y,z,bool,price) VALUES ("
                        + shelfLoc.getX() + "," + shelfLoc.getY() + "," + shelfLoc.getZ()
                        + ",0,10);");
                return 10;
            }
            else
            {
                int price = r.getInt("price");
                plugin.close(r);
                
                return price;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return 10;
    }
    
    public boolean isShelfDonate(Location shelfLoc)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM donate WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO donate (x,y,z,bool) VALUES ("
                        + shelfLoc.getX() + "," + shelfLoc.getY() + ","
                        + shelfLoc.getZ() + ",0);");
                return false;
            }
            else
            {
                int enabled = r.getInt("bool");
                plugin.close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isShelfEnabled(Location shelfLoc)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM enable WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
            if(!r.next())
            {
                int def = 1;
                plugin.close(r);
                if(plugin.getConfig().getBoolean("default_openable"))
                {
                    def = 1;
                }
                else
                {
                    def = 0;
                }
                plugin.runQuery("INSERT INTO enable (x,y,z,bool) VALUES ("
                        + shelfLoc.getX() + "," + shelfLoc.getY() + ","
                        + shelfLoc.getZ() + ", " + def + ");");
                return(def != 0);
            }
            else
            {
                int enabled = r.getInt("bool");
                plugin.close(r);
                
                return(enabled != 0);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean toggleShelf(Location shelfLoc)
    {
        boolean enabled = isShelfEnabled(shelfLoc);
        if(enabled)
            plugin.runQuery("UPDATE enable SET bool=0 WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
        else
            plugin.runQuery("UPDATE enable SET bool=1 WHERE x="
                    + shelfLoc.getX() + " AND y=" + shelfLoc.getY() + " AND z="
                    + shelfLoc.getZ() + ";");
        return !enabled;
    }
    
    public void toggleShelvesByName(String shelfName)
    {
        
        if(!shelfName.endsWith(" "))
        {
            if(!shelfName.equals(plugin.getConfig().getString(
                    "default_shelf_name")))
                shelfName += " ";
        }
        else
        {
            if(shelfName.equals(plugin.getConfig().getString(
                    "default_shelf_name")
                    + " "))
                shelfName = shelfName.substring(0, shelfName.length() - 1);
        }
        
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM names WHERE name='"
                    + shelfName + "';");
            List<Vector> vecs = new ArrayList<Vector>();
            HashMap<Vector, Boolean> selmap = new HashMap<Vector, Boolean>();
            
            while(r.next())
            {
                Vector loc = new Vector(r.getInt("x"), r.getInt("y"),
                        r.getInt("z"));
                vecs.add(loc);
            }
            plugin.close(r);
            for(Vector loc : vecs)
            {
                r = plugin.runQuery("SELECT * FROM enable WHERE x="
                        + loc.getX() + " AND y=" + loc.getY() + " AND z="
                        + loc.getZ() + ";");
                if(r.next())
                {
                    selmap.put(loc, r.getInt("bool") == 1 ? true : false);
                }
                plugin.close(r);
            }
            plugin.getSQLManager().setAutoCommit(false);
            for(Vector vec : selmap.keySet())
            {
                boolean bool = selmap.get(vec);
                int bool2 = bool == true ? 0 : 1;
                plugin.runQuery("UPDATE enable SET bool=" + bool2 + " WHERE x="
                        + vec.getX() + " AND y=" + vec.getY() + " AND z="
                        + vec.getZ() + ";");
            }
            plugin.getSQLManager().commit();
            plugin.getSQLManager().setAutoCommit(true);
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    //SHELF OWNERSHIP --------------------------------------------------------------------
    
    public boolean isOwner(Location loc, Player p)
    {
        if(loc.getBlock().getType() != Material.BOOKSHELF)
            return false;
        return isOwner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), p);
    }
    
    public boolean isOwner(int x, int y, int z, Player player)
    {
        if(player.isOp())
            return true;
        if(!plugin.getConfig().getBoolean("use_built_in_ownership"))
            return true;
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            if(!r.next())
            {
                plugin.close(r);
                return false;
            }
            else
            {
                String owners = r.getString("ownerString").toLowerCase();
                plugin.close(r);
                
                String[] splitOwners = owners.split("§");
                if(Arrays.asList(splitOwners).contains(
                        player.getName().toLowerCase())
                        || Arrays.asList(splitOwners).contains("all"))
                    return true;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public void setOwners(Location loc, String[] pList)
    {
        setOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void setOwners(int x, int y, int z, String[] pList)
    {
        String ownerString = "";
        for(String p : pList)
        {
            p = p.toLowerCase();
            ownerString += p + "§";
        }
        if(ownerString.length() > 0)
            ownerString = ownerString.substring(0, ownerString.length() - 1);
        
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            if(!r.next())
            {
                plugin.close(r);
                plugin.runQuery("INSERT INTO owners (x, y, z, ownerString) VALUES ("
                        + x + ", +" + y + ", " + z + ", '" + ownerString
                        + "');");
            }
            else
            {
                plugin.close(r);
                plugin.runQuery("UPDATE owners SET ownerString='" + ownerString
                        + "' WHERE x=" + x + " AND y=" + y + " AND z=" + z
                        + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public void addOwners(Location loc, String[] pList)
    {
        addOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void addOwners(int x, int y, int z, String[] pList)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            if(!r.next())
            {
                plugin.close(r);
                
                String ownerString = "";
                for(String p : pList)
                {
                    p = p.toLowerCase();
                    ownerString += p + "§";
                }
                ownerString = ownerString
                        .substring(0, ownerString.length() - 1);
                
                plugin.runQuery("INSERT INTO owners (x, y, z, ownerString) VALUES ("
                        + x + ", +" + y + ", " + z + ", '" + ownerString
                        + "');");
            }
            else
            {
                String currentOwnerString = r.getString("ownerString");
                plugin.close(r);
                
                String[] currentOwnerStringSplit = currentOwnerString
                        .split("§");
                String newOwnerString = "";
                
                for(String p : currentOwnerStringSplit)
                {
                    p = p.toLowerCase();
                    newOwnerString += p + "§";
                }
                for(String p : pList)
                {
                    p = p.toLowerCase();
                    if(Arrays.asList(currentOwnerStringSplit).contains(p))
                        continue;
                    newOwnerString += p + "§";
                }
                if(newOwnerString.length() > 0)
                    newOwnerString = newOwnerString.substring(0,
                            newOwnerString.length() - 1);
                
                plugin.runQuery("UPDATE owners SET ownerString='" + newOwnerString
                        + "' WHERE x=" + x + " AND y=" + y + " AND z=" + z
                        + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public void removeOwners(Location loc, String[] pList)
    {
        removeOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), pList);
    }
    
    public void removeOwners(int x, int y, int z, String[] pList)
    {
        for(int i = 0; i < pList.length; i++)
        {
            pList[i] = pList[i].toLowerCase();
        }
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            if(!r.next())
            {
                plugin.close(r);
            }
            else
            {
                String currentOwnerString = r.getString("ownerString");
                plugin.close(r);
                
                String[] currentOwnerStringSplit = currentOwnerString
                        .split("§");
                String newOwnerString = "";
                
                for(String p : currentOwnerStringSplit)
                {
                    p = p.toLowerCase();
                    if(Arrays.asList(pList).contains(p))
                        continue;
                    newOwnerString += p + "§";
                }
                if(newOwnerString.length() > 0)
                    newOwnerString = newOwnerString.substring(0,
                            newOwnerString.length() - 1);
                
                plugin.runQuery("UPDATE owners SET ownerString='" + newOwnerString
                        + "' WHERE x=" + x + " AND y=" + y + " AND z=" + z
                        + ";");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public String[] getOwners(Location loc)
    {
        return getOwners(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public String[] getOwners(int x, int y, int z)
    {
        try
        {
            ResultSet r = plugin.runQuery("SELECT * FROM owners WHERE x=" + x + " AND y=" + y
                    + " AND z=" + z + ";");
            if(!r.next())
            {
                plugin.close(r);
                return new String[] { "No Owners!" };
            }
            else
            {
                String ownerString = r.getString("ownerString");
                plugin.close(r);
                
                String[] ownerStringSplit = ownerString.split("§");
                return ownerStringSplit;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return new String[] { "Unknown Owners!" };
    }
    
}
