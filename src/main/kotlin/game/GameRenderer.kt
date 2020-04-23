package game

import ai.pathfinding.Path
import ai.pathfinding.graph.OccupancyMap
import game.component.*
import game.map.GameMap
import game.map.Terrain
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.math.Area
import util.math.Size
import util.rendering.tile.FullTile
import util.rendering.tile.Tile
import util.rendering.tile.TileRenderer
import util.rendering.tile.UnicodeTile

class GameRenderer(
    startX: Int,
    startY: Int,
    size: Size,
    private val offsetX: Int = 0,
    private val offsetY: Int = 0
) {
    val area = Area(startX, startY, size)

    constructor(size: Size) : this(0, 0, size, 0, 0)

    fun renderMap(renderer: TileRenderer, map: GameMap) {
        for (y in 0 until area.size.y) {
            for (x in 0 until area.size.x) {
                if (!area.size.isInside(x + offsetX, y + offsetY)) {
                    continue
                }

                val mapIndex = area.size.getIndex(x + offsetX, y + offsetY)

                if (map.entities.containsKey(mapIndex)) {
                    continue
                }

                val terrain = map.terrainList[mapIndex]
                val symbol = if (terrain == Terrain.FLOOR) {
                    "."
                } else {
                    "#"
                }

                renderer.renderUnicodeTile(symbol, Color.WHITE, area.x + x, area.y + y)
            }
        }
    }

    fun renderOccupancyMap(renderer: TileRenderer, map: OccupancyMap) {
        for (y in 0 until area.size.y) {
            for (x in 0 until area.size.x) {
                if (!area.size.isInside(x + offsetX, y + offsetY)) {
                    continue
                }

                val mapIndex = area.size.getIndex(x + offsetX, y + offsetY)
                val color = if (map[mapIndex]) Color.GREEN else Color.RED

                renderer.renderFullTile(color, area.x + x, area.y + y)
            }
        }
    }

    fun renderPath(renderer: TileRenderer, path: Path) {
        val tile = FullTile(Color.GRAY)
        path.indices.forEach { renderTile(renderer, tile, it) }
    }

    fun renderEntities(tileRenderer: TileRenderer, state: EcsState) {
        val bodyStore = state.getStorage<Body>()
        val graphicStore = state.getStorage<Graphic>()
        val healthStore = state.getStorage<Health>()

        for (entity in state.entities) {
            val body = bodyStore[entity]
            val graphic = graphicStore[entity]
            val health = healthStore[entity]

            if (health != null && health.state == HealthState.DEAD && body != null) {
                renderCopse(tileRenderer, body)
            } else if (body != null && graphic != null) {
                renderBody(tileRenderer, body, graphic)
            }
        }
    }

    private fun renderBody(tileRenderer: TileRenderer, body: Body, graphic: Graphic) = when (body) {
        is SimpleBody -> renderTile(tileRenderer, graphic.get(0), body.position)
        is BigBody -> renderTile(tileRenderer, graphic.get(0), body.position, body.size)
        is SnakeBody -> for (pos in body.positions) {
            renderTile(tileRenderer, graphic.get(0), pos)
        }
    }

    private fun renderTile(tileRenderer: TileRenderer, tile: Tile, pos: Int, bodySize: Int = 1) {
        tileRenderer.renderTile(tile, area.getX(pos), area.getY(pos), bodySize)
    }

    private val corpse = Graphic(UnicodeTile("%", Color.WHITE))

    private fun renderCopse(tileRenderer: TileRenderer, body: Body) =
        renderBody(tileRenderer, body, corpse)

}