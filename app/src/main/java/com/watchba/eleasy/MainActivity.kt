package com.example.eleasy // 确保包名一致

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eleasy.ui.theme.EleasyTheme

class MainActivity : ComponentActivity() {

    // 通过委托方式获取 ViewModel 实例
    private val batteryViewModel: BatteryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EleasyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 调用我们的UI函数，并把ViewModel传进去
                    BatteryInfoScreen(viewModel = batteryViewModel)
                }
            }
        }
    }
}

@Composable
fun BatteryInfoScreen(viewModel: BatteryViewModel) {
    // 使用 collectAsStateWithLifecycle 来安全地从ViewModel中收集数据
    // 当StateFlow的值变化时，这里会自动刷新界面
    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val chargingWattage by viewModel.chargingWattage.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "手机状态",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        InfoRow(label = "当前电量", value = batteryLevel)
        Spacer(modifier = Modifier.height(16.dp))
        InfoRow(label = "充电功率", value = chargingWattage)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(220.dp) // 给定一个宽度让它们对齐
    ) {
        Text(
            text = "$label:",
            fontSize = 20.sp,
            modifier = Modifier.weight(1f) // 占据可用空间
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EleasyTheme {
        // 这是一个预览，让我们在不运行App的情况下看到UI大概的样子
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "手机状态",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            InfoRow(label = "当前电量", value = "95%")
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(label = "充电功率", value = "18.50 W")
        }
    }
}

