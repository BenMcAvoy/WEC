package benmcavoy.wecclient.mixin.client;

import benmcavoy.wecclient.MouseMixinAccess;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin implements MouseMixinAccess {
    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Unique
    public Vec3i pos1 = null;
    @Unique
    public Vec3i pos2 = null;

    @Inject(at = @At("HEAD"), method = "onMouseButton")
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        ClientPlayerEntity player = client.player;

        // If we pressed a mouse button
        if (action != 1
                || player == null
                || client.world == null
                || client.crosshairTarget == null
                || client.crosshairTarget.getType() != HitResult.Type.BLOCK
                || player.getMainHandStack().getItem() != Items.WOODEN_SWORD
                || client.crosshairTarget.getPos().distanceTo(player.getPos()) > 5) {
            return;
        }

        // If we pressed the middle mouse button (2) then reset the positions
        if (button == 2) {
            client.inGameHud.getChatHud().addMessage(Text.of("WEC » Reset"));
            pos1 = null;
            pos2 = null;
        }

        // Get the block we are looking at
        Vec3i hitVec = ((BlockHitResult) client.crosshairTarget).getBlockPos();

        // Set position 1 or 2
        if (button == 0) {
            client.inGameHud.getChatHud().addMessage(Text.of("WEC » Position 1 set"));
            pos1 = hitVec;
        } else if (button == 1) {
            client.inGameHud.getChatHud().addMessage(Text.of("WEC » Position 2 set"));
            pos2 = hitVec;
        }
    }

    @Override
    public Pair<Vec3i, Vec3i> wecclient$access() {
        return new Pair<>(pos1, pos2);
    }
}