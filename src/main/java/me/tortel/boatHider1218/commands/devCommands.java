package me.tortel.boatHider1218.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
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
    public void forceinboat(CommandSender sender, Player target) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        if (!target.isOnline()) {
            sender.sendMessage("§cPlayer not found or not online.");
            return;
        }

        Location location = target.getLocation();
        location.setYaw(target.getEyeLocation().getYaw());

        Boat boat = nms.spawnBoat(null, location);
        if (boat != null) {
            boat.addPassenger(target);
            sender.sendMessage("§aSpawned a boat and forced " + target.getName() + " into it.");
        } else {
            sender.sendMessage("§cFailed to spawn the boat.");
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
    public void spawnboat(CommandSender sender, @co.aikar.commands.annotation.Optional String worldName,
                          @co.aikar.commands.annotation.Optional Double x,
                          @co.aikar.commands.annotation.Optional Double y,
                          @co.aikar.commands.annotation.Optional Double z) {
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
    @Subcommand("togglecollisions")
    @CommandCompletion("true|false")
    public void togglecollisions(CommandSender sender, boolean value) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setCollidable(value);
        }

        String status = value ? "enabled" : "disabled";
        sender.sendMessage("§aPlayer collision has been " + status + " for all players.");
    }
}