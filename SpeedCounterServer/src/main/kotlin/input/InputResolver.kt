package input

class InputResolver {
    fun resolveInput(input: Array<String>): String{
       val port = input[0]
        if (checkPort(port)) {
            println("Port is valid")
        } else {
            println("Port is invalid")
        }
        return port
    }

    private fun checkPort(port: String): Boolean {
        return !(port.isEmpty() || port.toInt() !in 1..65535)
    }
}