package hedeantheonlyone.github.io.nbt_viewer;

import net.minecraft.client.option.KeyBinding;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;


public class NbtViewerClient implements ClientModInitializer {

	private boolean showItemNbt = true;

	public static final KeyBinding toggleNbt = KeyBindingHelper.registerKeyBinding( new KeyBinding(
		"key.nbt_viewer.toggle_nbt",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		"category.nbt_viewer.nbt_viewer"));

	private boolean applyIndentation = true;

	public static final KeyBinding toggleIndentation = KeyBindingHelper.registerKeyBinding( new KeyBinding(
		"key.nbt_viewer.toggle_indentation",
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		"category.nbt_viewer.nbt_viewer"));




	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleNbt.wasPressed()) {

				showItemNbt = !showItemNbt;
				String msg = showItemNbt ? "setting.nbt_viewer.show_item_nbt_true" : "setting.nbt_viewer.show_item_nbt_false";
				client.player.sendMessage(Text.translatable(msg));
			}

			while (toggleIndentation.wasPressed()) {

				applyIndentation = !applyIndentation;
				String msg = applyIndentation ? "setting.nbt_viewer.apply_indentation_true" : "setting.nbt_viewer.apply_indentation_false";
				client.player.sendMessage(Text.translatable(msg));
			}
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			
			if (!showItemNbt)
				return;

			NbtCompound nbt = stack.getOrCreateNbt();

			if (nbt.isEmpty())
				return;

			List<Text> a = colorizeNbt(nbt.asString());

			for (Text b : a) {
				lines.add(b);
			}

			//lines = a;
		});
	}



	private List<Text> colorizeNbt(String nbt) {

		List<Text> nbtLines = new ArrayList<Text>();

		StringBuilder colorNbt = new StringBuilder();
		int pointer = 0;
		int indentation = 0;

		while (pointer < nbt.length()) {
			
			switch (nbt.charAt(pointer)) {
				
				case '{':
				case '[':
				case '(':
				case ',':
					if (applyIndentation) {

						if (nbt.charAt(pointer) != ',')
							indentation++;

						colorNbt.append("§f" + nbt.charAt(pointer));
						nbtLines.add(Text.literal(colorNbt.toString()));

						colorNbt = new StringBuilder();
						colorNbt.append(addPadding(indentation));
					}
					else
						colorNbt.append("§f" + nbt.charAt(pointer));

					break;
				
				case '}':
				case ']':
				case ')':
					if (applyIndentation) {

						indentation--;
						nbtLines.add(Text.literal(colorNbt.toString()));

						colorNbt = new StringBuilder();
						colorNbt.append(addPadding(indentation) + "§f" + nbt.charAt(pointer));
					}
					else
						colorNbt.append("§f" + nbt.charAt(pointer));

					break;

				case ':':
					colorNbt.append("§f" + nbt.charAt(pointer));
					pointer++;

					if (Character.isDigit(nbt.charAt(pointer))) {

						while (Character.isDigit(nbt.charAt(pointer))) {
							
							colorNbt.append("§6" + nbt.charAt(pointer));
							pointer++;
						}
						
						if (nbt.charAt(pointer) != ',' && nbt.charAt(pointer) != '}')
							colorNbt.append("§c" + nbt.charAt(pointer));
					}
					else
						continue;
					break;
				
				case '"':
					colorNbt.append("§f" + nbt.charAt(pointer));
					pointer++;

					while (nbt.charAt(pointer) != '"') {
						colorNbt.append("§a" + nbt.charAt(pointer));
						pointer++;
					}

					colorNbt.append("§f" + nbt.charAt(pointer));
					break;
				
				default:
					colorNbt.append("§b" + nbt.charAt(pointer));
			}

			pointer++;
		}
		
		nbtLines.add(Text.literal(colorNbt.toString()));

		return nbtLines;
	}



	private String addPadding(int width) {

		StringBuilder padding = new StringBuilder();

		for (int i = 0; i < width; i++)
			padding.append(" ");

		return padding.toString();
	}
}