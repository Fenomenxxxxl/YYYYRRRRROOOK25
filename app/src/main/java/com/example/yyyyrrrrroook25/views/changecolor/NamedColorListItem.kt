package com.example.yyyyrrrrroook25.views.changecolor

import com.example.yyyyrrrrroook25.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)
