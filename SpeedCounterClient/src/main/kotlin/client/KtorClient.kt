package client

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import output.Writer
import java.io.File

class KtorClient(private val filepath: String, private val writer: Writer) : Client {
    private val file = File(filepath)
    private val sizeOfFile = file.length()
    private val fileName = file.name
    override fun start(address: String, port: Int) = runBlocking(Dispatchers.IO) {
        val socket = aSocket(SelectorManager(Dispatchers.IO)).tcp().connect(address, port)
        val sendChannel = socket.openWriteChannel(autoFlush = true)
        val receiveChannel = socket.openReadChannel()

        sendChannel.writeStringUtf8("$fileName\n")
        sendChannel.writeLong(sizeOfFile)

        val fileInputStream = file.inputStream()
        val buffer = ByteArray(4096)
        var bytesRead: Int

        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            sendChannel.writeFully(buffer, 0, bytesRead)
        }

        val response = receiveChannel.readUTF8Line()
        writer.write("Server response: $response")
        fileInputStream.close()
        socket.close()
    }
}