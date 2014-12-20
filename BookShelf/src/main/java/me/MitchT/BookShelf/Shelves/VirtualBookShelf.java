package me.MitchT.BookShelf.Shelves;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBookShelf implements InventoryHolder
{

    private BookShelf originShelf;
    private Inventory inventory;

    public VirtualBookShelf(BookShelf originShelf)
    {
        this.originShelf = originShelf;
    }
    
    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }
    
    @Override
    public Inventory getInventory()
    {
        return this.inventory;
    }
    
    public BookShelf getOriginShelf()
    {
        return this.originShelf;
    }
    
}
