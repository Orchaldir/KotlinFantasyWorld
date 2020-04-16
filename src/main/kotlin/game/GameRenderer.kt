package game

import game.component.*
import game.map.GameMap
import game.map.Terrain
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.math.Size
import util.rendering.tile.Tile
import util.rendering.tile.TileRenderer
import util.rendering.tile.UnicodeTile

class GameRenderer(
    private val startX: Int,
    private val startY: Int,
    private val size: Size,
    private val offsetX: Int = 0,
    private val offsetY: Int = 0
) {

    constructor(size: Size) : this(0, 0, size, 0, 0)

    fun renderMap(renderer: TileRenderer, map: GameMap) {
        for (y in 0 until size.y) {
            for (x in 0 until size.x) {
                if (!size.isInside(x + offsetX, y + offsetY)) {
                    continue
                }

                val mapIndex = size.getIndex(x + offsetX, y + offsetY)

                if (map.entities.containsKey(mapIndex)) {
                    continue
                }

                val terrain = map.terrainList[mapIndex]
                val symbol = if (terrain == Terrain.FLOOR) {
                    "."
                } else {
                    "#"
                }

                renderer.renderUnicodeTile(symbol, Color.WHITE, startX + x, startY + y)
            }
        }
    }

    fun renderEntities(tileRenderer: TileRenderer, state: EcsState) {
        val bodyStore = state.getStorage<Body>()
        val graphicStore = state.getStorage<Graphic>()
        val healthStore = state.getStorage<Health>()

        for (entityId in state.entityIds) {
            val body = bodyStore[entityId]
            val graphic = graphicStore[entityId]
            val health = healthStore[entityId]

            if (health != null && health.state == HealthState.DEAD && body != null) {
                renderCopse(tileRenderer, body)
            } else if (body != null && graphic != null) {
                renderBody(tileRenderer, body, graphic)
            }
        }
    }

    private fun renderBody(tileRenderer: TileRenderer, body: Body, graphic: Graphic) = when (body) {
        is SimpleBody -> renderTile(tileRenderer, body.position, 1, graphic.get(0))
        is BigBody -> renderTile(tileRenderer, body.position, body.size, graphic.get(0))
        is SnakeBody -> for (pos in body.positions) {
            renderTile(tileRenderer, pos, 1, graphic.get(0))
        }
    }

    private fun renderTile(tileRenderer: TileRenderer, pos: Int, s: Int, tile: Tile) {
        tileRenderer.renderTile(tile, startX + size.getX(pos), startY + size.getY(pos), s)
    }

    private val corpse = Graphic(UnicodeTile("%", Color.WHITE))

    private fun renderCopse(tileRenderer: TileRenderer, body: Body) =
        renderBody(tileRenderer, body, corpse)

}