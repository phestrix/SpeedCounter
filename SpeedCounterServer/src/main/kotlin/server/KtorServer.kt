package server

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import output.Writer
import java.io.File
import java.io.FileOutputStream
import java.net.InetAddress
import java.net.NetworkInterface
import kotlin.collections.iterator


class KtorServer(private val port: Int, private val writer: Writer) : Server() {
    private val selectorManager = ActorSelectorManager(Dispatchers.IO)

    override fun start() = runBlocking(Dispatchers.IO) {
        try {
            val localAddress = getLocalIPv4Address()
            val serverSocket = aSocket(selectorManager).tcp().bind(localAddress, port = port)
            writer.write("Server started on $port $localAddress")
            while (true) {
                val clientSocket = serverSocket.accept()
                launch {
                    handleClient(clientSocket)
                }
            }
        } catch (e: Exception) {
            writer.write("Failed to start server: ${e.message}")
        }
    }

    private suspend fun handleClient(clientSocket: Socket) {
        try {
            withContext(Dispatchers.IO) {
                val input = clientSocket.openReadChannel()
                val output = clientSocket.openWriteChannel(autoFlush = true)

                val fileName = input.readUTF8Line() ?: throw Exception("Failed to read file name")
                val fileSize = input.readLong()

                val uploadDir = File("uploads")
                if (!uploadDir.exists()) uploadDir.mkdir()
                val file = File(uploadDir, fileName)

                FileOutputStream(file).use { fileOutput ->
                    val buffer = ByteArray(4096)
                    var totalBytesRead = 0L
                    val startTime = System.currentTimeMillis()
                    var transferComplete = false
                    val speedJob = launch {
                        while (!transferComplete) {
                            delay(1500)
                            val currentTime = System.currentTimeMillis()
                            val speed = totalBytesRead / ((currentTime - startTime) / 1024.0)
                            writer.write("Client ${clientSocket.remoteAddress}: Speed = $speed kilobytes/sec")
                        }
                    }

                    while (totalBytesRead < fileSize) {
                        val bytesRead = input.readAvailable(buffer, 0, buffer.size)
                        if (bytesRead == -1) break
                        fileOutput.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                    }

                    val endTime = System.currentTimeMillis()
                    val averageSpeed = totalBytesRead / ((endTime - startTime) / 1024.0)
                    writer.write("Client ${clientSocket.remoteAddress}: Average Speed = $averageSpeed kilobytes/sec")
                    transferComplete = true
                    speedJob.join()
                    if (totalBytesRead == fileSize) {
                        output.writeStringUtf8("File transfer successful\n")
                    } else {
                        output.writeStringUtf8("File transfer failed\n")
                    }
                }
            }
        } catch (e: Exception) {
            writer.write("Error: ${e.message}")
        } finally {
            clientSocket.close()
        }
    }

    private fun getLocalIPv4Address(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (networkInterface in interfaces) {
            val addresses = networkInterface.inetAddresses
            for (address in addresses) {
                if (!address.isLoopbackAddress && address is InetAddress && address.hostAddress.indexOf(':') == -1) {
                    return address.hostAddress
                }
            }
        }
        throw RuntimeException("No suitable IPv4 address found")
    }
}