package net.betterverse.BlockEffects.LampStone;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Timer implements Runnable {

    Lampstone plugin;
    
    public Timer(Lampstone instance) {
        plugin = instance;
    }
    
    @Override
    public void run() {
        for (Location loc : plugin.lamps) {
            World world = loc.getWorld();
            Block block = loc.getBlock();
            if (!world.isChunkLoaded(block.getChunk())) continue;
            Long time = world.getTime();
            
            if (time >= 13000L && time < 23400L) {
                if (block.getTypeId() != plugin.night_block)
                    block.setTypeId(plugin.night_block);
            } else {
                if (block.getTypeId() != plugin.day_block)
                    block.setTypeId(plugin.day_block);
            }
        }
    }

}
