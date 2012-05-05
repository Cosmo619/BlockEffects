package net.betterverse.BlockEffects.SignMessage;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignMessageListener implements Listener {
    
    SignMessage sm;
    
    HashMap<Player, Block> blocks = new HashMap<Player, Block>();
    
    BlockFace[] blockfaces = new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
    
    public SignMessageListener(SignMessage instance) {
        this.sm = instance;
    }
    
    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        if (sm.editors.containsKey(e.getPlayer()) && sm.editors.get(e.getPlayer()) == true) {
            e.setCancelled(true);
            
            Block b = blocks.get(e.getPlayer());
            blocks.remove(e.getPlayer());
            
            MessageSign ms = new MessageSign(e.getPlayer().getName(), e.getMessage(), b.getLocation());
            
            sm.saveMessageSign(ms);
            sm.editors.remove(e.getPlayer());
            
            e.getPlayer().sendMessage("§aSign Message set");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getState() instanceof Sign)) {
            Player p = e.getPlayer();
            
            if (sm.signs.containsKey(e.getClickedBlock().getLocation())) {
                MessageSign ms = sm.signs.get(e.getClickedBlock().getLocation());
                
                String message = ms.getMessage();
                message = message.replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1");
                
                p.sendMessage(message);
            }
            
            if (sm.editors.containsKey(p) && sm.editors.get(p) == false) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) e.getClickedBlock().getState().getData();
                Material mat = e.getClickedBlock().getRelative(sign.getAttachedFace()).getType();
                
                if (mat.equals(Material.SAND) || mat.equals(Material.GRAVEL) || mat.equals(Material.ICE)) {
                    p.sendMessage("§cYou can not register a sign that is attached to:");
                    p.sendMessage("§cSand, Gravel or Ice");
                    return;
                }
            
                p.sendMessage("§aSign registered, what would you like it to say?");
                
                blocks.put(p, e.getClickedBlock());
                sm.editors.put(p, true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        //They've attempted to break the actual sign
        if (sm.signs.containsKey(e.getBlock().getLocation())) {
            if ((!e.getPlayer().hasPermission("blockeffects.admin.signtext"))
                && (!sm.signs.get(e.getBlock().getLocation()).getOwner().equals(e.getPlayer().getName()))) {
                e.setCancelled(true);
                
                e.getPlayer().sendMessage("§cYou don't have permission to break that sign");
                return;
            }
            
            //They're allowed to break, we can remove
            sm.delMessageSign(sm.signs.get(e.getBlock().getLocation()));
        }
        
        //They broke a sign after selecting it
        if (blocks.containsKey(e.getPlayer()) && blocks.get(e.getPlayer()).equals(e.getBlock())) {
            e.getPlayer().sendMessage("§cSign registration cancelled");
            
            blocks.remove(e.getPlayer());
            sm.editors.remove(e.getPlayer());
        }
        
        //They've attempted to break a block surrounding the sign - brain hurt
        for(BlockFace face : blockfaces) {
            Block b = e.getBlock().getRelative(face);
            
            if ((b.getState() instanceof Sign) && sm.signs.containsKey(b.getLocation())) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                
                //Return if they're not breaking the block the sign is attached to.
                if (!b.getRelative(sign.getAttachedFace()).equals(e.getBlock())) return;
                
                if ((!e.getPlayer().hasPermission("blockeffects.admin.signtext"))
                    && (!sm.signs.get(b.getLocation()).getOwner().equals(e.getPlayer().getName()))) {
                    e.setCancelled(true);

                    e.getPlayer().sendMessage("§cYou don't have permission to break that block");
                    return;
                }
                
                //They're allowed to break, we can remove
                sm.delMessageSign(sm.signs.get(b.getLocation()));
            }
        }
        
    }
    
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        //Check all pushed blocks.
        for (Block b : e.getBlocks()) {
            if ((b.getState() instanceof Sign) && (!sm.signs.containsKey(b.getLocation()))) {
                e.setCancelled(true);
                return;
            }
        
            for(BlockFace face : blockfaces) {
                Block block = b.getRelative(face);

                if ((block.getState() instanceof Sign) && sm.signs.containsKey(block.getLocation())) {
                    org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();

                    //This block isn't attached to this sign, return.
                    if (!(block.getRelative(sign.getAttachedFace()).equals(b))) return;
                    
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if (!e.isSticky()) return;
        
        Block b = e.getBlock().getRelative(e.getDirection(), 2);
        
        if ((b.getState() instanceof Sign) && sm.signs.containsKey(b.getLocation())) {
            e.setCancelled(true);
        }
        
        for(BlockFace face : blockfaces) {
            Block block = b.getRelative(face);
            
            if ((block.getState() instanceof Sign) && sm.signs.containsKey(block.getLocation())) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();

                //This block isn't attached to this sign, return.
                if (!block.getRelative(sign.getAttachedFace()).equals(b)) return;

                e.setCancelled(true);
            }
        }
    }

}
