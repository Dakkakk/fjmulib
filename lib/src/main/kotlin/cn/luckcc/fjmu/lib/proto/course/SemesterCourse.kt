@file:Suppress("NonAsciiCharacters", "NOTHING_TO_INLINE")

package cn.luckcc.fjmu.lib.proto.course

import cn.luckcc.fjmu.lib.Semester
import java.time.DayOfWeek

var dayClassCount = 12
var weeksEachSemester = 20

class SemesterCourse internal constructor(
    override val yearSemester: YearSemester,
    override val classrooms: List<Classroom>,
    override val teachers: List<Teacher>,
    override val courses: List<Course>,
    override val teachingClasses: List<TeachingClass>,
    override val faculties: List<Faculty>,
    override val clazz: List<Clazz>,
) : ICourseDataContainer {

    internal inline fun mappingData() =
        cn.luckcc.fjmu.lib.utils.mappingData(
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz
        )

    final fun filterNotInclude(
        教学班组成: Set<String>
    ): SemesterCourse {
        val teachingClasses = teachingClasses.filterNot {
            it.教学班组成 in 教学班组成
        }
        val courses = teachingClasses.map {
            it.course
        }.distinct()
        val clazz = clazz.filter {
            it.教学班 in teachingClasses
        }
        val teachers = clazz.map {
            it.teacher
        }.distinct()
        val faculties = (courses.map {
            it.部门
        } + teachers.map { it.部门 }).distinct()
        val classrooms = clazz.map { it.教室 }.distinct()
        return SemesterCourse(
            yearSemester, classrooms, teachers, courses, teachingClasses, faculties, clazz
        )
    }

    fun atWeekOfSemester(weekOfSemester: Int): WeekCourse {
        val clazz = clazz.filter { it.weekOfSemester == weekOfSemester }
        val teachers = clazz.map {
            it.teacher
        }.distinct()
        val courses = clazz.map { it.course }.distinct()
        val teachingClasses = clazz.map { it.教学班 }.distinct()
        val classrooms = clazz.map { it.教室 }.distinct()
        val faculties = (courses.map {
            it.部门
        } + teachers.map { it.部门 }).distinct()
        return WeekCourse(
            yearSemester,
            weekOfSemester,
            classrooms,
            teachers,
            courses,
            teachingClasses,
            faculties,
            clazz,
        )
    }


    fun atDay(weekOfSemester: Int, dayOfWeek: DayOfWeek): DayCourse {
        val clazz = clazz.filter { it.weekOfSemester == weekOfSemester && it.dayOfWeek == dayOfWeek }
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



    override fun toString(): String {
        return "SemesterCourse(${yearSemester.value}, classrooms=${classrooms.size}, teachers=${teachers.size}, courses=${courses.size}, teachingClasses=${teachingClasses.size}, faculties=${faculties.size}, clazz=${clazz.size})"
    }

    companion object {
        val EmptyData = SemesterCourse(
            YearSemester(0, Semester.一,0), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            emptyList()
        )
    }
}