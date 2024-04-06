package cn.luckcc.fjmu.lib.utils

import cn.luckcc.fjmu.lib.proto.course.*

internal fun mappingData(
    classrooms: List<Classroom>,
    teachers: List<Teacher>,
    courses: List<Course>,
    teachingClasses: List<TeachingClass>,
    faculties: List<Faculty>,
    clazz: List<Clazz>,
){
    courses.forEach { course ->
        clazz.asSequence()
            .filter { clazz ->
                clazz.课程ID == course.课程ID
            }.forEach { clazz ->
                clazz.course = course
                course.clazzes.add(clazz)
            }
        teachingClasses.asSequence()
            .filter { teachingClass ->
                teachingClass.课程ID == course.课程ID
            }.forEach { teachingClass ->
                teachingClass.course = course
            }
    }
    teachingClasses.forEach { teachingClass ->
        clazz.asSequence()
            .filter { clazz ->
                clazz.教学班名称 == teachingClass.教学班名称
            }.forEach { clazz ->
                clazz.教学班 = teachingClass
            }
    }
    faculties.forEach { faculty ->
        courses.asSequence()
            .filter { course ->
                course.开课学院 == faculty.部门名称
            }.forEach { course ->
                course.部门 = faculty
                faculty.courses.add(course)
            }
        teachers.asSequence()
            .filter { teacher ->
                teacher.部门名称 == faculty.部门名称
            }.forEach { teacher ->
                teacher.部门 = faculty
                faculty.teachers.add(teacher)
            }
    }
    classrooms.forEach { classroom ->
        clazz.asSequence()
            .filter { clazz ->
                clazz.教室名称 == classroom.教室名称
            }.forEach { clazz ->
                clazz.教室 = classroom
            }
    }
    teachers.forEach { teacher ->
        clazz.asSequence()
            .filter { clazz ->
                clazz.教师ID == teacher.教工号
            }.forEach { clazz ->
                clazz.teacher = teacher
            }
    }
}