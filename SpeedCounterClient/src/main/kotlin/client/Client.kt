package client

interface Client {
    fun start(address: String, port: Int)
}