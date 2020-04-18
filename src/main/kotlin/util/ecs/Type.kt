package util.ecs

import kotlin.reflect.KClass

fun getType(type: KClass<*>) = type.simpleName ?: type.toString()