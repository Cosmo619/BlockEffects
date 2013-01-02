package net.betterverse.BlockEffects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StackCommand implements CommandExecutor, Listener {

    List<Integer> noStack;

    public StackCommand(Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        File configFile = new File(plugin.getDataFolder(), "stack.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        noStack = config.getIntegerList("noStack");
        if (noStack == null) {
            noStack = new ArrayList<Integer>();
            noStack.add(Material.POTION.getId());
        }
        config.set("noStack", noStack);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory() instanceof BrewerInventory) {
            BrewerInventory inv = (BrewerInventory)e.getView().getTopInventory();
            ItemStack item = e.getCurrentItem();
            if (e.isShiftClick() && item != null && item.getType() == Material.POTION && e.getSlot() != e.getRawSlot() && item.getAmount() > 1) {
                int removed = 0;
                for (int i = item.getAmount() > 3 ? 2 : item.getAmount()-1; i >= 0; i--) {
                    if (inv.getItem(i) == null) {
                        inv.setItem(i, new ItemStack(Material.POTION));
                        removed++;
                    }
                }
                item.setAmount(item.getAmount()- removed);
                e.setResult(Result.DENY);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can stack the items in their inventory.");
            return true;
        }
        // Let the plugin.yml handle permissions.
        stackInventory(((Player)sender).getInventory());
        return true;
    }

    public void stackInventory(Inventory inv) {
        Map<ItemStack, Integer> items = new HashMap<ItemStack, Integer>();
        ItemStack[] contents = inv.getContents();
        for (ItemStack item : contents) {
            if (item == null) continue;
            ItemStack single = createCloneWithAmount(item, 1);
            if (items.containsKey(single)) {
                items.put(single, items.get(single)+item.getAmount());
            } else {
                items.put(single, item.getAmount());
            }
        }
        inv.setContents(createInventory(items));
    }

    public ItemStack[] createInventory(Map<ItemStack, Integer> items) {
        List<ItemStack> contents = new ArrayList<ItemStack>();
        for (Entry<ItemStack, Integer> entry : items.entrySet()) {
            if (noStack.contains(entry.getKey().getTypeId())) {
                for (int i = entry.getValue(); i > 0; i--) {
                    contents.add(createCloneWithAmount(entry.getKey(), 1));
                }
                continue;
            }
            for (int i = entry.getValue(); i > 0; i -= 64) {
                contents.add(createCloneWithAmount(entry.getKey(), i > 64 ? 64: i));
            }
        }
        ItemStack[] arrayContents = new ItemStack[contents.size()];
        for (int i = 0; i < contents.size(); i++) {
            arrayContents[i] = contents.get(i);
        }
        return arrayContents;
    }

    public ItemStack createCloneWithAmount(ItemStack item, int amount) {
        Validate.isTrue(amount < 65 && amount > 0, "Can only create clones with an amount less than 65 and greater than 0!  Got "+ amount+" instead.");
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        return clone;

    }

}
