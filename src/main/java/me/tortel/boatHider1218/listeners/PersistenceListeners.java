package me.tortel.boatHider1218.listeners;

import me.tortel.boatHider1218.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PersistenceListeners implements Listener {

    private final NMSUtils nms;
    private final JavaPlugin plugin;

    public PersistenceListeners(JavaPlugin plugin) {
        this.nms = new NMSUtils();
        this.plugin = plugin;
    }

    @EventHandler
    public void worldLoadEvent(WorldLoadEvent event) {
        for (Entity entity : event.getWorld().getEntities()) {
            if (entity.getType().name().toLowerCase().contains("boat")) {
                replaceBoat((Boat) entity);
            }
        }
    }

    @EventHandler
    public void chunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.getType().name().toLowerCase().contains("boat")) {
                replaceBoat((Boat) entity);
            }
        }
    }

    @EventHandler
    public void boatSpawn(VehicleCreateEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Boat)) return;

        Boat boat = (Boat) vehicle;
        if (!nms.isCollisionless(boat)) {
            //System.out.println("a boat isn't collisionless :(");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!boat.isDead()) {
                        replaceBoat(boat);
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    private void replaceBoat(Boat boat) {
        Boat newBoat = nms.spawnBoat(boat, null);

        for (Entity passenger : boat.getPassengers()) {
            if (newBoat != null) {
                newBoat.addPassenger(passenger);
            }
        }

        boat.remove();
        Entity lingering = Bukkit.getEntity(boat.getUniqueId());
        if (lingering != null) lingering.remove();
    }
}