package game

import game.component.*
import util.ecs.EcsState
import util.math.Size
import util.rendering.tile.TileRenderer

fun renderEntities(tileRenderer: TileRenderer, size: Size, state: EcsState) {
    val bodyStore = state.getStorage<Body>()
    val graphicStore = state.getStorage<Graphic>()

    for (entityId in state.entityIds) {
        val body = bodyStore[entityId]
        val graphic = graphicStore[entityId]

        if (body != null && graphic != null) {
            renderBody(tileRenderer, size, body, graphic)
        }
    }
}

fun renderBody(tileRenderer: TileRenderer, size: Size, body: Body, graphic: Graphic) = when (body) {
    is SimpleBody -> tileRenderer.renderTile(
        graphic.get(0),
        size.getX(body.position),
        size.getY(body.position)
    )
    is BigBody -> tileRenderer.renderTile(
        graphic.get(0),
        size.getX(body.position),
        size.getY(body.position),
        body.size
    )
    is SnakeBody -> for (pos in body.positions) {
        tileRenderer.renderTile(graphic.get(0), size.getX(pos), size.getY(pos))
    }
}