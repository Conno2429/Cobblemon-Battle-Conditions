package io.github.conno2429.cobblemonbattleconditions.mixins.showdown;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.SwitchInstruction;
import io.github.conno2429.cobblemonbattleconditions.battle.BattleConditionsProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwitchInstruction.class)
public abstract class SwitchInstructionMixin {

    @Inject(method = "invoke", at = @At("TAIL"), remap = false)
    private void afterInvoke(PokemonBattle battle, CallbackInfo ci) {
        battle.dispatchWaiting(0F, () -> {
            BattleConditionsProcessor.processWeatherDisabling(battle);
            return null;
        });
    }
}