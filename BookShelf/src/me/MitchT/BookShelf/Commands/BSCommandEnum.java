package me.MitchT.BookShelf.Commands;

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

    BSCommandEnum(String commandName, String permissionName, Class<? extends BSCommand> commandClass)
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
