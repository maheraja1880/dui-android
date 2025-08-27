package com.sample.dynamicui.ui.framework

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sample.dynamicui.domain.model.Component

//@Composable
//fun DynamicNavHost(
//    navController: NavHostController,
//    startDestination: String = "home",
//    layouts: Map<String, Component>
//) {
//    NavHost(navController, startDestination = startDestination) {
//        layouts.forEach { (route, component) ->
//            composable(route) {
//                val vm: DynamicViewModel = hiltViewModel()
//                DynamicUIScreen(layoutId = route, navController = navController, vm = vm)
//            }
//        }
//        // fallback if no schema-defined screen
//        composable("not_found") {
//            Surface(modifier = Modifier) {
//                Text("Screen not found")
//            }
//        }
//    }
//}