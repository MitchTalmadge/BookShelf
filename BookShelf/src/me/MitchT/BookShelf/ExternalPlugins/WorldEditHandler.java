package me.MitchT.BookShelf.ExternalPlugins;

import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.Subscribe;

public class WorldEditHandler
{
    @Subscribe
    public void wrapForLogging(EditSessionEvent event)
    {
        Actor actor = event.getActor();
        
        if(actor != null && actor.isPlayer())
        {
            event.setExtent(new WorldEditLogger(actor, event.getExtent()));
        }
    }
}
