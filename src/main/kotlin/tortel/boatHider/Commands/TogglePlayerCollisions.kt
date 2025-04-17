package tortel.gamer.BoatHider.Commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Suggest

class TogglePlayerCollisions {

    @Command("toggleplayercollisions")
    fun onCommand(
        sender: Player,
        @Suggest("true", "false") value: Boolean?
    ) {
        // Ensure sender has the required permissions
        if (!sender.isOp) {
            sender.sendMessage("§cYou don't have permission to use this command.")
            return
        }

        if (value == null) {
            sender.sendMessage("§cUsage: /toggleplayercollisions <true|false>")
            return
        }

        // Toggle player collisions based on the given value
        Bukkit.getOnlinePlayers().forEach { player ->
            player.isCollidable = value
        }

        val status = if (value) "disabled" else "enabled"
        sender.sendMessage("§aPlayer collision has been $status for all players.")
    }
}
//package tortel.gamer.BoatHider.Commands
//
//import org.bukkit.Bukkit
//import org.bukkit.command.Command
//import org.bukkit.command.CommandExecutor
//import org.bukkit.command.CommandSender
//
//class TogglePlayerCollisions : CommandExecutor {
//    override fun onCommand(sender: CommandSender, cmd: Command, str: String, args: Array<out String>): Boolean {
//        if (!sender.isOp || args.isEmpty()) return false
//
//        val value = args[0].toBooleanStrictOrNull()
//        if (value == null){
//
//            sender.sendMessage("Usage: /TogglePlayerCollisions true/false")
//            return false
//        }
//
//        if (value == true){
//            Bukkit.getOnlinePlayers().forEach {
//                it.isCollidable = false
//            }
//        }else{
//            Bukkit.getOnlinePlayers().forEach {
//                it.isCollidable = true
//            }
//        }
//
//        return true
//    }
//
//}