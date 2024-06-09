package dev.triumphteam.backend.pages.docs.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/docs")
public data object Docs
