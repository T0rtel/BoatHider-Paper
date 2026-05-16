package me.tortel.boatHider1218.commands;

import me.tortel.boatHider1218.listeners.BoatListeners;
import me.tortel.boatHider1218.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class devCommands implements CommandExecutor, TabCompleter {

    private final NMSUtils nms = new NMSUtils();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /dev <subcommand> [args]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "forceinboat":
                return handleForceInBoat(sender, args);
            case "hideboats":
                return handleHideBoats(sender, args);
            case "spawnboat":
                return handleSpawnBoat(sender, args);
            case "togglecollisions":
                return handleToggleCollisions(sender, args);
            default:
                sender.sendMessage("§cUnknown subcommand: " + subcommand);
                return true;
        }
    }

    private boolean handleForceInBoat(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /dev forceinboat <player|@a>");
            return true;
        }

        String targetName = args[1];

        if (targetName.equals("@a")) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                forceIntoBoat(sender, target);
            }
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cPlayer not found or not online.");
            return true;
        }

        forceIntoBoat(sender, target);
        return true;
    }

    private boolean handleHideBoats(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /dev hideboats <true|false>");
            return true;
        }

        boolean hide = Boolean.parseBoolean(args[1]);
        BoatListeners.setHidingBoats(hide);

        sender.sendMessage(hide ? "§aBoat hiding enabled." : "§aBoat hiding disabled.");
        return true;
    }

    private boolean handleSpawnBoat(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Parse arguments
        String worldName = null;
        double x, y, z;

        if (args.length >= 2) {
            worldName = args[1];
        }

        if (args.length >= 4) {
            try {
                x = Double.parseDouble(args[2]);
                y = Double.parseDouble(args[3]);
                z = args.length >= 5 ? Double.parseDouble(args[4]) : player.getLocation().getZ();
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid coordinates. Use numbers for x, y, z.");
                return true;
            }
        } else {
            x = player.getLocation().getX();
            y = player.getLocation().getY();
            z = player.getLocation().getZ();
        }

        World world = Bukkit.getWorld(worldName != null ? worldName : player.getWorld().getName());
        if (world == null) {
            sender.sendMessage("§cWorld '" + worldName + "' not found.");
            return true;
        }

        Location location = new Location(world, x, y, z);
        nms.spawnBoat(null, location);
        sender.sendMessage("§aSpawned a boat at " + x + ", " + y + ", " + z + " in world '" + world.getName() + "'.");
        return true;
    }

    private boolean handleToggleCollisions(CommandSender sender, String[] args) {
        if (!(sender instanceof Player) || !((Player) sender).isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /dev togglecollisions <true|false>");
            return true;
        }

        boolean value = Boolean.parseBoolean(args[1]);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setCollidable(value);
        }

        String status = value ? "enabled" : "disabled";
        sender.sendMessage("§aPlayer collision has been " + status + " for all players.");
        return true;
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

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Subcommand completion
            completions.add("forceinboat");
            completions.add("hideboats");
            completions.add("spawnboat");
            completions.add("togglecollisions");
            return filterCompletions(completions, args[0]);
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "forceinboat":
                    // Player names
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                    completions.add("@a");
                    return filterCompletions(completions, args[1]);

                case "hideboats":
                    completions.add("true");
                    completions.add("false");
                    return filterCompletions(completions, args[1]);

                case "spawnboat":
                    // World names
                    for (World world : Bukkit.getWorlds()) {
                        completions.add(world.getName());
                    }
                    return filterCompletions(completions, args[1]);

                case "togglecollisions":
                    completions.add("true");
                    completions.add("false");
                    return filterCompletions(completions, args[1]);
            }
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("spawnboat")) {
            // X, Y, Z coordinates - just show player's current position as suggestion
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location loc = player.getLocation();

                if (args.length == 3) {
                    completions.add(String.valueOf((int) loc.getX()));
                } else if (args.length == 4) {
                    completions.add(String.valueOf((int) loc.getY()));
                } else if (args.length == 5) {
                    completions.add(String.valueOf((int) loc.getZ()));
                }
            }
        }

        return completions;
    }

    private List<String> filterCompletions(List<String> completions, String input) {
        List<String> filtered = new ArrayList<>();
        String lower = input.toLowerCase();

        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(lower)) {
                filtered.add(completion);
            }
        }

        return filtered;
    }
}