package tortel.gamer.BoatHider.nms

import org.bukkit.Location
import org.bukkit.entity.Boat
import tortel.gamer.BoatHider.nms.V1_21_R1.CollisionlessBoat

interface INMS {
    fun spawnBoat(var1: Location?): Boat?
    fun isCollisionless(var1: Boat?): Boolean
}
