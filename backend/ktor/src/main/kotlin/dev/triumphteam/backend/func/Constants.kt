package dev.triumphteam.backend.func

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO

const val PROJECT_FILE_NAME = "PROJECT.json"
const val PROJECT_ICON_NAME = "ICON.png"
const val MARKDOWN_FILE_EXTENSION = "md"

val SCOPE = CoroutineScope(IO)