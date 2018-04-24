package pl.grzeslowski.colibra.server

interface NewChannelListener {
    fun onNewChannel(channel: ColibraChannel)
}

interface NewMessageListener {
    fun onNewMessage(clientMessage: ClientMessage, channel: ColibraChannel): Boolean
}

interface TimeoutListener {
    fun timeout(channel: ColibraChannel)
}