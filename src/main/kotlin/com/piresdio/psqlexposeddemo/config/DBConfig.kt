package com.piresdio.psqlexposeddemo.config

import java.io.FileInputStream
import java.util.*

open class DBConfig(private val filename: String) {
    private val fis = FileInputStream(filename)
    private val prop = Properties()
    var dbUrl: String = ""
    var dbUser: String = ""
    var dbPassword: String = ""
    var dbDriver: String = ""

    init {
        prop.load(fis)
        dbUrl = prop["dbUrl"].toString()
        dbUser = prop["dbUser"].toString()
        dbPassword = prop["dbPassword"].toString()
        dbDriver = prop["dbDriver"].toString()
    }
}
