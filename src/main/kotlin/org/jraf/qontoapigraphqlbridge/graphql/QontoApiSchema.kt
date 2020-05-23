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

import graphql.AssertException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.ScalarWiringEnvironment
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import graphql.schema.idl.WiringFactory
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.bankaccount.DATA_FETCHER_BANK_ACCOUNTS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.bankaccount.DATA_FETCHER_BANK_ACCOUNTS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.label.DATA_FETCHER_LABELS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.label.DATA_FETCHER_LABELS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.member.DATA_FETCHER_MEMBERS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.member.DATA_FETCHER_MEMBERS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.organization.DATA_FETCHER_ORGANIZATION
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.organization.DATA_FETCHER_ORGANIZATION_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTIONS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTIONS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_ATTACHMENTS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_ATTACHMENTS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_INITIATOR
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_INITIATOR_NAME
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_LABELS
import org.jraf.qontoapigraphqlbridge.graphql.datafetchers.transaction.DATA_FETCHER_TRANSACTION_LABELS_NAME
import org.jraf.qontoapigraphqlbridge.graphql.scalar.SCALAR_TIMESTAMP_NAME
import org.jraf.qontoapigraphqlbridge.graphql.scalar.SCALAR_URL_NAME
import org.jraf.qontoapigraphqlbridge.graphql.scalar.createTimestampScalar
import org.jraf.qontoapigraphqlbridge.graphql.scalar.createUrlScalar
import java.io.BufferedReader
import java.io.InputStreamReader

private const val SCHEMA_RESOURCE = "/schema.graphqls"

private const val TYPE_NAME_QUERY = "Query"
private const val TYPE_NAME_TRANSACTION = "Transaction"

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
            .wiringFactory(object : WiringFactory {
                override fun providesScalar(environment: ScalarWiringEnvironment): Boolean {
                    return environment.scalarTypeDefinition.name in setOf(SCALAR_TIMESTAMP_NAME, SCALAR_URL_NAME)
                }

                override fun getScalar(environment: ScalarWiringEnvironment): GraphQLScalarType {
                    return when (environment.scalarTypeDefinition.name) {
                        SCALAR_TIMESTAMP_NAME -> createTimestampScalar(environment.scalarTypeDefinition)
                        SCALAR_URL_NAME -> createUrlScalar(environment.scalarTypeDefinition)
                        else -> throw AssertException("Internal error: should never happen")
                    }
                }
            })
            .type(
                newTypeWiring(TYPE_NAME_QUERY)
                    .dataFetcher(DATA_FETCHER_ORGANIZATION_NAME, DATA_FETCHER_ORGANIZATION)
                    .dataFetcher(DATA_FETCHER_LABELS_NAME, DATA_FETCHER_LABELS)
                    .dataFetcher(DATA_FETCHER_MEMBERS_NAME, DATA_FETCHER_MEMBERS)
                    .dataFetcher(DATA_FETCHER_BANK_ACCOUNTS_NAME, DATA_FETCHER_BANK_ACCOUNTS)
                    .dataFetcher(DATA_FETCHER_TRANSACTIONS_NAME, DATA_FETCHER_TRANSACTIONS)
            )
            .type(
                newTypeWiring(TYPE_NAME_TRANSACTION)
                    .dataFetcher(DATA_FETCHER_TRANSACTION_INITIATOR_NAME, DATA_FETCHER_TRANSACTION_INITIATOR)
                    .dataFetcher(DATA_FETCHER_TRANSACTION_LABELS_NAME, DATA_FETCHER_TRANSACTION_LABELS)
                    .dataFetcher(DATA_FETCHER_TRANSACTION_ATTACHMENTS_NAME, DATA_FETCHER_TRANSACTION_ATTACHMENTS)
            )
            .build()
    }
}