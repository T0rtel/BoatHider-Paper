package me.tortel.boatHider1218;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.tortel.boatHider1218.api.BoatHiderAPI;
import me.tortel.boatHider1218.commands.devCommands;
import me.tortel.boatHider1218.listeners.BoatListeners;
import me.tortel.boatHider1218.listeners.PersistenceListeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class BoatHiderMain extends JavaPlugin {
    @Getter
    private static BoatHiderMain instance;

    @Getter
    private BoatHiderAPI api;

    @Override
    public void onEnable() {
        instance = this;

        this.api = new BoatHiderAPI();

        if (!Objects.equals(Bukkit.getVersion().split("-")[0], "1.21.8")){
            System.out.println("not the required version.");
            throw new RuntimeException("Version inCompatible with BoatHider 1.21.8, expected 1.21.8 but found " + Bukkit.getVersion().split("-")[0]);
        }

        getServer().getPluginManager().registerEvents(new BoatListeners(), this);
        getServer().getPluginManager().registerEvents(new PersistenceListeners(this), this);
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new devCommands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
