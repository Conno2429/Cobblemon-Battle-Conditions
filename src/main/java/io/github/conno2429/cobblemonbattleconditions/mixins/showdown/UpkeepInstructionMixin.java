package io.github.conno2429.cobblemonbattleconditions.mixins.showdown;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.UpkeepInstruction;
import io.github.conno2429.cobblemonbattleconditions.battle.CBCInstructionsProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UpkeepInstruction.class)
public abstract class UpkeepInstructionMixin {
    @Inject(method = "invoke", at = @At("HEAD"), remap = false)
    private void fieldConditionCount(PokemonBattle battle, CallbackInfo ci) {
        CBCInstructionsProcessor.runFieldEffectTurns();
        System.out.println("Upkeep works lol");
    }
}
