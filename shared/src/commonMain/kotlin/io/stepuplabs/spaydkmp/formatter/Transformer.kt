package io.stepuplabs.spaydkmp.formatter

/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import kotlin.math.max
import kotlin.math.min

class Transformer internal constructor(private val formatter: Formatter) {
    private var formatToken: FormatToken? = null
    private var arg: Any? = null

    fun transform(token: FormatToken, argument: Any?): String { /* init data member to print */
        formatToken = token
        arg = argument

        var result: String = when (token.conversionType) {
            'S', 's' -> {
                transformFromString()
            }

            'd', 'o', 'x', 'X' -> {
                transformFromInteger()
            }

            '%' -> {
                transformFromPercent()
            }

            else -> {
                throw Exception(
                    token.conversionType.toString()
                )
            }
        }

        if (token.conversionType.isUpperCase()) {
            return result.uppercase()
        }
        return result
    }

    private fun transformFromString(): String {
        val result = StringBuilder()
        val startIndex = 0
        val flags = formatToken!!.flags
        if (formatToken!!.isFlagSet(FormatToken.FLAG_MINUS) && !formatToken!!.isWidthSet) {
            throw Exception("-" + formatToken!!.conversionType)
        }

        if (FormatToken.FLAGS_UNSET != flags && FormatToken.FLAG_MINUS != flags) {
            throw Exception()
        }

        result.append(arg)

        return padding(result, startIndex)
    }

    private fun transformFromPercent(): String {
        val result = StringBuilder("%") //$NON-NLS-1$
        val startIndex = 0
        val flags = formatToken!!.flags
        if (formatToken!!.isFlagSet(FormatToken.FLAG_MINUS) && !formatToken!!.isWidthSet) {
            throw Exception("-" + formatToken!!.conversionType)
        }

        if (FormatToken.FLAGS_UNSET != flags && FormatToken.FLAG_MINUS != flags) {
            throw Exception()
        }
        if (formatToken!!.isPrecisionSet) {
            throw Exception()
        }

        return padding(result, startIndex)
    }

    private fun padding(source: StringBuilder, startIndex: Int): String {
        var src = source
        var start = startIndex

        val paddingRight = formatToken
            ?.isFlagSet(FormatToken.FLAG_MINUS)
        var paddingChar = '\u0020' // space as padding char.

        if (formatToken!!.isFlagSet(FormatToken.FLAG_ZERO)) {
            paddingChar = '0'
        } else {
            start = 0
        }

        var width = formatToken!!.width
        val precision = formatToken!!.precision
        var length = src.length

        if (precision >= 0) {
            length = min(length, precision)
            src = StringBuilder(
                src.substring(0, length) + src.substring(src.length)
            )
        }
        if (width > 0) {
            width = max(src.length, width)
        }
        if (length >= width) {
            return src.toString()
        }

        val paddings = CharArray(width - length) { paddingChar }
        val insertString = paddings.concatToString()

        if (paddingRight!!) {
            src.append(insertString)
        } else {
            src.insert(start, insertString)
        }

        return src.toString()
    }

    private fun transformFromInteger(): String {
        var startIndex = 0
        var isNegative = false
        var result = StringBuilder()
        val currentConversionType = formatToken!!.conversionType
        if (
            formatToken!!.isFlagSet(FormatToken.FLAG_MINUS)
            || formatToken!!.isFlagSet(FormatToken.FLAG_ZERO)
        ) {
            if (!formatToken!!.isWidthSet) {
                throw Exception()
            }
        }

        if (formatToken!!.isFlagSet(FormatToken.FLAG_ADD)
            && formatToken!!.isFlagSet(FormatToken.FLAG_SPACE)
        ) {
            throw Exception(formatToken!!.getStrFlags())
        }
        if (formatToken!!.isPrecisionSet) {
            throw Exception(
            )
        }

        val value: Long = when (arg) {
            is Long -> (arg as Long).toLong()
            is Int -> (arg as Int).toLong()
            is Short -> (arg as Short).toLong()
            is Byte -> (arg as Byte).toLong()
            else -> {
                error("Value not supported [$arg] for type `$currentConversionType'")
            }
        }

        if ('d' != currentConversionType) {
            if (formatToken!!.isFlagSet(FormatToken.FLAG_ADD)
                || formatToken!!.isFlagSet(FormatToken.FLAG_SPACE)
                || formatToken!!.isFlagSet(FormatToken.FLAG_COMMA)
                || formatToken!!.isFlagSet(FormatToken.FLAG_PARENTHESIS)
            ) {
                throw Exception(
                )
            }
        }

        if (formatToken!!.isFlagSet(FormatToken.FLAG_SHARP)) {
            startIndex += when (currentConversionType) {
                'd' -> {
                    throw Exception(
                    )
                }

                'o' -> {
                    result.append("0")
                    1
                }

                else -> {
                    result.append("0x")
                    2
                }
            }
        }

        if (
            formatToken!!.isFlagSet(FormatToken.FLAG_MINUS)
            && formatToken!!.isFlagSet(FormatToken.FLAG_ZERO)
        ) {
            throw Exception(formatToken!!.getStrFlags())
        }

        if (value < 0) {
            isNegative = true
        }

        if ('d' == currentConversionType) {
            result.append(arg.toString())
        } else {
            isNegative = false
        }

        if (!isNegative) {
            if (formatToken!!.isFlagSet(FormatToken.FLAG_ADD)) {
                result.insert(0, '+')
                startIndex += 1
            }
            if (formatToken!!.isFlagSet(FormatToken.FLAG_SPACE)) {
                result.insert(0, ' ')
                startIndex += 1
            }
        }

        if (isNegative && formatToken!!.isFlagSet(FormatToken.FLAG_PARENTHESIS)) {
            result = wrapParentheses(result)
            return result.toString()
        }

        if (isNegative && formatToken!!.isFlagSet(FormatToken.FLAG_ZERO)) {
            startIndex++
        }

        return padding(result, startIndex)
    }

    private fun wrapParentheses(result: StringBuilder): StringBuilder {
        result.deleteAt(0)
        result.insert(0, '(')

        if (formatToken!!.isFlagSet(FormatToken.FLAG_ZERO)) {
            formatToken!!.width -= 1
            padding(result, 1)
            result.append(')')
        } else {
            result.append(')')
            padding(result, 0)
        }

        return result
    }
}