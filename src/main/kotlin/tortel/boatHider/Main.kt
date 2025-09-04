package tortel.boatHider

import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Matrix4f
import org.joml.Vector3f
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import tortel.gamer.BoatHider.BoatListeners
import tortel.gamer.BoatHider.Commands.ForceInBoatCommand
import tortel.gamer.BoatHider.Commands.HidingBoatsCommand
import tortel.gamer.BoatHider.Commands.SpawnBoatCommand
import tortel.gamer.BoatHider.IncompatibleVersionException
import tortel.gamer.BoatHider.PersistenceListeners
import tortel.gamer.BoatHider.nms.INMS
import tortel.gamer.BoatHider.nms.V1_21_4_R1.NMSV1_21_R4
import java.lang.Math.*
import kotlin.contracts.contract

class Main : JavaPlugin() {

    companion object {
        var instance : Plugin? = null
            private set

        var listeners: BoatListeners? = null
        var nms: INMS? = NMSV1_21_R4()
        lateinit var hitboxEntity : Interaction
        lateinit var bhitboxEntity : Interaction
        lateinit var DisplayEntity : ItemDisplay
    }
    //TODO: fix /hideboats false doesnt show boats again,
    // and make the boats types configurable
    override fun onEnable() {
        if (Bukkit.getVersion().split("-")[0] != "1.21.4"){
            println("not the required version.")
            throw IncompatibleVersionException(Bukkit.getVersion())
        }
        instance = this
        // nms = getNMS()
        registerCommands()
        listeners = BoatListeners(this)
        Bukkit.getPluginManager().registerEvents(listeners!!, this)
        Bukkit.getPluginManager().registerEvents(PersistenceListeners(nms!!, this), this)

        test().runTaskTimer(instance!!, 0, 1) // every 10 seconds

        hitboxEntity = Bukkit.getWorld("world")!!.spawnEntity(Location(Bukkit.getWorld("world"), 0.0,0.0,0.0), EntityType.INTERACTION) as Interaction

        hitboxEntity.isGlowing = true

        hitboxEntity.interactionWidth = 1.5f
        hitboxEntity.interactionHeight = 1.5f

        bhitboxEntity = Bukkit.getWorld("world")!!.spawnEntity(Location(Bukkit.getWorld("world"), 0.0,0.0,0.0), EntityType.INTERACTION) as Interaction

        bhitboxEntity.isGlowing = true

        bhitboxEntity.interactionWidth = 1.5f
        bhitboxEntity.interactionHeight = 1.5f

        DisplayEntity = Bukkit.getWorld("world")!!.spawnEntity(Location(Bukkit.getWorld("world"), 0.0,0.0,0.0), EntityType.ITEM_DISPLAY) as ItemDisplay
        DisplayEntity.isGlowing = true
        val itemStack = ItemStack(Material.DIAMOND)
        val meta = itemStack.itemMeta
        meta.setCustomModelData(11003)
        itemStack.setItemMeta(meta)
        DisplayEntity.setItemStack(itemStack)

        DisplayEntity.setTransformation(
            Transformation(
                Vector3f(0.0f, 0.0f, 0.0f),
                AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f),
                Vector3f(1.0f, 1.0f, 1.0f),
                AxisAngle4f(0.0f, 0.0f, 0.0f, 1.0f),
            )
        )
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun registerCommands(){//
        val lamp: Lamp<BukkitCommandActor> = BukkitLamp.builder(instance!! as JavaPlugin)
            .build()

        lamp.register(HidingBoatsCommand())
        lamp.register(ForceInBoatCommand())
        lamp.register(SpawnBoatCommand())
        //lamp.register(TogglePlayerCollisions())
//        getCommand("hideboats")?.setExecutor(HidingBoatsCommand())
//        getCommand("forceinboat")?.setExecutor(ForceInBoatCommand())
//        getCommand("spawnboat")?.setExecutor(SpawnBoatCommand())
//        getCommand("toggleplayercollisions")?.setExecutor(TogglePlayerCollisions())
    }


//    fun registerCommands() {
//        val commandManager = server.commandMap
//
//        commandManager.register("forceinboat", ForceInBoatCommand())
//        commandManager.register("hideboats", HidingBoatsCommand())
//        commandManager.register("spawnboat", SpawnBoatCommand())
//        commandManager.register("toggleplayercollisions", TogglePlayerCollisions())
//    }


    fun getBlocksInEntityHitbox(entity : Entity) {
        val blocks: List<Block> = ArrayList();
        val boundingBox: org.bukkit.util.BoundingBox = entity.getBoundingBox(); // Get the entity's bounding box
        val world: World = entity.getWorld();

        // Iterate through all block positions within the bounding box
        val minX: Int = floor(boundingBox.getMinX()).toInt();
        val maxX: Int = ceil(boundingBox.getMaxX()).toInt();
        val minY: Int = floor(boundingBox.getMinY()).toInt();
        val maxY: Int = ceil(boundingBox.getMaxY()).toInt();
        val minZ: Int = floor(boundingBox.getMinZ()).toInt();
        val maxZ: Int = ceil(boundingBox.getMaxZ()).toInt();

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val block = world.getBlockAt(x, y, z)
                    if (block.type != Material.AIR) { // Ignore air blocks
                        blocks.plus(block)
                    }
                }
            }
        }
    }

    class test() : BukkitRunnable() {

        private var angle = 0f

        override fun run() {
            angle += 5f // Increase rotation angle
            if (angle >= 360f) angle = 0f

            Bukkit.getOnlinePlayers().forEach { player ->
                if (player.gameMode == GameMode.SPECTATOR) return
                val result = player.world.rayTraceBlocks(player.eyeLocation, player.eyeLocation.direction, 3.0,
                    FluidCollisionMode.NEVER, true) ?: return
                val block = result.hitBlock?: return
                val face : BlockFace? = result.hitBlockFace
                var pos: Location = result.hitPosition.toLocation(player.world)

                //do block checks
                if (!block.type.isOccluding) return

                if (block.blockData is Slab || block.blockData is Stairs) return

                if (!block.isSolid) return


                //Bukkit.broadcastMessage("$rotationyaw")

                val direction = if (face != null) {
                    // Use the opposite direction of the hit face to move towards player
                    Vector(-face.modX.toDouble(), -face.modY.toDouble(), -face.modZ.toDouble())
                } else {
                    // Fallback: use reverse of player's look direction
                    player.eyeLocation.direction.normalize().multiply(-1.0)
                }

                val offset = when(face){
                    BlockFace.UP -> Vector(0.0, 0.6, 0.0)
                    BlockFace.DOWN -> Vector(0.0, -0.6, 0.0)

                    BlockFace.NORTH -> Vector(0.0, 0.1, 0.0)
                    BlockFace.SOUTH -> Vector(0.0, 0.1, 0.0)//.add(direction.multiply(0.55))//0.0,0.1,-0.6
                    BlockFace.EAST -> Vector(0.0, 0.1, 0.0)//.add(direction.multiply(0.55))
                    BlockFace.WEST -> Vector(0.0, 0.1, 0.0)//.add(direction.multiply(0.55))

                    else -> Vector(0.0, 0.0, 0.0)
                }.add(direction.multiply(0.55))

                val hitboxoffset = when(face){
                    BlockFace.UP -> Vector(0.0, 0.6, 0.0)
                    BlockFace.DOWN -> Vector(0.0, -0.6, 0.0)

                    BlockFace.NORTH -> Vector(0.0, 0.5, 0.0)
                    BlockFace.SOUTH -> Vector(0.0, 0.5, 0.0)//Vector(0.0, 0.5, -0.3)
                    BlockFace.EAST -> Vector(0.0, 0.5, 0.0)
                    BlockFace.WEST -> Vector(0.0, 0.5, 0.0)

                    else -> Vector(0.0, 0.0, 0.0)
                }.add(direction.multiply(0.55))

                val bhitboxoffset = when(face){
                    BlockFace.UP -> Vector(0.0, 0.6, 0.0)
                    BlockFace.DOWN -> Vector(0.0, -0.6, 0.0)

                    BlockFace.NORTH -> Vector(0.0, 0.2, 0.0)
                    BlockFace.SOUTH -> Vector(0.0, 0.2, 0.0)//Vector(0.0, 0.5, -0.3)
                    BlockFace.EAST -> Vector(0.0, 0.2, 0.0)
                    BlockFace.WEST -> Vector(0.0, 0.2, 0.0)//0.4

                    else -> Vector(0.0, 0.0, 0.0)
                }.add(direction.multiply(-4.5))

                val yrotation = when(face){
                    BlockFace.UP -> 0f
                    BlockFace.DOWN -> 0f

                    BlockFace.NORTH -> 180f
                    BlockFace.SOUTH -> 0f
                    BlockFace.EAST -> 90f
                    BlockFace.WEST -> -90f

                    else -> 0f
                }

                pos = pos.subtract(offset)

                //val offset = Vector(0.0, 0.1, -0.6)
                DisplayEntity.teleport(pos) // // //.subtract(direction.normalize().multiply(0.5))
               // val hitboxoffset = Vector(0.0, 0.0, 0.0)//Vector(0.0, 0.5, -0.3) <- south only
                hitboxEntity.teleport(DisplayEntity.location.subtract(hitboxoffset))
                bhitboxEntity.teleport(DisplayEntity.location.subtract(bhitboxoffset))


                rotateEntity(DisplayEntity, yrotation)

                val boundingBox = hitboxEntity.boundingBox

                for (x in boundingBox.maxX.toInt() ..boundingBox.minX.toInt()){
                    for (y in boundingBox.maxY.toInt()..boundingBox.minY.toInt()){
                        for (z in boundingBox.maxZ.toInt()..boundingBox.minZ.toInt()){
                            val b = player.world.getBlockAt(x,y,z)
                            Bukkit.broadcastMessage("$x $y $z")
                            player.world.spawnParticle(
                                Particle.DUST,
                                b.location.add(0.5,0.5,0.5),
                                20,
                                0.1, 0.1, 0.1, 0.0,
                                Particle.DustOptions(Color.GREEN, 2.0f)
                            )
                        }
                    }
                }

//                val bboundingBox = bhitboxEntity.boundingBox
//
//                for (x in bboundingBox.maxX.toInt() ..bboundingBox.minX.toInt()){
//                    for (y in bboundingBox.maxY.toInt()..bboundingBox.minY.toInt()){
//                        for (z in bboundingBox.maxZ.toInt()..bboundingBox.minZ.toInt()){
//                            val b = player.world.getBlockAt(x,y,z)
//                            player.world.spawnParticle(
//                                Particle.DUST,
//                                b.location.add(0.5,0.5,0.5),
//                                20,
//                                0.1, 0.1, 0.1, 0.0,
//                                Particle.DustOptions(Color.GREEN, 2.0f)
//                            )
//                        }
//                    }
//                }


//                player.world.spawnParticle(
//                    Particle.DUST,
//                    block.location.add(0.5, 0.5, 0.5),
//                    20,
//                    0.1, 0.1, 0.1, 0.0,
//                    Particle.DustOptions(Color.GREEN, 0.2f)
//                )
            }
        }
    }




}

private fun Main.test.rotateEntity(entity : ItemDisplay, degree : Float) {
        entity.setTransformationMatrix(Matrix4f().rotateY((toRadians(degree.toDouble()).toFloat())))// + 0.1f /* prevent the client from interpolating in reverse */
//    entity.transformation = Transformation(
//        entity.transformation.translation,
//        AxisAngle4f(-Math.toRadians(degree.toDouble()).toFloat(), 1f, 0f, 0f),
//        entity.transformation.scale,
//        AxisAngle4f(Math.toRadians(degree.toDouble()).toFloat(), 0f, 0f, 1f),
//    )
}
