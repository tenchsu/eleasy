package com.example.eleasy // 确保这个包名和你的项目一致

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    // 使用 StateFlow 来保存电量，这样 Compose 界面可以自动监听它的变化
    private val _batteryLevel = MutableStateFlow("...")
    val batteryLevel = _batteryLevel.asStateFlow()

    // 使用 StateFlow 来保存充电功率
    private val _chargingWattage = MutableStateFlow("...")
    val chargingWattage = _chargingWattage.asStateFlow()

    // 创建一个广播接收器来监听系统电量变化
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                // 获取电量百分比
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level != -1 && scale != -1) {
                    val batteryPct = level * 100 / scale.toFloat()
                    _batteryLevel.value = "${batteryPct.toInt()}%"
                }

                // 获取充电状态
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                if (isCharging) {
                    // 如果在充电，尝试获取功率
                    val batteryManager = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    // 获取瞬时电流，单位是微安 (µA)
                    val chargeCurrent = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    // 获取电压，单位是毫伏 (mV)
                    val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

                    // 功率(W) = 电压(V) * 电流(A)
                    // 注意: chargeCurrent 可能是负值（放电时），我们只计算充电时的功率
                    if (chargeCurrent > 0 && voltage > 0) {
                        // (电流/1,000,000) * (电压/1000)
                        val power = (chargeCurrent / 1000000.0) * (voltage / 1000.0)
                        // 将结果格式化为保留两位小数的字符串
                        _chargingWattage.value = "%.2f W".format(power)
                    } else {
                        // 充电但无法获取精确功率时显示
                        _chargingWattage.value = "充电中"
                    }
                } else {
                    // 如果没有充电
                    _chargingWattage.value = "未充电"
                }
            }
        }
    }

    // init 代码块会在 ViewModel 被创建时执行
    init {
        // 创建一个意图过滤器，只接收电量变化的广播
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        // 注册广播接收器，告诉系统我们关心电量变化
        application.registerReceiver(batteryReceiver, filter)
    }

    // onCleared 会在 ViewModel 被销毁时执行
    override fun onCleared() {
        super.onCleared()
        // 取消注册广播接收器，防止内存泄漏
        getApplication<Application>().unregisterReceiver(batteryReceiver)
    }
}
