package server
import kotlinx.coroutines.*
import output.Writer
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class ServerImplementation(private val port: Int, private val writer: Writer) : Server() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val serverSocket = ServerSocket(port)

    override fun start() {
        writer.write("Server started on port $port")
        while (true) {
            val clientSocket = serverSocket.accept()
            scope.launch {
                handleClient(clientSocket)
            }
        }
    }

    private suspend fun handleClient(clientSocket: Socket) {
        try {
            withContext(Dispatchers.IO) {
                DataInputStream(clientSocket.getInputStream()).use { input ->
                    DataOutputStream(clientSocket.getOutputStream()).use { output ->
                        val fileName = input.readUTF()
                        val fileSize = input.readLong()
                        val uploadsDir = File("uploads")
                        if (!uploadsDir.exists()) uploadsDir.mkdir()
                        val file = File(uploadsDir, fileName)

                        FileOutputStream(file).use { fileOutput ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            var totalBytesRead = 0L
                            val startTime = System.currentTimeMillis()
                            var lastTime = startTime

                            while (totalBytesRead < fileSize) {
                                bytesRead = input.read(buffer)
                                if (bytesRead == -1) break
                                fileOutput.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead

                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTime >= 3000) {
                                    val speed = totalBytesRead / ((currentTime - startTime) / 1000.0)
                                    writer.write("Client ${clientSocket.remoteSocketAddress}: Speed = $speed bytes/sec")
                                    lastTime = currentTime
                                }
                            }

                            val endTime = System.currentTimeMillis()
                            val averageSpeed = totalBytesRead / ((endTime - startTime) / 1000.0)
                            writer.write("Client ${clientSocket.remoteSocketAddress}: Average Speed = $averageSpeed bytes/sec")

                            if (totalBytesRead == fileSize) {
                                output.writeUTF("File transfer successful")
                            } else {
                                output.writeUTF("File transfer failed")
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            clientSocket.close()
        }
    }
}