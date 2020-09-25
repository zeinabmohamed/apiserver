package com.ktorapidemo

import com.ktorapidemo.db.dao.ProductDao
import com.ktorapidemo.dto.Product
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}
fun main(){
    val dao = ProductDao(
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver"))
    val port = System.getenv("PORT")?.toInt() ?: 23567
    embeddedServer(Netty, port) {
        dao.init()
        install(CallLogging)
        install(ContentNegotiation) {
            jackson {}
        }
        routing {
            route("/products") {
                get {
                    call.respond(mapOf("products" to dao.getAllProducts()))
                }
                post {
                    val product = call.receive<Product>()
                    dao.createProduct(product.title, product.description, product.price)
                    call.respond("ok")
                }
                put {
                    val product = call.receive<Product>()
                    dao.updateProduct(product.id, product.title, product.description, product.price)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]
                    if (id != null) {
                        dao.deleteProduct(id.toInt())
                        call.respond("")
                    }else{
                        call.respond(status = HttpStatusCode.BadRequest,"Id not exist")
                    }
                }

                get("/{id}") {
                    val id = call.parameters["id"]
                    if (id != null) {
                        val response = dao.getProduct(id.toInt())
                        if (response != null)
                            call.respond(response)
                        else call.respond("No such product found!")
                    }
                }
            }
        }
    }.start(wait = true)
}


