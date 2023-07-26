package com.example.yyyyrrrrroook25.model.colors

import com.example.foundation.model.Repository
import kotlinx.coroutines.flow.Flow

typealias ColorListener = (NamedColor) -> Unit

/**
 * Repository interface example.
 *
 * Provides access to the available colors and current selected color.
 */
interface ColorsRepository : Repository {

    /**
     * Get the list of all available colors that may be chosen by the user.
     */
    suspend fun getAvailableColors(): List<NamedColor>

    /**
     * Get the color content by its ID
     */
    suspend fun getById(id: Long): NamedColor


    suspend fun getCurrentColor(): NamedColor

     fun setCurrentColor(color: NamedColor): Flow<Int>

    /**
     * Listen for the current color changes.
     * The listener is triggered immediately with the current value when calling this method.
     */


    fun listenerCurrentColor(): Flow<NamedColor>

}