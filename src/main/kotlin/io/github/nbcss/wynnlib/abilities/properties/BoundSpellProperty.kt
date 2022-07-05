package io.github.nbcss.wynnlib.abilities.properties

import com.google.gson.JsonElement
import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.data.SpellSlot

class BoundSpellProperty(ability: Ability, data: JsonElement): AbilityProperty(ability) {
    companion object: Factory {
        override fun create(ability: Ability, data: JsonElement): AbilityProperty {
            return BoundSpellProperty(ability, data)
        }
        override fun getKey(): String = "spell"
    }
    private val spell: SpellSlot = SpellSlot.fromName(data.asString) ?: SpellSlot.SPELL_1

    fun getSpell(): SpellSlot = spell
}