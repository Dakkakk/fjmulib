suspend fun testCetResultApi()= testApi { db ->
    getCETResults()?.let { download ->
        db.addCetResults(username, download) {
            if (it.isEmpty()) {
                println("no updates cetResult")
            } else {
                for (result in it) {
                    println("update->$result")
                }
            }
        }
    }
}