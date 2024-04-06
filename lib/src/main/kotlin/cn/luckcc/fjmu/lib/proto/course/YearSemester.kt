package cn.luckcc.fjmu.lib.proto.course

import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Semester
import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.localizedText
import java.time.LocalDate

@Serializable
class YearSemester(
    override val schoolYear: Int,
    override val semester: Semester,
    val start: Long,
): ISemesterPair {
    val startDay:LocalDate
        get() {
            require(start>0){
                "Cannot get startDay from a YearSemester without property 'start' initialized"
            }
            return start.epochDay
        }
    fun getStartOfWeek(weekOfSemester:Int):Long{
        return start+7*(weekOfSemester-1)
    }
    inline fun getStartDayOfWeek(weekOfSemester: Int):LocalDate{
        return getStartOfWeek(weekOfSemester).epochDay
    }
    fun getWeekOfSemester(date: LocalDate): Int {
        val epochDay=date.epochDay
        return if (epochDay>=start){
            (epochDay-start)/7
        }else{
            -((start-epochDay+6)/7)
        }.toInt()
    }
    inline val simplePair: SemesterPair
        get() = SemesterPair(schoolYear, semester)
    operator fun component3()=startDay
    override fun toString(): String {
        return "YearSemester($schoolYear-$semester,start:${startDay.localizedText})"
    }
}
