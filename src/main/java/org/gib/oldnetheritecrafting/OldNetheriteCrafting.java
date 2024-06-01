package org.gib.oldnetheritecrafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class OldNetheriteCrafting extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack[] contents = event.getInventory().getContents();
        if (contents == null || contents.length < 3) return;

        ItemStack base = contents[1];
        ItemStack addition = contents[2];

        if (base == null || base.getType() == Material.AIR || addition == null) {
            return;
        }

        Material resultType = null;
        switch (base.getType()) {
            case DIAMOND_SWORD:
                resultType = Material.NETHERITE_SWORD;
                break;
            case DIAMOND_PICKAXE:
                resultType = Material.NETHERITE_PICKAXE;
                break;
            case DIAMOND_AXE:
                resultType = Material.NETHERITE_AXE;
                break;
            case DIAMOND_SHOVEL:
                resultType = Material.NETHERITE_SHOVEL;
                break;
            case DIAMOND_HOE:
                resultType = Material.NETHERITE_HOE;
                break;
            case DIAMOND_HELMET:
                resultType = Material.NETHERITE_HELMET;
                break;
            case DIAMOND_CHESTPLATE:
                resultType = Material.NETHERITE_CHESTPLATE;
                break;
            case DIAMOND_LEGGINGS:
                resultType = Material.NETHERITE_LEGGINGS;
                break;
            case DIAMOND_BOOTS:
                resultType = Material.NETHERITE_BOOTS;
                break;
            default:
                break;
        }

        if (resultType != null && addition.getType() == Material.NETHERITE_INGOT) {
            ItemStack result = new ItemStack(resultType);
            result.setItemMeta(base.getItemMeta());
            event.setResult(result);
        }
    }

    @EventHandler
    public void onSmithingTableClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != org.bukkit.event.inventory.InventoryType.SMITHING) {
            return;
        }

        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
            ItemStack resultItem = event.getCurrentItem();
            if (resultItem != null && resultItem.getType().name().startsWith("NETHERITE_")) {
                // Transfer the result item to player's inventory
                PlayerInventory playerInventory = event.getWhoClicked().getInventory();
                if (playerInventory.firstEmpty() >= 0) {
                    playerInventory.addItem(resultItem);

                    // Remove the original items from the smithing table
                    Inventory smithingInventory = event.getClickedInventory();
                    smithingInventory.setItem(1, null); // Remove the base item
                    smithingInventory.setItem(2, null); // Remove the netherite ingot

                    // Deduct one netherite ingot from the player's inventory
                    Player player = (Player) event.getWhoClicked();
                    ItemStack netheriteIngot = new ItemStack(Material.NETHERITE_INGOT, 1);
                    player.getInventory().removeItem(netheriteIngot);
                }
            }
        } else if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.CRAFTING) {
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // Prevent adding tools to the other slot of the smithing table
                if (event.getSlot() == 2) {
                    // Cancel the event if the player tries to add more than one netherite ingot
                    if (clickedItem.getAmount() > 1) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
