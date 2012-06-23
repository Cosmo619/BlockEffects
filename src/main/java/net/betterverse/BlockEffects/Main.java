package net.betterverse.BlockEffects;

import net.betterverse.BlockEffects.EnderCrystal.EnderCrystal;
import net.betterverse.BlockEffects.LampStone.Lampstone;
import net.betterverse.BlockEffects.SignMessage.SignMessage;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    public Database db;
    public Lampstone ls;
    public SignColors sc;
    public MessagePlates mp;
    public SignMessage sm;
    public ChestSign cs;
    public EnderCrystal ec;
    
    @Override
    public void onEnable() {
        db = new Database(this);
        ls = new Lampstone(this);
        sc = new SignColors(this);
        mp = new MessagePlates(this);
        sm = new SignMessage(this);
        cs = new ChestSign(this);
        ec = new EnderCrystal(this);
        
        getCommand("blockeffects").setExecutor(new Commands(this));
    }
    
    @Override
    public void onDisable() {
        db.sqlite.close();
    }

}
