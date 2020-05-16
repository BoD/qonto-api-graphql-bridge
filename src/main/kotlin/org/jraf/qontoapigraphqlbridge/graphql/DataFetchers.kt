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

import graphql.schema.DataFetcher

private val BOOKS = listOf(
    mapOf(
        "id" to "book-1",
        "name" to "Harry Potter and the Philosopher's Stone",
        "pageCount" to "223",
        "authorId" to "author-1"
    ),
    mapOf(
        "id" to "book-2",
        "name" to "Moby Dick",
        "pageCount" to "635",
        "authorId" to "author-2"
    ),
    mapOf(
        "id" to "book-3",
        "name" to "Interview with the vampire",
        "pageCount" to "371",
        "authorId" to "author-3"
    )
)

private val AUTHORS = listOf(
    mapOf(
        "id" to "author-1",
        "firstName" to "Joanne",
        "lastName" to "Rowling"
    ),
    mapOf(
        "id" to "author-2",
        "firstName" to "Herman",
        "lastName" to "Melville"
    ),
    mapOf(
        "id" to "author-3",
        "firstName" to "Anne",
        "lastName" to "Rice"
    )
)

fun getBookByIdDataFetcher(): DataFetcher<Map<String, String>?> {
    return DataFetcher { env ->
        val bookId: String = env.getArgument("id")
        BOOKS.firstOrNull { it["id"] == bookId }
    }
}

fun getAuthorDataFetcher(): DataFetcher<Map<String, String>?> {
    return DataFetcher { env ->
        val book = env.getSource<Map<String, String>>()
        val authorId = book["authorId"]
        AUTHORS.firstOrNull { it["id"] == authorId }
    }
}