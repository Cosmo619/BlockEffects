package net.betterverse.Lampstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LSCommandListener
        implements CommandExecutor {
    private static Lampstone plugin;

    public LSCommandListener(Lampstone instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("lampstone")) {
            if ((sender instanceof Player)) {
                Player player = (Player) sender;
                if (player.hasPermission("Lampstone.create")) {
                    if (plugin.isPlacing(player)) {
                        plugin.removeFromPlacingList(player);
                        player.sendMessage("Lampstone mode disabled");
                    } else {
                        plugin.addToPlacingList(player);
                        player.sendMessage("Lampstone mode enabled.");
                    }
                   
                } else player.sendMessage("You do not have permission!");
                return true;
            }
        }
        return false;
    }
}