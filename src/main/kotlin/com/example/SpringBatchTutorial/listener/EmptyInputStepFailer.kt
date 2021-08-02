package com.example.SpringBatchTutorial.listener

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.AfterStep

class EmptyInputStepFailer {

    @AfterStep
    fun afterStep(execution: StepExecution): ExitStatus {
        return if (execution.readCount > 0) {
            execution.exitStatus
        } else ExitStatus.FAILED
    }

}