package com.piresdio.psqlexposeddemo.model

import org.jetbrains.exposed.sql.Table
import java.util.*

object Snacks: Table() {
    val id = varchar("id", 36).primaryKey().default(UUID.randomUUID().toString())
    val name = varchar("name", length = 16)
    val price = integer("price")

    init {
        index(true, name)
    }
}

data class Snack(
    val id: String,
    val name: String,
    val price: Int
)