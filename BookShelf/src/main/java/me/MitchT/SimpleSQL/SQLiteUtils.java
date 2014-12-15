/**
 * @author Mitch Talmadge (AKA Pew446)
 * 
 *         Date Created:
 *         May 12, 2013
 */

package me.MitchT.SimpleSQL;

import java.io.File;

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
public class SQLiteUtils
{
    private String directory;
    private String filename;
    private File file;
    private String extension = ".db";
    private Database db;
    
    public SQLiteUtils(Database db)
    {
        this.db = db;
    }
    
    public String getDirectory()
    {
        return directory;
    }
    
    public void setDirectory(String directory)
    {
        if(directory == null || directory.length() == 0)
            db.printError("Directory cannot be null or empty.");
        else
            this.directory = directory;
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public void setFilename(String filename)
    {
        if(filename == null || filename.length() == 0)
            db.printError("Filename cannot be null or empty.");
        else if(filename.contains("/") || filename.contains("\\")
                || filename.endsWith(".db"))
            db.printError("The database filename cannot contain: /, \\, or .db.");
        else
            this.filename = filename;
    }
    
    public String getExtension()
    {
        return extension;
    }
    
    public void setExtension(String extension)
    {
        if(extension == null || extension.length() == 0)
            db.printError("Extension cannot be null or empty.");
        if(extension.charAt(0) != '.')
            db.printError("Extension must begin with a period");
    }
    
    public File getFile()
    {
        return this.file;
    }
    
    public void setFile(String directory, String filename)
    {
        setDirectory(directory);
        setFilename(filename);
        
        File folder = new File(getDirectory());
        if(!folder.exists())
            folder.mkdir();
        
        file = new File(folder.getAbsolutePath() + File.separator
                + getFilename() + getExtension());
    }
    
    public void setFile(String directory, String filename, String extension)
    {
        setExtension(extension);
        this.setFile(directory, filename);
    }
}
