import okhttp3.internal.format

suspend fun testCreditOverviewApi()= testApi {
    getCreditOverview()?.let { creditOverviews ->
        it.addCreditOverviews(username, creditOverviews) { changes ->
            if (changes.isEmpty()) {
                println("nothing changed in creditOverview")
            } else {
                for ((绩点分类名称, oldCredit, newCredit) in changes) {
                    println(format("$绩点分类名称 %.3f->%.3f", oldCredit, newCredit))
                }
            }
        }
    }
}