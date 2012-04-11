package net.betterverse.Lampstone;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    Lampstone plugin;
    
    public Command(Lampstone instance) {
        plugin = instance;
    }
    
    @Override //org.bukkit.command.Command wat? NetBeans, u fail.
    public boolean onCommand(CommandSender s, org.bukkit.command.Command c, String l, String[] args) {
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
