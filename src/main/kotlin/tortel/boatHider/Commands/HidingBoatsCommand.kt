package tortel.gamer.BoatHider.Commands


import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Suggest
import tortel.gamer.BoatHider.BoatListeners
import tortel.boatHider.Main

class HidingBoatsCommand {
    @Command("hideboats")
    fun onCommand(sender: Player, @Suggest("true", "false") hide : Boolean): Boolean {
        if (sender !is Player || !sender.isOp) return false

        if (hide == null) {
            sender.sendMessage("§cInvalid second argument. Use true or false.")
            return true
        }

        if (hide) {
            startRunnable(BoatListeners(Main.instance!!))
            sender.sendMessage("§aBoat hiding enabled.")
        } else {
            stopRunnable(BoatListeners(Main.instance!!))
            sender.sendMessage("§aBoat hiding disabled.")
        }


        return false
    }

    fun startRunnable(listeners : BoatListeners) {
        listeners.setHidingBoats(true)
    }

    fun stopRunnable(listeners : BoatListeners) {
        listeners.setHidingBoats(false)
    }



}