package com.lyra.notation

import android.app.Application

class NotationApp: Application() {
    override fun onCreate() {
        super.onCreate()

        NoteStorage.init(this)
        NoteStorage.initNoteFiles()
    }
}