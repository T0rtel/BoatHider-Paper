package tortel.gamer.BoatHider

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import tortel.gamer.BoatHider.BoatListeners.BoatManager.hiding

class BoatListeners(private val plugin: Plugin) : Listener {
    //private var hook: NoxesiumUtilsHook? = null
    object BoatManager {
        var hiding: Boolean = false
    }
    /*
    init {
        try {
            Class.forName("me.superneon4ik.noxesiumutils.NoxesiumUtils")
            hook = NoxesiumUtilsHook(this)
            Bukkit.getPluginManager().registerEvents(hook, plugin)
        } catch (var3: ClassNotFoundException) {
            hook = null
        }
    }

     */



    @EventHandler
    fun worldChangedEvent(event: PlayerChangedWorldEvent) {
        showEveryoneToPlayer(event.player)
        if (hiding) {
            hideEntityToPlayer(event.player) // this.
        }
    }

    @EventHandler
    fun entityEnterBoat(event: VehicleEnterEvent) {
        if (hiding) {
            val vehicle = event.vehicle
            if (vehicle.type.name.contains("boat", true)) {
                val entered = event.entered
                val isPlayer = event.entered is Player
                val player = if (isPlayer) event.entered as Player else null

                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    if (entered != onlinePlayer && onlinePlayer.isInsideVehicle && onlinePlayer.vehicle !== event.vehicle){
                        hideEntity(onlinePlayer, vehicle)
                        hideEntity(onlinePlayer, entered)
                    }
                }
                /*
                val var6: Iterator<*> = Bukkit.getOnlinePlayers().iterator()
                while (var6.hasNext()) {
                    val player2 = var6.next() as Player
                    if (entered !== player2 && player2.isInsideVehicle && player2.vehicle !== event.vehicle) {
                        // if its not the player that entered(plr2) and plr2 is inside vehicle
                        // and his vehicle isnt the one that was entered
                        hideEntity(player2, vehicle)
                        hideEntity(player2, entered)
                    }
                }

                 */
                if (isPlayer) {
                    this.hideEntityToPlayer(player, vehicle)
                }
            }
        }
    }

    @EventHandler
    fun entityLeaveBoat(event: VehicleExitEvent) {
        if (hiding) {
            val vehicle = event.vehicle
            if (vehicle.type.name.contains("boat", true)) {
                val exited: Entity = event.exited
                val isPlayer = event.exited is Player
                val player = if (isPlayer) event.exited as Player else null

                for (onlinePlayer in Bukkit.getOnlinePlayers()) {

                    onlinePlayer.showEntity(plugin, exited)

                    if (event.vehicle.passengers.size == 1) {
                        onlinePlayer.showEntity(plugin, event.vehicle)
                    }
                }
                /*
                val var6: Iterator<*> = Bukkit.getOnlinePlayers().iterator()
                while (var6.hasNext()) {
                    val player2 = var6.next() as Player
                    player2.showEntity(plugin, exited)
                    if (event.vehicle.passengers.size == 1) {
                        player2.showEntity(plugin, event.vehicle)

                    }
                }

                 */
                if (isPlayer) {
                    showEveryoneToPlayer(player)
                }
            }
        }
    }

    @EventHandler
    fun vehicleDestroyEvent(event: VehicleDestroyEvent) {
        if (hiding) {
            if (event.vehicle is Boat) {
                val var2: Iterator<*> = event.vehicle.passengers.iterator()
                while (var2.hasNext()) {
                    val entity = var2.next() as Entity
                    if (entity is Player) {
                        showEveryoneToPlayer(entity)
                    }
                    val var6: Iterator<*> = Bukkit.getOnlinePlayers().iterator()
                    while (var6.hasNext()) {
                        val player = var6.next() as Player
                        player.showEntity(plugin, entity)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onjoin(event : PlayerJoinEvent){
        if (hiding){
            object : BukkitRunnable() {
                override fun run() {

                    if (!event.player.isInsideVehicle) return

                    if (event.player.vehicle?.type?.name?.contains("boat", true) == true){
                        hideEntityToPlayer(event.player, event.player.vehicle as Vehicle, 1L)

                        for (otherPlayer in Bukkit.getOnlinePlayers()) {
                            hideEntity(otherPlayer, event.player)
                        }
                    }

                }
            }.runTaskLater(plugin, 5L)

        }else{
            showEveryoneToPlayer(event.player)
        }
    }

    fun showEveryoneToPlayer(player: Player?) {
        //show everyone to a specific player
        val var2: Iterator<*> = Bukkit.getWorlds().iterator()
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            val entity = onlinePlayer as Entity
            player!!.showEntity(plugin, entity)
            val boat = entity.vehicle?: continue
            player.showEntity(plugin, boat)
        }
        /*
        while (var2.hasNext()) {
            val world = var2.next() as World
            val var4: Iterator<*> = world.entities.iterator()
            while (var4.hasNext()) {
                val entity = var4.next() as Entity
                player!!.showEntity(plugin, entity)
            }
        }

         */
    }

    @JvmOverloads
    fun hideEntityToPlayer(player: Player?, boat: Vehicle? = null as Vehicle?, delay: Long = 1L) {
        //hide a specific entity for a specified player
        object : BukkitRunnable() {
            override fun run() {
                if (player!!.isInsideVehicle) {
                    val var1: Iterator<*> = player.world.getEntitiesByClass(Boat::class.java).iterator() // get all boats
                    while (true) {
                        var entity: Entity
                        do {
                            do {
                                do {
                                    if (!var1.hasNext()) {
                                        return
                                    }
                                    entity = var1.next() as Entity
                                } while (entity.passengers.size == 0)
                            } while (entity.passengers.contains(player))
                        } while (entity === boat)
                        hideEntity(player, entity)
                        val var3: Iterator<*> = entity.passengers.iterator()
                        while (var3.hasNext()) {
                            val riding = var3.next() as Entity
                            hideEntity(player, riding)
                        }
                    }
                }
            }
        }.runTaskLater(plugin, delay)
    }
//
    fun hideEntity(target: Player, hidden: Entity) { // TODO: REPLACE THIS
       // if (hook == null || !hook!!.canShowSafely(target)) {

        //}
        target.hideEntity(plugin, hidden)
    }

    fun setHidingBoats(value: Boolean) {
        //this.hiding = value
        val onlinePlayersIterator: Iterator<*>
        var player: Player
        hiding = value
        if (value) {
            hiding = true
            onlinePlayersIterator = Bukkit.getOnlinePlayers().iterator()
            while (onlinePlayersIterator.hasNext()) {
                player = onlinePlayersIterator.next() as Player
                hideEntityToPlayer(player)
            }
        } else {
            hiding = false
            onlinePlayersIterator = Bukkit.getOnlinePlayers().iterator()
            while (onlinePlayersIterator.hasNext()) {
                player = onlinePlayersIterator.next() as Player
                showEveryoneToPlayer(player)
            }
        }
    }
}
