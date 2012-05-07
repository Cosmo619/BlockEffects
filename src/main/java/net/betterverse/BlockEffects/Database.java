package net.betterverse.BlockEffects;

import java.io.File;
import java.sql.Connection;
import lib.net.darqy.SQLib.SQLite;

public class Database {
    
    public SQLite sqlite;
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
        sqlite = new SQLite(Utils.log, "[BlockEffects] ", "blockeffects.db", plugin.getDataFolder().getAbsolutePath());
        sqlite.open();
        conn = sqlite.getConnection();
        
        if (!sqlite.tableExists("lamps")) {
            Utils.log.info("[BlockEffects] lamps table didn't exist! Creating...");
            sqlite.modifyQuery("CREATE TABLE `lamps` (`id` INTEGER PRIMARY KEY, `world` VARCHAR(32), `x` INT, `y` INT, `z` INT);");
        }
        
        if(!sqlite.tableExists("messagesigns")) {
            Utils.log.info("[BlockEffects] messagesigns table didn't exist! Creating...");
            sqlite.modifyQuery("CREATE  TABLE `messagesigns` (`id` INTEGER PRIMARY KEY, `owner` VARCHAR(32) NOT NULL, `message` TEXT NOT NULL, `location` BLOB NOT NULL);");
        }
    }
    
}
