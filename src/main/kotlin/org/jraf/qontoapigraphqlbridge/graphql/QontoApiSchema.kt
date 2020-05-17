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

package org.jraf.qontoapigraphqlbridge.graphql

import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.label.LABELS_DATA_FETCHER
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.member.MEMBERS_DATA_FETCHER
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.organization.ORGANIZATION_DATA_FETCHER
import java.io.BufferedReader
import java.io.InputStreamReader

private const val SCHEMA_RESOURCE = "/schema.graphqls"

class QontoApiSchema {
    val schema = buildSchema()

    private fun buildSchema(): GraphQLSchema {
        val typeDefinitionRegistry = SchemaParser().parse(getSchemaResourceAsReader())
        val runtimeWiring = buildRuntimeWiring()
        return SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
    }

    private fun getSchemaResourceAsReader() = BufferedReader(
        InputStreamReader(this::class.java.getResourceAsStream(SCHEMA_RESOURCE))
    )

    private fun buildRuntimeWiring(): RuntimeWiring {
        return RuntimeWiring.newRuntimeWiring()
            .scalar(TIMESTAMP_SCALAR)
            .scalar(URL_SCALAR)
            .type(
                newTypeWiring("Query")
                    .dataFetcher("organization", ORGANIZATION_DATA_FETCHER)
                    .dataFetcher("labels", LABELS_DATA_FETCHER)
                    .dataFetcher("members", MEMBERS_DATA_FETCHER)
            )
            .build()
    }
}