package io.github.bradenk04.claims

import com.flowpowered.math.vector.Vector2d
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.markers.ExtrudeMarker
import de.bluecolored.bluemap.api.markers.MarkerSet
import de.bluecolored.bluemap.api.markers.ShapeMarker
import de.bluecolored.bluemap.api.math.Color
import de.bluecolored.bluemap.api.math.Shape
import io.github.bradenk04.claims.BluemapHelper.convertToShape
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import org.bukkit.Bukkit
import org.bukkit.World
import java.awt.geom.Area
import java.awt.geom.PathIterator
import java.awt.geom.Rectangle2D

object BluemapHelper {
    var isBluemapEnabled = false
    lateinit var bluemapApi: BlueMapAPI
    lateinit var claimSet: MarkerSet

    fun initialize() {
        isBluemapEnabled = Bukkit.getPluginManager().isPluginEnabled("BlueMap")
        if (isBluemapEnabled) {
            BlueMapAPI.onEnable {
                bluemapApi = it

                claimSet = MarkerSet.builder()
                    .label("claims")
                    .build()

                val claims = Database.claims.getAllClaims()
                var claimWorld: World? = null
                claims.forEach {
                    if (claimWorld == null) claimWorld = Bukkit.getWorld(it.chunks.first().world)
                    val claimShape = it.convertToShape() ?: return@forEach
                    val minY = claimWorld?.minHeight?.toFloat() ?: -64f
                    val maxY = claimWorld?.maxHeight?.toFloat() ?: 320f
                    val marker = ExtrudeMarker.builder()
                        .label(it.getFormattedClaimName())
                        .shape(claimShape, minY, maxY)
                        .fillColor(Color(46, 49, 59, 0.2f))
                        .lineColor(Color(46, 49, 59, 1f))
                        .lineWidth(2)
                        .depthTestEnabled(false)
                        .build()

                    claimSet.put(it.id.toString(), marker)
                }

                bluemapApi.getWorld(claimWorld).ifPresent {
                    for (map in it.maps) {
                        map.markerSets["claims-claims"] = claimSet
                    }
                }
            }
        }


    }

    fun Claim.convertToShape(): Shape? {
        if (chunks.isEmpty()) return null

        val area = Area()

        for (chunk in chunks) {
            val rect = Rectangle2D.Double(
                (chunk.x * 16).toDouble(),
                (chunk.z * 16).toDouble(),
                16.0,
                16.0
            )
            area.add(Area(rect))
        }

        var shape: Shape? = null

        val iter = area.getPathIterator(null)
        val coords = DoubleArray(6)
        val currentPoints = mutableListOf<Vector2d>()

        while (!iter.isDone) {
            val type = iter.currentSegment(coords)

            when (type) {
                PathIterator.SEG_MOVETO -> currentPoints.add(Vector2d(coords[0], coords[1]))
                PathIterator.SEG_LINETO -> currentPoints.add(Vector2d(coords[0], coords[1]))
                PathIterator.SEG_CLOSE -> {
                    shape = Shape(currentPoints)
                }
            }
            iter.next()
        }

        return shape
    }

    fun registerClaim(claim: Claim) {
        val claimWorld = Bukkit.getWorld(claim.chunks.first().world)
        val minY = claimWorld?.minHeight?.toFloat() ?: -64f
        val maxY = claimWorld?.maxHeight?.toFloat() ?: 320f
        val claimShape = claim.convertToShape() ?: return
        val marker = ExtrudeMarker.builder()
            .label(claim.getFormattedClaimName())
            .shape(claimShape, minY, maxY)
            .fillColor(Color(0, 255, 0, 0.2f))
            .fillColor(Color(0, 255, 0, 1f))
            .lineWidth(2)
            .depthTestEnabled(false)
            .build()

        claimSet.put(claim.id.toString(), marker)
    }
}