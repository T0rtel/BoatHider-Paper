package me.tortel.boatHider1218.listeners;

import me.tortel.boatHider1218.BoatHiderMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BoatListeners implements Listener {

    public static boolean hiding = false;

    @EventHandler
    public void worldChangedEvent(PlayerChangedWorldEvent event) {
        showEveryoneToPlayer(event.getPlayer());
        if (hiding) {
            hideEntityToPlayer(event.getPlayer(), null, 1L);
        }
    }

    @EventHandler
    public void entityEnterBoat(VehicleEnterEvent event) {
        if (!hiding) return;

        Vehicle vehicle = event.getVehicle();
        if (!vehicle.getType().name().toLowerCase().contains("boat")) return;

        Entity entered = event.getEntered();
        boolean isPlayer = entered instanceof Player;
        Player player = isPlayer ? (Player) entered : null;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (entered != onlinePlayer && onlinePlayer.isInsideVehicle()
                    && onlinePlayer.getVehicle() != vehicle) {
                hideEntity(onlinePlayer, vehicle);
                hideEntity(onlinePlayer, entered);
            }
        }

        if (isPlayer) {
            hideEntityToPlayer(player, vehicle, 1L);
        }
    }

    @EventHandler
    public void entityLeaveBoat(VehicleExitEvent event) {
        if (!hiding) return;

        Vehicle vehicle = event.getVehicle();
        if (!vehicle.getType().name().toLowerCase().contains("boat")) return;

        Entity exited = event.getExited();
        boolean isPlayer = exited instanceof Player;
        Player player = isPlayer ? (Player) exited : null;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showEntity(BoatHiderMain.getInstance(), exited);
            if (event.getVehicle().getPassengers().size() == 1) {
                onlinePlayer.showEntity(BoatHiderMain.getInstance(), vehicle);
            }
        }

        if (isPlayer) {
            showEveryoneToPlayer(player);
        }
    }

    @EventHandler
    public void vehicleDestroyEvent(VehicleDestroyEvent event) {
        if (!hiding) return;
        if (!(event.getVehicle() instanceof Boat)) return;

        for (Entity entity : event.getVehicle().getPassengers()) {
            if (entity instanceof Player) {
                showEveryoneToPlayer((Player) entity);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showEntity(BoatHiderMain.getInstance(), entity);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (hiding) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.getPlayer();
                    if (!player.isInsideVehicle()) return;

                    Vehicle vehicle = (Vehicle) player.getVehicle();
                    if (vehicle != null && vehicle.getType().name().toLowerCase().contains("boat")) {
                        hideEntityToPlayer(player, vehicle, 1L);
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            hideEntity(other, player);
                        }
                    }
                }
            }.runTaskLater(BoatHiderMain.getInstance(), 5L);
        } else {
            showEveryoneToPlayer(event.getPlayer());
        }
    }

    public static void showEveryoneToPlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            player.showEntity(BoatHiderMain.getInstance(), onlinePlayer);
            Entity boat = onlinePlayer.getVehicle();
            if (boat != null) {
                player.showEntity(BoatHiderMain.getInstance(), boat);
            }
        }
    }

    public static void hideEntityToPlayer(Player player, Vehicle boat, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isInsideVehicle()) return;

                for (Entity entity : player.getWorld().getEntitiesByClass(Boat.class)) {
                    if (entity.getPassengers().isEmpty()) continue;
                    if (entity.getPassengers().contains(player)) continue;
                    if (entity == boat) continue;

                    hideEntity(player, entity);
                    for (Entity riding : entity.getPassengers()) {
                        hideEntity(player, riding);
                    }
                }
            }
        }.runTaskLater(BoatHiderMain.getInstance(), delay);
    }

    public static void hideEntity(Player target, Entity hidden) {
        target.hideEntity(BoatHiderMain.getInstance(), hidden);
    }

    public static void setHidingBoats(boolean value) {
        hiding = value;
        if (value) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hideEntityToPlayer(player, null, 1L);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                showEveryoneToPlayer(player);
            }
        }
    }
}