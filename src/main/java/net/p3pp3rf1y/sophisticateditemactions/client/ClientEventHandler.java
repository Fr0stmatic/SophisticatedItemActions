package net.p3pp3rf1y.sophisticateditemactions.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.p3pp3rf1y.sophisticateditemactions.client.gui.ItemActionsTranslationHelper;
import net.p3pp3rf1y.sophisticateditemactions.client.render.EntityHighlightRenderer;
import net.p3pp3rf1y.sophisticateditemactions.client.render.ItemFlightAnimator;
import net.p3pp3rf1y.sophisticateditemactions.common.HighlightHandler;
import net.p3pp3rf1y.sophisticateditemactions.common.ItemTransferHandler;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.neoforged.neoforge.client.settings.KeyConflictContext.GUI;
import static net.neoforged.neoforge.client.settings.KeyConflictContext.IN_GAME;

public class ClientEventHandler {
	private static final String KEYBIND_SOPHISTICATEDCORE_CATEGORY = "key.category.sophisticateditemactions.main";
	public static final KeyMapping ITEM_HIGHLIGHT_KEYBIND = new KeyMapping(ItemActionsTranslationHelper.INSTANCE.translKeybind("item_highlight"),
			ClientEventHandler.ItemHighlightKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_SEMICOLON), KEYBIND_SOPHISTICATEDCORE_CATEGORY);
	public static final KeyMapping ITEM_DEPOSIT_KEYBIND = new KeyMapping(ItemActionsTranslationHelper.INSTANCE.translKeybind("deposit_item"),
			ClientEventHandler.ItemHighlightKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_APOSTROPHE), KEYBIND_SOPHISTICATEDCORE_CATEGORY);
	public static final KeyMapping ITEM_RESTOCK_KEYBIND = new KeyMapping(ItemActionsTranslationHelper.INSTANCE.translKeybind("restock_item"),
			ClientEventHandler.ItemHighlightKeyConflictContext.INSTANCE, InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_BACKSLASH), KEYBIND_SOPHISTICATEDCORE_CATEGORY);
	private static final List<Supplier<ItemStack>> HOVERED_STACK_SUPPLIERS = new ArrayList<>();

	public static void registerHoveredStackSupplier(Supplier<ItemStack> stackSupplier) {
		HOVERED_STACK_SUPPLIERS.add(stackSupplier);
	}

	public static void registerHandlers(IEventBus modBus) {
		modBus.addListener(ClientEventHandler::registerKeyMappings);

		IEventBus eventBus = NeoForge.EVENT_BUS;
		eventBus.addListener(ClientEventHandler::handleKeyInput);
		eventBus.addListener(ClientEventHandler::onPostClientTick);
		eventBus.addListener(ClientEventHandler::handleGuiKeyPress);
		eventBus.addListener(ClientEventHandler::handleGuiMouseKeyPress);
		eventBus.addListener(ClientEventHandler::renderLevelStage);
	}

	private static void renderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			return;
		}
		float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
		ItemFlightAnimator.render(event.getPoseStack(), partialTick, event.getCamera().getPosition());
		EntityHighlightRenderer.render(event.getPoseStack(), partialTick, event.getCamera().getPosition());
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(ITEM_HIGHLIGHT_KEYBIND);
		event.register(ITEM_DEPOSIT_KEYBIND);
		event.register(ITEM_RESTOCK_KEYBIND);
	}

	public static void handleGuiKeyPress(ScreenEvent.KeyPressed.Pre event) {
		InputConstants.Key key = InputConstants.getKey(event.getKeyCode(), event.getScanCode());
		if (ITEM_HIGHLIGHT_KEYBIND.isActiveAndMatches(key) && event.getScreen() instanceof AbstractContainerScreen<?> screen && tryHighlightItem(screen.getSlotUnderMouse())) {
			screen.onClose();
			event.setCanceled(true);
		}
	}

	public static void handleGuiMouseKeyPress(ScreenEvent.MouseButtonPressed.Pre event) {
		InputConstants.Key input = InputConstants.Type.MOUSE.getOrCreate(event.getButton());
		if (ITEM_HIGHLIGHT_KEYBIND.isActiveAndMatches(input) && event.getScreen() instanceof AbstractContainerScreen<?> screen && tryHighlightItem(screen.getSlotUnderMouse())) {
			screen.onClose();
			event.setCanceled(true);
		}
	}

	public static void onPostClientTick(ClientTickEvent.Post event) {
		if (ITEM_HIGHLIGHT_KEYBIND.consumeClick()) {
			tryHighlightItem();
		}
	}

	private static boolean tryHighlightItem(@Nullable Slot slot) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (slot == null || player == null || slot.getItem().isEmpty()) {
			return false;
		}

		HighlightHandler.highlightItem(player, slot.getItem());

		return true;
	}

	private static void tryHighlightItem() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || player.getMainHandItem().isEmpty()) {
			return;
		}

		HighlightHandler.highlightItem(player, player.getMainHandItem());
	}

	public static void handleKeyInput(InputEvent.Key event) {
		Screen screen = Minecraft.getInstance().screen;
		if (screen != null && screen.isFocused()) {
			return;
		}

		if (!ITEM_DEPOSIT_KEYBIND.isUnbound() && ITEM_DEPOSIT_KEYBIND.getKey().getValue() == event.getKey() && event.getAction() == GLFW.GLFW_PRESS) {
			tryDepositItem(event);
		} else if (!ITEM_RESTOCK_KEYBIND.isUnbound() && ITEM_RESTOCK_KEYBIND.getKey().getValue() == event.getKey() && event.getAction() == GLFW.GLFW_PRESS) {
			tryRestockItem(event);
		}
	}

	private static void tryRestockItem(InputEvent.Key event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		int mods = event.getModifiers();
		boolean mainInventory = (mods & GLFW.GLFW_MOD_SHIFT) != 0;
		boolean hotbar = (mods & GLFW.GLFW_MOD_ALT) != 0;
		boolean fillEmpty = (mods & GLFW.GLFW_MOD_CONTROL) != 0;

		Screen screen = Minecraft.getInstance().screen;
		ItemStack filter = getHoveredStack();
		int slot;
		if (filter.isEmpty()) {
			if (screen != null) {
				if (screen instanceof AbstractContainerScreen<?> containerScreen) {
					Slot slotUnderMouse = containerScreen.getSlotUnderMouse();
					if (slotUnderMouse != null && !slotUnderMouse.getItem().isEmpty()) {
						filter = slotUnderMouse.getItem();
						slot = slotUnderMouse.getSlotIndex();
					} else {
						return; // exit if not hovering over a slot
					}
				} else {
					return; // exit if not in a container screen
				}
			} else {
				filter = player.getMainHandItem();
				slot = player.getInventory().selected;
			}
		} else {
			slot = player.getInventory().getFreeSlot();
			fillEmpty = true;
			if (slot == -1) {
				return;
			}
		}
		if (mainInventory || hotbar) {
			ItemTransferHandler.restockMultipleItems(player, filter, mainInventory, hotbar, fillEmpty);
		} else {
			ItemTransferHandler.restockItem(player, filter, slot, fillEmpty);
		}
	}

	private static ItemStack getHoveredStack() {
		for (Supplier<ItemStack> supplier : HOVERED_STACK_SUPPLIERS) {
			ItemStack stack = supplier.get();
			if (!stack.isEmpty()) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static void tryDepositItem(InputEvent.Key event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		int mods = event.getModifiers();
		boolean mainInventory = (mods & GLFW.GLFW_MOD_SHIFT) != 0;
		boolean onlyMatching = (mods & GLFW.GLFW_MOD_CONTROL) == 0;
		boolean hotbar = (mods & GLFW.GLFW_MOD_ALT) != 0;

		if (mainInventory || hotbar) {
			tryDepositMultipleItems(player, mainInventory, hotbar, onlyMatching);
			return;
		}

		Screen screen = Minecraft.getInstance().screen;
		if (screen != null) {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				tryDepositItem(player, containerScreen.getSlotUnderMouse(), onlyMatching);
			}
		} else {
			tryDepositItem(player, onlyMatching);
		}
	}

	private static void tryDepositMultipleItems(Player player, boolean mainInventory, boolean hotbar, boolean onlyMatching) {
		ItemTransferHandler.depositMultipleItems(player, mainInventory, hotbar, onlyMatching);
	}

	private static void tryDepositItem(Player player, boolean onlyMatching) {
		ItemStack item = player.getMainHandItem();
		if (!item.isEmpty()) {
			ItemTransferHandler.depositItem(player, player.getInventory().selected, onlyMatching);
		}
	}

	private static boolean tryDepositItem(Player player, @Nullable Slot slot, boolean onlyMatching) {
		if (slot == null || slot.getItem().isEmpty() || !(slot.container instanceof Inventory)) {
			return false;
		}
		ItemTransferHandler.depositItem(player, slot.getSlotIndex(), onlyMatching);
		return true;
	}

	private static class ItemHighlightKeyConflictContext implements IKeyConflictContext {
		public static final ItemHighlightKeyConflictContext INSTANCE = new ItemHighlightKeyConflictContext();

		@Override
		public boolean isActive() {
			return (IN_GAME.isActive() && !Minecraft.getInstance().player.getMainHandItem().isEmpty()) || GUI.isActive();
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return this == other;
		}
	}
}
