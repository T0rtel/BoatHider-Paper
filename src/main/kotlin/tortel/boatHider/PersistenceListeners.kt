package tortel.gamer.BoatHider

import org.bukkit.Bukkit
import tortel.gamer.BoatHider.nms.INMS
import org.bukkit.Chunk
import org.bukkit.craftbukkit.entity.CraftBoat
import org.bukkit.entity.Boat
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Vehicle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleCreateEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tortel.gamer.BoatHider.nms.V1_21_R1.CollisionlessBoat
import java.util.*
import java.util.function.Function

class PersistenceListeners(nms: INMS, plugin: JavaPlugin) : Listener {
    private val nms: INMS
    private val plugin: JavaPlugin

    init {
        this.nms = nms
        this.plugin = plugin
    }

    @EventHandler
    fun worldLoadEvent(event: WorldLoadEvent) {
        val var2: Array<Chunk> = event.getWorld().getLoadedChunks()
        val var3 = var2.size
        for (var4 in 0 until var3) {
            val chunk = var2[var4]
            val var5 = event.world.entities.iterator()
            /*
            val var6: Iterator<*> =
                Arrays.stream<Entity>(chunk.entities).filter { entity: Entity -> entity.type == EntityType.BOAT }
                    .map<Any?>(
                        Function<Entity, Any?> { entity: Entity? -> entity as Boat? }).toList().iterator()

             */
            while (var5.hasNext()) {
                val boat = var5.next()
                if (boat.type.name.contains("boat", true)){
                    replaceBoat(boat as Boat)
                }

            }
        }
    }

    @EventHandler
    fun chunkLoadEvent(event: ChunkLoadEvent) {
        /*
        val var2: Iterator<*> = Arrays.stream<Entity>(event.getChunk().getEntities())
            .filter { entity: Entity -> entity.type == EntityType.BOAT }
            .map<Any?>(
                Function<Entity, Any?> { entity: Entity? -> entity as Boat? }).toList().iterator()

         */
        val var22 = event.chunk.entities.iterator()
        while (var22.hasNext()) {
            val boat = var22.next()// as Boat
            if (boat.type.name.contains("boat", true)){ // boat.type == EntityType.BOAT
                replaceBoat(boat as Boat)
            }

        }
    }

    @EventHandler
    fun boatSpawn(event: VehicleCreateEvent) {
        val var3: Vehicle = event.getVehicle()
        if (var3 is Boat) {
            val boat: Boat = var3 as Boat
            if (!nms.isCollisionless(boat)) {
                println("a boat isnt collisionless :(")
                object : BukkitRunnable() {
                    override fun run() {
                        if (!boat.isDead()) {
                            replaceBoat(boat)
                        }
                    }
                }.runTaskLater(plugin, 1L)
                return
            }
        }
    }

    private fun replaceBoat(boat: Boat) {
        val newBoat : Boat? = nms.spawnBoat(boat, null)

        for (passenger in boat.passengers) {
            newBoat?.addPassenger(passenger)
        }

        boat.remove()
        Bukkit.getEntity(boat.uniqueId)?.remove()
        //println("old boat should be removed now.")
        //get everyone inside that boat if there is
//        val var3: Iterator<*> = boat.getPassengers().iterator()
//        while (var3.hasNext()) {
//            val passenger = var3.next() as Entity
//            if (newBoat != null) {
//                newBoat.addPassenger(passenger)
//            }
//        }


    }
}
