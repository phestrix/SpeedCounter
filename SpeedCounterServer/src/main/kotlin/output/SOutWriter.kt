package output

import java.io.PrintStream

class SOutWriter : Writer {
    private var outputStream = PrintStream(System.out)

    override fun write(message: String) {
        outputStream.println(message)
    }

    override fun setOutputStream(outputStream: Any) {
        if(outputStream is PrintStream){
            this.outputStream = outputStream
        }
        else{
            throw IllegalArgumentException("Invalid output stream")
        }
    }

    override fun getOutputStream(): Any {
        return outputStream
    }
}