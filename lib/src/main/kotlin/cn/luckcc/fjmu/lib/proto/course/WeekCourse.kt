@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import zx.dkk.utils.time.epochDay
import java.time.DayOfWeek
import java.time.LocalDate

class WeekCourse internal constructor(
    override val yearSemester: YearSemester,
    val weekOfSemester: Int,
    override val classrooms: List<Classroom>,
    override val teachers: List<Teacher>,
    override val courses: List<Course>,
    override val teachingClasses: List<TeachingClass>,
    override val faculties: List<Faculty>,
    override val clazz: List<Clazz>,
) : ICourseDataContainer {
    val startOfWeek:Long=getStartOfWeek(weekOfSemester)
    val startDayOfWeek:LocalDate
        get() = startOfWeek.epochDay
    private var freeClasses_field:List<FreeClass>?=null
    val freeClasses:List<FreeClass>
        get() {
            if (freeClasses_field==null){
                freeClasses_field=getFreeClassForWeek(weekOfSemester, clazz)
            }
            return freeClasses_field?: emptyList()
        }
    override fun toString(): String {
        return "WeekCourse(${yearSemester.value}第${weekOfSemester}周, classrooms=${classrooms.size}, teachers=${teachers.size}, courses=${courses.size}, teachingClasses=${teachingClasses.size}, faculties=${faculties.size}, clazz=${clazz.size}))"
    }

    fun atDay(dayOfWeek: DayOfWeek): DayCourse {
        val clazz = clazz.filter { it.dayOfWeek == dayOfWeek }
        val teachers = clazz.map {
            it.teacher
        }.distinct()
        val courses = clazz.map { it.course }.distinct()
        val teachingClasses = clazz.map { it.教学班 }.distinct()
        val classrooms = clazz.map { it.教室 }.distinct()
        val faculties = (courses.map {
            it.部门
        } + teachers.map { it.部门 }).distinct()
        return DayCourse(
            yearSemester,
            weekOfSemester,
            dayOfWeek,
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz,
        )
    }
}

