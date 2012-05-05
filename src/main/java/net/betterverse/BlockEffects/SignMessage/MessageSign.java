package net.betterverse.BlockEffects.SignMessage;

import org.bukkit.Location;

public class MessageSign {
    
    private String owner;
    private String message;
    private Location loc;
    
    /**
     * MessageSign constructor
     * 
     * @param owner Owner name
     * @param message Sign message
     * @param loc Sign location
     */
    public MessageSign(String owner, String message, Location loc) {
        this.owner = owner;
        this.message = message;
        this.loc = loc;
    }
    
    /**
     * Get owner name
     */
    public String getOwner() {
        return owner;
    }
    
    /**
     * Get message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get location
     */
    public Location getLocation() {
        return loc;
    }
    
}
