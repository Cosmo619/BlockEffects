package net.betterverse.Lampstone;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Lampstone extends JavaPlugin
{
  private final LSBlockListener blockListener = new LSBlockListener(this);

  private LSTimeThread thread = null;

  private HashMap<String, LSLocation> lampstoneBlocks = new HashMap();
  private ArrayList<Player> playersPlacing = new ArrayList();
  private ArrayList<World> worldsLoaded = new ArrayList();

  private SaverAndLoader sAndL = new SaverAndLoader(this);
  public static String locationsFile = "lampstoneLocations.yml";
  public static String propertiesFile = "lampstoneProperties.yml";
  public int blockDay = 20;
  public int blockNight = 89;

  public void onDisable()
  {
  }

  public void onEnable()
  {
    PluginDescriptionFile pdfFile = getDescription();
    System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");

    PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(blockListener, this);

    getCommand("lampstone").setExecutor(new LSCommandListener(this));


    this.worldsLoaded = ((ArrayList)getServer().getWorlds());

    LoadProperties.load(this);

    this.thread = new LSTimeThread(this);
    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, this.thread, 0L, 40L);

    this.lampstoneBlocks = this.sAndL.loadBlocks();
  }

  public boolean isPlacing(Player player)
  {
    return this.playersPlacing.contains(player);
  }

  public void addToPlacingList(Player player)
  {
    if (!this.playersPlacing.contains(player))
      this.playersPlacing.add(player);
  }

  public void removeFromPlacingList(Player player)
  {
    if (this.playersPlacing.contains(player))
      this.playersPlacing.remove(player);
  }

  public boolean isLampstoneBlock(Block block)
  {
    LSLocation l = new LSLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

    for (LSLocation l1 : this.lampstoneBlocks.values()) {
      if ((l1.getX() == l.getX()) && (l1.getY() == l.getY()) && (l1.getZ() == l.getZ()) && (l1.getWorldName().equals(l.getWorldName()))) {
        return true;
      }
    }

    return false;
  }

  public void addLampstoneBlock(Block block)
  {
    if (!this.lampstoneBlocks.containsValue(new LSLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getName()))) {
      String uid = String.valueOf(block.hashCode() + System.currentTimeMillis());
      this.lampstoneBlocks.put(uid, new LSLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getName()));
      this.sAndL.saveBlock(uid, new LSLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getName()));
    }
  }

  public void removeLampstoneBlock(Block block)
  {
    String uid = null;
    LSLocation l = new LSLocation(block.getX(), block.getY(), block.getZ(), block.getWorld().getName());

    for (String s : this.lampstoneBlocks.keySet()) {
      LSLocation l1 = (LSLocation)this.lampstoneBlocks.get(s);

      if ((l1.getX() == l.getX()) && (l1.getY() == l.getY()) && (l1.getZ() == l.getZ()) && (l1.getWorldName().equals(l.getWorldName()))) {
        uid = s;
        break;
      }

    }

    if (uid == null) {
      System.out.println("ÅÅH");
    }

    this.lampstoneBlocks.remove(uid);
    this.sAndL.removeBlock(uid);
  }

  public HashMap<String, LSLocation> getLampstones() {
    return this.lampstoneBlocks;
  }

  public Boolean worldIsLoaded(String worldName)
  {
    for (World world : this.worldsLoaded) {
      if (world.getName().equalsIgnoreCase(worldName)) {
        return Boolean.valueOf(true);
      }

    }

    return Boolean.valueOf(false);
  }

  public void addWorld(World world) {
    this.worldsLoaded.add(world);
  }

  public void removeWorld(World world) {
    if (this.worldsLoaded.contains(world))
      this.worldsLoaded.remove(world);
  }
}