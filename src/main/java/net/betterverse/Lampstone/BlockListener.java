package net.betterverse.Lampstone;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    
    Lampstone plugin;
    
    public BlockListener(Lampstone instance) {
        plugin = instance;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent evt) {
        Player p = evt.getPlayer();
        Block block = evt.getBlock();
        if (block.getTypeId() == plugin.night_block && plugin.placers.contains(p)) {
            plugin.addLamp(block);
            p.sendMessage("§aLampstone placed!");
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent evt) {
        Player p = evt.getPlayer();
        Block block = evt.getBlock();
        if (block.getTypeId() == plugin.night_block || block.getTypeId() == plugin.day_block) {
            if (plugin.isLamp(block)) {
                if (p.hasPermission("Lampstone.break")) {
                    plugin.removeLamp(block);
                    p.sendMessage("§aYou have broken a Lampstone");
                } else {
                    evt.setCancelled(true);
                    p.sendMessage("§cYou don't have permission to remove that Lampstone");
                }
            }
        }
    }

}
