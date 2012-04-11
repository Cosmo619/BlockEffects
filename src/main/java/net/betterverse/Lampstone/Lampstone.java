package net.betterverse.Lampstone;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Lampstone extends JavaPlugin {
    
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String prefix = "[Lampstone] ";
    PluginManager pm;
    
    public String pFolder;
    
    public static sqlCore sqlite;
    
    public static int day_block = 20;
    public static int night_block = 89;
    
    public static List<Location> lamps;
    public static List<Player> placers;
    
    @Override
    public void onEnable() {
        pFolder = this.getDataFolder().getPath();
        pm = getServer().getPluginManager();
        lamps = new ArrayList<Location>();
        placers = new ArrayList<Player>();
        
        loadConf();
        setupDatabase();
        
        loadLamps();
        getCommand("lampstone").setExecutor(new Command(this));
        pm.registerEvents(new BlockListener(this), this);
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Timer(this), 0L, 200L);
        
        log.info(prefix + this.getDescription().getVersion() + " enabled");
    }
    
    @Override
    public void onDisable() {
        sqlite.close();
    }
    
    void setupDatabase() {
        sqlite = new sqlCore(log, prefix, "lampstone", pFolder);
        sqlite.initialize();
        
        if (!sqlite.checkTable("lamps")) {
            log.info(prefix + "Creating lamps table..");
            String query = "CREATE TABLE lamps (`id` INT AUTO_INCREMENT PRIMARY_KEY, `world` VARCHAR(32), `x` INT, `y` INT, `z` INT);";
            sqlite.createTable(query);
        }
    }
    
    void loadConf() {
        YamlConfiguration config;
        
        try {
            File configFile = new File(pFolder, "config.yml");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            
            config = (YamlConfiguration) getConfig();
            
            day_block = config.getInt("day_block", day_block);
            night_block = config.getInt("night_block", night_block);
            config.set("day_block", day_block);
            config.set("night_block", night_block);
            
            saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void loadLamps() {
        log.info(prefix + "Loading lamps...");
        lamps.clear();
        int i = 0;
        
        ResultSet results = sqlite.sqlQuery("SELECT * FROM lamps");
        try {
            while(results.next()) {
                World world = getServer().getWorld(results.getString("world"));
                if (world != null) {
                    Location loc = new Location(world, results.getInt("x"), results.getInt("y"), results.getInt("z"));
                    lamps.add(loc);
                    i++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info(prefix + "Loaded " + i + " lamps");
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
