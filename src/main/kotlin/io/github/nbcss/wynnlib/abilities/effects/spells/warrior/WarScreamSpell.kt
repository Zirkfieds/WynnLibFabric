package io.github.nbcss.wynnlib.abilities.effects.spells.warrior

import com.google.gson.JsonObject
import io.github.nbcss.wynnlib.abilities.display.*
import io.github.nbcss.wynnlib.abilities.effects.AbilityEffect
import io.github.nbcss.wynnlib.abilities.effects.spells.SpellUnlock
import io.github.nbcss.wynnlib.abilities.properties.AreaOfEffectProperty
import io.github.nbcss.wynnlib.abilities.properties.DamageProperty
import io.github.nbcss.wynnlib.abilities.properties.DurationProperty

class WarScreamSpell(json: JsonObject): SpellUnlock(json), DamageProperty, DurationProperty, AreaOfEffectProperty {
    companion object: AbilityEffect.Factory {
        override fun create(properties: JsonObject): WarScreamSpell {
            return WarScreamSpell(properties)
        }
    }
    private val damage: DamageProperty.Damage = DamageProperty.read(json)
    private val duration: Double = DurationProperty.read(json)
    private val aoe: AreaOfEffectProperty.AreaOfEffect = AreaOfEffectProperty.read(json)

    override fun getDamage(): DamageProperty.Damage = damage

    override fun getDuration(): Double = duration

    override fun getAreaOfEffect(): AreaOfEffectProperty.AreaOfEffect = aoe

    override fun getTooltipItems(): List<EffectTooltip> {
        return listOf(ManaCostTooltip, DamageTooltip, DurationTooltip, AreaOfEffectTooltip)
    }
}