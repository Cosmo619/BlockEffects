package net.betterverse.BlockEffects.EnderCrystal;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EnderCrystalCommand implements CommandExecutor {
    private final EnderCrystal main;
    
    public EnderCrystalCommand(EnderCrystal main) {
        this.main = main;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
            String[] args) {
        if (args.length != 1) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is player only!");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("place")) {
            if (!(player.hasPermission("BlockEffects.endercrystal.place"))) {
                player.sendMessage("You don't have permission!");
                return true;
            }
    		Block block = player.getTargetBlock(null, 300);
            if (block == null) {
                return true;
            }
            
            final BlockPlaceEvent e = new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), player.getItemInHand(), player, true);
            main.main.getServer().getPluginManager().callEvent(e);
            if (e.isCancelled()) return true;
            Block above = block.getRelative(BlockFace.UP);
            Block fire = above.getRelative(BlockFace.UP);
			if (above.getType() != Material.AIR || fire.getType() != Material.AIR) {
                    player.sendMessage("Not enough space at target location!");
                return true;
            }
            RegisteredServiceProvider<Economy> economy = main.getMain().getServer().getServicesManager().getRegistration(Economy.class);
            if (economy != null) {
                Economy eco = economy.getProvider();
                if (!(eco.has(player.getName(), main.getCost()))) {
                    player.sendMessage("You don't have enough money!");
                    return true;
                } else {
                    eco.withdrawPlayer(player.getName(), main.getCost());
                }
            }
            player.getWorld().spawn(above.getLocation().add(0.5D, 0D, 0.5D), org.bukkit.entity.EnderCrystal.class);
            above.setType(Material.BEDROCK);
            fire.setType(Material.FIRE);
            player.sendMessage("Crystal placed!");
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!(player.hasPermission("BlockEffects.endercrystal.remove"))) {
                player.sendMessage("You don't have permission!");
                return true;
            }	
            for (Entity e : player.getNearbyEntities(1D, 1D, 1D)) {
                if (e.getType() == EntityType.ENDER_CRYSTAL) {
                    Block block = e.getLocation().getBlock().getRelative(BlockFace.DOWN);
                    
                    final BlockBreakEvent evt = new BlockBreakEvent(block, player);
                    main.main.getServer().getPluginManager().callEvent(evt);
                    if (evt.isCancelled()) return true;
                    
                    if (block.getType() == Material.BEDROCK) {
                        block.setType(Material.AIR);
                    }
                    e.remove();
                }
            }
            player.sendMessage("Crystal(s) removed!");
        } else {
            player.sendMessage("Unknown command!");
            return true;
        }
        return true;
    }

}