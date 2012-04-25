package net.betterverse.BlockEffects;

import net.betterverse.BlockEffects.LampStone.Lampstone;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    Lampstone ls;
    SignColors sc;
    MessagePlates mp;
    
    @Override
    public void onEnable() {
        ls = new Lampstone(this);
        sc = new SignColors(this);
        mp = new MessagePlates(this);
    }
    
    @Override
    public void onDisable() {
        Lampstone.sqlite.close();
    }

}
