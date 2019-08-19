package com.piresdio.psqlexposeddemo.repositories

import com.piresdio.psqlexposeddemo.config.DBConfig
import com.piresdio.psqlexposeddemo.model.Snack
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.*

class SnackRepositoryTest {
    private val propertiesFile = SnackRepositoryTest::class.java.getResource("/application.properties").path
    private val dbConfig = DBConfig(propertiesFile)
    private val snackRepository = SnackRepository(
        dbUrl = dbConfig.dbUrl,
        user = dbConfig.dbUser,
        password = dbConfig.dbPassword,
        driver = dbConfig.dbDriver
    )
    private var snackName = UUID.randomUUID().toString().subSequence(0, 15).toString()
    private var snackPrice = (1..20).shuffled().first()

    @BeforeEach
    fun initVars() {
        snackName = UUID.randomUUID().toString().subSequence(0, 15).toString()
        snackPrice = (1..20).shuffled().first()
    }

    @Test
    fun `test insert`() {
        val snackId = snackRepository.add(
            snackName = snackName,
            snackPrice = snackPrice
        )

        val snack: Snack = snackRepository.select(snackId)!!
        Assertions.assertAll(
            Executable { Assertions.assertEquals(snack.id, snackId) },
            Executable { Assertions.assertEquals(snack.name, snackName) } ,
            Executable { Assertions.assertEquals(snack.price, snackPrice) }
        )

    }

    @Test
    fun `test update`() {
        val newPrice = (1..20).shuffled().first()

        var snackId = snackRepository.add(
            snackName = snackName,
            snackPrice = snackPrice
        )

        snackId = snackRepository.update(snackId, snackPrice = newPrice)
        val snack = snackRepository.select(snackId)
        Assertions.assertEquals(snack!!.price, newPrice)
    }

    @Test
    fun `test delete`() {
        val snackId = snackRepository.add(
            snackName = snackName,
            snackPrice = snackPrice
        )

        val deleted = snackRepository.delete(snackId)
        Assertions.assertTrue(deleted)
    }
}