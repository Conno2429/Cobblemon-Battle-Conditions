package io.github.conno2429.cobblemonbattleconditions.mixins.showdown;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.interpreter.instructions.TurnInstruction;
import io.github.conno2429.cobblemonbattleconditions.battle.BattleConditionsProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurnInstruction.class)
public abstract class TurnInstructionMixin {
    @Shadow(remap = false) public abstract BattleMessage getMessage();

    @Inject(method = "invoke", at = @At("TAIL"), remap = false)
    private void sideStartTrack(PokemonBattle battle, CallbackInfo ci) {
        if (battle.getTurn() >= 1) {
            battle.dispatchWaiting(0F, () -> {
                BattleConditionsProcessor.updateCounters(battle);
                return null;
            });
        }
    }
}
