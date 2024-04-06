@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib.proto.course
import kotlinx.serialization.Serializable
import zx.dkk.utils.collections.SingletonList
import zx.dkk.utils.collections.singletonListOf
import zx.dkk.utils.time.HHmm
import java.time.LocalTime

@Serializable
class Timeline(
    val start: Int,
    val end: Int,
) : Comparable<Timeline> {

    constructor(startTime: LocalTime,endTime: LocalTime):this(startTime.toSecondOfDay(),endTime.toSecondOfDay())
    val startTime:LocalTime
        get() = LocalTime.ofSecondOfDay(start.toLong())
    val endTime:LocalTime
        get() = LocalTime.ofSecondOfDay(end.toLong())
    override fun compareTo(other: Timeline): Int {
        return if (start.compareTo(other.start) == 0) end.compareTo(other.end)
        else start.compareTo(other.start)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Timeline

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start
        result = 31 * result + end
        return result
    }

    override fun toString(): String {
        return "Timeline(${startTime.HHmm}-${endTime.HHmm})"
    }

}
val 旗山校区时间表:SingletonList<Timeline> = singletonListOf<Timeline>().apply {
    var start = LocalTime.of(8, 20)
    repeat(2){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start=LocalTime.of(9,55)
    repeat(3){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start= LocalTime.of(14,0)
    repeat(4){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start= LocalTime.of(18,30)
    repeat(3){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
}
val 乌山校区时间表= singletonListOf<Timeline>().apply {
    var start = LocalTime.of(8, 0)
    repeat(2){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start=LocalTime.of(9,45)
    repeat(3){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start= LocalTime.of(14,30)
    repeat(4){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
    start= LocalTime.of(19,0)
    repeat(3){times->
        add(
            Timeline(
                start.plusMinutes(45L * times),
                start.plusMinutes((45 * times + 40).toLong())
            )
        )
    }
}