package io.github.conno2429.cobblemonbattleconditions.mixins.client;


import com.cobblemon.mod.common.client.gui.battle.BattleOverlay;
import io.github.conno2429.cobblemonbattleconditions.client.BattleConditionsOverlay;
import io.github.conno2429.cobblemonbattleconditions.client.CobblemonBattleConditionsClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BattleOverlay.class)
public abstract class ConditionsDisplayGUIMixin {

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void conditionsRenderMixin(GuiGraphics context, DeltaTracker tickDelta, CallbackInfo ci) {
        if (CobblemonBattleConditionsClient.INSTANCE.getToggleConditions()) {
            BattleConditionsOverlay.INSTANCE.renderConditionsOverlay(context);
        }
    }
}
