package me.tortel.boatHider1218.nms;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CollisionlessBoat extends Boat {

    public CollisionlessBoat(EntityType<? extends Boat> entitytype, Level level, Supplier<Item> dropItem) {
        super(entitytype, level, dropItem);
    }


    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        // no-op
    }

    @Override
    public double rideHeight(EntityDimensions dimensions) {
        return (double) (dimensions.height() / 3.0f);
    }

}
