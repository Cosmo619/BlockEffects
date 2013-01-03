package net.betterverse.BlockEffects;

import java.awt.Button;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public class ThrowBuild implements Listener, CommandExecutor {

    private List<Integer> noBreak;
    Set<String> users = new HashSet<String>();

    int maxDistance = 25;

    public ThrowBuild(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        File configFile = new File(plugin.getDataFolder(), "throwbuild.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        maxDistance = config.getInt("maxDistance", 25);
        config.set("maxDistance", maxDistance);
        noBreak = config.getIntegerList("noBreak");
        if (noBreak == null) {
            noBreak = new ArrayList<Integer>();
            noBreak.add(Material.AIR.getId());
        }
        config.set("noBreak", noBreak);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (users.contains(e.getPlayer().getName())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR) {
                throwBreak(e.getPlayer());
            } else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                throwBuild(e.getPlayer());
            }
        }
    }
    
    public void throwBreak(Player player) {
        // Get where the block where they're looking.
        List<Block> targets = player.getLastTwoTargetBlocks(null, maxDistance);
        // Ensure they have a target.
        if (targets == null || targets.size() < 2) {
            return;
        }
        if (noBreak.contains(targets.get(1).getTypeId())) {
            player.sendMessage(ChatColor.RED+ targets.get(1).getType().toString()+ " cannot be broken via throwbuild.");
            return;
        }
        // Compatability with other plugins.
        BlockBreakEvent event = new BlockBreakEvent(targets.get(1), player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        // Break the block.
        targets.get(1).breakNaturally();
    }

    public void throwBuild(Player player) {
        // Get targets.
        List<Block> targets = player.getLastTwoTargetBlocks(null, maxDistance);
        // Validate targets and the item in the player's hand.
        if (targets == null || targets.size() < 2 || targets.get(1).getType() == Material.AIR || player.getItemInHand() == null || player.getItemInHand().getTypeId() > 122) {
            return;
        }
        // Chest placement is a hassle, they're very finicky.
        if (player.getItemInHand().getType() == Material.CHEST) {
            player.sendMessage(ChatColor.RED+"Sorry, at this time chests cannot be placed via throwbuild.");
            return;
        }
        
        Block target = targets.get(0);
        // Keep a blockstate in case we need to revert(if the event is cancelled).
        BlockState state = targets.get(0).getState();
        // Durability is important for things like wool.
        target.setTypeIdAndData(player.getItemInHand().getTypeId(), (byte)player.getItemInHand().getDurability(), true);
        MaterialData data = getMaterialData(target);
        // Make everything face the correct way.
        if (data instanceof DirectionalContainer) {
            // This should make the container face the topposite of where the player is looking, or in other words, towards the player.
            // getClosestFace is off by 90 degrees(too lazy to fix), so add 90 rather than 180.
            ((DirectionalContainer) data).setFacingDirection(getClosestFace(player.getLocation().getYaw()+90));
        } else if (data instanceof Directional) {
            // Things like torches, stairs, etc. need to be attached to the correct block near them.
            ((Directional) data).setFacingDirection(target.getFace(targets.get(1)).getOppositeFace());
        }
        target.setData(data.getData());
        // Compatability with other plugins.  
        BlockPlaceEvent event = new BlockPlaceEvent(target, state, targets.get(1), player.getItemInHand(), player, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            state.update(true);
            return; // We don't want to remove items if they can't build there.
        }
        int amount = player.getItemInHand().getAmount() -1;
        // Creative players shouldn't lose items.
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (amount <= 0) {
                player.setItemInHand(null);
            } else {
                ItemStack clone = player.getItemInHand();
                clone.setAmount(amount);
                player.setItemInHand(clone);
            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Go home console, you're drunk.");
            return true;
        }
        if (users.contains(sender.getName())) {
            users.remove(sender.getName());
            sender.sendMessage(ChatColor.GREEN+"Throwbuild disabled!");
            return true;
        }
        users.add(sender.getName());
        sender.sendMessage(ChatColor.GREEN+"Throwbuild enabled!");
        return true;
    }

    public MaterialData getMaterialData(Block b) {
        return b.getType().getNewData(b.getData());
    }

    public BlockFace getClosestFace(float direction){
        direction = direction % 360;
        if(direction < 0)
            direction += 360;
        direction = Math.round(direction / 45);
        switch((int)direction){
        case 0:
            return BlockFace.WEST;
        case 1:
            return BlockFace.NORTH_WEST;
        case 2:
            return BlockFace.NORTH;
        case 3:
            return BlockFace.NORTH_EAST;
        case 4:
            return BlockFace.EAST;
        case 5:
            return BlockFace.SOUTH_EAST;
        case 6:
            return BlockFace.SOUTH;
        case 7:
            return BlockFace.SOUTH_WEST;
        default:
            return BlockFace.WEST;

        }
    }


}
