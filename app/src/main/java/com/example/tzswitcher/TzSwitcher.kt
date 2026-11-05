package com.example.tzswitcher

import android.content.Context
import android.provider.Settings
import java.util.TimeZone

object TzSwitcher {
    const val DEFAULT_TARGET = "America/New_York"

    private fun su(cmd: String) {
        Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
    }

    fun target(c: Context): String =
        c.getSharedPreferences("tz", 0).getString("target", DEFAULT_TARGET)!!

    fun setTarget(c: Context, id: String) =
        c.getSharedPreferences("tz", 0).edit().putString("target", id).apply()

    // 亮 = 自动时区关闭，且当前时区就是目标时区（读取不需要 root）
    fun isActive(c: Context): Boolean {
        val auto = Settings.Global.getInt(c.contentResolver, "auto_time_zone", 1)
        return auto == 0 && TimeZone.getDefault().id == target(c)
    }

    // 亮 -> 暗：重新打开自动时区；暗 -> 亮：关自动时区并设为目标时区
    fun toggle(c: Context) {
        if (!isActive(c)) {
            su("settings put global auto_time_zone 0")
            su("cmd alarm set-timezone ${target(c)}")
        } else {
            su("settings put global auto_time_zone 1")
        }
    }
}
