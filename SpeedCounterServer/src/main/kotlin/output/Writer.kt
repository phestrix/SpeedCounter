package output

interface Writer {
    fun write(message: String)
    fun setOutputStream(outputStream: Any)
    fun getOutputStream(): Any
}