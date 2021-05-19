@file:OptIn(KtorExperimentalLocationsAPI::class)

package dev.triumphteam.backend.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@Location("webhook")
class Webhook