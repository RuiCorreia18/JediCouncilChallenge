package com.example.jedicouncilchallenge.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.jedicouncilchallenge.data.remote.dto.StarshipDto
import org.junit.jupiter.api.Test

class StarshipMapperTest {

    private val dto = StarshipDto(
        url = "https://sw.simplr.sh/api/starships/12.json",
        name = "X-wing",
        model = "T-65 X-wing",
        manufacturer = "Incom Corporation",
        starshipClass = "Starfighter"
    )

    @Test
    fun `toStarship extracts id from url`() {
        assertThat(dto.toStarship().id).isEqualTo(12)
    }

    @Test
    fun `toStarship maps all fields`() {
        val starship = dto.toStarship()
        assertThat(starship.name).isEqualTo("X-wing")
        assertThat(starship.model).isEqualTo("T-65 X-wing")
        assertThat(starship.manufacturer).isEqualTo("Incom Corporation")
        assertThat(starship.starshipClass).isEqualTo("Starfighter")
    }
}
