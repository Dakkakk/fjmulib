@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

class Teacher(
    教工号: String,
    姓名: String,
    部门名称: String,
    机构: String,
) : Comparable<Teacher> {
    var 教工号: String = 教工号
        internal set
    var 姓名: String = 姓名
        internal set
    var 部门名称: String = 部门名称
        internal set
    var 机构: String = 机构
        internal set

    lateinit var 部门: Faculty
    override fun compareTo(other: Teacher): Int {
        return if (部门名称 == other.部门名称)
            教工号.compareTo(other.教工号)
        else
            部门名称.compareTo(other.部门名称)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Teacher) return false

        if (教工号 != other.教工号) return false
        if (部门名称 != other.部门名称) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 教工号.hashCode()
        result = 31 * result + 部门名称.hashCode()
        return result
    }

    override fun toString(): String {
        return "Teacher(${部门名称}/${机构} ${教工号}${姓名})"
    }

}
