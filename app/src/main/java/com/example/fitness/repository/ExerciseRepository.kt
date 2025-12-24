//package com.example.fitness.repository
//
//import com.example.fitness.dao.ExerciseDao
//import com.example.fitness.entity.Exercise
//
//class ExerciseRepository(private val exerciseDao: ExerciseDao) {
//
//    suspend fun getAllExercises(): List<Exercise> = exerciseDao.getAllExercises()
//
//    suspend fun insert(exercise: Exercise) = exerciseDao.insert(exercise)
//
//    suspend fun update(exercise: Exercise) = exerciseDao.update(exercise)
//
//    suspend fun delete(exercise: Exercise) = exerciseDao.delete(exercise)
//
//    suspend fun getExercisesByGroup(groupName: String): List<Exercise> =
//        exerciseDao.getExercisesByGroup(groupName)
//
//}
//
