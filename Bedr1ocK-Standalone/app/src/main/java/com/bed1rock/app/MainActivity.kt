package com.bed1rock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bed1rock.app.data.SettingsRepository
import com.bed1rock.app.domain.FileManager
import com.bed1rock.app.model.ThemeMode
import com.bed1rock.app.ui.MainViewModel
import com.bed1rock.app.ui.screens.*
import com.bed1rock.app.ui.theme.Bed1rockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val fileManager = FileManager(this)
        val settingsRepo = SettingsRepository(this)
        val viewModel = MainViewModel(fileManager, settingsRepo)

        setContent {
            val settings by viewModel.settings.collectAsState(initial = com.bed1rock.app.model.AppSettings())
            
            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            Bed1rockTheme(darkTheme = darkTheme, dynamicColor = settings.useDynamicColor) {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0; navController.navigate("import") },
                    icon = { Text("🌍") },
                    label = { Text("Import") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1; navController.navigate("addon") },
                    icon = { Text("📦") },
                    label = { Text("Add-ons") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2; navController.navigate("settings") },
                    icon = { Text("⚙️") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "import",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("import") { WorldImportScreen(viewModel) }
            composable("addon") { AddonInjectorScreen(viewModel) }
            composable("settings") { SettingsScreen(viewModel) }
        }
    }
}