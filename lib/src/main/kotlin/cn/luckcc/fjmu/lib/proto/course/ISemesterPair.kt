package cn.luckcc.fjmu.lib.proto.course

import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Semester
@Serializable
sealed interface ISemesterPair:Comparable<ISemesterPair>{
    val schoolYear:Int
    val semester: Semester
    override fun compareTo(other: ISemesterPair): Int {
        return compareTo(other.schoolYear,other.semester)
    }
    fun compareTo(schoolYear: Int,semester: Semester):Int{
        return  if (this.schoolYear==schoolYear){
            this.semester.compareTo(semester)
        }else
            this.schoolYear.compareTo(schoolYear)
    }
    infix fun eq(other: ISemesterPair):Boolean{
        return this.semester==other.semester&&this.schoolYear==other.schoolYear
    }
    val value:String
        get() = "${schoolYear}学年第${semester}学期"
    operator fun component1()=schoolYear
    operator fun component2()=semester
    fun toYearSemester(start:Long)= YearSemester(schoolYear, semester, start)
}