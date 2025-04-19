package tortel.gamer.BoatHider.nms.V1_21_4_R1


import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftBoat
import org.bukkit.entity.Boat
import org.bukkit.entity.Entity
import tortel.gamer.BoatHider.nms.INMS
import tortel.gamer.BoatHider.nms.V1_21_R1.CollisionlessBoat

class NMSV1_21_R4 : INMS {
    override fun spawnBoat(oldboat: Boat?, locationn: Location?): Boat? {
        lateinit var location : Location
        lateinit var boat : CollisionlessBoat
        lateinit var level : ServerLevel
        if (oldboat != null && locationn == null){
            location = oldboat.location
            level = (location.world as CraftWorld?)!!.getHandle()
            boat = CollisionlessBoat(
                //entitytype = oldboat.type as EntityType<out net.minecraft.world.entity.vehicle.Boat>,//EntityType<out net.minecraft.world.entity.vehicle.Boat>.OAK_BOAT,
                entitytype = bukkitToNMSEntityType(oldboat.type) as EntityType<out net.minecraft.world.entity.vehicle.Boat>,
                level,
                dropItem = { net.minecraft.world.item.Items.JUNGLE_BOAT })
            val yaw = Location.normalizeYaw(location.yaw)
            boat.setRot(yaw, 0.0f)
            boat.setPos(location.x, location.y, location.z)
        }else if (locationn != null && oldboat == null){
            location = locationn
            level = (location.world as CraftWorld?)!!.getHandle()
            boat = CollisionlessBoat(
                entitytype = EntityType<out net.minecraft.world.entity.vehicle.Boat>.ACACIA_BOAT,//EntityType<out net.minecraft.world.entity.vehicle.Boat>.OAK_BOAT,
                level,
                dropItem = { net.minecraft.world.item.Items.JUNGLE_BOAT })

        }

        val yaw = Location.normalizeYaw(location.yaw)
        boat.setRot(yaw, 0.0f)
        boat.setPos(location.x, location.y, location.z)

        level.addFreshEntity(boat)
        println("spawning a new collisionless boat")

        return level
            .getWorld()
            .getEntity(boat.uuid) as? Boat
    }

    override fun isCollisionless(boat: Boat?): Boolean {
        return (boat as CraftBoat).getHandle() is CollisionlessBoat
    }
}

fun bukkitToNMSEntityType(bukkitType: org.bukkit.entity.EntityType): EntityType<out net.minecraft.world.entity.vehicle.Boat>? {
    return when (bukkitType) {
        org.bukkit.entity.EntityType.OAK_BOAT -> EntityType.OAK_BOAT
        org.bukkit.entity.EntityType.SPRUCE_BOAT -> EntityType.SPRUCE_BOAT
        org.bukkit.entity.EntityType.BIRCH_BOAT -> EntityType.BIRCH_BOAT
        org.bukkit.entity.EntityType.JUNGLE_BOAT -> EntityType.JUNGLE_BOAT
        org.bukkit.entity.EntityType.ACACIA_BOAT -> EntityType.ACACIA_BOAT
        org.bukkit.entity.EntityType.DARK_OAK_BOAT -> EntityType.DARK_OAK_BOAT
        org.bukkit.entity.EntityType.MANGROVE_BOAT -> EntityType.MANGROVE_BOAT
        else -> null // Add additional mappings as necessary, or return null for unsupported types
    }
}

