package net.betterverse.Lampstone;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class LSBlockListener implements Listener
{
  private static Lampstone plugin;

  public LSBlockListener(Lampstone instance)
  {
    plugin = instance;
  }

  public void onBlockBreak(BlockBreakEvent event)
  {
    Block block = event.getBlock();

    if (!(event.getPlayer().hasPermission("Lampstone.break"))) {
      if (plugin.isLampstoneBlock(block)) {
        event.setCancelled(true);
      }
      return;
    }

    if (plugin.isLampstoneBlock(block)) {
      plugin.removeLampstoneBlock(block);

      System.out.println("[Lampstone] Lampstone block removed by " + event.getPlayer().getDisplayName());
    }
  }

  public void onBlockPlace(BlockPlaceEvent event)
  {
    Block block = event.getBlockPlaced();
    Player player = event.getPlayer();

    if ((block.getType().equals(Material.getMaterial(plugin.blockNight))) && (plugin.isPlacing(player))) {
      plugin.addLampstoneBlock(block);
      player.sendMessage("Lampstone block placed.");
    }
  }
}