package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PinProject
import kotlinx.coroutines.flow.Flow

@Dao
interface PinProjectDao {
    @Query("SELECT * FROM pin_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<PinProject>>

    @Query("SELECT * FROM pin_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): PinProject?

    @Query("SELECT * FROM pin_projects ORDER BY createdAt DESC LIMIT 5")
    fun getRecentProjects(): Flow<List<PinProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: PinProject): Long

    @Update
    suspend fun updateProject(project: PinProject)

    @Query("DELETE FROM pin_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("DELETE FROM pin_projects")
    suspend fun deleteAllProjects()

    @Query("SELECT COUNT(*) FROM pin_projects")
    fun getProjectCount(): Flow<Int>
}
