package cn.luckcc.fjmu.lib.proto.course

import kotlinx.serialization.Serializable
import cn.luckcc.fjmu.lib.Semester

@Serializable
class SemesterPair(
    override val schoolYear:Int, override val semester: Semester,
): ISemesterPair {


    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SemesterPair) return false

        if (schoolYear != other.schoolYear) return false
        if (semester != other.semester) return false

        return true
    }

    override fun hashCode(): Int {
        var result = schoolYear
        result = 31 * result + semester.hashCode()
        return result
    }

}