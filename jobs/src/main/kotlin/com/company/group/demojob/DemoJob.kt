package com.company.group.demojob

import com.company.group.demolib.Greeter
import com.company.group.job.Job
import com.company.group.job.JobContext

class DemoJob : Job {
    override fun work(context: JobContext) {
        val message : String = Greeter().greet("group")
        print(message)
    }
}
