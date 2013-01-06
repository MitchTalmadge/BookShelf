package me.Pew446.BookShelf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;


import me.Pew446.BookShelf.BookShelf;
import net.minecraft.server.v1_4_6.EntityItem;
public class BookListener implements Listener {
	public static BookShelf plugin;
	public BookListener(BookShelf instance) {
		plugin = instance;
	}	
	private String author;
    private String title;
    private String[] pages;
    private Enchantment etype;
    private short mapdur = 0;
    private int elvl = 0;
    private boolean loading = false;
    HashMap<Location, InventoryHolder> map = new HashMap<Location, InventoryHolder>();
    HashMap<Location, Inventory> map2 = new HashMap<Location, Inventory>();
    HashMap<Location, Boolean> map3 = new HashMap<Location, Boolean>();
    static ResultSet r;
	@EventHandler
	public void onClick(PlayerInteractEvent j)
	{
		Player p = j.getPlayer();
		if(j.getClickedBlock() != null)
		{
			if(j.getClickedBlock().getType() == Material.BOOKSHELF)
			{
				if(!j.getPlayer().isSneaking())
				{
					if(j.getPlayer().getItemInHand().getType() == Material.BOOKSHELF)
					{
						return;
					}
					if(j.getAction() == Action.RIGHT_CLICK_BLOCK && !loading)
					{
						Location loc = j.getClickedBlock().getLocation();
						if(!plugin.getConfig().getBoolean("top-bottom_access"))
						{
							if(j.getBlockFace() == BlockFace.UP 
									| j.getBlockFace() == BlockFace.DOWN)
							{
								return;
							}
						}

						try {
							r = BookShelf.mysql.query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!r.next())
							{
								r.close();
								BookShelf.mysql.query("INSERT INTO copy (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+",0);");
							}
							else
							{
								r.close();
							}
							r = BookShelf.mysql.query("SELECT * FROM enable WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							if(!r.next())
							{
								int def = 1;
								r.close();
								if(plugin.getConfig().getBoolean("default_openable"))
								{
									def = 1;
								}
								else
								{
									def = 0;
								}
								BookShelf.mysql.query("INSERT INTO enable (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", "+def+");");
								if(def == 0)
									return;
							}
							else
							{
								boolean open = r.getBoolean("bool");
								r.close();
								if(!open)
								{
									return;
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if(!map.containsKey(j.getClickedBlock().getLocation()))
						{
							Inventory inv = Bukkit.createInventory(p, plugin.getConfig().getInt("rows")*9, plugin.getConfig().getString("shelf_title"));
							Block cl = j.getClickedBlock();
							int x = cl.getX();
							int y = cl.getY();
							int z = cl.getZ();
							map.put(cl.getLocation(), inv.getHolder());
							map2.put(cl.getLocation(), inv);
							
							
							try {
								r = BookShelf.mysql.query("SELECT COUNT(*) FROM items WHERE x=" + x + " AND y=" + y + " AND z=" + z + ";");
								if(!r.next())
								{
									r.close();
									p.openInventory(inv);
									if(!map3.containsKey(loc))
										map3.put(loc, true);
									return;
								}
								else
								{
									r.close();
									r = BookShelf.mysql.query("SELECT * FROM items WHERE x=" + x + " AND y=" + y + " AND z=" + z + ";");
									ArrayList<String> auth = new ArrayList<String>();
									ArrayList<String> titl = new ArrayList<String>();
									ArrayList<Integer> type = new ArrayList<Integer>();
									ArrayList<Integer> id = new ArrayList<Integer>();
									ArrayList<Integer> loca = new ArrayList<Integer>();
									ArrayList<Integer> amt = new ArrayList<Integer>();
				 					while(r.next())
									{
										auth.add(r.getString("author"));
										titl.add(r.getString("title"));
										id.add(r.getInt("id"));
										type.add(r.getInt("type"));
										loca.add(r.getInt("loc"));
										amt.add(r.getInt("amt"));
									}
									r.close();
									ArrayList<String> pages = new ArrayList<String>();
									for(int i=0;i<id.size();i++)
									{
										if(type.get(i) == Material.BOOK.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.BOOK, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_3.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_3, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_4.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_4, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_5.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_5, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_6.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_6, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_7.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_7, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_8.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_8, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_9.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_9, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_10.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_10, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_11.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_11, amt.get(i)));
										}
										else if(type.get(i) == Material.RECORD_12.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.RECORD_12, amt.get(i)));
										}
										else if(type.get(i) == 2257)
										{
											inv.setItem(loca.get(i), new ItemStack(2257, amt.get(i)));
										}
										else if(type.get(i) == 2256)
										{
											inv.setItem(loca.get(i), new ItemStack(2256, amt.get(i)));
										}
										else if(type.get(i) == Material.PAPER.getId())
										{
											inv.setItem(loca.get(i), new ItemStack(Material.PAPER, amt.get(i)));
										}
										else if(type.get(i) == Material.MAP.getId())
										{
											r = BookShelf.mysql.query("SELECT * FROM maps WHERE id="+id.get(i)+";");
											while(r.next())
											{
												mapdur = r.getShort("durability");
											}
											r.close();
											inv.setItem(loca.get(i), generateItemStack(3));
										}
										else if(type.get(i) == Material.ENCHANTED_BOOK.getId())
										{
											r = BookShelf.mysql.query("SELECT * FROM enchant WHERE id="+id.get(i)+";");
											String enchant = "";
											while(r.next())
											{
												enchant = r.getString("type");
												elvl = r.getInt("level");
											}
											r.close();
											etype = Enchantment.getByName(enchant);
											inv.setItem(loca.get(i), generateItemStack(2));
										}
										else
										{
											r = BookShelf.mysql.query("SELECT * FROM pages WHERE id="+id.get(i)+";");
											while(r.next())
											{
												pages.add(r.getString("text"));
											}
											r.close();
											String[] thepages = new String[pages.size()];
											thepages = pages.toArray(thepages);
											if(type.get(i) == Material.WRITTEN_BOOK.getId())
											{
												Book(titl.get(i), auth.get(i), thepages);
												inv.setItem(loca.get(i), generateItemStack(0));
												pages.clear();
											}
											else if(type.get(i) == Material.BOOK_AND_QUILL.getId())
											{
												Book("null", "null", thepages);
												inv.setItem(loca.get(i), generateItemStack(1));
												pages.clear();
											}
										}
									}
									auth.clear();
									titl.clear();
									type.clear();
									id.clear();
									loca.clear();
									amt.clear();
									p.openInventory(inv);
									if(!map3.containsKey(loc))
										map3.put(loc, true);
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							Inventory inv = map2.get(j.getClickedBlock().getLocation());
							Player player = (Player) inv.getHolder();
							if(player.getName() == p.getName())
							{
								j.setCancelled(true);
							}
							else
							{
								p.openInventory(inv);
							}
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("rawtypes")
	public Location getKey(HashMap map, InventoryHolder inv) 
	{
		Set key = map.keySet();
		for (Iterator i = key.iterator(); i.hasNext();) 
			{
				Location next = (Location) i.next();
				if (map.get(next).equals(inv)) 
				{
					return next;
				}
			}
		return null;
	}
	@EventHandler
	public void onAdd(InventoryCloseEvent u)
	{
		
		final InventoryCloseEvent j = u;
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin,  new Runnable() {
			   public void run() {
		if(map.containsValue(j.getInventory().getHolder())){
			loading = true;
			Location loc = getKey(map,j.getInventory().getHolder());
			ItemStack[] cont = j.getInventory().getContents();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			if(j.getInventory().getHolder() instanceof Player)
			{
				Player player = (Player) j.getInventory().getHolder();
				if(j.getPlayer().getName() == player.getName())
				{
					map.remove(loc);
					j.getInventory().getViewers().remove(j.getInventory().getHolder());
					if(!j.getInventory().getViewers().isEmpty())
					{
						map.put(loc, (Player) j.getInventory().getViewers().iterator().next());
					}
					else
					{
						
						try {
							r = BookShelf.mysql.query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
							BookShelf.mysql.getConnection().setAutoCommit(false);
							if(r.getInt("bool") == 0)
							{
								r.close();
								if(map3.get(loc))
								{
									BookShelf.mysql.query("DELETE FROM items WHERE x=" + x + " AND y=" + y + " AND z=" + z + ";");
									for(int i=0;i<cont.length;i++)
									{
										if(cont[i] != null)
										{
											if(cont[i].getType() == Material.BOOK_AND_QUILL || cont[i].getType() == Material.WRITTEN_BOOK)
											{
												Book(cont[i]);
												String title = getTitle().replaceAll("'", "''");
												String author = getAuthor().replaceAll("'", "''");
												int type = cont[i].getTypeId(); 
												if(cont[i].getType() == Material.BOOK_AND_QUILL)
												{
													BookShelf.mysql.query("INSERT INTO items (x,y,z,author,title,type,loc,amt) VALUES ("+x+","+y+","+z+",'null','null',"+type+","+i+",1);");
												}
												else
												{
													BookShelf.mysql.query("INSERT INTO items (x,y,z,author,title,type,loc,amt) VALUES ("+x+","+y+","+z+",'"+author+"','"+title+"',"+type+","+i+",1);");	
												}
												int id = getidxyz(x,y,z);
												BookShelf.mysql.query("DELETE FROM pages WHERE id="+id+";");
												for(int k=0;k<getPages().length;k++)
												{
													BookShelf.mysql.query("INSERT INTO pages (id, text) VALUES ("+id+",'"+getPages()[k].replaceAll("'", "''")+"');");
												}
											}
											else if(cont[i].getType() == Material.BOOK 
													| cont[i].getType() == Material.RECORD_3 
													| cont[i].getType() == Material.RECORD_4 
													| cont[i].getType() == Material.RECORD_5 
													| cont[i].getType() == Material.RECORD_6 
													| cont[i].getType() == Material.RECORD_7 
													| cont[i].getType() == Material.RECORD_8 
													| cont[i].getType() == Material.RECORD_9 
													| cont[i].getType() == Material.RECORD_10 
													| cont[i].getType() == Material.RECORD_11 
													| cont[i].getType() == Material.RECORD_12
													| cont[i].getType().getId() == 2257
													| cont[i].getType().getId() == 2256
													| cont[i].getType() == Material.PAPER)
											{
												int type = cont[i].getTypeId(); 
												BookShelf.mysql.query("INSERT INTO items (x,y,z,author,title,type,loc,amt) VALUES ("+x+","+y+","+z+", 'null', 'null',"+type+","+i+","+cont[i].getAmount()+");");
											}
											else if(cont[i].getType() == Material.ENCHANTED_BOOK)
											{
												int type = cont[i].getTypeId(); 
												BookShelf.mysql.query("INSERT INTO items (x,y,z,author,title,type,loc,amt) VALUES ("+x+","+y+","+z+", 'null', 'null',"+type+","+i+","+cont[i].getAmount()+");");
												int id = getidxyz(x,y,z);
												EnchantmentStorageMeta book = (EnchantmentStorageMeta)cont[i].getItemMeta();
												Map<Enchantment, Integer> enchants = book.getStoredEnchants();
												Enchantment enchant = null;
												for ( Enchantment key : enchants.keySet() ) {
												    enchant = key;
												}
												Integer lvl = book.getStoredEnchantLevel(enchant);
												String type2 = enchant.getName();
												BookShelf.mysql.query("INSERT INTO enchant (id, type, level) VALUES ("+id+",'"+type2+"','"+lvl+"');");
											}
											else if(cont[i].getType() == Material.MAP)
											{
												int type = cont[i].getTypeId();
												ItemStack map = cont[i];
												int dur = map.getDurability();
												BookShelf.mysql.query("INSERT INTO items (x,y,z,author,title,type,loc,amt) VALUES ("+x+","+y+","+z+", 'null', 'null',"+type+","+i+","+cont[i].getAmount()+");");
												int id = getidxyz(x,y,z);
												BookShelf.mysql.query("INSERT INTO maps (id, durability) VALUES ("+id+",'"+dur+"');");
											}
										}
									}
									BookShelf.mysql.getConnection().commit();
									BookShelf.mysql.getConnection().setAutoCommit(true);
									map3.remove(loc);
								}
							}
							else
							{
								r.close();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						map2.remove(loc);
					}
				}
			}
			loading=false;
		}
	}
		});
	}
		
	@EventHandler
	public void onBreak(BlockBreakEvent j)
	{
		if(map.containsKey(j.getBlock().getLocation()))
		{
			Inventory inv = map2.get(j.getBlock().getLocation());
			List<HumanEntity> viewers = inv.getViewers();
			for(int i = 0;i<viewers.size();i++)
			{
				viewers.get(i).closeInventory();
			}
		}
		if(j.getBlock().getType() == Material.BOOKSHELF)
		{
			
			try {
				r = BookShelf.mysql.query("SELECT * FROM items WHERE x=" + j.getBlock().getX() + " AND y=" + j.getBlock().getY() + " AND z=" + j.getBlock().getZ() + ";");
				ArrayList<String> auth = new ArrayList<String>();
				ArrayList<String> titl = new ArrayList<String>();
				ArrayList<Integer> type = new ArrayList<Integer>();
				ArrayList<Integer> id = new ArrayList<Integer>();
				ArrayList<Integer> amt = new ArrayList<Integer>();
				while(r.next())
				{
					auth.add(r.getString("author"));
					titl.add(r.getString("title"));
					id.add(r.getInt("id"));
					type.add(r.getInt("type"));
					amt.add(r.getInt("amt"));
				}
				r.close();
				ArrayList<String> pages = new ArrayList<String>();
				String enchant = "";
				BookShelf.mysql.getConnection().setAutoCommit(false);
				for(int i=0;i<id.size();i++)
				{
					if(type.get(i) == Material.ENCHANTED_BOOK.getId())
					{
						r = BookShelf.mysql.query("SELECT * FROM enchant WHERE id="+id.get(i)+";");
						while(r.next())
						{
							enchant = r.getString("type");
							elvl = r.getInt("level");
						}
						r.close();
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						etype = Enchantment.getByName(enchant);
						Location loc = j.getBlock().getLocation();
						Random gen = new Random();
						double xs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						double ys = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						double zs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX() + xs, loc.getY() + ys, loc.getZ() + zs, (CraftItemStack.asNMSCopy(generateItemStack(2))));
						((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
					}
					else if(type.get(i) == Material.MAP.getId())
					{
						r = BookShelf.mysql.query("SELECT * FROM maps WHERE id="+id.get(i)+";");
						while(r.next())
						{
							mapdur = r.getShort("durability");
						}
						r.close();
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						Location loc = j.getBlock().getLocation();
						Random gen = new Random();
						double xs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						double ys = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						double zs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
						EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX() + xs, loc.getY() + ys, loc.getZ() + zs, (CraftItemStack.asNMSCopy(generateItemStack(3))));
						((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
					}
					else if(type.get(i) == Material.BOOK.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.BOOK, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_3.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_3, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_4.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_4, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_5.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_5, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_6.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_6, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_7.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_7, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_8.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_8, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_9.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_9, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_10.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_10, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_11.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_11, amt.get(i)));
					}
					else if(type.get(i) == Material.RECORD_12.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.RECORD_12, amt.get(i)));
					}
					else if(type.get(i) == 2257) //Record
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(2257, amt.get(i)));
					}
					else if(type.get(i) == 2256) //Record
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(2256, amt.get(i)));
					}
					else if(type.get(i) == Material.PAPER.getId())
					{
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						j.getBlock().getWorld().dropItem(j.getBlock().getLocation(), new ItemStack(Material.PAPER, amt.get(i)));
					}
					else
					{
						r = BookShelf.mysql.query("SELECT * FROM pages WHERE id="+id.get(i)+";");
						while(r.next())
						{
							pages.add(r.getString("text"));
						}
						r.close();
						String[] thepages = new String[pages.size()];
						thepages = pages.toArray(thepages);
						if(type.get(i) == Material.WRITTEN_BOOK.getId())
						{
							Book(titl.get(i), auth.get(i), thepages);
							Location loc = j.getBlock().getLocation();
							Random gen = new Random();
							double xs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							double ys = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							double zs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX() + xs, loc.getY() + ys, loc.getZ() + zs, (CraftItemStack.asNMSCopy(generateItemStack(0))));
							((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
							pages.clear();
						}
						else if(type.get(i) == Material.BOOK_AND_QUILL.getId())
						{
							Book("null", "null", thepages);
							Location loc = j.getBlock().getLocation();
							Random gen = new Random();
							double xs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							double ys = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							double zs = gen.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
							EntityItem entity = new EntityItem(((CraftWorld) loc.getWorld()).getHandle(), loc.getX() + xs, loc.getY() + ys, loc.getZ() + zs, (CraftItemStack.asNMSCopy(generateItemStack(1))));
							((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
							pages.clear();
						}
						BookShelf.mysql.query("DELETE FROM items WHERE id=" + id.get(i) + ";");
						BookShelf.mysql.query("DELETE FROM pages WHERE id=" + id.get(i) + ";");
					}
				}
				BookShelf.mysql.getConnection().commit();
				BookShelf.mysql.getConnection().setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Location loc = j.getBlock().getLocation();
			try {
				BookShelf.mysql.query("DELETE FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@EventHandler
	public void onInv(InventoryClickEvent j)
	{
		if(j.getInventory().getTitle() == plugin.getConfig().getString("shelf_title"))
		{
			if(j.getCurrentItem() == null)
			{
				return;
			}
			if(plugin.getConfig().getBoolean("permissions.allow_maps") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.maps"))
			{
				if(j.getCurrentItem().getType() == Material.MAP)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.MAP)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_book") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.book"))
			{
				if(j.getCurrentItem().getType() == Material.BOOK)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.BOOK)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_enchanted_book") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.enchanted_book"))
			{
				if(j.getCurrentItem().getType() == Material.ENCHANTED_BOOK)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.ENCHANTED_BOOK)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_book_and_quill") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.baq"))
			{
				if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.BOOK_AND_QUILL)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_signed") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.signed"))
			{
				if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.WRITTEN_BOOK)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_records") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.records"))
			{
				if(j.getCurrentItem().getType() == Material.RECORD_3 
						| j.getCurrentItem().getType() == Material.RECORD_4
						| j.getCurrentItem().getType() == Material.RECORD_5
						| j.getCurrentItem().getType() == Material.RECORD_6
						| j.getCurrentItem().getType() == Material.RECORD_7
						| j.getCurrentItem().getType() == Material.RECORD_8
						| j.getCurrentItem().getType() == Material.RECORD_9
						| j.getCurrentItem().getType() == Material.RECORD_10
						| j.getCurrentItem().getType() == Material.RECORD_11
						| j.getCurrentItem().getType() == Material.RECORD_12
						| j.getCurrentItem().getType().getId() == 2257
						| j.getCurrentItem().getType().getId() == 2256)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.RECORD_3 
						| j.getCursor().getType() == Material.RECORD_4
						| j.getCursor().getType() == Material.RECORD_5
						| j.getCursor().getType() == Material.RECORD_6
						| j.getCursor().getType() == Material.RECORD_7
						| j.getCursor().getType() == Material.RECORD_8
						| j.getCursor().getType() == Material.RECORD_9
						| j.getCursor().getType() == Material.RECORD_10
						| j.getCursor().getType() == Material.RECORD_11
						| j.getCursor().getType() == Material.RECORD_12
						| j.getCursor().getType().getId() == 2257
						| j.getCursor().getType().getId() == 2256)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("permissions.allow_paper") == false || !Bukkit.getPlayer(j.getWhoClicked().getName()).hasPermission("bookshelf.paper"))
			{
				if(j.getCurrentItem().getType() == Material.PAPER)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.PAPER)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(j.getCurrentItem().getType() == Material.MAP
					| j.getCurrentItem().getType() == Material.BOOK
					| j.getCurrentItem().getType() == Material.ENCHANTED_BOOK
					| j.getCurrentItem().getType() == Material.BOOK_AND_QUILL
					| j.getCurrentItem().getType() == Material.WRITTEN_BOOK
					| j.getCurrentItem().getType() == Material.RECORD_3
					| j.getCurrentItem().getType() == Material.RECORD_4
					| j.getCurrentItem().getType() == Material.RECORD_5
					| j.getCurrentItem().getType() == Material.RECORD_6
					| j.getCurrentItem().getType() == Material.RECORD_7
					| j.getCurrentItem().getType() == Material.RECORD_8
					| j.getCurrentItem().getType() == Material.RECORD_9
					| j.getCurrentItem().getType() == Material.RECORD_10
					| j.getCurrentItem().getType() == Material.RECORD_11
					| j.getCurrentItem().getType() == Material.RECORD_12
					| j.getCurrentItem().getType().getId() == 2257
					| j.getCurrentItem().getType().getId() == 2256)
			{
				return;
			}
			if(j.getCursor().getType() == Material.MAP
					| j.getCursor().getType() == Material.BOOK
					| j.getCursor().getType() == Material.ENCHANTED_BOOK
					| j.getCursor().getType() == Material.BOOK_AND_QUILL
					| j.getCursor().getType() == Material.WRITTEN_BOOK
					| j.getCursor().getType() == Material.RECORD_3
					| j.getCursor().getType() == Material.RECORD_4
					| j.getCursor().getType() == Material.RECORD_5
					| j.getCursor().getType() == Material.RECORD_6
					| j.getCursor().getType() == Material.RECORD_7
					| j.getCursor().getType() == Material.RECORD_8
					| j.getCursor().getType() == Material.RECORD_9
					| j.getCursor().getType() == Material.RECORD_10
					| j.getCursor().getType() == Material.RECORD_11
					| j.getCursor().getType() == Material.RECORD_12
					| j.getCursor().getType().getId() == 2257
					| j.getCursor().getType().getId() == 2256)
			{
				return;
			}
			j.setCancelled(true);
		}
		else if(j.getInventory().getTitle() == "mob.villager")
		{
			if(j.getCurrentItem() == null)
			{
				return;
			}
			if(plugin.getConfig().getBoolean("villager_trading.allow_book") == false)
			{
				if(j.getCurrentItem().getType() == Material.BOOK)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.BOOK)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("villager_trading.allow_book_and_quill") == false)
			{
				if(j.getCurrentItem().getType() == Material.BOOK_AND_QUILL)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.BOOK_AND_QUILL)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("villager_trading.allow_signed") == false)
			{
				if(j.getCurrentItem().getType() == Material.WRITTEN_BOOK)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.WRITTEN_BOOK)
				{
					j.setCancelled(true);
					return;
				}
			}
			if(plugin.getConfig().getBoolean("villager_trading.allow_paper") == false)
			{
				if(j.getCurrentItem().getType() == Material.PAPER)
				{
					j.setCancelled(true);
					return;
				}
				else if(j.getCursor().getType() == Material.PAPER)
				{
					j.setCancelled(true);
					return;
				}
			}
		}
	}
	@EventHandler
	public void onPlace(BlockPlaceEvent j)
	{
		if(j.getBlock().getType() == Material.BOOKSHELF)
		{
			if(j.getBlockAgainst().getType() == Material.BOOKSHELF)
			{
				Location loc = j.getBlock().getLocation();
				try {
					BookShelf.mysql.query("INSERT INTO copy (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0);");
					int def = 1;
					if(plugin.getConfig().getBoolean("default_openable"))
					{
						def = 1;
					}
					else
					{
						def = 0;
					}
					BookShelf.mysql.query("INSERT INTO enable (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", "+def+");");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			else
			{
				Location loc = j.getBlock().getLocation();
				try {
					BookShelf.mysql.query("INSERT INTO copy (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", 0);");
					int def = 1;
					if(plugin.getConfig().getBoolean("default_openable"))
					{
						def = 1;
					}
					else
					{
						def = 0;
					}
					BookShelf.mysql.query("INSERT INTO enable (x,y,z,bool) VALUES ("+loc.getX()+","+loc.getY()+","+loc.getZ()+", "+def+");");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(j.getBlock().getType() == Material.WALL_SIGN)
		{
			return;
		}
		if(j.getBlockAgainst().getType() == Material.BOOKSHELF)
		{
			if(j.getBlockAgainst().getFace(j.getBlock()) == BlockFace.UP | j.getBlockAgainst().getFace(j.getBlock()) == BlockFace.DOWN)
			{
				if(!plugin.getConfig().getBoolean("top-bottom_access"))
				{
					return;
				}
			}
			j.setBuild(false);
			j.setCancelled(true);
		}
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent j)
	{
		Player p = j.getPlayer();
		if(p.getTargetBlock(null, 10).getType() == Material.BOOKSHELF)
		{
			if(j.getItemDrop().getItemStack().getType() == Material.BOOK 
					| j.getItemDrop().getItemStack().getType() == Material.WRITTEN_BOOK 
					| j.getItemDrop().getItemStack().getType() == Material.BOOK_AND_QUILL 
					| j.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK 
					| j.getItemDrop().getItemStack().getType() == Material.MAP 
					| j.getItemDrop().getItemStack().getType() == Material.PAPER
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_4
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_5
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_6
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_7
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_8
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_9
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_10
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_11
					| j.getItemDrop().getItemStack().getType() == Material.RECORD_12
					| j.getItemDrop().getItemStack().getTypeId() == 2257
					| j.getItemDrop().getItemStack().getTypeId() == 2256)
			{
				Location loc = p.getTargetBlock(null, 10).getLocation();
				
				try {
					r = BookShelf.mysql.query("SELECT * FROM copy WHERE x="+loc.getX()+" AND y="+loc.getY()+" AND z="+loc.getZ()+";");
					if(r.getInt("bool") == 1)
					{
						j.setCancelled(true);
					}
					r.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
	}
	int getidxyz(int x, int y, int z)
	{
		int last = -1;
		
		try {
			r = BookShelf.mysql.query("SELECT * FROM items WHERE x=" + x + " AND y=" + y + " AND z=" + z + " ORDER BY id DESC LIMIT 1;");
			last = r.getInt("id");
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return last;
	}
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) { return entry.getKey(); }
        }
        return null;
    }
	public void Book(ItemStack bookItem){
        BookMeta bookData = (BookMeta)bookItem.getItemMeta();
        if(bookItem.getType() == Material.WRITTEN_BOOK)
        {
	        this.author = bookData.getAuthor();
	        this.title = bookData.getTitle();
        }
        else
        {
        	this.author = "null";
        	this.title = "null";
        }
        if(bookData == null)
        {
        	String[] sPages = {""};
                    
            this.pages = sPages;
            return;
        }
        List<String> nPages;
        nPages = bookData.getPages();
        String[] sPages = new String[nPages.size()];
        for(int i = 0;i<nPages.size();i++)
        {
            sPages[i] = nPages.get(i).toString();
        }
                
        this.pages = sPages;
    }
    void Book(String title, String author, String[] pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    public String getAuthor()
    {
        return author;
    }
    public void setAuthor(String sAuthor)
    {
        author = sAuthor;
    }
    public String getTitle()
    {
        return title;
    }
    public String[] getPages()
    {
        return pages;
    }
    public ItemStack generateItemStack(int type){
    	ItemStack newbooka = new ItemStack(Material.WRITTEN_BOOK);
  		ItemStack newbook1a = new ItemStack(Material.BOOK_AND_QUILL);
    	ItemStack newbook2a = new ItemStack(Material.ENCHANTED_BOOK);
    	ItemStack newmapa = new ItemStack(Material.MAP);
    	
  		BookMeta newbook = (BookMeta)newbooka.getItemMeta();
  		BookMeta newbook1 = (BookMeta)newbook1a.getItemMeta();
  		EnchantmentStorageMeta newbook2 = (EnchantmentStorageMeta)newbook2a.getItemMeta();
  		
  		if(type == 3)
  		{
  			newmapa.setDurability(mapdur);
  			return newmapa;
  		}
  		else if(type == 2)
        {
        	newbook2.addStoredEnchant(etype, elvl, false);
        	newbook2a.setItemMeta(newbook2);
        	return newbook2a;
        }
        else if(type == 1)
        {
        	newbook1.setAuthor(author);
        	newbook1.setTitle(title);
        	for(int i = 0;i<pages.length;i++)
            {  
        		newbook1.addPage(pages[i]);
            }
        	newbook1a.setItemMeta(newbook1);
	        return newbook1a;
        }
        else if(type == 0)
        {
        	
        	newbook.setAuthor(author);
        	newbook.setTitle(title);
        	for(int i = 0;i<pages.length;i++)
            {  
        		newbook.addPage(pages[i]);
            }
        	newbooka.setItemMeta(newbook);
	        return newbooka;
        }
        else
        {
        	return null;
        }
    }
	
}
