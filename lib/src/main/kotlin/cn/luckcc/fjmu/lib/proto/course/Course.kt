@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import kotlinx.serialization.Transient
import cn.luckcc.fjmu.lib.Semester
import zx.dkk.utils.collections.NONE
import zx.dkk.utils.collections.OrderedList
import zx.dkk.utils.format

class Course(
    课程ID: String,
    课程名称: String,
    assessment: String,
    examination: String,
    examinationMethod: String,
    type: String,
    课程类型: String,
    课程性质: String,
    type2: String,
    开课学院: String,
    学分: String,
    学年: Int,
    学期: Semester,
    index: Int = 0
) : Comparable<Course> {
    var 课程ID = 课程ID
        internal set
    var 课程名称 = 课程名称
        internal set
    var assessment = assessment
        internal set
    var examination = examination
        internal set
    var examinationMethod = examinationMethod
        internal set
    var type = type
        internal set
    var 课程类型 = 课程类型
        internal set
    var 课程性质 = 课程性质
        internal set
    var type2 = type2
        internal set
    var 开课学院 = 开课学院
        internal set
    var 学分 = 学分
        internal set
    var 学年 = 学年
        internal set
    var 学期 = 学期
        internal set
    var index: Int = index
        internal set
    val clazzes = OrderedList<Clazz>(clazzComparator)
    lateinit var 部门: Faculty

    inline val s课程考核方式:String
        get() {
            val sb=StringBuilder()
            if (assessment!=""&&assessment!= NONE)
                sb.append(assessment)
            if (examination!=""&&examination!= NONE)
                sb.append(" $examination")
            if (examinationMethod!=""&&examinationMethod!= NONE)
                sb.append("($examinationMethod)")
            if (sb.isEmpty())
                sb.append("无")
            return sb.toString()
        }
    inline val s课程类型:String
        get() {
            val sb=StringBuilder()
            sb.append(type)
            if (课程类型!=type)
                sb.append(" $课程类型")
            if (type2!= NONE&&type2!="")
                sb.append(" $type2")
            sb.append(" $课程性质")
            return sb.toString()
        }

    @Transient
    var 学时分类: List<Pair<String, Int>> = emptyList()
        internal set
    inline val totalClassHour: Int
        get() {
            return 学时分类.sumOf {
                it.second
            }
        }

    override fun compareTo(other: Course): Int {
        return if (学年 == other.学年) {
            if (学期 == other.学期) {
                课程ID.compareTo(other.课程ID)
            } else
                学期.compareTo(other.学期)
        } else
            学年.compareTo(other.学年)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Course

        if (课程ID != other.课程ID) return false
        if (学年 != other.学年) return false
        if (学期 != other.学期) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 课程ID.hashCode()
        result = 31 * result + 学年
        result = 31 * result + 学期.hashCode()
        return result
    }

    override fun toString(): String {
        return "Course(${学年}第${学期}学期${
            format(
                "%03d",
                index
            )
        } $开课学院-$课程名称($课程ID)${学分}分, $assessment($examination/$examinationMethod), type='$type', 课程类型='$课程类型', 课程性质='$课程性质', type2='$type2',学时分类=$学时分类)"
    }


}

val clazzComparator = Comparator { a: Clazz, b: Clazz ->
    with(a) {
        if (a.教学班名称==b.教学班名称){
            if (学年 == b.学年) {
                if (学期 == b.学期) {
                    if (weekOfSemester == b.weekOfSemester) {
                        if (dayOfWeek == b.dayOfWeek) {
                            if (上课开始节次 == b.上课开始节次) {
                                if (学时 == b.学时) {
                                    课程ID.compareTo(b.课程ID)
                                } else 学时.compareTo(b.学时)
                            } else 上课开始节次.compareTo(b.上课开始节次)
                        } else
                            dayOfWeek.compareTo(b.dayOfWeek)
                    } else weekOfSemester.compareTo(b.weekOfSemester)
                } else
                    学期.compareTo(b.学期)
            } else
                学年.compareTo(b.学年)
        }else
            a.教学班名称.compareTo(b.教学班名称)
    }
}