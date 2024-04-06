import cn.luckcc.fjmu.lib.Semester
import cn.luckcc.fjmu.lib.def.username
import cn.luckcc.fjmu.lib.proto.course.SemesterPair
import java.time.DayOfWeek

suspend fun testAdditionalCourseApi(){
    val username= username
    val semester= SemesterPair(2022, Semester.二)
    val semesterCourse= db.getCourseData(username,semester)
    val weekCourse= db.getCourseData(username, semester,5)
    val dayCourse= db.getCourseData(username, semester,5,DayOfWeek.MONDAY)
    val semesters= db.getAllSemesters(username)
    weekCourse.freeClasses
    val semesterCourseInfo= db.getAllCourseInfo(username,semester)
    val allCourseInfo= db.getAllCourseInfo(username)
    val course= db.getCourse(username,semester,"0550054")
    println("$semesterCourse$weekCourse$dayCourse$semesters$semesterCourseInfo$allCourseInfo$course")
}
suspend fun testCourseApi() = testApi { db ->
    getAllAvailableSchoolYearsAndCurrentValue()?.let {
        for (a in it.second) {
            println(a)
        }
    }
    val semester = SemesterPair(2022, Semester.二)
    val teachingClasses = getTeachingClasses(semester, useRecommendTable = false)
    if (teachingClasses == null) {
        println("error get teachingclass")
        return@testApi
    }
    getCoursesForTeachingClasses(
        semester,
        teachingClasses,
        needConnectJWGL = false
    ) { index, courseId ->
        println("${courseId}->$index")
        index
    }?.let { courseData ->
        db.addCourseDataFor(username, courseData) { removes, appends ->
            if (removes.isEmpty()) {
                println("nothing removed")
            } else for (remove in removes) {
                println(remove)
            }
            if (appends.isEmpty()) {
                println("nothing appended")
            } else for (append in appends) {
                println(append)
            }
        }
//        db.testGetCourse(username, 2022, Semester.二)
//        db.testGetCourse(username, 2022, Semester.二, 1)
    }
}

