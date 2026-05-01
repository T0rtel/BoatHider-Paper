package me.tortel.boatHider1218.api;

import me.tortel.boatHider1218.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

public class BoatHiderAPI {

    private final NMSUtils nms;

    public BoatHiderAPI() {
        this.nms = new NMSUtils();
    }

    public void forceIntoBoat(Player target) {
        Location location = target.getLocation();
        location.setYaw(target.getEyeLocation().getYaw());
        Boat boat = nms.spawnBoat(null, location);
        if (boat != null) {
            boat.addPassenger(target);
        }
    }

    public void forceAllIntoBoat() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            forceIntoBoat(p);
        }
    }

}
