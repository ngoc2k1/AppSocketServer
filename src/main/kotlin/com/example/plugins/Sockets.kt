package com.example.plugins

import com.example.*
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat/room/{id}") {
            println("Adding user!")
            val roomId = call.parameters["id"] ?: return@webSocket

            // Handle new connections
//            send("You are connected to room $roomId")
//            send("Xin chào bạn. Bạn muốn được tư vấn gì ạ?")

            val thisConnection = Connection(this)
            thisConnection.roomId = roomId
            connections += thisConnection
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        if (it.roomId == roomId){
                            it.session.send(textWithUsername+"\n")
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}

