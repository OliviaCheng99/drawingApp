package com.example

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.sql.Timestamp


object Post: IntIdTable(){ // object for singleton table
    val imagePath = varchar("path", 255)
    val uid = varchar("uid", 255)
    val postTime = long("postTime")
}
