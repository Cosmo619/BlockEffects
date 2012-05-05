package net.betterverse.BlockEffects;

import com.alta189.sqlLibrary.SQLite.sqlCore;
import java.io.File;
import java.sql.Connection;

public class Database {
    
    public sqlCore sqlite;
    public Connection conn;
    Main plugin;

    public Database(Main instance) {
        this.plugin = instance;
        check();
        setup();
    }
    
    private void check() {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/lampstone.db");
        if (file.exists()) {
            file.renameTo(new File(plugin.getDataFolder().getAbsolutePath() + "/blockeffects.db"));
        }
        
    }
    
    private void setup() {
        sqlite = new sqlCore(Utils.log, "[BlockEffects] ", "blockeffects", plugin.getDataFolder().getAbsolutePath());
        sqlite.initialize();
        conn = sqlite.getConnection();
        
        if (!sqlite.checkTable("lamps")) {
            Utils.log.info("[BlockEffects] lamps table didn't exist! Creating...");
            sqlite.createTable("CREATE TABLE `lamps` (`id` INT PRIMARY_KEY AUTO_INCREMENT, `world` VARCHAR(32), `x` INT, `y` INT, `z` INT);");
        }
        
        if(!sqlite.checkTable("messagesigns")) {
            Utils.log.info("[BlockEffects] messagesigns table didn't exist! Creating...");
            sqlite.createTable("CREATE  TABLE `messagesigns` (`id` INT PRIMARY_KEY AUTO_INCREMENT, `owner` VARCHAR(32) NOT NULL, `message` TEXT NOT NULL, `location` BLOB NOT NULL);");
        }
    }
    
}
