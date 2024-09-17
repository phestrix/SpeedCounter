package client

import output.Writer
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Socket

class ClientImplementation(private val filepath: String, private val writer: Writer) : Client {
    private val file = File(filepath)
    private val sizeOfFile = file.length()


    override fun start(address: String, port: Int) {
        try {
            Socket(address, port).use { socket ->
                DataOutputStream(socket.getOutputStream()).use { output ->
                    DataInputStream(socket.getInputStream()).use { input ->
                        // Send file name
                        output.writeUTF(file.name)
                        // Send file size
                        output.writeLong(sizeOfFile)
                        // Send file content
                        FileInputStream(file).use { fileInput ->
                            val buffer = ByteArray(4096)
                            var bytesRead: Int
                            while (fileInput.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                        output.flush()

                        // Read server response
                        val response = input.readUTF()
                        writer.write("Server response: $response");
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}