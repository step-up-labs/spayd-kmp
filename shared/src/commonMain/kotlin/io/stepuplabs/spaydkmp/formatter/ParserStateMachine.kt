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
class ParserStateMachine(
    private val format: CharArrayBuffer,
) {
    val nextFormatToken: FormatToken
        get() {
            token = FormatToken()
            token!!.formatStringStartIndex = format.position()

            while (true) {
                if (EXIT_STATE != state) {
                    currentChar = nextFormatChar
                    if (EOS == currentChar
                        && ENTRY_STATE != state
                    ) {
                        throw Exception(
                            formatString
                        )
                    }
                }
                when (state) {
                    EXIT_STATE -> {
                        processExitState()
                        return token as FormatToken
                    }

                    ENTRY_STATE -> {
                        processEntryState()
                    }

                    START_CONVERSION_STATE -> {
                        processStartConversionState()
                    }

                    FLAGS_STATE -> {
                        processFlagsState()
                    }

                    WIDTH_STATE -> {
                        processWidthState()
                    }

                    PRECISION_STATE -> {
                        processPrecisionState()
                    }

                    CONVERSION_TYPE_STATE -> {
                        processConversionTypeState()
                    }

                    SUFFIX_STATE -> {
                        processSuffixState()
                    }
                }
            }
        }

    private var token: FormatToken? = null
    private var state = ENTRY_STATE
    private var currentChar = 0.toChar()
    private val nextFormatChar: Char
        get() = if (format.hasRemaining()) {
            format.get()
        } else {
            EOS
        }
    private val formatString: String
        get() {
            val end: Int = format.position()
            format.rewind()
            val formatString: String = format.subSequence(
                token!!.formatStringStartIndex, end
            ).toString()
            format.position(end)

            return formatString
        }

    fun reset() {
        currentChar = FormatToken.UNSET.toChar()
        state = ENTRY_STATE
        token = null
    }

    private fun processEntryState() {
        if (EOS == currentChar) {
            state = EXIT_STATE
        } else if ('%' == currentChar) {
            state = START_CONVERSION_STATE
        }
    }

    private fun processStartConversionState() {
        if (currentChar.isDigit()) {
            val position: Int = format.position() - 1
            val number = parseInt(format)
            var nextChar = 0.toChar()
            if (format.hasRemaining()) {
                nextChar = format.get()
            }
            if ('$' == nextChar) {
                if (number > 0) {
                    token!!.argIndex = number - 1
                } else if (number == FormatToken.UNSET) {
                    throw Exception(
                        formatString
                    )
                }
                state = FLAGS_STATE
            } else {
                if ('0' == currentChar) {
                    state = FLAGS_STATE
                    format.position(position)
                } else { // the digital sequence stands for the width.
                    state = WIDTH_STATE
                    // do not get the next char.
                    format.position(format.position() - 1)
                    token!!.width = number
                }
            }
            currentChar = nextChar
        } else if ('<' == currentChar) {
            state = FLAGS_STATE
            token!!.argIndex = FormatToken.LAST_ARGUMENT_INDEX
        } else {
            state = FLAGS_STATE
            format.position(format.position() - 1)
        }
    }

    private fun processFlagsState() {
        if (token!!.setFlag(currentChar)) {
            // empty
        } else if (currentChar.isDigit()) {
            token!!.width = parseInt(format)
            state = WIDTH_STATE
        } else if ('.' == currentChar) {
            state = PRECISION_STATE
        } else {
            state = CONVERSION_TYPE_STATE
            format.position(format.position() - 1)
        }
    }

    private fun processWidthState() {
        if ('.' == currentChar) {
            state = PRECISION_STATE
        } else {
            state = CONVERSION_TYPE_STATE
            // do not get the next char.
            format.position(format.position() - 1)
        }
    }

    private fun processPrecisionState() {
        if (currentChar.isDigit()) {
            token!!.precision = parseInt(format)
        } else {
            throw Exception(formatString)
        }
        state = CONVERSION_TYPE_STATE
    }

    private fun processConversionTypeState() {
        token!!.conversionType = currentChar
        state = if ('t' == currentChar || 'T' == currentChar) {
            SUFFIX_STATE
        } else {
            EXIT_STATE
        }
    }

    private fun processSuffixState() {
        token!!.dateSuffix = currentChar
        state = EXIT_STATE
    }

    private fun processExitState() {
        token!!.plainText = formatString
    }

    private fun parseInt(buffer: CharArrayBuffer): Int {
        val start: Int = buffer.position() - 1
        var end: Int = buffer.limit()

        while (buffer.hasRemaining()) {
            if (!buffer.get().isDigit()) {
                end = buffer.position() - 1
                break
            }
        }
        buffer.position(0)
        val intStr: String = buffer.subSequence(start, end).toString()
        buffer.position(end)
        return try {
            intStr.toInt()
        } catch (e: NumberFormatException) {
            FormatToken.UNSET
        }
    }

    companion object {
        private const val EOS = (-1).toChar()
        private const val EXIT_STATE = 0
        private const val ENTRY_STATE = 1
        private const val START_CONVERSION_STATE = 2
        private const val FLAGS_STATE = 3
        private const val WIDTH_STATE = 4
        private const val PRECISION_STATE = 5
        private const val CONVERSION_TYPE_STATE = 6
        private const val SUFFIX_STATE = 7
    }
}