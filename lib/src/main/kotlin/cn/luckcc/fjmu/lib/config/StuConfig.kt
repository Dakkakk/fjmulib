package cn.luckcc.fjmu.lib.config

import cn.luckcc.fjmu.lib.proto.course.ISemesterPair
import cn.luckcc.fjmu.lib.proto.course.YearSemester

interface StuConfig {
    var username:String
    var password:String
    var rememberMe:Boolean
    var deviceId:String
    var initialSemesters:Pair<ISemesterPair,List<Int>>
    var initialSemester:YearSemester

}