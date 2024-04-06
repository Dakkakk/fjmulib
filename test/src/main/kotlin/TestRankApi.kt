import okhttp3.internal.format

suspend fun testRankApi()= testApi {
    getRank()?.let { ranks ->
        it.addRanks(username, ranks) { changeList, selfChange ->
            if (changeList.isEmpty()) {
                println("no credit change")
            } else {
                changeList.forEach { (index, old, new) ->
                    println(format("%2d:$old->$new", index + 1))
                }
            }
            if (selfChange == null) {
                println("no self change")
            } else {
                val (old, new) = selfChange
                val (oldRank, oldCredit) = old
                val (newRank, newCredit) = new
                println(format("self %2d(%.5f)->%2d(%.5f)", oldRank, oldCredit, newRank, newCredit))
            }
        }
    }
}