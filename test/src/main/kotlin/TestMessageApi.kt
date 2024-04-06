suspend fun testMessageApi()= testApi { db ->
    getMessageTypes()?.forEach { type ->
        getMessages(type)?.let { messages ->
            db.addMessages(username, messages) {
                if (it.isEmpty()) {
                    println("no Message append in ${type.type}")
                } else {
                    for (msg in it) {
                        println("append $msg")
                    }
                }
            }
        }
    }
    db.getMessages(username).let {
        if (it.isEmpty()) {
            println("no message in db")
        } else {
            for (msg in it) {
                println(msg)
            }
        }
    }
    db.getMessages(username, "其他服务").let {
        if (it.isEmpty()) {
            println("其他消息:no message in db")
        } else {
            for (msg in it) {
                println(msg)
            }
        }
    }
}