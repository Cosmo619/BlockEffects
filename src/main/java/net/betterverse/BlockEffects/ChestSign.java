package net.betterverse.BlockEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestSign implements Listener {

    Main main;
    HashMap<Player, Sign> signs = new HashMap<Player, Sign>();
    List<Player> placers = new ArrayList<Player>();

    public ChestSign(Main instance) {
        this.main = instance;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            
            //Handle clicking chest
            if ((e.getClickedBlock().getType().equals(Material.CHEST)) && (signs.containsKey(e.getPlayer()))) {
                if (e.getBlockFace().equals(BlockFace.UP) || e.getBlockFace().equals(BlockFace.DOWN)) return;
               
                Block b = e.getClickedBlock().getRelative(e.getBlockFace());
                Sign sign = signs.get(e.getPlayer());

                b.setTypeIdAndData(68, getByte(e.getBlockFace()), true);
                Sign newsign = (Sign) b.getState();

                for(int i = 0; i < 3; i++) {
                    newsign.setLine(i, sign.getLine(i));
                }
                
                newsign.update();
                sign.getLocation().getBlock().setType(Material.AIR);
                
                signs.remove(e.getPlayer());
            }
        
            //Handle clicking sign
            if ((e.getClickedBlock().getState() instanceof Sign) && (placers.contains(e.getPlayer()))) {
                signs.put(e.getPlayer(), (Sign) e.getClickedBlock().getState());
                placers.remove(e.getPlayer());

                e.getPlayer().sendMessage("§aGo click a chest!");
            }
        }
    }
    
    public byte getByte(BlockFace bf) {
        switch(bf) {
            case NORTH:
                return 0x4;
            case EAST:
                return 0x2;
            case SOUTH:
                return 0x5;
            case WEST:
                return 0x3;
            default:
                return 0x4;
        }
    }
    
    public void addPlacer(Player p) {
        placers.add(p);
    }
    
}
