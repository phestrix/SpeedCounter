package controller

import input.InputResolver
import output.SOutWriter
import server.Server
import serverFactory.ServerFactory

class Controller(input: Array<String>) {
    private val inputResolver = InputResolver()
    private val port = inputResolver.resolveInput(input).toInt()
    private val writer = SOutWriter()
    private val server: Server = ServerFactory().createServerByName("Ktor", port, writer)

    fun run(){
        server.start()
    }
}