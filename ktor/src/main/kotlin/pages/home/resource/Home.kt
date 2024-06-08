package dev.triumphteam.backend.pages.home.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
public data object Home
