package net.betterverse.BlockEffects.LampStone;

import com.alta189.sqlLibrary.SQLite.sqlCore;
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
    
    Main plugin;
    
    public static final String prefix = "[BlockEffects][Lampstone] ";
    PluginManager pm;
    
    public String pFolder;
    
    public static sqlCore sqlite;
    
    public static int day_block;
    public static int night_block;
    
    public static List<Location> lamps;
    public static List<Player> placers;
    
    public Lampstone(Main instance) {
        this.plugin = instance;
        pFolder = plugin.getDataFolder().getPath();
        pm = plugin.getServer().getPluginManager();
        lamps = new ArrayList<Location>();
        placers = new ArrayList<Player>();
        
        loadConf();
        setupDatabase();
        
        loadLamps();
        plugin.getCommand("lampstone").setExecutor(new Commands(this));
        pm.registerEvents(new BlockListener(this), plugin);
        
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Timer(this), 0L, 200L);
        
        Utils.log.info(prefix + " enabled");
    }
    

    
    private void setupDatabase() {
        sqlite = new sqlCore(Utils.log, prefix, "lampstone", pFolder);
        sqlite.initialize();
        
        if (!sqlite.checkTable("lamps")) {
            Utils.log.info(prefix + "Creating lamps table..");
            String query = "CREATE TABLE lamps (`id` INT AUTO_INCREMENT PRIMARY_KEY, `world` VARCHAR(32), `x` INT, `y` INT, `z` INT);";
            sqlite.createTable(query);
        }
    }
    
    private void loadConf() {
        Configuration config = plugin.getConfig();

        day_block = config.getInt("day_block", 20);
        night_block = config.getInt("night_block", 89);
        config.set("day_block", day_block);
        config.set("night_block", night_block);
        
        plugin.saveConfig();
    }
    
    private void loadLamps() {
        Utils.log.info(prefix + "Loading lamps...");
        lamps.clear();
        int i = 0;
        
        ResultSet results = sqlite.sqlQuery("SELECT * FROM lamps");
        try {
            while(results.next()) {
                World world = Bukkit.getServer().getWorld(results.getString("world"));
                if (world != null) {
                    Location loc = new Location(world, results.getInt("x"), results.getInt("y"), results.getInt("z"));
                    lamps.add(loc);
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Utils.log.info(prefix + "Loaded " + i + " lamps");
    }
    
    public void addLamp(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        World world = block.getWorld();
        Location loc = new Location(world, x, y, z);
        lamps.add(loc);
        String query = "INSERT INTO lamps (world, x, y, z) VALUES ('"+world.getName()+"', "+x+", "+y+", "+z+");";
        sqlite.insertQuery(query);
    }
    
    public void removeLamp(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        World world = block.getWorld();
        Location loc = new Location(world, x, y, z);
        lamps.remove(loc);
        String query = "DELETE FROM lamps WHERE world = '"+world.getName()+"' AND x = "+x+" AND y = "+y+" AND z = "+z+";";
        sqlite.deleteQuery(query);
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
