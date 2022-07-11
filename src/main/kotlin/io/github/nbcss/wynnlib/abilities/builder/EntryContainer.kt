package io.github.nbcss.wynnlib.abilities.builder

import io.github.nbcss.wynnlib.abilities.Ability
import io.github.nbcss.wynnlib.abilities.AbilityMetadata
import io.github.nbcss.wynnlib.abilities.builder.entries.ExtendEntry
import io.github.nbcss.wynnlib.abilities.builder.entries.PropertyEntry
import io.github.nbcss.wynnlib.abilities.builder.entries.ReplaceSpellEntry
import io.github.nbcss.wynnlib.abilities.builder.entries.SpellEntry
import io.github.nbcss.wynnlib.abilities.properties.info.BoundSpellProperty

class EntryContainer(abilities: Collection<Ability> = emptyList()) {
    private val entries: MutableMap<String, PropertyEntry>
    init {
        entries = LinkedHashMap()
        val spells: MutableSet<AbilityMetadata> = HashSet()
        val replaces: MutableSet<AbilityMetadata> = HashSet()
        val extending: MutableSet<AbilityMetadata> = HashSet()
        val dummy: MutableSet<AbilityMetadata> = HashSet()
        for (ability in abilities) {
            ability.getMetadata()?.let {
                when (it.getFactory().getKey()) {
                    SpellEntry.getKey() -> {spells.add(it)}
                    ReplaceSpellEntry.getKey() -> {replaces.add(it)}
                    ExtendEntry.getKey() -> {extending.add(it)}
                    else -> {dummy.add(it)}
                }
            }
        }
        spells.sortedBy { BoundSpellProperty.from(it.ability)?.getSpell()?.ordinal ?: 99 }
            .mapNotNull {it.createEntry(this)}.forEach { putEntry(it) }
        replaces.mapNotNull {it.createEntry(this)}.forEach { putEntry(it) }
        dummy.mapNotNull {it.createEntry(this)}.forEach { putEntry(it) }
        var keys: List<AbilityMetadata>
        do {
            keys = extending.toList()
            for (meta in keys) {
                val entry = meta.createEntry(this)
                if (entry != null){
                    putEntry(entry)
                    extending.remove(meta)
                }
            }
        }while (extending.isNotEmpty() && extending.size < keys.size)
        //we can warn something if extending is still not empty here?
        abilities.forEach { it.updateEntries(this) }
    }

    fun putEntry(entry: PropertyEntry) {
        //println("put " + entry.getAbility().getKey())
        entries[entry.getKey()] = entry
    }

    fun getEntry(key: String): PropertyEntry? {
        return entries[key]
    }

    fun getEntries(): List<PropertyEntry> {
        return entries.values.toList()
    }

    fun getSize(): Int = entries.size
}