package net.betterverse.BlockEffects;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MessagePlates implements Listener {
    
    Main plugin;
    
    public MessagePlates(Main instance) {
        this.plugin = instance;
        setup();
    }
    
    private void setup() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.PHYSICAL)) return;
        if (e.getClickedBlock().getType().equals(Material.WOOD_PLATE) || e.getClickedBlock().getType().equals(Material.STONE_PLATE)) {
            BlockState block = e.getClickedBlock().getRelative(BlockFace.DOWN, 2).getState();
            if (!(block instanceof Sign)) return;
            Sign s = (Sign) block;
            for (String str : s.getLines()) {
                if (str.isEmpty() || str.equals("")) continue;
                e.getPlayer().sendMessage(str);
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getState() instanceof Sign) {
            Material mat = e.getBlock().getRelative(BlockFace.UP, 2).getType();
            if (mat.equals(Material.WOOD_PLATE) || mat.equals(Material.STONE_PLATE)) {
                if (!e.getPlayer().hasPermission("blockeffects.messageplate")) {
                    e.getPlayer().sendMessage("§cYou can't place a Message Plate sign");
                    e.setCancelled(true);
                }
            }
        }
        
        if (e.getBlock().getType().equals(Material.WOOD_PLATE) || e.getBlock().getType().equals(Material.STONE_PLATE)) {
            BlockState bs = e.getBlock().getRelative(BlockFace.DOWN, 2).getState();
            if (bs instanceof Sign) {
                if (!e.getPlayer().hasPermission("blockeffects.messageplate")) {
                    e.getPlayer().sendMessage("§cYou can't place a Message Plate plate");
                    e.setCancelled(true);
                }
            }
        }
    }
    
}
