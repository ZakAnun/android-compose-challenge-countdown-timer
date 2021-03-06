package com.example.androiddevchallenge

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class MainViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    /**
     * template data source
     */
    var hourTimes = listOf("0.6 hour", "0.7 hour", "0.8 hour", "0.9 hour","1.0 hour", "1.1 hour", "1.2 hour")
    var minTimes = listOf("1 min", "5 min", "10 min", "15 min", "20 min", "25 min", "30 min")
    var secondTimes = listOf("10 sec", "20 sec", "30 sec", "40 sec", "50 sec", "60 sec")

    var isCalculating = mutableStateOf(false)
    var selectTime = mutableStateOf("")
    var realHour = mutableStateOf("")
    var realMinute = mutableStateOf("")
    var realSecond = mutableStateOf("")

    /**
     * 计算秒数
     */
    var calcSecond = 0

    /**
     * 总秒数
     */
    var totalSecond = 0

    private var job: Job? = null

    /**
     * 倒计时开始
     */
    fun startCountdown() {
        if (selectTime.value.isEmpty()) {
            return
        }
        isCalculating.value = true
        kotlin.runCatching {
            if (selectTime.value.contains("hour")) {
                handleHour()
            }
            if (selectTime.value.contains("min")) {
                handleMin()
            }
            if (selectTime.value.contains("sec")) {
                handleSecond()
            }
        }.onFailure {
            Log.d(TAG, "startCountdown: failure, cause by cast exception!")
        }
    }

    /**
     * 暂停倒计时
     */
    fun pauseCountdown() {
        job?.run {
            if (isActive) {
                cancel()
            }
        }
        isCalculating.value = false
    }

    /**
     * 重置倒计时
     */
    fun resetCountdown(saveSelected: Boolean = false) {
        if (isCalculating.value) {
            pauseCountdown()
            isCalculating.value = false
        }
        calcSecond = 0
        totalSecond = 0
        if (!saveSelected) {
            selectTime.value = ""
        }
        realHour.value = ""
        realMinute.value = ""
        realSecond.value = ""
    }

    /**
     * 处理小时
     */
    private fun handleHour() {
        startJob(if (calcSecond == 0) {
            val res = selectTime.value.split(" ")[0].toFloat()
            if (res >= 1) {
                realHour.value = "1"
            } else {
                realHour.value = ""
            }
            (res * 60 * 60).toInt()
        } else {
            calcSecond
        })
    }

    /**
     * 处理分钟
     */
    private fun handleMin() {
        startJob(if (calcSecond == 0) {
            selectTime.value.split(" ")[0].toInt() * 60
        } else {
            calcSecond
        })
    }

    /**
     * 处理秒
     */
    private fun handleSecond() {
        startJob(if (calcSecond == 0) {
            selectTime.value.split(" ")[0].toInt()
        } else {
            calcSecond
        })
    }

    /**
     * 开始倒计时工作
     */
    private fun startJob(second: Int) {
        calcSecond = second
        totalSecond = second
        job = viewModelScope.launch(Dispatchers.IO) {
            isCalculating.value = true
            while (calcSecond >= 0) {
                delay(1000)
                var min: Int
                var realSecOne: Int
                var realSecTwo: Int
                if (calcSecond < 60) {
                    min = 0
                    realSecOne = calcSecond / 10
                    realSecTwo = calcSecond % 10
                } else {
                    min = calcSecond / 60
                    realSecOne = (calcSecond % 60) / 10
                    realSecTwo = (calcSecond % 60) % 10
                }
                Log.d("MainViewModel", "calcSecond = $calcSecond, min = $min, sec = ${realSecOne}${realSecTwo}")
                withContext(Dispatchers.Main) {
                    realMinute.value = "$min"
                    realSecond.value = "${realSecOne}${realSecTwo}"
                    calcSecond -= 1
                    if (calcSecond <= 0) {
                        isCalculating.value = false
                        "Times Up !!!".toast()
                    }
                }
            }
        }.apply {
            start()
        }
    }

    /**
     * 选择倒计时时间
     */
    fun selectTime(selectItem: String) {
        selectTime.value = selectItem
        totalSecond = 0
        if (isCalculating.value) {
            resetCountdown(true)
            isCalculating.value = false
        }
        realHour.value = ""
        realMinute.value = ""
        realSecond.value = ""
    }

    private fun String.toast() {
        Toast.makeText(getApplication(), this, Toast.LENGTH_SHORT).show()
    }
}