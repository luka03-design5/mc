package dev.axiom.mixin;

import dev.axiom.modules.KnifeCosmetics;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void axiom$inspect(
        AbstractClientPlayerEntity player,
        float tickProgress,
        float pitch,
        Hand hand,
        float swingProgress,
        ItemStack item,
        float equipProgress,
        MatrixStack matrices,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        int light,
        CallbackInfo ci
    ) {
        if (KnifeCosmetics.inspectTicks <= 0) return;
        if (hand != Hand.MAIN_HAND) return;

        float t = KnifeCosmetics.inspectTicks - tickProgress;
        float p = 1f - Math.max(0f, Math.min(1f, t / 36f));
        float spin = (float) (Math.sin(p * Math.PI) * 110f);
        float lift = (float) (Math.sin(p * Math.PI) * 0.25f);
        float tilt = (float) (Math.sin(p * Math.PI * 2) * 18f);

        matrices.translate(0.0, lift, 0.05);
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(spin));
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(tilt));
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(spin * 0.35f));
    }
}
