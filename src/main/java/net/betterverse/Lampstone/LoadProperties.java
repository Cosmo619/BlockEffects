package net.betterverse.Lampstone;

import java.io.File;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LoadProperties
{
  public static void load(Lampstone instance)
  {
    File file = new File(instance.getDataFolder(), Lampstone.propertiesFile);

    if (!file.exists()) {
      System.out.println("\tConfiguration doesn't exist");
      try
      {
        file.createNewFile();
        System.out.println("\tCreated new configuration file");
      } catch (IOException e) {
        e.printStackTrace();
      }

      FileConfiguration c = YamlConfiguration.loadConfiguration(file);

      c.set("blockDay", Integer.valueOf(20));
      c.set("blockNight", Integer.valueOf(89));
			try {
				c.save(file);
			} catch (IOException ex) {
        System.out.println("Unable to save configuration file.");
        instance.getPluginLoader().disablePlugin(instance);
			}
    }

    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    int blockDay = config.getInt("blockDay", 20);
    int blockNight = config.getInt("blockNight", 89);

    if (Material.getMaterial(blockDay) == null) {
      System.out.println("Lampstone: Your blockDay ID is invalid. Please change it in lampstoneProperties.yml. Will fall back to default ID (20).");
      blockDay = 20;
    }

    if (Material.getMaterial(blockNight) == null) {
      System.out.println("Lampstone: Your blockNight ID is invalid. Please change it in lampstoneProperties.yml. Will fall back to default ID (89).");
      blockNight = 89;
    }

    instance.blockDay = blockDay;
    instance.blockNight = blockNight;
  }
}