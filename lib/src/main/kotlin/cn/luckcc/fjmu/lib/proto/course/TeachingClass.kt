@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import cn.luckcc.fjmu.lib.Semester

class TeachingClass(
    教学班ID: String,
    教学班名称: String,
    课程ID: String,
    学年: Int,
    学期: Semester,
    教学班组成: String
) : Comparable<TeachingClass> {
    lateinit var course: Course
    var 教学班ID: String = 教学班ID
        internal set
    var 教学班名称: String = 教学班名称
        internal set
    var 课程ID: String = 课程ID
        internal set
    var 学年: Int = 学年
        internal set
    var 学期: Semester = 学期
        internal set
    var 教学班组成: String = 教学班组成
        internal set

    override fun compareTo(other: TeachingClass): Int {
        return if (学年 == other.学年) {
            if (学期 == other.学期) {
                if (课程ID==other.课程ID){
                    教学班名称.compareTo(other.教学班名称)
                }else
                    课程ID.compareTo(other.课程ID)
            } else
                学期.compareTo(other.学期)
        } else
            学年.compareTo(other.学年)
    }



    override fun toString(): String {
        return "TeachingClass(${学年}第${学期}学期${课程ID} $教学班名称 $教学班组成')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TeachingClass) return false

        if (教学班名称 != other.教学班名称) return false
        if (学年 != other.学年) return false
        if (学期 != other.学期) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 教学班名称.hashCode()
        result = 31 * result + 学年
        result = 31 * result + 学期.hashCode()
        return result
    }

}
