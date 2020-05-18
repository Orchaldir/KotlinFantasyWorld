package game

import ai.pathfinding.NoPathFound
import ai.pathfinding.Path
import ai.pathfinding.PathfindingResult
import ai.pathfinding.graph.OccupancyMap
import game.component.*
import game.map.GameMap
import game.map.Terrain
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.math.Direction
import util.math.createPolygon
import util.math.rectangle.Rectangle
import util.math.rectangle.Size
import util.rendering.tile.*

class GameRenderer(
    startX: Int,
    startY: Int,
    size: Size,
    private val offsetX: Int = 0,
    private val offsetY: Int = 0
) {
    val area = Rectangle(startX, startY, size)

    // tiles
    private val successTile = FullTile(Color.DARKGREEN)
    private val errorTile = FullTile(Color.DARKRED)
    private val pathWithMovementPointsTile = FullTile(Color.GRAY)
    private val pathWithoutMovementPointsTile = FullTile(Color.GRAY.darker().darker())

    // direction
    private val directionColor = Color.DARKRED
    private val northTile = PolygonTile(createPolygon(0.3, 0.2, 0.7, 0.2, 0.5, 0.0), directionColor)
    private val eastTile = PolygonTile(createPolygon(0.8, 0.3, 0.8, 0.7, 1.0, 0.5), directionColor)
    private val southTile = PolygonTile(createPolygon(0.3, 0.8, 0.7, 0.8, 0.5, 1.0), directionColor)
    private val westTile = PolygonTile(createPolygon(0.0, 0.5, 0.2, 0.3, 0.2, 0.7), directionColor)

    // graphics
    private val corpseGraphic = Graphic(UnicodeTile("%", Color.WHITE))

    constructor(size: Size) : this(0, 0, size, 0, 0)

    fun renderMap(renderer: TileRenderer, map: GameMap) {
        for (y in 0 until area.size.y) {
            for (x in 0 until area.size.x) {
                if (!area.size.isInside(x + offsetX, y + offsetY)) {
                    continue
                }

                val mapIndex = area.size.getIndex(x + offsetX, y + offsetY)

                renderTile(renderer, map, mapIndex, x, y)
            }
        }
    }

    fun renderTiles(renderer: TileRenderer, map: GameMap, indices: Set<Int>) {
        for (index in indices) {
            val (x, y) = area.size.getPos(index)

            renderTile(renderer, map, index, x, y)
        }
    }

    private fun renderTile(
        renderer: TileRenderer,
        map: GameMap,
        index: Int,
        x: Int,
        y: Int
    ) {
        if (map.entities.containsKey(index)) {
            return
        }

        val terrain = map.terrainList[index]
        val symbol = if (terrain == Terrain.FLOOR) {
            "."
        } else {
            "#"
        }

        renderer.renderUnicodeTile(symbol, Color.WHITE, area.x + x, area.y + y)
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

    fun renderPath(renderer: TileRenderer, path: Path, movementPoints: Int) {
        path.indices.forEachIndexed { i, position ->
            renderTile(
                renderer,
                if (i < movementPoints) pathWithMovementPointsTile else pathWithoutMovementPointsTile,
                position
            )
        }
    }

    fun renderPathfindingResult(renderer: TileRenderer, result: PathfindingResult, movementPoints: Int) {
        if (result is Path) {
            renderPath(renderer, result, movementPoints)
            renderSuccess(renderer, result.indices.last(), result.size)
        } else if (result is NoPathFound) {
            result.goals.forEach { renderError(renderer, it, result.size) }
        }
    }

    fun renderEntities(tileRenderer: TileRenderer, state: EcsState, renderDir: Boolean = false) {
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

                if (renderDir) renderDirection(tileRenderer, body)
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

    private fun renderDirection(tileRenderer: TileRenderer, body: Body) = when (body) {
        is SimpleBody -> renderDirection(tileRenderer, body.direction, body.position)
        is BigBody -> renderDirection(tileRenderer, body.direction, body.position, body.size)
        is SnakeBody -> renderDirection(tileRenderer, body.direction, body.positions.first())
    }

    fun renderTile(tileRenderer: TileRenderer, tile: Tile, pos: Int, bodySize: Int = 1) =
        tileRenderer.renderTile(tile, area.getX(pos), area.getY(pos), bodySize)

    fun renderDirection(tileRenderer: TileRenderer, direction: Direction, pos: Int, bodySize: Int = 1) =
        tileRenderer.renderTile(getDirectionTile(direction), area.getX(pos), area.getY(pos), bodySize)

    private fun getDirectionTile(direction: Direction) = when (direction) {
        Direction.NORTH -> northTile
        Direction.EAST -> eastTile
        Direction.SOUTH -> southTile
        Direction.WEST -> westTile
    }

    fun renderSuccess(tileRenderer: TileRenderer, pos: Int, bodySize: Int = 1) =
        tileRenderer.renderTile(successTile, area.getX(pos), area.getY(pos), bodySize)

    fun renderError(tileRenderer: TileRenderer, pos: Int, bodySize: Int = 1) =
        tileRenderer.renderTile(errorTile, area.getX(pos), area.getY(pos), bodySize)

    private fun renderCopse(tileRenderer: TileRenderer, body: Body) =
        renderBody(tileRenderer, body, corpseGraphic)

}