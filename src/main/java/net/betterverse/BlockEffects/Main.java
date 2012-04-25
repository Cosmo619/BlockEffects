package net.betterverse.BlockEffects;

import net.betterverse.BlockEffects.LampStone.Lampstone;
import net.betterverse.BlockEffects.SignColors.SignColors;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    Lampstone ls;
    SignColors sc;
    
    @Override
    public void onEnable() {
        ls = new Lampstone(this);
        sc = new SignColors(this);
    }
    
    @Override
    public void onDisable() {
        Lampstone.sqlite.close();
    }

}
