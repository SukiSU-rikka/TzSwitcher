package com.example.tzswitcher

import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class TzTileService : TileService() {
    private val main = Handler(Looper.getMainLooper())

    override fun onStartListening() = refresh()

    override fun onClick() {
        Thread {
            TzSwitcher.toggle(this)
            Thread.sleep(600) // 等系统时区生效
            main.post { refresh() }
        }.start()
    }

    private fun refresh() {
        qsTile?.apply {
            state = if (TzSwitcher.isActive(this@TzTileService))
                Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            label = "切换时区"
            updateTile()
        }
    }
}
