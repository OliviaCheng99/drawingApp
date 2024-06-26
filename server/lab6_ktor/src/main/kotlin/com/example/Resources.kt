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
                            PostDataGet( it[Post.id].value, it[Post.uid], it[Post.postTime], encodedImg)
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
            var imageBytes: ByteArray? = null
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "uid" -> uid = part.value
                        }
                    }
                    is PartData.FileItem -> {
//                        if (part.name == "image") {
                            imageBytes = part.streamProvider().readBytes()
//                        }
                    }

                    else -> {}
                }
            }

            if (uid!=null && imageBytes!=null) {
                withContext(Dispatchers.IO) {
                    val time = System.currentTimeMillis()
                    val path = "src/images/$time.png"
                    File(path).writeBytes(imageBytes!!) // write image to file

                    newSuspendedTransaction {
                        // insert to database
                        Post.insert {
                            it[Post.imagePath] = path
                            it[Post.postTime] = time
                            it[Post.uid] = uid!!
                        }
                    }
                }
                call.respondText("Image post success.")
            } else {
                call.respondText("Missing data.", status = HttpStatusCode.BadRequest)
            }
        }

        delete("/posts/{postTiming}") {
            val idParam = call.parameters["postTiming"]
            print(idParam)
            val longTiming = idParam?.toLongOrNull()
            var deleteCount = 0
            if (longTiming != null) {
                newSuspendedTransaction(Dispatchers.IO, DBSettings.db) {
                    //delete image
                    val path = Post.select { Post.postTime eq longTiming}
                        .map { it[Post.imagePath] }.first()
                    //delete image from file
                    File(path).delete()
                    // dele from database
                    deleteCount = Post.deleteWhere { Post.postTime eq longTiming }
                }
            }else{
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }

            if (deleteCount > 0) {
                call.respondText("Post deleted with post time:  $longTiming")
            }else{
                call.respond(HttpStatusCode.NotFound, "Post not found")
            }

        }

    }
}

// for get method
@Serializable data class PostDataGet(val id: Int, val userId: String, val time: Long, val imageBytes: String)
// for delete method
//@Serializable data class PostDataDel(val id: Int, val imageName: String, val time: Long, val imageBytes: String)
