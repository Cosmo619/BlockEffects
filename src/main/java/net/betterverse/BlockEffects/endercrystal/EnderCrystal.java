package net.betterverse.BlockEffects.endercrystal;

import java.io.File;
import java.io.IOException;

import net.betterverse.BlockEffects.Main;

import org.bukkit.configuration.file.YamlConfiguration;

public class EnderCrystal {
    public static final String PREFIX = "[BlockEffects][EnderCrystal] ";
    
    private final File configFile;
    
    private Main main;
    
    private int cost;
    
    public EnderCrystal(Main main) {
        this.main = main;
        this.configFile = new File(main.getDataFolder(), "endercrystals.yml");
        if (!(configFile.exists())) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        main.getCommand("endercrystal").setExecutor(new EnderCrystalCommand(this));  
        main.getServer().getPluginManager().registerEvents(new EnderCrystalListener(), main);
        
        loadConfig();
    }
    
    public void loadConfig() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        cost = config.getInt("cost", 50000);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Main getMain() {
        return main;
    }
    
    public int getCost() {
        return cost;
    }

}
