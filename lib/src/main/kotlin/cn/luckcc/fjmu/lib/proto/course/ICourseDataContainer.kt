package cn.luckcc.fjmu.lib.proto.course

import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.utils.classesPerDay
import zx.dkk.utils.time.epochDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

interface ICourseDataContainer{
    val classrooms: List<Classroom>
    val teachers: List<Teacher>
    val courses: List<Course>
    val teachingClasses: List<TeachingClass>
    val faculties: List<Faculty>
    val clazz: List<Clazz>
    val isEmpty
        get() = clazz.isEmpty()
    val yearSemester: YearSemester
    val schoolYear:Int
        get() = yearSemester.schoolYear
    val semester: Semester
        get() = yearSemester.semester
    val startOfSemester:Long
        get() = yearSemester.start
    fun getStartOfWeek(weekOfSemester: Int):Long{
        return yearSemester.start+7*(weekOfSemester-1)
    }
    fun getDay(weekOfSemester: Int,dayOfWeek: DayOfWeek):Long{
        return yearSemester.start+7*(weekOfSemester-1)+dayOfWeek.value-1
    }
    val startDayOfSemester:LocalDate
        get() = yearSemester.start.epochDay
    fun getFreeClassForWeek(
        weekOfSemester: Int,
        clazz: List<Clazz>,
        tmp: Array<MutableList<Clazz>?> = Array(classesPerDay){null}
    ):List<FreeClass>{
        val list= arrayListOf<FreeClass>()
        val clazzSeperated=clazz.groupBy {
            it.dayOfWeek
        }
        DayOfWeek.values().forEach { dayOfWeek ->
            Arrays.setAll(tmp){null}
            list.addAll(getFreeClassForDay(weekOfSemester, dayOfWeek, clazzSeperated[dayOfWeek]?: emptyList(), tmp))
        }
        return list
    }
    fun getFreeClassForDay(
        weekOfSemester:Int,
        dayOfWeek: DayOfWeek,
        clazz:List<Clazz>,
        tmp:Array<MutableList<Clazz>?> = Array(classesPerDay){null}
    ):List<FreeClass>{
        for (item in clazz){
            val start=item.start.coerceAtLeast(1)-1
            val end=item.end.coerceAtMost(classesPerDay)-1
            for (index in start..end){
                val list= tmp[index]?:arrayListOf()
                list.add(item)
                tmp[index]=list
            }
        }
        val freeClasses= arrayListOf<FreeClass>()
        for (index in tmp.indices){
            val list=tmp[index]
            if (list==null){
                freeClasses.add(FreeClass(dayOfWeek,index+1,weekOfSemester))
            }else{
                list.forEach {
                    it.overlappedClasses=(list+it.overlappedClasses).distinct()
                }
            }
        }
        return freeClasses
    }
}