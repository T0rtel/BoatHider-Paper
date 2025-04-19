package tortel.gamer.BoatHider.Commands

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Suggest
import tortel.boatHider.Main
import tortel.gamer.BoatHider.nms.V1_21_R1.CollisionlessBoat

class SpawnBoatCommand {

    @Command("spawnboat")
    fun onCommand(
        sender: Player,
        @Suggest("<world>") @Optional worldName: String?,
        @Optional x: Double?,
        @Optional y: Double?,
        @Optional z: Double?
    ) {
        val finalx = x ?: sender.location.x
        val finaly = y ?: sender.location.y
        val finalz = z ?: sender.location.z
        // Ensure sender has the required permissions
        if (!sender.isOp) {
            sender.sendMessage("§cYou don't have permission to use this command.")
            return
        }

        val world = Bukkit.getWorld(worldName ?: sender.location.world.name)
        if (world == null) {
            sender.sendMessage("§cWorld '$worldName' not found.")
            return
        }

        val location = Location(world, finalx, finaly, finalz)
        Main.nms?.spawnBoat(null,location)
        sender.sendMessage("§aSpawned a boat at $finalx, $finalz, $finalz in world '${world.name}'.")
    }
}


//package tortel.gamer.BoatHider.Commands
//
//import org.bukkit.Bukkit
//import org.bukkit.Location
//import org.bukkit.command.Command
//import org.bukkit.command.CommandExecutor
//import org.bukkit.command.CommandSender
//import org.bukkit.entity.Player
//import tortel.boatHider.Main
//
//class SpawnBoatCommand : CommandExecutor {
//    override fun onCommand(sender: CommandSender, cmd: Command, string: String, args: Array<out String>): Boolean {
//        if (args == null || sender !is Player || !sender.isOp) return false
//        if (args.isEmpty()) {
//            sender.sendMessage("§cUsage: spawnboat <world> <x> <y> <z>")
//            return true
//        }
//
//        val world = sender.world
//
//        val x = args[1].toDoubleOrNull()
//        val y = args[2].toDoubleOrNull()
//        val z = args[3].toDoubleOrNull()
//        if (x == null || y == null || z == null) {
//            sender.sendMessage("§cCoordinates must be valid numbers.")
//            return true
//        }
//
//        val location = Location(world, x, y, z)
//        Main.nms?.spawnBoat(location)
//        sender.sendMessage("§aSpawned a boat at $x, $y, $z in world ${world.name}.")
//        return true
//    }
//}