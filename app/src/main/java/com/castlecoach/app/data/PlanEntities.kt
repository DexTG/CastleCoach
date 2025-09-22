package com.castlecoach.app.data

import androidx.room.*
import kotlinx.datetime.*

enum class ItemType {
    MEAL, SNACK, HYDRATE, WORKOUT, WALK // <- add WALK if you use it
}

@Entity(tableName = "plan_items",
    indices = [Index(value = ["date", "timeMinutes"])]
)
data class PlanItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,            // e.g., today
    val timeMinutes: Int,           // minutes from 00:00 for sorting (e.g., 6*60 = 360 for 06:00)
    val type: ItemType,
    val title: String,
    val details: String? = null,
)

@Entity(
    tableName = "completions",
    primaryKeys = ["planItemId"],
    foreignKeys = [
        ForeignKey(
            entity = PlanItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["planItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planItemId")]
)
data class CompletionEntity(
    val planItemId: Long,
    val completedAt: LocalDateTime,
)

data class PlanItemWithDone(
    @Embedded val item: PlanItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planItemId",
        entity = CompletionEntity::class
    )
    val completion: CompletionEntity?
)

@Dao
interface PlanDao {
    @Transaction
    @Query("SELECT * FROM plan_items WHERE date = :date ORDER BY timeMinutes ASC")
    suspend fun getForDate(date: LocalDate): List<PlanItemWithDone>

    @Insert
    suspend fun insertAll(items: List<PlanItemEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(done: CompletionEntity)

    @Query("DELETE FROM plan_items WHERE date = :date")
    suspend fun clearForDate(date: LocalDate)

    @Query("DELETE FROM completions WHERE planItemId = :planItemId")
    suspend fun clearCompletion(planItemId: Long)
}
