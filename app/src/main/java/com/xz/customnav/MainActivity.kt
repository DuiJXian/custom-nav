package com.xz.customnav

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.xz.customnav.ui.CustomNavActivity
import com.xz.customnav.ui.NavigationActivity
import com.xz.customnav.ui.StartScreen
import com.xz.customnav.ui.theme.CustomNav

class MainActivity : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomNav {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    Box(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row {

                            Button(onClick = {
                                val intent = Intent(context, NavigationActivity::class.java)
                                context.startActivity(intent)
                            }) {
                                Text("官方路由")
                            }

                            Button(onClick = {
                                val intent = Intent(context, CustomNavActivity::class.java)
                                context.startActivity(intent)
                            }) {
                                Text("自定义路由")
                            }
                        }
                    }
                }
            }
        }
    }
}
