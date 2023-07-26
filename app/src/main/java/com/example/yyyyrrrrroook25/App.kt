package com.example.yyyyrrrrroook25

import android.app.Application
import com.example.foundation.BaseApplication
import com.example.foundation.model.coroutines.IoDispatcher
import com.example.foundation.model.coroutines.WorkerDispatcher
import com.example.yyyyrrrrroook25.model.colors.InMemoryColorsRepository
import kotlinx.coroutines.Dispatchers


/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    // holder classes are used because we have 2 dispatchers of the same type
    private val ioDispatcher = IoDispatcher(Dispatchers.IO) // for IO operations
    private val workerDispatcher = WorkerDispatcher(Dispatchers.Default) // for CPU-intensive operations

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        ioDispatcher,
        workerDispatcher,

        InMemoryColorsRepository(ioDispatcher)
    )

}