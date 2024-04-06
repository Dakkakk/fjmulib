@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course

import cn.luckcc.fjmu.lib.Campus
class Classroom(教室名称: String, 教室类型: String, 教室ID: String, 教学楼: String, 校区: Campus) :Comparable<Classroom>{

    var 教室名称:String = 教室名称
        internal set
    var 教室类型:String = 教室类型
        internal set
    var 教室ID:String = 教室ID
        internal set
    var 教学楼:String = 教学楼
        internal set
    var 校区: Campus = 校区
        internal set

    override fun compareTo(other: Classroom): Int {
        return if (校区==other.校区){
            if (教学楼==other.教学楼){
                教室名称.compareTo(other.教室名称)
            }else
                教学楼.compareTo(other.教学楼)
        }else
            校区.compareTo(other.校区)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Classroom

        if (教室名称 != other.教室名称) return false
        if (教学楼 != other.教学楼) return false
        if (校区 != other.校区) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 教室名称.hashCode()
        result = 31 * result + 教学楼.hashCode()
        result = 31 * result + 校区.hashCode()
        return result
    }

    override fun toString(): String {
        return "Classroom(${校区}${教学楼}${教室名称}($教室ID),类型=${教室类型})"
    }

}
