package game

import game.component.*
import javafx.scene.paint.Color
import util.ecs.EcsState
import util.math.Size
import util.rendering.tile.TileRenderer
import util.rendering.tile.UnicodeTile

fun renderEntities(tileRenderer: TileRenderer, size: Size, state: EcsState) {
    val bodyStore = state.getStorage<Body>()
    val graphicStore = state.getStorage<Graphic>()
    val healthStore = state.getStorage<Health>()

    for (entityId in state.entityIds) {
        val body = bodyStore[entityId]
        val graphic = graphicStore[entityId]
        val health = healthStore[entityId]

        if (health != null && health.state == HealthState.DEAD && body != null) {
            renderCopse(tileRenderer, size, body)
        } else if (body != null && graphic != null) {
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

val corpse = Graphic(UnicodeTile("%", Color.WHITE))

fun renderCopse(tileRenderer: TileRenderer, size: Size, body: Body) = renderBody(tileRenderer, size, body, corpse)