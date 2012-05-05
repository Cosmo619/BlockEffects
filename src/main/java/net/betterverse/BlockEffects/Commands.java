package net.betterverse.BlockEffects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    
    Main main;
    
    public Commands(Main instance) {
        this.main = instance;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (args.length < 1) return false;
        if (args[0].equalsIgnoreCase("signtext")) {
            if (!(s instanceof Player)) return false;
            
            if (!s.hasPermission("blockeffects.signtalk")) {
                s.sendMessage("§cYou don't have permission to do that");
                return true;
            }
            
            s.sendMessage("§aGo right click on a sign to set its message");
            
            main.sm.setEditor((Player) s);
        } else if(args[0].equalsIgnoreCase("signchest")) {
            if (!(s instanceof Player)) return false;
            
            if (!s.hasPermission("blockeffects.signchest")) {
                s.sendMessage("§cYou don't have permission to do that");
                return true;
            }
            
            s.sendMessage("§aClick the sign you want to attach to a chest");
            
            main.cs.addPlacer((Player) s);
        } else {
            s.sendMessage("§cInvalid argument");
        }
        
        return true;
    }

}
