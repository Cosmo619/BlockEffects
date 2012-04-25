package net.betterverse.BlockEffects.LampStone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    Lampstone plugin;
    
    public Commands(Lampstone instance) {
        plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return false;
        Player p = (Player) s;
        if (p.hasPermission("Lampstone.create")) {
            if (plugin.placers.contains(p)) {
                plugin.placers.remove(p);
                p.sendMessage("§aLampstone mode disabled");
                return true;
            } else {
                plugin.placers.add(p);
                p.sendMessage("§aLampstone mode enabled");
                return true;
            }
        } else {
            p.sendMessage("§cYou don't have permission to do that!");
            return true;
        }   
    }

}
