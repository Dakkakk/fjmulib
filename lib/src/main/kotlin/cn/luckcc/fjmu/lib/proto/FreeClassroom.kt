package cn.luckcc.fjmu.lib.proto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import zx.dkk.utils.collections.NONE

@Serializable
data class FreeClassroom(
    @SerialName("CDLB")
    var type: String = NONE,
    @SerialName("JSBH")
    var id: String = NONE,
    @SerialName("JSMC")
    var name: String = NONE,
    @SerialName("JXLMC")
    var building: String = NONE,
    @SerialName("XQDM")
    var unknownField: String = NONE,
    @SerialName("XQMC")
    var campus: String = NONE
): Comparable<FreeClassroom>{
//    override var isCustom: Boolean=false
    override fun compareTo(other: FreeClassroom): Int {
        return if (campus==other.campus)
            id.compareTo(other.id)
        else
            campus.compareTo(other.campus)
    }
    override fun toString(): String {
        return "Classroom(id='$id', name='$name', type='$type', campus='$campus')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FreeClassroom

        if (id != other.id) return false
        if (campus != other.campus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + campus.hashCode()
        return result
    }

}



