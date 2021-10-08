package dev.triumphteam.backend.func

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO

const val PROJECT_FILE_NAME = "PROJECT.json"
const val MARKDOWN_FILE_EXTENSION = "md"

val SCOPE = CoroutineScope(IO)