package com.company.group.job

/**
 * All Kotlin jobs that are run in ATA job system need to implement this interface.
 */
interface Job {
    /**
     * Entry point for the job. JobRunner calls this method to run the job.
     *
     * @param context holds database connections, job options etc
     */
    fun work(context: JobContext)
}
