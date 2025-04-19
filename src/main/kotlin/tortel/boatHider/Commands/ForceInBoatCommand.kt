package tortel.gamer.BoatHider.Commands

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Suggest
import tortel.boatHider.Main

class ForceInBoatCommand {

    @Command("forceinboat")
    fun onCommand(
        sender: Player,
        targetPlayerName: Player
    ) {
        // Ensure the sender has permissions
        if (!sender.isOp) {
            sender.sendMessage("§cYou don't have permission to use this command.")
            return
        }

        // Get the target player
        val targetPlayer = targetPlayerName//Bukkit.getPlayer(targetPlayerName)
        if (targetPlayer == null || !targetPlayer.isOnline) {
            sender.sendMessage("§cPlayer not found or not online.")
            return
        }

        // Use target player's location and adjust yaw
        val location = targetPlayer.location.apply {
            yaw = targetPlayer.eyeLocation.yaw
        }

        // Spawn the boat and add the player as a passenger
        val boat = Main.nms?.spawnBoat(null, location)
        if (boat != null) {
            boat.addPassenger(targetPlayer)
            sender.sendMessage("§aSpawned a boat and forced ${targetPlayer.name} into it.")
        } else {
            sender.sendMessage("§cFailed to spawn the boat.")
        }
    }
}

//import org.bukkit.Bukkit
////import org.bukkit.command.Command
////import org.bukkit.command.CommandExecutor
////import org.bukkit.command.CommandSender
//import org.bukkit.entity.Player
//import tortel.boatHider.Main

//class ForceInBoatCommand : CommandExecutor {
//    override fun onCommand(sender: CommandSender, cmd: Command, string: String, args: Array<out String>): Boolean {
//        if (args == null || sender !is Player || !sender.isOp) return false
//        if (args.isEmpty()) {
//            sender.sendMessage("§cUsage: /mcm.BoatHider hidingboats <true|false>")
//            return true
//        }
//
//        val player = Bukkit.getPlayer(args[0])
//        if (player == null || !player.isOnline) {
//            sender.sendMessage("§cPlayer not found or not online.")
//            return true
//        }
//
//        val location = player.location.apply {
//            yaw = player.eyeLocation.yaw
//        }
//        Main.nms?.spawnBoat(location)?.addPassenger(player)
//        sender.sendMessage("§aSpawned a boat and forced ${player.name} into it.")
//
//        return true
//    }
//
//}