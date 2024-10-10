package controller

import client.Client
import client.KtorClient
import input.InputParser
import input.InputResolver
import output.SOutWriter
import output.Writer

class Controller(input: Array<String>) {
    private val inputParser: InputParser = InputResolver(input)
    private val outputWriter: Writer = SOutWriter()
    private val client: Client = KtorClient(inputParser.getPath(), outputWriter)

    fun run(){
        client.start(inputParser.getAddress(), inputParser.getPort())
    }
}