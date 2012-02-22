package net.betterverse.Lampstone;

import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class LSWorldListener implements Listener
{
  private static Lampstone plugin;

  public LSWorldListener(Lampstone instance)
  {
    plugin = instance;
  }

  public void onWorldLoad(WorldLoadEvent event) {
    plugin.addWorld(event.getWorld());
  }
}