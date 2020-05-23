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

package org.jraf.qontoapigraphqlbridge.graphql.model.transaction

enum class TransactionVatRate {
    RATE_CUSTOM,
    RATE_ZERO,
    RATE_2_1,
    RATE_5_5,
    RATE_10,
    RATE_20,
    ;

    companion object {
        fun fromFloat(floatValue: Float?): TransactionVatRate? {
            return when (floatValue) {
                null -> null
                0F -> RATE_ZERO
                2.1F -> RATE_2_1
                5.5F -> RATE_5_5
                10F -> RATE_10
                20F -> RATE_20
                else -> RATE_CUSTOM
            }
        }
    }
}
