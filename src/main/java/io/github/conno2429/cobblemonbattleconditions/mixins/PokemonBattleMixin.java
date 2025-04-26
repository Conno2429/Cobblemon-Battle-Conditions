package io.github.conno2429.cobblemonbattleconditions.mixins;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import io.github.conno2429.cobblemonbattleconditions.battle.CBCInstructionsProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(PokemonBattle.class)
public abstract class PokemonBattleMixin {
    @Inject(method = "end", at = @At("TAIL"), remap = false)
    private void injectEnd(CallbackInfo ci) {
        CBCInstructionsProcessor.processBattleEnd();
    }

}