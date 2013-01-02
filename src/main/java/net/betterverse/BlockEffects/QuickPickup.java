package net.betterverse.BlockEffects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickPickup implements Listener, CommandExecutor {
    Map<String, Long> users = new HashMap<String, Long>();

    public QuickPickup(JavaPlugin p) {
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (users.containsKey(e.getPlayer().getName())) {
            Collection<ItemStack> drops = e.getBlock().getDrops();
            e.getPlayer().sendMessage(drops.toString());
            e.getPlayer().sendMessage(Integer.toString(drops.size()));
            HashMap<Integer, ItemStack> noFit = e.getPlayer().getInventory().addItem(drops.toArray(new ItemStack[drops.size()]));
            if (!noFit.isEmpty()) {
                sendMessage(e.getPlayer());
                for (ItemStack item : noFit.values()) {
                    e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
                }
            }
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);

        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        users.remove(e.getPlayer().getName());
    }

    public void sendMessage(Player p) {
        // Check the time since they last received a message that their inventory was full.
        if (System.currentTimeMillis() - users.get(p.getName()) > (15 * 1000)) {
            p.sendMessage(ChatColor.RED+"Your inventory is full, items will drop to the ground until you make more room.");
            users.put(p.getName(), System.currentTimeMillis());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Go home console, you're drunk.");
            return true;
        }
        users.put(sender.getName(), (long)0);
        sender.sendMessage(ChatColor.GREEN+"QuickPickup activated!");
        
        return true;
    }
}
