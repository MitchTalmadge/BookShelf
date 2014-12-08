/**
 * @author Mitch Talmadge
 *         Date Created:
 *         Jul 25, 2013
 */

package me.Pew446.BookShelf.LWC;

import java.lang.reflect.Field;

import com.griefcraft.lwc.LWCPlugin;

public class LWCPluginHandler 
{
    public LWCPluginHandler(LWCPlugin plugin)
    {
        try
        {
            Field lwc = plugin.getClass().getDeclaredField("lwc");
            LWCHandler LWCHandler = new LWCHandler(plugin);
            lwc.setAccessible(true);
            lwc.set(plugin, LWCHandler);
            LWCHandler.load();
        }
        catch(SecurityException e)
        {
            e.printStackTrace();
        }
        catch(NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch(IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
    
}
