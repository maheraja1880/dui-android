package com.example.duifeature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sample.dynamicui.ui.framework.DynamicUIScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DUIActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DynamicUIScreen(layoutId = "home")
        }
    }
}