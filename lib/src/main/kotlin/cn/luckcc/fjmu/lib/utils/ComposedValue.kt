package cn.luckcc.fjmu.lib.utils

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.min

const val classesPerDay=12//todo editable

val Int.cv: ComposedValue
    get() = ComposedValue(this)
fun composeValue(week: Int, dayOfWeek: DayOfWeek, index: Int)
        = composeValue((week - 1) * 7 + dayOfWeek.value ,index)

/**
 * @param dayOfSemester 1..n
 * @param index 1..classesPerDay
 */
fun composeValue(dayOfSemester:Int,index:Int): ComposedValue {
    return ComposedValue((dayOfSemester-1) * 12 + min(index, classesPerDay) - 1)
}
@JvmInline
value class ComposedValue(
    val composedValue:Int
):Comparable<ComposedValue>{
    val day
        get() = composedValue/ classesPerDay +1
    val week
        get() = (day-1)/7+1
    val dayOfWeek: DayOfWeek
        get() {
            val d=day%7
            return if (d==0) DayOfWeek.SUNDAY else DayOfWeek.of(d)
        }
    val index
        get() = composedValue% classesPerDay +1

    operator fun plus(other: ComposedValue) =
        ComposedValue(composedValue = this.composedValue + other.composedValue)

    operator fun plus(other:Int)= ComposedValue(this.composedValue+other)

    /**
     * Subtract a ComposedValue from another one.
     */
    operator fun minus(other: ComposedValue) =
        ComposedValue(composedValue = this.composedValue - other.composedValue)

    operator fun minus(other: Int) =
        ComposedValue(composedValue = this.composedValue - other)

    /**
     * This is the same as multiplying the ComposedValue by -1.0.
     */
    operator fun unaryMinus() = ComposedValue(-composedValue)

    /**
     * Support comparing Dimensions with comparison operators.
     */
    override operator fun compareTo(other: ComposedValue) = composedValue.compareTo(other.composedValue)
    operator fun rangeTo(composedValue: ComposedValue)
            = ComposedValueRange(this,composedValue)

    operator fun rangeTo(composedValue: Int)
            = ComposedValueRange(this, ComposedValue(composedValue))
    override fun toString() = "第${week}周${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}第${index}节"
}

class ComposedValueIterator(
    start: ComposedValue,
    private val endInclusive: ComposedValue,
    private val step:Int,
): Iterator<ComposedValue>{
    private var hasNext: Boolean = if (step > 0) start <= endInclusive else start >= endInclusive
    private var next: ComposedValue = if (hasNext) start else endInclusive
    override fun hasNext(): Boolean =hasNext

    override fun next(): ComposedValue {
        val value = next
        if (value == endInclusive) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        }
        else {
            next += step
        }
        return value
    }

}
class ComposedValueRange(
    override val start: ComposedValue,
    override val endInclusive: ComposedValue
) : Iterable<ComposedValue>, ClosedRange<ComposedValue> {

    override fun contains(value: ComposedValue): Boolean {
        return this.start <= value && value <= endInclusive
    }

    /**
     * Checks whether the range is empty.
     *
     * The range is empty if its start value is greater than the end value.
     */
    override fun isEmpty(): Boolean = start > endInclusive

    override fun equals(other: Any?): Boolean =
        other is ComposedValueRange && (isEmpty() && other.isEmpty() ||
                start == other.start && endInclusive == other.endInclusive)

    override fun iterator(): Iterator<ComposedValue> = ComposedValueIterator(start,endInclusive,1)

    override fun toString(): String = "$start..$endInclusive"


    override fun hashCode(): Int {
        if (isEmpty())
            return -1
        var result = start.hashCode()
        result = 31 * result + endInclusive.hashCode()
        return result
    }

    companion object {
        /** An empty range of values of type Int. */
        val EMPTY: ComposedValueRange = ComposedValueRange(1.cv, 0.cv)
    }
}