package net.betterverse.BlockEffects.SignMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import net.betterverse.BlockEffects.Main;
import net.betterverse.BlockEffects.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SignMessage {
    
    Main main;
    
    //Player being player, Boolean being if they've selected a sign
    public HashMap<Player, Boolean> editors = new HashMap<Player, Boolean>();
    public HashMap<Location, MessageSign> signs = new HashMap<Location, MessageSign>();
    
    public static final String prefix = "[BlockEffects][SignMessage] ";
    
    public SignMessage(Main instance) {
        this.main = instance;
        
        main.getServer().getPluginManager().registerEvents(new SignMessageListener(this), instance);
        
        try {
            readDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load MessageSigns into memory
     */
    private void readDb() throws SQLException {
        Statement stmt = main.db.conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM `messagesigns`");
        
        try {
            while(rs.next()) {
                String owner = rs.getString("owner");
                String message = rs.getString("message");
                byte[] buf = rs.getBytes("location");
                
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf));
                Object o = ois.readObject();
                
                Location loc = this.deserialize((Map) o);
                
                MessageSign ms = new MessageSign(owner, message, loc);
                
                signs.put(loc, ms);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        
        Utils.log.info(prefix + "Loaded " + signs.size() + " message signs");
    }
    
    /**
     * Saves MessageSign information to database
     */
    private void writeDb(MessageSign ms) {
        try {
            PreparedStatement stmt = main.db.conn.prepareStatement("INSERT INTO `messagesigns` (`owner`, `message`, `location`) VALUES (?, ?, ?);");

            stmt.setString(1, ms.getOwner());
            stmt.setString(2, ms.getMessage());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(serialize(ms.getLocation()));
            oos.close();

            stmt.setBytes(3, baos.toByteArray());

            stmt.execute();
            stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Removes a sign from the database
     */
    private void removeDb(MessageSign ms) {
        try {
            PreparedStatement stmt = main.db.conn.prepareStatement("DELETE FROM `messagesigns` WHERE `owner`=? AND `message`=?;");

            stmt.setString(1, ms.getOwner());
            stmt.setString(2, ms.getMessage());
            
            stmt.execute();
            stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Make a Location object serializable
     * 
     * @param loc The location to serialize
     */
    public Map<String, Object> serialize(Location loc) {
        Map<String, Object> serialized = new HashMap<String, Object>();
        
        serialized.put("world", loc.getWorld().getName());
        serialized.put("x", loc.getX());
        serialized.put("y", loc.getY());
        serialized.put("z", loc.getZ());
        
        return serialized;
    }
    
    /**
     * Gets the Location object from a Map
     * 
     * @param map The map to retrieve Location from
     */
    public Location deserialize(Map<String, Object> map) {
        World world = Bukkit.getWorld((String) map.get("world"));
        
        if (world == null) return null;
        
        double x = (Double) map.get("x");
        double y = (Double) map.get("y");
        double z = (Double) map.get("z");
        
        return new Location(world, x, y, z);
    }
    
    /**
     * Set a player to be allowed to set the message of a sign
     */
    public void setEditor(Player p) {
        editors.put(p, false);
    }
    
    /**
     * Puts new MessageSign in memory, saves to database
     * 
     * @param ms MessageSign to save
     */
    public void saveMessageSign(MessageSign ms) {
        writeDb(ms);
        
        signs.put(ms.getLocation(), ms);
    }
    
    /**
     * Removes a MessageSign from database, and memory
     * 
     * @param ms MessageSign to remove
     */
    public void delMessageSign(MessageSign ms) {
        removeDb(ms);
        
        signs.remove(ms.getLocation());
    }

}
