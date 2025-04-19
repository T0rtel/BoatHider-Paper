package tortel.boatHider

import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import tortel.gamer.BoatHider.BoatListeners
import tortel.gamer.BoatHider.Commands.ForceInBoatCommand
import tortel.gamer.BoatHider.Commands.HidingBoatsCommand
import tortel.gamer.BoatHider.Commands.SpawnBoatCommand
import tortel.gamer.BoatHider.Commands.TogglePlayerCollisions
import tortel.gamer.BoatHider.IncompatibleVersionException
import tortel.gamer.BoatHider.PersistenceListeners
import tortel.gamer.BoatHider.nms.INMS
import tortel.gamer.BoatHider.nms.V1_21_4_R1.NMSV1_21_R4

class Main : JavaPlugin() {

    companion object {
        var instance : Plugin? = null
            private set

        var listeners: BoatListeners? = null
        var nms: INMS? = NMSV1_21_R4()

    }

    override fun onEnable() {
        if (Bukkit.getVersion().split("-")[0] != "1.21.4"){
            println("not the required version.")
            throw IncompatibleVersionException(Bukkit.getVersion())
        }
        instance = this
        // nms = getNMS()
        registerCommands()
        listeners = BoatListeners(this)
        Bukkit.getPluginManager().registerEvents(listeners!!, this)
        Bukkit.getPluginManager().registerEvents(PersistenceListeners(nms!!, this), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun registerCommands(){//
        val lamp: Lamp<BukkitCommandActor> = BukkitLamp.builder(instance!! as JavaPlugin)
            .build()

        lamp.register(HidingBoatsCommand())
        lamp.register(ForceInBoatCommand())
        lamp.register(SpawnBoatCommand())
        lamp.register(TogglePlayerCollisions())
//        getCommand("hideboats")?.setExecutor(HidingBoatsCommand())
//        getCommand("forceinboat")?.setExecutor(ForceInBoatCommand())
//        getCommand("spawnboat")?.setExecutor(SpawnBoatCommand())
//        getCommand("toggleplayercollisions")?.setExecutor(TogglePlayerCollisions())
    }

//    fun registerCommands() {
//        val commandManager = server.commandMap
//
//        commandManager.register("forceinboat", ForceInBoatCommand())
//        commandManager.register("hideboats", HidingBoatsCommand())
//        commandManager.register("spawnboat", SpawnBoatCommand())
//        commandManager.register("toggleplayercollisions", TogglePlayerCollisions())
//    }




}
