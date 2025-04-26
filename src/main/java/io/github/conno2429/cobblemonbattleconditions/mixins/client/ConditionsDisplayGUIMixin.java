package io.github.conno2429.cobblemonbattleconditions.mixins.client;

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import io.github.conno2429.cobblemonbattleconditions.battle.CBCInstructionsProcessor;
import java.util.List;
import kotlin.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



import static com.cobblemon.mod.common.api.gui.GuiUtilsKt.blitk;

@Mixin(BattleOverlay.class)
public abstract class ConditionsDisplayGUIMixin {

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void conditionsRenderMixin(DrawContext context, float tickDelta, CallbackInfo ci) {
        List<Pair<String, Integer>> fieldList = CBCInstructionsProcessor.INSTANCE.getFieldList();

        Identifier textureLocation = new Identifier("cobblemon", "textures/gui/battle/battle_log_expanded.png");

        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        int textureWidth = 169;
        int textureHeight = 101;

        int x = screenWidth - 181;
        int y = (screenHeight - textureHeight) / 2;

        double opacity = CobblemonClient.INSTANCE.getBattleOverlay().getOpacity();

        Identifier font = CobblemonResources.INSTANCE.getDEFAULT_LARGE();


        int alpha = (int)(opacity * 255) << 24;
        int color = 0x00FFFFFF | alpha;

        int textX = x + 13;
        int textY = y + 6;
        int lineHeight = 10;

        blitk(context.getMatrices(), textureLocation, x, y, textureHeight, textureWidth, 0, 0, textureWidth, textureHeight, 0, 1, 1, 1, opacity, true, 1.0f);
        context.enableScissor(x + 5, y + 6, x + 174, y + 98);
        for (int i = 0; i < fieldList.size(); i++) {
            final int finalI = i;
            Pair<String, Integer> effectPair = fieldList.get(i);
            String effect = effectPair.getFirst();
            int turnCounter = effectPair.getSecond();

            String rawText = "";
            if (effect.contains("Terrain")) {
                if (turnCounter < 5) {
                    rawText = effect + " turns remaining: " + (8 - turnCounter) + " or " + (5 - turnCounter);
                } else {
                    rawText = effect + " turns remaining: " + (5 - turnCounter);
                }
            } else {
                rawText = effect + " turns remaining: " + (5 - (turnCounter + 1));
            }
            MutableText mutable = Text.literal(rawText).formatted(Formatting.BOLD);
            RenderHelperKt.drawScaledText(context, font, mutable, textX, textY + finalI * lineHeight, 1.0f, opacity, Integer.MAX_VALUE, color, false, false, null, null);
        }

        context.disableScissor();
    }
}
