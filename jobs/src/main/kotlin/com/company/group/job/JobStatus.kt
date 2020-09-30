package com.company.group.job

enum class JobStatus(val dbValue: String) {
    COMPLETED("completed"),
    FAILED("failed"),
    RUNNING("running");

    companion object {
        fun fromDbValue(value: String): JobStatus {
            return when (value.toLowerCase()) {
                COMPLETED.dbValue -> COMPLETED
                RUNNING.dbValue -> RUNNING
                else -> FAILED
            }
        }
    }
}
