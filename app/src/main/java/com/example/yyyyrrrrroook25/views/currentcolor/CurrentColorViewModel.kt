package com.example.yyyyrrrrroook25.views.currentcolor

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foundation.model.ErrorResult
import com.example.foundation.model.PendingResult
import com.example.foundation.model.SuccessResult
import com.example.foundation.model.takeSuccess
import com.example.foundation.model.dispatchers.Dispatcher
import com.example.yyyyrrrrroook25.R
import com.example.yyyyrrrrroook25.model.colors.ColorListener
import com.example.yyyyrrrrroook25.model.colors.ColorsRepository
import com.example.yyyyrrrrroook25.model.colors.NamedColor
import com.example.foundation.navigator.Navigator
import com.example.foundation.uiactions.UiActions
import com.example.foundation.views.BaseViewModel
import com.example.foundation.views.LiveResult
import com.example.foundation.views.MutableLiveResult
import com.example.yyyyrrrrroook25.views.changecolor.ChangeColorFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor


    // --- example of listening results via model layer

    init {
        viewModelScope.launch {
            colorsRepository.listenerCurrentColor().collect {
                _currentColor.postValue(SuccessResult(it))
            }
        }
        load()


    }



    // --- example of listening results directly from the screen

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
        }
    }

    // ---

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
    load()
    }

    private fun load() = into(_currentColor) {
        colorsRepository.getCurrentColor()
    }

}