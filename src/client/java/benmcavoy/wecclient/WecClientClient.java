package benmcavoy.wecclient;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WecClientClient implements ClientModInitializer {
	public static final String MOD_ID = "wec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralCommandNode<FabricClientCommandSource> wecCommand = ClientCommandManager
					.literal("set")
					.build();

			ArgumentCommandNode<FabricClientCommandSource, BlockStateArgument> setCommand = ClientCommandManager
					.argument("block", BlockStateArgumentType.blockState(registryAccess))
					.executes((ctx) -> {
						Pair<Vec3i, Vec3i> pos = ((MouseMixinAccess) MinecraftClient.getInstance().mouse).wecclient$access();
						Vec3i pos1 = pos.getLeft();
						Vec3i pos2 = pos.getRight();

						BlockState block = ctx.getArgument("block", BlockStateArgument.class).getBlockState();
						String blockName = block.toString();

						String[] split = blockName.split("}", 2);
						String blockNameStripped = split[0].substring(6);
						String blockStateData = split.length > 1 ? split[1] : "";

						assert MinecraftClient.getInstance().player != null;
						String command = String.format("fill %d %d %d %d %d %d %s%s", pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), blockNameStripped, blockStateData);

                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);

						return Command.SINGLE_SUCCESS;
					})
					.build();

			dispatcher.getRoot().addChild(wecCommand);
			wecCommand.addChild(setCommand);
		});

		LOGGER.info("Initializing WEC");
	}
}
