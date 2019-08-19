package com.piresdio.psqlexposeddemo.repositories

import com.piresdio.psqlexposeddemo.model.Snack
import com.piresdio.psqlexposeddemo.model.Snacks
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class SnackRepository (
    dbUrl: String,
    driver: String,
    user: String,
    password: String
) {
    init {
        Database.connect(url = dbUrl, driver = driver, user = user, password = password)
        transaction {
            SchemaUtils.create(Snacks)
        }
    }

    private fun toSnack(resultRow: ResultRow): Snack {
        return Snack(
            id = resultRow[Snacks.id],
            name = resultRow[Snacks.name],
            price = resultRow[Snacks.price]
        )
    }

    fun select(snackId: String): Snack? {
        return transaction {
            Snacks
                .select { Snacks.id eq snackId }
                .mapNotNull { toSnack(it) }
                .singleOrNull()
        }
    }

    fun add(snackId: String = UUID.randomUUID().toString(), snackName: String, snackPrice: Int): String {
        return transaction {
            Snacks.insert {
                it[id] = snackId
                it[name] = snackName
                it[price] = snackPrice
            } get Snacks.id
        }
    }

    fun update(snackId: String, snackName: String? = null, snackPrice: Int? = null): String {
        val existingResult = transaction {
            Snacks
                .select { Snacks.id eq snackId }
                .map { toSnack(it) }
                .firstOrNull()
        } ?: return "Snack with ID '$snackId' does not exist!"

        delete(snackId)
        val newSnackId = existingResult.id
        val newSnackName = snackName ?: existingResult.name
        val newSnackPrice = snackPrice ?: existingResult.price
        add(newSnackId, newSnackName, newSnackPrice)
        /*transaction {
            Snacks.update {
                it[name] = snackName ?: existingResult.name
                it[price] = snackPrice ?: existingResult.price
            }
        }*/

        return newSnackId
    }

    fun delete(snackId: String): Boolean {
        transaction {
            Snacks
                .select { Snacks.id eq snackId }
                .map { it }
                .firstOrNull()
        } ?: return false

        val deletedCount = transaction {
            Snacks.deleteWhere {
                Snacks.id eq snackId
            }
        }
        return when(deletedCount) {
            0 -> false
            else -> true
        }
    }
}