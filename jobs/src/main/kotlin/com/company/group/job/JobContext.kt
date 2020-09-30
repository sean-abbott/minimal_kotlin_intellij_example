package com.company.group.job

import me.liuwj.ktorm.database.Database

import com.company.group.database.DatabaseType
import java.time.LocalDate

const val DATA_DATE = "data_date"
const val CLASSNAME = "classname"

data class JobContext(
        val options: Map<String, Any>,
        val databases: Map<DatabaseType, () -> Database>) {

    fun ataDb(): Database = (databases[DatabaseType.ATA] ?: error("ATA database not initialized")).invoke()
    fun snowflake(): Database = (databases[DatabaseType.SNOWFLAKE] ?: error("Snowflake not initialized")).invoke()

    fun dataDate(): LocalDate {
        return options[DATA_DATE]?.let {
            LocalDate.parse(it as String)
        } ?: LocalDate.now()
    }

    fun entryDate(dateOffset: Long = 1): LocalDate {
        if (dateOffset < 1) {
            throw IllegalArgumentException("Date offset can't be less than 1")
        }
        return dataDate().minusDays(dateOffset)
    }

    fun job(): Job {
        val job = options[CLASSNAME]?.let {
            Class.forName(it as String)?.getDeclaredConstructor()?.newInstance() as? Job
        }
        return checkNotNull(job) { "Unable to find job to run. Please make sure class name is set correctly" }
    }
}
