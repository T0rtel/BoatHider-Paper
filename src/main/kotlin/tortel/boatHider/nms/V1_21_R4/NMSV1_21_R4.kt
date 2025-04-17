package tortel.gamer.BoatHider.nms.V1_21_4_R1

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftBoat
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import tortel.gamer.BoatHider.nms.INMS
import tortel.gamer.BoatHider.nms.V1_21_R1.CollisionlessBoat

class NMSV1_21_R4 : INMS {
    override fun spawnBoat(location: Location?): Boat? {

        val level = (location?.world as CraftWorld?)!!.getHandle()
        val boat = CollisionlessBoat(
            entitytype = net.minecraft.world.entity.EntityType<out net.minecraft.world.entity.vehicle.Boat>.OAK_BOAT, level, null)
        val yaw = Location.normalizeYaw(location.yaw)
        boat.setRot(yaw, 0.0f)
        boat.setPos(location.x, location.y, location.z)

        level.addFreshEntity(boat)
        //boat.setBoatType(EntityBoat.EnumBoatType.a)


        println("spawning a new collisionless boat")

        //return boat.bukkitEntity as Boat
        return level
            .getWorld()
            .getEntity(boat.uuid) as? Boat
    }

    override fun isCollisionless(boat: Boat?): Boolean {
        return (boat as CraftBoat).getHandle() is CollisionlessBoat
    }
}
