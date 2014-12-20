package me.MitchT.BookShelf.Commands;

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
public enum BSCommandEnum
{
    UNLIMITED("bsunlimited", "unlimited", BSC_Unlimited.class),
    TOGGLE("bstoggle", "toggle", BSC_Toggle.class),
    RELOAD("bsreload", "reload", BSC_Reload.class),
    SHOP("bsshop", "shop", BSC_Shop.class),
    NAME("bsname", "name", BSC_Name.class),
    TOWNY("bstowny", "towny", BSC_Towny.class),
    DONATE("bsdonate", "donate", BSC_Donate.class),
    SETOWNERS("bssetowners", "setowners", BSC_SetOwners.class),
    ADDOWNERS("bsaddowners", "addowners", BSC_AddOwners.class),
    REMOVEOWNERS("bsremoveowners", "removeowners", BSC_RemoveOwners.class),
    GETOWNERS("bsgetowners", "getowners", BSC_GetOwners.class),
    EDIT("bsedit", "edit", BSC_Edit.class);
    
    private String commandName;
    private String permissionName;
    private Class<? extends BSCommand> commandClass;
    
    BSCommandEnum(String commandName, String permissionName,
            Class<? extends BSCommand> commandClass)
    {
        this.commandName = commandName;
        this.permissionName = permissionName;
        this.commandClass = commandClass;
    }
    
    public static BSCommandEnum getEnumByCommandName(String commandName)
    {
        for(BSCommandEnum enumVal : values())
        {
            if(enumVal.getCommandName().equalsIgnoreCase(commandName))
            {
                return enumVal;
            }
        }
        return null;
    }
    
    public String getCommandName()
    {
        return commandName;
    }
    
    public String getPermissionName()
    {
        return permissionName;
    }
    
    public Class<? extends BSCommand> getCommandClass()
    {
        return commandClass;
    }
    
}
