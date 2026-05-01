package me.tortel.boatHider1218.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tortel.boatHider1218.listeners.BoatListeners;
import me.tortel.boatHider1218.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

@CommandAlias("dev")
public class devCommands extends BaseCommand {

    private final NMSUtils nms = new NMSUtils();

    // /dev forceinboat <player>
    @Subcommand("forceinboat")
    @CommandCompletion("@players")
    public void forceinboat(CommandSender sender, @Optional String targetName) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        if (targetName == null) {
            sender.sendMessage("§cUsage: /dev forceinboat <player|@a>");
            return;
        }

        if (targetName.equals("@a")) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                forceIntoBoat(sender, target);
            }
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cPlayer not found or not online.");
            return;
        }

        forceIntoBoat(sender, target);
    }

    private void forceIntoBoat(CommandSender sender, Player target) {
        Location location = target.getLocation();
        location.setYaw(target.getEyeLocation().getYaw());

        Boat boat = nms.spawnBoat(null, location);
        if (boat != null) {
            boat.addPassenger(target);
            sender.sendMessage("§aSpawned a boat and forced " + target.getName() + " into it.");
        } else {
            sender.sendMessage("§cFailed to spawn the boat for " + target.getName() + ".");
        }
    }

    // /dev hideboats <true|false>
    @Subcommand("hideboats")
    @CommandCompletion("true|false")
    public void hideboats(CommandSender sender, boolean hide) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        BoatListeners.setHidingBoats(hide);

        sender.sendMessage(hide ? "§aBoat hiding enabled." : "§aBoat hiding disabled.");
    }

    // /dev spawnboat [world] [x] [y] [z]
    @Subcommand("spawnboat")
    @CommandCompletion("@worlds")
    public void spawnboat(CommandSender sender, @Optional String worldName,
                          @Optional Double x,
                          @Optional Double y,
                          @Optional Double z) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        Player player = (Player) sender;
        double finalX = x != null ? x : player.getLocation().getX();
        double finalY = y != null ? y : player.getLocation().getY();
        double finalZ = z != null ? z : player.getLocation().getZ();

        World world = Bukkit.getWorld(worldName != null ? worldName : player.getLocation().getWorld().getName());
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' not found.");
            return;
        }

        Location location = new Location(world, finalX, finalY, finalZ);
        nms.spawnBoat(null, location);
        sender.sendMessage("§aSpawned a boat at " + finalX + ", " + finalY + ", " + finalZ + " in world '" + world.getName() + "'.");
    }

    // /dev togglecollisions <true|false>
//    @Subcommand("togglecollisions")
//    @CommandCompletion("true|false")
//    public void togglecollisions(CommandSender sender, boolean value) {
//        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
//            sender.sendMessage("§cYou don't have permission to use this command.");
//            return;
//        }
//
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            player.setCollidable(value);
//        }
//
//        String status = value ? "enabled" : "disabled";
//        sender.sendMessage("§aPlayer collision has been " + status + " for all players.");
//    }
}