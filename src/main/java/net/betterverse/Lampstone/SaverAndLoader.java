package net.betterverse.Lampstone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SaverAndLoader
{
  private static Lampstone plugin;
  private static FileConfiguration file;
	private static File f;

  public SaverAndLoader(Lampstone instance)
  {
    plugin = instance;

    File p = plugin.getDataFolder() != null ? plugin.getDataFolder() : new File("plugins/Lampstone");
    f = new File(p, Lampstone.locationsFile);

    if (!p.exists()) {
      p.mkdirs();
    }

    if (!f.exists()) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }

      file = YamlConfiguration.loadConfiguration(f);
      file.set("locations", null);
			try {
				file.save(f);
			} catch (IOException ex) {
				Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
			}
    } else {
      file = YamlConfiguration.loadConfiguration(f);
    }
  }

  public void saveBlock(String uid, LSLocation block)
  {
		try {
			file.load(f);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidConfigurationException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		}

    ArrayList location = new ArrayList();
    double x = block.getX();
    double y = block.getY();
    double z = block.getZ();
    String w = block.getWorldName();

    location.add(Double.valueOf(x));
    location.add(Double.valueOf(y));
    location.add(Double.valueOf(z));
    location.add(w);

    file.set("locations." + uid, location);
		try {
			file.save(f);
		} catch (IOException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
  }

  public void removeBlock(String uid)
  {
		try {
			file.load(f);

			file.set("locations." + uid,null);

			file.save(f);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidConfigurationException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
  }

  public HashMap<String, LSLocation> loadBlocks()
  {
		try {
			file.load(f);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvalidConfigurationException ex) {
			Logger.getLogger(SaverAndLoader.class.getName()).log(Level.SEVERE, null, ex);
		}

    HashMap blockLocations = new HashMap();

    List locations = file.getStringList("locations");

    long loaded = 0L;

    if (locations != null)
    {
      for (int i = 0; i < locations.size(); i++) {
        List location = file.getList("locations." + (String)locations.get(i));

        int x = (int)Double.parseDouble(location.get(0).toString());
        int y = (int)Double.parseDouble(location.get(1).toString());
        int z = (int)Double.parseDouble(location.get(2).toString());
        String w = location.get(3).toString();

        blockLocations.put((String)locations.get(i), new LSLocation(x, y, z, w));

        loaded += 1L;
      }
    }

    System.out.println("[Lampstone] " + loaded + " lampstones loaded.");

    return blockLocations;
  }
}