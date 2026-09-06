package me.tortel.boatHider1218.nms;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBoat;
import net.minecraft.core.registries.BuiltInRegistries;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class NMSUtils {

    public org.bukkit.entity.Boat spawnBoat(org.bukkit.entity.Boat oldBoat, Location locationn) {
        Location location;
        CollisionlessBoat boat;
        ServerLevel level;

        if (oldBoat != null && locationn == null) {
            location = oldBoat.getLocation();
            level = ((CraftWorld) location.getWorld()).getHandle();
            boat = new CollisionlessBoat(
                    (EntityType<? extends Boat>) bukkitToNMSEntityType(oldBoat.getType()),
                    level,
                    () -> Items.JUNGLE_BOAT
            );
        } else if (locationn != null && oldBoat == null) {
            location = locationn;
            level = ((CraftWorld) location.getWorld()).getHandle();
            boat = new CollisionlessBoat(
                    bukkitToNMSEntityType(org.bukkit.entity.EntityType.ACACIA_BOAT),
                    level,
                    () -> Items.JUNGLE_BOAT
            );
        } else {
            return null;
        }

        float yaw = Location.normalizeYaw(location.getYaw());
        boat.setRot(yaw, 0.0f);
        boat.setPos(location.getX(), location.getY(), location.getZ());

        level.addFreshEntity(boat);
        System.out.println("spawning a new collisionless boat");

        org.bukkit.entity.Entity entity = level.getWorld().getEntity(boat.getUUID());
        assert entity != null;
        entity.setPersistent(true);
        return entity instanceof org.bukkit.entity.Boat ? (org.bukkit.entity.Boat) entity : null;
    }


    public boolean isCollisionless(org.bukkit.entity.Boat boat) {
        return ((CraftBoat) boat).getHandle() instanceof CollisionlessBoat;
    }

    public static EntityType<? extends Boat> bukkitToNMSEntityType(org.bukkit.entity.EntityType bukkitType) {
        Identifier key = CraftNamespacedKey.toMinecraft(bukkitType.getKey());
        @SuppressWarnings("unchecked")
        EntityType<? extends Boat> nmsType =
                (EntityType<? extends Boat>) BuiltInRegistries.ENTITY_TYPE.getValue(key);
        return nmsType;
    }

//    public static EntityType<? extends Boat> bukkitToNMSEntityType(org.bukkit.entity.EntityType bukkitType) {
//        switch (bukkitType) {
//            case OAK_BOAT:       return EntityType.OAK_BOAT;
//            case SPRUCE_BOAT:    return EntityType.SPRUCE_BOAT;
//            case BIRCH_BOAT:     return EntityType.BIRCH_BOAT;
//            case JUNGLE_BOAT:    return EntityType.JUNGLE_BOAT;
//            case ACACIA_BOAT:    return EntityType.ACACIA_BOAT;
//            case DARK_OAK_BOAT:  return EntityType.DARK_OAK_BOAT;
//            case MANGROVE_BOAT:  return EntityType.MANGROVE_BOAT;
//            default:             return null;
//        }
//    }

}
