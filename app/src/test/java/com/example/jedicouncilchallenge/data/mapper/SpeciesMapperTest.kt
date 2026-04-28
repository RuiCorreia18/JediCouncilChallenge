package com.example.jedicouncilchallenge.data.mapper

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.jedicouncilchallenge.data.remote.dto.SpeciesDto
import org.junit.jupiter.api.Test

class SpeciesMapperTest {

    @Test
    fun `toSpecies extracts id from url and maps name`() {
        val dto = SpeciesDto(
            url = "https://sw.simplr.sh/api/species/1.json",
            name = "Human"
        )
        val species = dto.toSpecies()
        assertThat(species.id).isEqualTo(1)
        assertThat(species.name).isEqualTo("Human")
    }
}
