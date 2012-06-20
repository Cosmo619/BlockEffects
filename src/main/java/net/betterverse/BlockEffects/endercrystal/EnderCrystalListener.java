package net.betterverse.BlockEffects.endercrystal;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EnderCrystalListener implements Listener {
    
    @EventHandler
    public void handle(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        if (type != EntityType.ENDER_CRYSTAL) {
            return;
        }
        World w = event.getLocation().getWorld();
        if (w.getEnvironment() != World.Environment.THE_END) {
            event.setCancelled(true);
        }
    }

}
