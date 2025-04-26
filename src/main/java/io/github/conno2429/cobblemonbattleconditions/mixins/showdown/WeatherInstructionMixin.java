package io.github.conno2429.cobblemonbattleconditions.mixins.showdown;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.WeatherInstruction;
import io.github.conno2429.cobblemonbattleconditions.battle.CBCInstructionsProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherInstruction.class)
public abstract class WeatherInstructionMixin {
    @Shadow(remap = false) public abstract BattleMessage getMessage();

    @Inject(method = "invoke", at = @At("TAIL"), remap = false)
    private void weatherCount(PokemonBattle battle, CallbackInfo ci) {
        CBCInstructionsProcessor.processWeatherTurns(battle, getMessage());
    }
}
