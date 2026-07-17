package dev.doraemon.item;

import dev.doraemon.DoraemonMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {

	public static final Item DORAYAKI = register("dorayaki", new Item(new Item.Settings().maxCount(64)));
	public static final Item SUMMONING_BELL = register("summoning_bell", new SummoningBellItem(new Item.Settings().maxCount(1)));

	private ModItems() {
	}

	private static Item register(String path, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(DoraemonMod.MOD_ID, path), item);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(DORAYAKI));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(SUMMONING_BELL));
	}
}
