package tortel.gamer.BoatHider.nms.V1_21_R1


import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.function.Supplier

class CollisionlessBoat(
    entitytype: EntityType<out Boat?>, level: Level,
    dropItem: Supplier<Item>?
)
    : Boat(entitytype, level, null) {

//    override fun canCollideWithBukkit(entity: Entity): Boolean {
//        return false
//    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun canCollideWith(entity: Entity): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun push(entity: Entity) {
        return
    }

    override fun rideHeight(p0: EntityDimensions): Double {
        return (p0.height() / 3.0f).toDouble()
    }


    /*
    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun canCollideWith(var0: Entity?): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun push(entity : Entity){
        return;
    }

     */


}
