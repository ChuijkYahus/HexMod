package at.petrak.hexcasting.api.spell.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.LegacySpellDatum
import at.petrak.hexcasting.api.spell.SpellList
import at.petrak.hexcasting.api.spell.casting.CastingContext
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor

class MishapOthersName(val other: Player) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer =
        dyeColor(DyeColor.BLACK)

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<LegacySpellDatum<*>>) {
        ctx.caster.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 20 * 60))
    }

    override fun errorMessage(ctx: CastingContext, errorCtx: Context) =
        error("others_name", other.name)

    companion object {
        @JvmStatic
        fun getTrueNameFromDatum(datum: LegacySpellDatum<*>, caster: Player): Player? {
            if (datum.payload is Player && datum.payload != caster)
                return datum.payload
            else if (datum.payload !is SpellList)
                return null

            val poolToSearch: MutableList<LegacySpellDatum<*>> =
                datum.payload.filterIsInstance<LegacySpellDatum<*>>().toMutableList()

            while (poolToSearch.isNotEmpty()) {
                val datumToCheck = poolToSearch[0]
                poolToSearch.removeAt(0)

                if (datumToCheck.payload is Player && datumToCheck.payload != caster)
                    return datumToCheck.payload
                else if (datumToCheck.payload is SpellList)
                    poolToSearch.addAll(datumToCheck.payload.filterIsInstance<LegacySpellDatum<*>>())
            }

            return null
        }

        @JvmStatic
        fun getTrueNameFromArgs(datums: List<LegacySpellDatum<*>>, caster: Player): Player? {
            return datums.firstNotNullOfOrNull { getTrueNameFromDatum(it, caster) }
        }
    }
}
