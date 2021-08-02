package com.example.SpringBatchTutorial.skippolicy

import org.springframework.batch.core.step.skip.SkipPolicy
import org.springframework.batch.item.ParseException
import java.io.FileNotFoundException

class FileVerificationSkipper: SkipPolicy {
    override fun shouldSkip(t: Throwable, skipCount: Int): Boolean {
        return if (t is FileNotFoundException) {
            false
        } else {
            t is ParseException && skipCount <= 10
        }
    }
}