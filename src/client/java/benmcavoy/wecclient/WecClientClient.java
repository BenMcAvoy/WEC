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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WecClientClient implements ClientModInitializer {
	public static final String MOD_ID = "wec";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralCommandNode<FabricClientCommandSource> wecCommand = ClientCommandManager
					.literal("wec")
					.build();

			ArgumentCommandNode<FabricClientCommandSource, BlockStateArgument> setCommand = ClientCommandManager
					.argument("block", BlockStateArgumentType.blockState(registryAccess))
					.executes((ctx) -> {
						Pair<Vec3d, Vec3d> pos = ((MouseMixinAccess) MinecraftClient.getInstance().mouse).wecclient$access();
						Vec3d pos1 = pos.getLeft();
						Vec3d pos2 = pos.getRight();

						BlockState block = ctx.getArgument("block", BlockStateArgument.class).getBlockState();
						String blockName = block.toString();
						MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(blockName));

						String[] split = blockName.split("}", 2);
						String blockNameStripped = split[0].substring(6);
						String blockStateData = split.length > 1 ? split[1] : "";

						assert MinecraftClient.getInstance().player != null;
						String command = String.format("fill %d %d %d %d %d %d %s%s", (int) pos1.x, (int) pos1.y, (int) pos1.z, (int) pos2.x, (int) pos2.y, (int) pos2.z, blockNameStripped, blockStateData);

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
