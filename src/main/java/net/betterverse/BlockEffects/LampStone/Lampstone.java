package net.betterverse.BlockEffects.LampStone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.betterverse.BlockEffects.Main;
import net.betterverse.BlockEffects.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class Lampstone {
    
    Main main;
    
    public static final String prefix = "[BlockEffects][Lampstone] ";
    PluginManager pm;
    
    public String pFolder;
    
    public static int day_block;
    public static int night_block;
    
    public static List<Location> lamps;
    public static List<Player> placers;
    
    public Lampstone(Main instance) {
        this.main = instance;
        pFolder = main.getDataFolder().getPath();
        pm = main.getServer().getPluginManager();
        lamps = new ArrayList<Location>();
        placers = new ArrayList<Player>();
        
        loadConf();
        loadLamps();
        
        main.getCommand("lampstone").setExecutor(new Commands(this));
        pm.registerEvents(new BlockListener(this), main);
        
        main.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Timer(this), 0L, 200L);
        
        Utils.log.info(prefix + "enabled");
    }
    
    private void loadConf() {
        Configuration config = main.getConfig();

        day_block = config.getInt("day_block", 20);
        night_block = config.getInt("night_block", 89);
        config.set("day_block", day_block);
        config.set("night_block", night_block);
        
        main.saveConfig();
    }
    
    private void loadLamps() {
        Utils.log.info(prefix + "Loading lamps...");
        lamps.clear();
        
        ResultSet results = main.db.sqlite.sqlQuery("SELECT * FROM lamps");
        try {
            while(results.next()) {
                World world = Bukkit.getServer().getWorld(results.getString("world"));
                if (world != null) {
                    Location loc = new Location(world, results.getInt("x"), results.getInt("y"), results.getInt("z"));
                    lamps.add(loc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.log.info(prefix + "Loaded " + lamps.size() + " lamps");
    }
    
    public void addLamp(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        World world = block.getWorld();
        Location loc = new Location(world, x, y, z);
        lamps.add(loc);
        String query = "INSERT INTO lamps (world, x, y, z) VALUES ('"+world.getName()+"', "+x+", "+y+", "+z+");";
        main.db.sqlite.insertQuery(query);
    }
    
    public void removeLamp(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        World world = block.getWorld();
        Location loc = new Location(world, x, y, z);
        lamps.remove(loc);
        String query = "DELETE FROM lamps WHERE world = '"+world.getName()+"' AND x = "+x+" AND y = "+y+" AND z = "+z+";";
        main.db.sqlite.deleteQuery(query);
    }
    
    public boolean isLamp(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        World world = block.getWorld();
        Location loc = new Location(world, x, y, z);
        return lamps.contains(loc);
    }

}
