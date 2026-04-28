package com.example.jedicouncilchallenge.data.mapper

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.example.jedicouncilchallenge.data.remote.dto.CharacterDto
import org.junit.jupiter.api.Test

class CharacterMapperTest {

    private val minimalDto = CharacterDto(
        url = "https://sw.simplr.sh/api/people/1.json",
        name = "Luke Skywalker",
        gender = "male",
        birthYear = "19BBY",
        height = "172",
        mass = "77",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        species = emptyList(),
        films = emptyList(),
        starships = emptyList(),
        homeworld = null
    )

    @Test
    fun `toCharacter extracts id from url`() {
        assertThat(minimalDto.toCharacter().id).isEqualTo(1)
    }

    @Test
    fun `toCharacter maps all scalar fields`() {
        val character = minimalDto.toCharacter()
        assertThat(character.name).isEqualTo("Luke Skywalker")
        assertThat(character.gender).isEqualTo("male")
        assertThat(character.birthYear).isEqualTo("19BBY")
        assertThat(character.height).isEqualTo("172")
        assertThat(character.mass).isEqualTo("77")
        assertThat(character.hairColor).isEqualTo("blond")
        assertThat(character.skinColor).isEqualTo("fair")
        assertThat(character.eyeColor).isEqualTo("blue")
    }

    @Test
    fun `toCharacter extracts ids from relationship url lists`() {
        val dto = minimalDto.copy(
            species = listOf("https://sw.simplr.sh/api/species/1.json"),
            films = listOf(
                "https://sw.simplr.sh/api/films/1.json",
                "https://sw.simplr.sh/api/films/3.json"
            ),
            starships = listOf("https://sw.simplr.sh/api/starships/12.json")
        )
        val character = dto.toCharacter()
        assertThat(character.speciesIds).containsExactly(1)
        assertThat(character.filmIds).containsExactly(1, 3)
        assertThat(character.starshipIds).containsExactly(12)
    }

    @Test
    fun `toCharacter maps null homeworld to null homeworldId`() {
        assertThat(minimalDto.copy(homeworld = null).toCharacter().homeworldId).isNull()
    }

    @Test
    fun `toCharacter extracts homeworldId from url`() {
        val dto = minimalDto.copy(homeworld = "https://sw.simplr.sh/api/planets/1.json")
        assertThat(dto.toCharacter().homeworldId).isEqualTo(1)
    }
}
