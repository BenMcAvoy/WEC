package benmcavoy.wecclient.mixin.client;

import benmcavoy.wecclient.MouseMixinAccess;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.Keyboard;

import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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
	public Vec3d pos1 = null;
	@Unique
	public Vec3d pos2 = null;

	@Inject(at = @At("HEAD"), method = "onMouseButton")
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
		// If we pressed a mouse button
		if (action != 1)
			return;

		// If we pressed the middle mouse button (2) then reset the positions
		if (button == 2) {
			client.inGameHud.getChatHud().addMessage(Text.of("Reset"));
			pos1 = null;
			pos2 = null;
		}

		// If we have a client
		if (client.player == null)
			return;

		// If we are holding a wood sword
		if (client.player.getMainHandStack().getItem() != net.minecraft.item.Items.WOODEN_SWORD)
			return;

		// If we have a crosshair target
		if (client.crosshairTarget == null)
			return;

		// Check if it's a block and in range
		if (client.crosshairTarget.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK)
			return;

		// If we have a world
		if (client.world == null)
			return;

		// Get the block we are looking at
		BlockHitResult hit = (BlockHitResult) client.crosshairTarget;

		// Get the coordinates of the block and not the coordinates of the mouse on the block
		Vec3d hitVec = hit.getBlockPos().toCenterPos();

		Vec3d d = new Vec3d(hitVec.x, hitVec.y, hitVec.z - 1);

		if (hit.getType() != HitResult.Type.BLOCK)
			return;

		// Set position 1 or 2
		if (button == 0) {
			client.inGameHud.getChatHud().addMessage(Text.of("Position 1 set"));
			pos1 = d;
		} else if (button == 1) {
			client.inGameHud.getChatHud().addMessage(Text.of("Position 2 set"));
			pos2 = d;
		}
	}

	@Override
	public Pair<Vec3d, Vec3d> wecclient$access() {
		return new Pair<>(pos1, pos2);
	}
}