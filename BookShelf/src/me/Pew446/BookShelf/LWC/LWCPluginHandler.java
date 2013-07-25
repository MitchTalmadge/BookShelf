/**
@author	Mitch Talmadge
Date Created:
	Jul 25, 2013
*/

package me.Pew446.BookShelf.LWC;

import com.griefcraft.lwc.LWCPlugin;

public class LWCPluginHandler extends LWCPlugin
{
	public LWCHandler LWCHandler;
	private LWCPlugin plugin;

	public LWCPluginHandler(LWCPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void onDisable() 
	{
		if(LWCHandler != null)
		{
			LWCHandler.destruct();
		}
		super.onDisable();
	}

	public void init() {
		this.LWCHandler = new LWCHandler(plugin);
	}
}