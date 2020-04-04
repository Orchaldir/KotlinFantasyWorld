package util.redux.middleware

import mu.KotlinLogging
import util.redux.Dispatcher

private val logger = KotlinLogging.logger {}

@Suppress("UNUSED_PARAMETER")
fun <Action, State> logAction(dispatcher: Dispatcher<Action>, supplier: () -> State): Dispatcher<Action> {
    return { action ->
        logger.info("Dispatch $action")
        dispatcher(action)
    }
}