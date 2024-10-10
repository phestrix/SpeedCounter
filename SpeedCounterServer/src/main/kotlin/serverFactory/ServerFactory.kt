package serverFactory

import output.Writer
import server.JavaNetServer
import server.KtorServer
import server.Server

/*
*
* Factory class to create server instances like prototype pattern
*
 */
class ServerFactory {
    /**
     * Create server instance by name
     *
     * @param name name of the server (now available: JavaNet, Ktor)
     * @param port port number
     * @param writer writer instance
     * @return server instance
     */
    fun createServerByName(name: String, port: Int, writer: Writer): Server {
        return when (name) {
            "JavaNet" -> JavaNetServer(port, writer)
            "Ktor" -> KtorServer(port, writer)
            else -> throw Exception("Server not found")
        }
    }
}
