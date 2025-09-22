package com.castlecoach.app.data

import androidx.room.*
import androidx.room.TypeConverter
import kotlinx.datetime.*

@Database(entities = [PlanItemEntity::class, CompletionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun planDao(): PlanDao
}

class Converters {
    @TypeConverter fun localDateToString(v: LocalDate?): String? = v?.toString()
    @TypeConverter fun stringToLocalDate(v: String?): LocalDate? = v?.let { LocalDate.parse(it) }

    @TypeConverter fun localDateTimeToString(v: LocalDateTime?): String? = v?.toString()
    @TypeConverter fun stringToLocalDateTime(v: String?): LocalDateTime? = v?.let { LocalDateTime.parse(it) }

    @TypeConverter fun itemTypeToString(v: ItemType?): String? = v?.name
    @TypeConverter fun stringToItemType(v: String?): ItemType? = v?.let { ItemType.valueOf(it) }
}
