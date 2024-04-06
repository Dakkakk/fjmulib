@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.localizedText
import java.time.DayOfWeek
import java.time.LocalDate

class DayCourse internal constructor(
    override val yearSemester: YearSemester,
    val weekOfSemester: Int,
    val dayOfWeek: DayOfWeek,
    override val classrooms: List<Classroom>,
    override val teachers: List<Teacher>,
    override val courses: List<Course>,
    override val teachingClasses: List<TeachingClass>,
    override val faculties: List<Faculty>,
    override val clazz: List<Clazz>,
) : ICourseDataContainer {
    val dayValue:Long=getDay(weekOfSemester, dayOfWeek)
    val day:LocalDate
        get() = dayValue.epochDay
    private var freeClasses_field:List<FreeClass>?=null
    val freeClasses:List<FreeClass>
        get() {
            if (freeClasses_field==null){
                freeClasses_field=getFreeClassForDay(weekOfSemester, dayOfWeek, clazz)
            }
            return freeClasses_field?: emptyList()
        }
    override fun toString(): String {
        return "WeekCourse(${yearSemester.value}学期第${weekOfSemester}周${dayOfWeek.localizedText}, classrooms=${classrooms.size}, teachers=${teachers.size}, courses=${courses.size}, teachingClasses=${teachingClasses.size}, faculties=${faculties.size}, clazz=${clazz.size}))"
    }
}