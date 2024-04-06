package cn.luckcc.fjmu.lib.proto.course

import java.time.DayOfWeek

interface IKlass:Comparable<IKlass>{
    val dayOfWeek: DayOfWeek
    val 上课开始节次: Int
    val 学时: Int
    override fun compareTo(other: IKlass): Int {
        return if (dayOfWeek.compareTo(other.dayOfWeek) == 0) {
            if (上课开始节次.compareTo(other.上课开始节次) == 0) {
                return 学时.compareTo(other.学时)
            } else 上课开始节次.compareTo(other.上课开始节次)
        } else dayOfWeek.compareTo(other.dayOfWeek)
    }
}
val iKlassComparator= Comparator { a: IKlass, b: IKlass ->
    a.compareTo(b)
}
data class FreeClass(
    override val dayOfWeek: DayOfWeek,
    override val 上课开始节次: Int,
    val weekOfSemester:Int
): IKlass {
    override val 学时: Int=1
}
