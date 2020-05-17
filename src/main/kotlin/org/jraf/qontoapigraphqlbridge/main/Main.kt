/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2020-present Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.qontoapigraphqlbridge.main

import graphql.schema.GraphQLSchema
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ktor.graphql.config
import ktor.graphql.graphQL
import org.jraf.qontoapigraphqlbridge.auth.AUTH_KEY
import org.jraf.qontoapigraphqlbridge.auth.AuthenticationInformation
import org.jraf.qontoapigraphqlbridge.auth.getAuthenticationInformation
import org.jraf.qontoapigraphqlbridge.graphql.QontoApiSchema

private const val DEFAULT_PORT = 8042
private const val ENV_PORT = "PORT"
private const val APP_URL = "https://qonto-api-graphql-bridge.herokuapp.com"

val schema: GraphQLSchema = QontoApiSchema().schema

fun main() {
    val listenPort = System.getenv(ENV_PORT)?.toInt() ?: DEFAULT_PORT
    embeddedServer(Netty, listenPort) {
        install(DefaultHeaders)

        install(StatusPages) {
            status(HttpStatusCode.NotFound) {
                call.respondText(
                    text = "Usage: $APP_URL/graphql/",
                    status = it
                )
            }

            exception<IllegalArgumentException> { exception ->
                call.respond(HttpStatusCode.BadRequest, exception.message ?: "Bad request")
            }
        }

        routing {
            get("/") {
                call.respondText(
                    """
                    <html>
                    <body>
                    <h1>Qonto API GraphQL Bridge</h1>
                    Hello, World!<br>
                    This bridge allows you to access the <a href="https://api-doc.qonto.eu/2.0/welcome/">Qonto API</a> using GraphQL.<br>
                    <br>
                    The GraphQL endpoint is accessible <a href="graphql">here</a><br>.
                    <br>
                    More information: <a href="https://github.com/BoD/qonto-api-graphql-bridge">project on Github</a> - Author: <a href="mailto:BoD@JRAF.org">BoD@JRAF.org</a>
                    </body></html>
                """, ContentType.Text.Html.withCharset(Charsets.UTF_8)
                )
            }

            graphQL("/graphql", schema) {
                config {
                    graphiql = true
                    context = call.getAuthenticationInformation()
                }
            }.apply {
                // Handle authentication
                intercept(ApplicationCallPipeline.Call) {
                    val authorizationHeader = call.request.header(HttpHeaders.Authorization)
                    if (authorizationHeader == null || !authorizationHeader.matches(Regex(".+:.+"))) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            "Missing or invalid ${HttpHeaders.Authorization} header, please refer to https://api-doc.qonto.eu/2.0/welcome/authentication"
                        )
                        finish()
                    } else {
                        call.attributes.put(AUTH_KEY, AuthenticationInformation(authorizationHeader))
                    }
                }
            }
        }
    }.start(wait = true)
}
