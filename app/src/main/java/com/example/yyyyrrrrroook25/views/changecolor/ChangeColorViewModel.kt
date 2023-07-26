package com.example.yyyyrrrrroook25.views.changecolor


import android.content.res.Resources
import androidx.lifecycle.*
import com.example.foundation.model.*
import com.example.yyyyrrrrroook25.R
import com.example.yyyyrrrrroook25.model.colors.ColorsRepository
import com.example.yyyyrrrrroook25.model.colors.NamedColor
import com.example.foundation.navigator.Navigator
import com.example.foundation.uiactions.UiActions
import com.example.foundation.views.BaseViewModel

import com.example.yyyyrrrrroook25.views.changecolor.ChangeColorFragment.Screen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val resources: Resources,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors =
        MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getStateFloww("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    // main destination (contains merged values from _availableColors & _currentColorId)

    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _saveInProgress,
        ::mergeSources
    )

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = viewState
        .map { result ->
        return@map if (result is SuccessResult) {
            val currentColor = result.data.colorList.first { it.selected }
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_title_simple)
        }
    }
        .asLiveData()

    init {
        load()

    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.value = true
            val currentColorId = _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor).collect()
            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e !is CancellationException) uiActions.toast(uiActions.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), then we use both of these values in order to create a list of
     * [NamedColorListItem], it is a list to be displayed in RecyclerView.
     */
    private fun mergeSources(colors: Result<List<NamedColor>>, currentColorId: Long, saveInProgress: Progress): Result<ViewState> {

        return colors.map { colorsList ->
            ViewState(
                colorList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress.isInProgress(),
                showCancelButton = !saveInProgress.isInProgress(),
                showSaveProgressBar = saveInProgress.isInProgress(),

                saveProgressPercentage = saveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value, saveInProgress.getPercentage())
            )
        }
    }

    private fun load() = into(_availableColors) {
        return@into colorsRepository.getAvailableColors()
    }

    data class ViewState(
        val colorList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String
    )
}