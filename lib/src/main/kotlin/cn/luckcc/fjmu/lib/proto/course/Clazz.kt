@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import kotlinx.serialization.Transient
import cn.luckcc.fjmu.lib.Semester
import zx.dkk.utils.collections.SingletonList
import zx.dkk.utils.format
import zx.dkk.utils.time.HHmm
import zx.dkk.utils.time.epochDay
import zx.dkk.utils.time.localizedText
import java.time.DayOfWeek

class Clazz(
    课程ID: String,
    教学班ID: String,
    教学班名称: String,
    教师ID: String,
    教室名称: String,
    /**
     * 课次，该次课是相应课程的第几次课
     */
    课次: Int,
    /**
     * 该次课的上课时间（星期几）
     */
    dayOfWeek: DayOfWeek,
    /**
     * 该次课所在的教学周
     */
    weekOfSemester: Int,
    内容: String,
    /**
     * 课程性质
     */
    课程性质: String,
    /**
     * 学时类型
     */
    学时类型: String,
    /**
     * 上课要求
     */
    上课要求: String,
    /**
     * 上课日期
     */
    epochDay: Long,
    /**
     * 该次课的开始节次
     */
    上课开始节次: Int,
    /**
     * 该次课的长度/学时数
     */
    学时: Int,
    /**
     * 备注
     */
    备注: String,
    学年: Int,
    学期: Semester,
    var refCalendarEventId: Long?,
) : IKlass {
    var 课程ID: String = 课程ID
        internal set
    var 教学班ID: String=教学班ID
        internal set
    var 教学班名称: String = 教学班名称
        internal set
    var 教师ID: String=教师ID
        internal set
    var 教室名称: String = 教室名称
        internal set

    /**
     * 课次，该次课是相应课程的第几次课
     */
    var 课次: Int = 课次
        internal set

    /**
     * 该次课的上课时间（星期几）
     */
    override var dayOfWeek: DayOfWeek = dayOfWeek
        internal set

    /**
     * 该次课所在的教学周
     */
    var weekOfSemester: Int = weekOfSemester
        internal set
    var 内容: String = 内容
        internal set

    /**
     * 课程性质
     */
    var 课程性质: String=课程性质
        internal set

    /**
     * 学时类型
     */
    var 学时类型: String = 学时类型
        internal set

    /**
     * 上课要求
     */
    var 上课要求: String = 上课要求
        internal set

    /**
     * 上课日期
     */
    var epochDay: Long = epochDay
        internal set

    /**
     * 该次课的开始节次
     */
    override var 上课开始节次: Int = 上课开始节次
        internal set

    /**
     * 该次课的长度/学时数
     */
    override var 学时: Int = 学时
        internal set

    /**
     * 备注
     */
    var 备注: String = 备注
        internal set
    var 学年: Int = 学年
        internal set
    var 学期: Semester = 学期
        internal set

    @Transient
    val date = epochDay.epochDay
    val start
        get() = 上课开始节次
    val end
        get() = 上课开始节次 + 学时 - 1

    lateinit var course: Course
    lateinit var 教学班: TeachingClass
    lateinit var 教室: Classroom
    lateinit var teacher: Teacher
    val 标题
        get() = course.课程名称
    val 学分
        get() = "学分:${course.学分}"
    val 节次
        get() = "节次:$上课开始节次${if (学时 == 1) "" else "-${上课开始节次 + 学时 - 1}"}"
    val 教师
        get() = "教师:${teacher.姓名}(${teacher.部门名称})"
    val 上课地点
        get() = "上课地点:$教室名称"
    val s课程考核方式
        get() = course.s课程考核方式
    val 学时数
        get() = "学时:$学时类型$学时(总${course.totalClassHour})"
    val 上课内容
        get() = "内容:$内容"
    val s课程类型
        get() = course.s课程类型
    var overlappedClasses: List<Clazz> = emptyList()
        internal set

    fun startTime(timelines: SingletonList<Timeline>) = timelines.getOrNull(上课开始节次 - 1)?.startTime
    fun endTime(timelines: SingletonList<Timeline>) = timelines.getOrNull(end - 1)?.endTime


    /**
     * 10:10-10:40
     */
    fun timeText(timelines: SingletonList<Timeline>): String {
        val startTime = startTime(timelines)
        val endTime = endTime(timelines)
        return if (startTime == null || endTime == null) ""
        else "${startTime.HHmm}-${endTime.HHmm}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Clazz

        if (课程ID != other.课程ID) return false
        if (教学班ID != other.教学班ID) return false
        if (教学班名称 != other.教学班名称) return false
        if (课次 != other.课次) return false
        if (epochDay != other.epochDay) return false
        if (上课开始节次 != other.上课开始节次) return false
        if (学时 != other.学时) return false
        if (学年 != other.学年) return false
        if (学期 != other.学期) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 课程ID.hashCode()
        result = 31 * result + 教学班ID.hashCode()
        result = 31 * result + 教学班名称.hashCode()
        result = 31 * result + 课次
        result = 31 * result + epochDay.hashCode()
        result = 31 * result + 上课开始节次
        result = 31 * result + 学时
        result = 31 * result + 学年
        result = 31 * result + 学期.hashCode()
        return result
    }

    override fun toString(): String {
        return "Clazz(${学年}第${学期}学期 $课程ID-$教学班名称 $课程性质 第${
            format(
                "%02d",
                课次
            )
        }次课 第${weekOfSemester}周${dayOfWeek.localizedText}${start}-$end($学时数) $教室名称 teacher=$教师ID, 内容='$内容', 上课要求='$上课要求', 备注='$备注')"
    }


}

private val String.`val`
    get() = if (isEmpty()) "" else "$this "