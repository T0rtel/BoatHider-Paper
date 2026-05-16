package me.tortel.boatHider1218;

import lombok.Getter;
import me.tortel.boatHider1218.commands.devCommands;
import me.tortel.boatHider1218.listeners.BoatListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatHiderMain extends JavaPlugin {

    @Getter
    private static BoatHiderMain instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new BoatListeners(), this);

        // Register commands
        devCommands devCommands = new devCommands();

        // Register the main /dev command with both executor and tab completer
        getCommand("dev").setExecutor(devCommands);
        getCommand("dev").setTabCompleter(devCommands);

        getLogger().info("BoatHider has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BoatHider has been disabled!");
    }
}