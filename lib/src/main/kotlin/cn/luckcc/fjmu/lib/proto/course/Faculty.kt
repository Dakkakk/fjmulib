package cn.luckcc.fjmu.lib.proto.course

import zx.dkk.utils.collections.OrderedList

data class Faculty(
    val 部门名称: String,
) : Comparable<Faculty> {
    val teachers = OrderedList<Teacher>(teacherComparator)

    val courses=OrderedList<Course>(courseComparator)
    override fun compareTo(other: Faculty): Int {
        return 部门名称.compareTo(other.部门名称)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Faculty

        if (部门名称 != other.部门名称) return false

        return true
    }

    override fun hashCode(): Int {
        return 部门名称.hashCode()
    }
}
private val teacherComparator= Comparator { a: Teacher, b: Teacher ->
    a.compareTo(b)
}
private val courseComparator = Comparator { a: Course, b: Course ->
    if (a.开课学院 == b.开课学院)
        if (a.课程名称 == b.课程名称)
            a.课程ID.compareTo(b.课程ID)
        else
            a.课程名称.compareTo(b.课程名称)
    else
        a.开课学院.compareTo(b.开课学院)
}