package com.example.duifeature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.sample.dynamicui.ui.framework.DynamicUIScreen
import com.sample.dynamicui.ui.framework.DynamicViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sample.dynamicui.ui.framework.DynamicUiIntent
import com.sample.dynamicui.ui.framework.DynamicUiState
import androidx.compose.material.icons.filled.ArrowBack

@AndroidEntryPoint
class DUIActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = intent?.data?.lastPathSegment ?: "home"

        setContent {
            val vm: DynamicViewModel = hiltViewModel()
            val state by vm.state.collectAsState()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Dynamic UI") },
                        navigationIcon = if (state is DynamicUiState.Success && (state as DynamicUiState.Success).canGoBack) {
                            {
                                IconButton(onClick = { vm.handleIntent(DynamicUiIntent.Back) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        } else null
                    )
                }
            ) { innerPadding ->
                DynamicUIScreen(
                    layoutId = layoutId,
                    modifier = Modifier.padding(innerPadding),
                    vm = vm
                )
            }
        }
    }
}