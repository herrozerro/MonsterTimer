package com.singularitylathe.monstertimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownUI()
        }
    }
}

@Preview
@Composable
fun CountdownUI() {
    // Use a mutable state to store the countdown text values
    var countDownText by remember { mutableStateOf("") }
    var worldCountDownText by remember { mutableStateOf("") }

    // Initialize CountdownManager outside of the effect to avoid frequent recreations
    val countdownManager = remember { CountdownManager() }

    LaunchedEffect(Unit) {
        while (true) {
            // Update the state every second
            countDownText = countdownManager.countDownTextWithSeconds
            worldCountDownText = countdownManager.worldCountDownTextWithSeconds
            Log.d("MainActivity", countdownManager.worldCountDownTextWithSeconds)
            Log.d("MainActivity", countdownManager.worldCountDownTextWithSeconds)
            delay(1000)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Monster Refresh: $countDownText")
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "Biome Refresh: $worldCountDownText")
    }
}

class CountdownManager {

    // Get the current UTC time
    private val currentTime: Calendar
        get() = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    val countDownText: String
        get() = timeLeftForNextMonsterRefresh(includeSeconds = false)

    val worldCountDownText: String
        get() = timeLeftForNextBiomeChange(includeSeconds = false)

    val countDownTextWithSeconds: String
        get() = timeLeftForNextMonsterRefresh(includeSeconds = true)

    val worldCountDownTextWithSeconds: String
        get() = timeLeftForNextBiomeChange(includeSeconds = true)

    private fun timeLeftForNextBiomeChange(includeSeconds: Boolean): String {
        val nextBiomeChange = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val differenceInMillis = nextBiomeChange.timeInMillis - currentTime.timeInMillis
        val secondsLeft = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis) % 60
        val minutesLeft = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) % 60
        val hoursLeft = TimeUnit.MILLISECONDS.toHours(differenceInMillis)

        return if (includeSeconds) {
            String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft)
        } else {
            String.format("%02d:%02d", hoursLeft, minutesLeft)
        }
    }

    private fun timeLeftForNextMonsterRefresh(includeSeconds: Boolean): String {
        val nextMonsterRefresh = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            val nextRefreshHour = 3 * ((currentTime.get(Calendar.HOUR_OF_DAY) / 3) + 1)
            set(Calendar.HOUR_OF_DAY, nextRefreshHour % 24)
        }

        val differenceInMillis = nextMonsterRefresh.timeInMillis - currentTime.timeInMillis
        val secondsLeft = TimeUnit.MILLISECONDS.toSeconds(differenceInMillis) % 60
        val minutesLeft = TimeUnit.MILLISECONDS.toMinutes(differenceInMillis) % 60
        val hoursLeft = TimeUnit.MILLISECONDS.toHours(differenceInMillis)

        return if (includeSeconds) {
            String.format("%02d:%02d:%02d", hoursLeft, minutesLeft, secondsLeft)
        } else {
            String.format("%02d:%02d", hoursLeft, minutesLeft)
        }
    }
}