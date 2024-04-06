@file:Suppress("NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.msg

import zx.dkk.utils.messagechain.*

const val processGetCourseData="获取课程信息"
const val processGetBookRequest="获取教材征订"
const val processGetBulletins="获取布告栏信息"
const val processGetCETResult="获取CET成绩"
const val processGetCredit="获取所有成绩"
const val processGetCreditOverview="获取成绩总览"
const val processGetFreeClassrooms="获取空教室"
const val processGetMessages="获取消息"
const val processGetRanks="获取班级排名"
const val processGetSimpleInfo="获取必要信息"
const val processGetCurrSchoolYearSemester="获取当前学年学期"
const val processGetAllAvailableSchoolYears="获取所有可用学年"
const val processGetAllUnFinishedPapers="获取所有未完成的评教"
const val processDoPaper="评教"
val processes= setOf(
    processGetCourseData, processGetBookRequest, processGetBulletins,
    processGetCETResult, processGetCredit, processGetCreditOverview, processGetCurrSchoolYearSemester,
    processGetFreeClassrooms, processGetMessages, processGetRanks, processGetSimpleInfo,
    processGetAllAvailableSchoolYears, processGetAllUnFinishedPapers, processDoPaper
)
inline fun <T>MessageSender.fail(message:String):T?{
    postMessage(message,1f)
    return null
}
inline fun <T>MessageSender.success(message: String,data:T):T{
    postMessage(message,1f)
    return data
}
inline fun registerDoPaper(receiver: MessageReceiver)=
    registerTypeReceiver(processDoPaper,receiver)
inline fun registerGetAllUnFinishedPapers(receiver: MessageReceiver)=
    registerTypeReceiver(processGetAllUnFinishedPapers,receiver)
inline fun registerGetAllAvailableSchoolYears(receiver: MessageReceiver)=
    registerTypeReceiver(processGetAllAvailableSchoolYears,receiver)
inline fun registerGetCurrSchoolYearSemester(receiver: MessageReceiver)=
    registerTypeReceiver(processGetCurrSchoolYearSemester,receiver)
inline fun registerGetScheduleClient(receiver: MessageReceiver)=
    registerTypeReceiver(processes,receiver)
inline fun registerGetCourseData(receiver: MessageReceiver)=
    registerTypeReceiver(processGetCourseData,receiver)
inline fun registerGetBookRequests(receiver: MessageReceiver)
=registerTypeReceiver(processGetBookRequest,receiver)
inline fun registerGetBulletins(receiver: MessageReceiver)
        =registerTypeReceiver(processGetBulletins,receiver)
inline fun registerGetCETResults(receiver: MessageReceiver)
        =registerTypeReceiver(processGetCETResult,receiver)
inline fun registerGetCredits(receiver: MessageReceiver)
        =registerTypeReceiver(processGetCredit,receiver)
inline fun registerGetCreditOverviews(receiver: MessageReceiver)
        =registerTypeReceiver(processGetCreditOverview,receiver)
inline fun registerGetFreeClassrooms(receiver: MessageReceiver)
        =registerTypeReceiver(processGetFreeClassrooms,receiver)
inline fun registerGetMessages(receiver: MessageReceiver)
        =registerTypeReceiver(processGetMessages,receiver)
inline fun registerGetRanks(receiver: MessageReceiver)
        =registerTypeReceiver(processGetRanks,receiver)
inline fun registerGetSimpleInfo(receiver: MessageReceiver)
        =registerTypeReceiver(processGetSimpleInfo,receiver)