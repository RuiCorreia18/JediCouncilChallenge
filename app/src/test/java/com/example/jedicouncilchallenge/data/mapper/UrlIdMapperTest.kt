package com.example.jedicouncilchallenge.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test

class UrlIdMapperTest {

    @Test
    fun `resourceId extracts single-digit id`() {
        assertThat("https://sw.simplr.sh/api/people/1.json".resourceId()).isEqualTo(1)
    }

    @Test
    fun `resourceId extracts multi-digit id`() {
        assertThat("https://sw.simplr.sh/api/planets/42.json".resourceId()).isEqualTo(42)
    }

    @Test
    fun `resourceId fails for malformed url`() {
        assertThrows<IllegalStateException> {
            "not-a-url".resourceId()
        }
    }

    @Test
    fun `resourceIdOrNull returns null for null input`() {
        val url: String? = null
        assertThat(url.resourceIdOrNull()).isNull()
    }

    @Test
    fun `resourceIdOrNull returns null for malformed url`() {
        assertThat("no-id-here".resourceIdOrNull()).isNull()
    }

    @Test
    fun `resourceIdOrNull returns id for valid url`() {
        assertThat("https://sw.simplr.sh/api/starships/9.json".resourceIdOrNull()).isEqualTo(9)
    }
}
