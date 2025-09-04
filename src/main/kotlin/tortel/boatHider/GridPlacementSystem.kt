package tortel.boatHider

import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import org.joml.Matrix4f
import tortel.boatHider.Main.Companion.DisplayEntity
import tortel.boatHider.Main.Companion.bhitboxEntity
import tortel.boatHider.Main.Companion.hitboxEntity
import java.lang.Math.toRadians
import kotlin.math.floor

class GridPlacementSystem() : BukkitRunnable() {
    private var angle = 0f

    override fun run() {
        angle += 5f
        if (angle >= 360f) angle = 0f

        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.gameMode == GameMode.SPECTATOR) return@forEach

            val result = player.world.rayTraceBlocks(
                player.eyeLocation,
                player.eyeLocation.direction,
                3.0,
                FluidCollisionMode.NEVER,
                true
            ) ?: return@forEach

            val block = result.hitBlock ?: return@forEach
            val face: BlockFace? = result.hitBlockFace
            var pos: Location = result.hitPosition.toLocation(player.world)

            // Block checks
            if (!block.type.isOccluding) return@forEach
            if (block.blockData is Slab || block.blockData is Stairs) return@forEach
            if (!block.isSolid) return@forEach

            // Apply grid snapping
            pos = applyGridSnapping(pos)

            // Rest of your code for entity placement and rotation...
            val direction = if (face != null) {
                Vector(-face.modX.toDouble(), -face.modY.toDouble(), -face.modZ.toDouble())
            } else {
                player.eyeLocation.direction.normalize().multiply(-1.0)
            }

            // ... [rest of your existing code for offsets and entity placement]

            val offset = when(face){
                BlockFace.UP -> Vector(0.0, 0.6, 0.0)
                BlockFace.DOWN -> Vector(0.0, -0.6, 0.0)

                BlockFace.NORTH -> Vector(0.0, 0.1, 0.0)
                BlockFace.SOUTH -> Vector(0.0, 0.1, 0.0)
                BlockFace.EAST -> Vector(0.0, 0.1, 0.0)
                BlockFace.WEST -> Vector(0.0, 0.1, 0.0)

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

                BlockFace.NORTH -> Vector(0.0, 0.5, 0.0)
                BlockFace.SOUTH -> Vector(0.0, 0.5, 0.0)//Vector(0.0, 0.5, -0.3)
                BlockFace.EAST -> Vector(0.0, 0.5, 0.0)
                BlockFace.WEST -> Vector(0.0, 0.5, 0.0)

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

            //grid based placement

            //val offset = Vector(0.0, 0.1, -0.6)
            DisplayEntity.teleport(pos) // // //.subtract(direction.normalize().multiply(0.5))
            // val hitboxoffset = Vector(0.0, 0.0, 0.0)//Vector(0.0, 0.5, -0.3) <- south only
            hitboxEntity.teleport(DisplayEntity.location.subtract(hitboxoffset))
            bhitboxEntity.teleport(DisplayEntity.location.subtract(bhitboxoffset))

            rotateEntity(DisplayEntity, yrotation)

            val hitboxBlocks = getBlocksInBoundingBox(hitboxEntity.boundingBox, player.world, 3.0, DisplayEntity)
            val bhitboxBlocks = getBlocksInBoundingBox(bhitboxEntity.boundingBox, player.world, 2.0, DisplayEntity)

            val isHitboxColliding = hitboxBlocks.any { it.isSolid }
            val BHitboxContainsAir = bhitboxBlocks.any { it.isAir }

            Bukkit.broadcastMessage("H is colliding: $isHitboxColliding || BH has air:$BHitboxContainsAir}")
        }
    }

    private fun rotateEntity(entity : ItemDisplay, degree : Float) {
        entity.setTransformationMatrix(Matrix4f().rotateY((toRadians(degree.toDouble()).toFloat())))// + 0.1f /* prevent the client from interpolating in reverse */

}

    private fun applyGridSnapping(location: Location): Location {
        val world = location.world
        val x = location.x
        val y = location.y
        val z = location.z

        // Snap to edges (whole numbers)
        // Determine which edge is closest
        val relX = x - floor(x)
        val relY = y - floor(y)
        val relZ = z - floor(z)

        // Find the closest edge for each axis
        val snapX = if (relX < 0.25) 0.0 else if (relX > 0.75) 1.0 else 0.5
        val snapY = if (relY < 0.25) 0.0 else if (relY > 0.75) 1.0 else 0.5
        val snapZ = if (relZ < 0.25) 0.0 else if (relZ > 0.75) 1.0 else 0.5



        return Location(
            world,
            floor(x) + snapX,
            floor(y) + snapY,
            floor(z) + snapZ
        )
    }

    fun getBlocksInBoundingBox(boundingBox: BoundingBox, world: World, MaxDist : Double, EntityToCompareDistTo : Entity): List<Material> {
        val blockCounts = mutableListOf<Material>()

        val min = boundingBox.min
        val max = boundingBox.max

        // Iterate through all blocks in the bounding box
        for (x in min.blockX..max.blockX) {
            for (y in min.blockY..max.blockY) {
                for (z in min.blockZ..max.blockZ) {
                    val block = world.getBlockAt(x, y, z)
                    if(block.location.distance(EntityToCompareDistTo.location) > MaxDist) continue

                    blockCounts.add(block.type)
                }
            }
        }

        return blockCounts
    }
}