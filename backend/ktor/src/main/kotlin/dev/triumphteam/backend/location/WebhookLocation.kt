@file:OptIn(KtorExperimentalLocationsAPI::class)

package dev.triumphteam.backend.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

/**
 * Location for listening to the webhook
 */
@Location("webhook")
class WebhookLocation