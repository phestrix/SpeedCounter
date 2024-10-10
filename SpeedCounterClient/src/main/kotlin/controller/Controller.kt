package controller

import client.Client
import client.JavaClient
import client.KtorClient
import input.InputParser
import input.InputResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import output.SOutWriter
import output.Writer

class Controller(private val input: Array<String>) {
    private val inputParser: InputParser = InputResolver(input)
    private val outputWriter: Writer = SOutWriter()
    private val client: Client = KtorClient(inputParser.getPath(), outputWriter)

    fun run(){
        client.start(inputParser.getAddress(), inputParser.getPort())
    }
}