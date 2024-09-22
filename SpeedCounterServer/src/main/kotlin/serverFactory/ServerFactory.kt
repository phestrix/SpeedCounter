package serverFactory

import output.Writer
import server.JavaNetServer
import server.KtorServer
import server.Server

class ServerFactory {
    fun createServerByName(name: String, port: Int, writer: Writer): Server {
        return when (name) {
            "JavaNet" -> JavaNetServer(port, writer)
            "Ktor" -> KtorServer(port, writer)
            else -> throw Exception("Server not found")
        }
    }
}
