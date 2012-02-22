package net.betterverse.Lampstone;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LSTimeThread
  implements Runnable
{
  private static Lampstone plugin;

  public LSTimeThread(Lampstone instance)
  {
    plugin = instance;
  }

  public void run()
  {
    HashMap<String,LSLocation> lampstones = plugin.getLampstones();

    for (LSLocation blockLocation : lampstones.values())
    {
      if (plugin.getServer().getWorld(blockLocation.getWorldName()) == null) {
        continue;
      }
      World world = plugin.getServer().getWorld(blockLocation.getWorldName());
      Block block = world.getBlockAt(blockLocation.getX(), 
        blockLocation.getY(), 
        blockLocation.getZ());
      long time = world.getTime();

      if (!world.isChunkLoaded(block.getChunk()))
        continue;
      if ((time > 13000L) && (time < 23400L)) {
        if (!block.getType().equals(Material.getMaterial(plugin.blockNight)))
          block.setType(Material.getMaterial(plugin.blockNight));
      }
      else {
        if (((time <= 23400L) || (time >= 24000L)) && ((time <= 0L) || (time >= 13000L) || 
          (block.getType().equals(Material.getMaterial(plugin.blockDay))))) continue;
        block.setType(Material.getMaterial(plugin.blockDay));
      }
    }
  }
}