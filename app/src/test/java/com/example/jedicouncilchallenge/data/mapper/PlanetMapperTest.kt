package com.example.jedicouncilchallenge.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.jedicouncilchallenge.data.remote.dto.PlanetDto
import org.junit.jupiter.api.Test

class PlanetMapperTest {

    private val dto = PlanetDto(
        url = "https://sw.simplr.sh/api/planets/1.json",
        name = "Tatooine",
        climate = "arid",
        terrain = "desert",
        population = "200000"
    )

    @Test
    fun `toPlanet extracts id from url`() {
        assertThat(dto.toPlanet().id).isEqualTo(1)
    }

    @Test
    fun `toPlanet maps all fields`() {
        val planet = dto.toPlanet()
        assertThat(planet.name).isEqualTo("Tatooine")
        assertThat(planet.climate).isEqualTo("arid")
        assertThat(planet.terrain).isEqualTo("desert")
        assertThat(planet.population).isEqualTo("200000")
    }
}
