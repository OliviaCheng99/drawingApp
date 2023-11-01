package com.example

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.util.Base64

fun Application.configureResources() {
    install(Resources)
    routing{

        get("/posts") {
            //handler for /posts
            call.respond(
                newSuspendedTransaction(Dispatchers.IO) {
                    Post.selectAll()
                        .map {
                            val path = it[Post.imagePath]
                            val imageBytes: ByteArray = File(path).readBytes()
                            val encodedImg = Base64.getEncoder().encodeToString(imageBytes)
                            PostDataGet( it[Post.id].value,it[Post.imageName], it[Post.postTime], encodedImg)
                        }
                }
            )
        }


//        get("/posts/{uid}") {
////            val idParam = call.parameters["id"]
//            // Convert idParam to Int:
//            val intId = idParam?.toIntOrNull()
//            if (intId != null) {
//                call.respond(
//                    newSuspendedTransaction(Dispatchers.IO) {
//                        Post.select { Post.id eq EntityID(intId, Post) }
//                            .map { PostData2(it[Post.id].value, it[Post.text], it[Post.postTime]) }
//                    }
//                )
//            } else {
//                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//            }
//        }
/*
        get("/posts/post") {
            val timeParam = call.request.queryParameters["since"]
            // Convert timeParam to Long:
            val longTime = timeParam?.toLongOrNull()

            if (longTime != null) {
                call.respond(
                    newSuspendedTransaction(Dispatchers.IO) {
                        Post.select{ Post.postTime greaterEq longTime }
                            .map { PostData2( it[Post.id].value,it[Post.text], it[Post.postTime]) }
                    }
                )
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid time")
            }
        }*/
//
        post("/posts") {
            val multipart = call.receiveMultipart()
            var uid: String? = null
            var imageName: String? = null
            var imageBytes: ByteArray? = null
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "uid" -> uid = part.value
                            "imageName" -> imageName = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        imageBytes = part.streamProvider().readBytes()
                    }

                    else -> {}
                }
            }

            if (uid!=null && imageName!=null && imageBytes!=null) {
                withContext(Dispatchers.IO) {
                    val path = "src/images/$imageName"
                    File(path).writeBytes(imageBytes!!)

                    newSuspendedTransaction {
                        Post.insert {
                            it[Post.imagePath] = "src/images/$imageName"
                            it[Post.imageName] = imageName!!
                            it[Post.postTime] = System.currentTimeMillis()
                            it[Post.uid] = uid!!
                        }
                    }
                }
                call.respondText("Image post success.")
            } else {
                call.respondText("Missing data.", status = HttpStatusCode.BadRequest)
            }
        }

        delete("/posts/{imageId}") {
            val idParam = call.parameters["imageId"]
            val intId = idParam?.toIntOrNull()
            var deleteCount = 0
            if (intId != null) {
                newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                    //delete image
                    val path = Post.select { Post.id eq EntityID(intId, Post) }
                        .map { it[Post.imagePath] }.first()
                    //delete image from file
                    File(path).delete()
                    // dele from database
                    deleteCount = Post.deleteWhere { Post.id eq EntityID(intId, Post) }
                }
            }else{
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }

            if (deleteCount > 0) {
                call.respondText("Post deleted with id $intId")
            }else{
                call.respond(HttpStatusCode.NotFound, "Post not found")
            }

        }

    }
}

// for get method
@Serializable data class PostDataGet(val id: Int, val imageName: String, val time: Long, val imageBytes: String)
// for delete method
//@Serializable data class PostDataDel(val id: Int, val imageName: String, val time: Long, val imageBytes: String)
