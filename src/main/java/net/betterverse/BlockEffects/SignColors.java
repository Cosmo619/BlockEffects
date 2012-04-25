package net.betterverse.BlockEffects;

import net.betterverse.BlockEffects.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignColors implements Listener {
    
    Main plugin;
    
    public SignColors(Main instance) {
        this.plugin = instance;
        setup();
    }
    
    public void setup() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String s;
        for (int i = 0; i <= 3; i++) {
            if (e.getPlayer().hasPermission("blockeffects.signcolors")) {
                s = e.getLine(i).replaceAll("(?i)&([a-fk-or0-9])", "\u00A7$1");
            } else {
                s = e.getLine(i).replaceAll("(?i)&([a-fk-or0-9])", "");
            }
            e.setLine(i, s);
        }
    }
    
}
