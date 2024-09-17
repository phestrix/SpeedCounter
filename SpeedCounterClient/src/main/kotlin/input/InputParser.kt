package input

interface InputParser {
    fun getAddress(): String
    fun getPort(): Int
    fun getPath(): String
}