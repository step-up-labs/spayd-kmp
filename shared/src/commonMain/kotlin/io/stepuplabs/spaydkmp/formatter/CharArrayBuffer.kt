package io.stepuplabs.spaydkmp.formatter

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
class CharArrayBuffer : Buffer {
    private val backingArray: CharArray
    private val offset: Int

    constructor(array: CharArray) : this(array.size, array, 0)
    constructor(capacity: Int, backingArray: CharArray, offset: Int) : super(capacity) {
        this.backingArray = backingArray
        this.offset = offset
    }

    override fun toString(): String {
        val offset1 = offset + position

        return backingArray.concatToString(offset1, offset1 + remaining())
    }

    fun get(): Char {
        if (position == limit) {
            throw Exception()
        }

        return backingArray[offset + position++]
    }

    fun subSequence(start: Int, end: Int): CharArrayBuffer {
        if (start < 0 || end < start || end > remaining()) {
            throw IndexOutOfBoundsException()
        }

        val result: CharArrayBuffer = duplicate()
        result.limit(position + end)
        result.position(position + start)

        return result
    }

    operator fun get(index: Int): Char {
        if (index < 0 || index >= limit) {
            throw IndexOutOfBoundsException()
        }
        return backingArray[offset + index]
    }

    operator fun get(dest: CharArray, off: Int, len: Int): CharArrayBuffer {
        val length = dest.size

        if (off < 0 || len < 0 || off.toLong() + len.toLong() > length) {
            throw IndexOutOfBoundsException()
        }
        if (len > remaining()) {
            throw Exception()
        }

        backingArray.copyInto(
            dest,
            off,
            offset + position,
            offset + position + len,
        )
        position += len

        return this
    }

    private fun copy(other: CharArrayBuffer, markOfOther: Int): CharArrayBuffer {
        val buf = CharArrayBuffer(other.capacity, other.backingArray, other.offset)
        buf.limit = other.limit
        buf.position = other.position
        buf.mark = markOfOther

        return buf
    }

    private fun duplicate(): CharArrayBuffer = copy(this, mark)
}