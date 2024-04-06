@file:Suppress("NonAsciiCharacters")

package cn.luckcc.fjmu.lib

enum class Semester(val `val`:String,val actualIndex:Int){
    一("3",1),
    二("12",2),
    三("16",3);

    companion object{
        fun ofVal(semester:String): Semester {
            if (semester== 一.`val`)
                return 一
            else if (semester== 二.`val`)
                return 二
            return 三
        }
        fun ofActualIndex(index: Int): Semester {
            return when(index){
                1-> 一
                2-> 二
                3-> 三
                else -> 一
            }
        }
    }
}