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

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLScalarType.newScalar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

val TIMESTAMP_SCALAR: GraphQLScalarType = newScalar()
    .name("Timestamp")
    .coercing(TimestampScalarCoercing)
    .build()

object TimestampScalarCoercing : Coercing<Date?, String?> {
    override fun parseValue(input: Any?): Date? {
        throw UnsupportedOperationException()
    }

    override fun parseLiteral(input: Any?): Date? {
        throw UnsupportedOperationException()
    }

    override fun serialize(dataFetcherResult: Any?): String? {
        return (dataFetcherResult as? Date)?.let { DATE_SERIALIZED_FORMAT.format(it) }
    }

}

private val DATE_SERIALIZED_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
