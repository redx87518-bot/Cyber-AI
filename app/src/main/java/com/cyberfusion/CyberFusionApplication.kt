package com.cyberfusion

import android.app.Application
import com.cyberfusion.core.database.room.CyberFusionDatabase

class CyberFusionApplication : Application() {
    val database by lazy { CyberFusionDatabase.getInstance(this) }
}
