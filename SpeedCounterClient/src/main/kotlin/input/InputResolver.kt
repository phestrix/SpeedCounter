package input

class InputResolver(input: Array<String>) : InputParser {
    private val mapOfResults = HashMap<String, Any>()
    private fun validateInput(input: Array<String>): Boolean {
        if (input.size != 3) {
            println("Invalid input. Please provide address, port and path.")
            return false
        }
        return true
    }

    private fun checkAddress(address: String): Boolean {
        if (address.isEmpty()
            || address.length > 253
            || address.startsWith("-")
            || address.startsWith(".")
            || address.endsWith("-")
            || address.endsWith(".")
        )
            return false

        val ipv4Regex =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()
        if (ipv4Regex.matches(address)) return true
        val labels = address.split(".")
        labels.forEach { label ->
            if (label.isEmpty()
                || label.length > 63
                || !label.matches("^[a-zA-Z0-9-]*$".toRegex())
            )
                return false
        }
        return true
    }

    private fun checkPort(port: String): Boolean {
        return !(port.isEmpty() || port.toInt() !in 1..65535)
    }

    init {
        if (!validateInput(input)) {
            mapOfResults["error"] = "Invalid input. Please provide address, port and path."
        }

        checkAddress(input[0]).let {
            if (!it) mapOfResults["error"] = "Invalid address."
        }

        checkPort(input[1]).let {
            if (!it) mapOfResults["error"] = "Invalid port."
        }
        mapOfResults["address"] = input[0]
        mapOfResults["port"] = input[1].toInt()
        mapOfResults["path"] = input[2]

    }

    override fun getAddress(): String {
        return mapOfResults["address"] as String
    }

    override fun getPort(): Int {
        return mapOfResults["port"] as Int
    }

    override fun getPath(): String {
        return mapOfResults["path"] as String
    }


}