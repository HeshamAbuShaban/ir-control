package dev.training.ir_control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.training.ir_control.ui.screen.IrControlScreen
import dev.training.ir_control.ui.theme.IRControlTheme
import dev.training.ir_control.ui.vm.IrViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IRControlTheme {
                val viewModel: IrViewModel = hiltViewModel()
                IrControlScreen(viewModel)
            }
        }
    }
}