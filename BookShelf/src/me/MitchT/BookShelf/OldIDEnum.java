package me.MitchT.BookShelf;

import org.bukkit.Material;

import com.google.common.collect.HashBiMap;

// Thanks, Adam753!
/**
 * 
 * BookShelf - A Bukkit & Spigot mod allowing the placement of items
 * into BookShelves. <br>
 * Copyright (C) 2012-2014 Mitch Talmadge (mitcht@aptitekk.com)<br>
 * <br>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.<br>
 * <br>
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br>
 * <br>
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * @author Mitch Talmadge (mitcht@aptitekk.com)
 */
public abstract class OldIDEnum
{
    private static HashBiMap<Integer, Material> materials = OldIDEnum
            .setupMap();
    
    public static Material getMaterialById(int id)
    {
        return materials.get(id);
    }
    
    public static int getIdOfMaterial(Material pMat)
    {
        return materials.inverse().get(pMat);
    }
    
    public static HashBiMap<Integer, Material> setupMap()
    {
        HashBiMap<Integer, Material> retmap = HashBiMap.create();
        
        retmap.put(0, Material.AIR);
        retmap.put(1, Material.STONE);
        retmap.put(2, Material.GRASS);
        retmap.put(3, Material.DIRT);
        retmap.put(4, Material.COBBLESTONE);
        retmap.put(5, Material.WOOD);
        retmap.put(6, Material.SAPLING);
        retmap.put(7, Material.BEDROCK);
        retmap.put(8, Material.WATER);
        retmap.put(9, Material.STATIONARY_WATER);
        retmap.put(10, Material.LAVA);
        retmap.put(11, Material.STATIONARY_LAVA);
        retmap.put(12, Material.SAND);
        retmap.put(13, Material.GRAVEL);
        retmap.put(14, Material.GOLD_ORE);
        retmap.put(15, Material.IRON_ORE);
        retmap.put(16, Material.COAL_ORE);
        retmap.put(17, Material.LOG);
        retmap.put(18, Material.LEAVES);
        retmap.put(19, Material.SPONGE);
        retmap.put(20, Material.GLASS);
        retmap.put(21, Material.LAPIS_ORE);
        retmap.put(22, Material.LAPIS_BLOCK);
        retmap.put(23, Material.DISPENSER);
        retmap.put(24, Material.SANDSTONE);
        retmap.put(25, Material.NOTE_BLOCK);
        retmap.put(26, Material.BED_BLOCK);
        retmap.put(27, Material.POWERED_RAIL);
        retmap.put(28, Material.DETECTOR_RAIL);
        retmap.put(29, Material.PISTON_STICKY_BASE);
        retmap.put(30, Material.WEB);
        retmap.put(31, Material.LONG_GRASS);
        retmap.put(32, Material.DEAD_BUSH);
        retmap.put(33, Material.PISTON_BASE);
        retmap.put(34, Material.PISTON_EXTENSION);
        retmap.put(35, Material.WOOL);
        retmap.put(36, Material.PISTON_MOVING_PIECE);
        retmap.put(37, Material.YELLOW_FLOWER);
        retmap.put(38, Material.RED_ROSE);
        retmap.put(39, Material.BROWN_MUSHROOM);
        retmap.put(40, Material.RED_MUSHROOM);
        retmap.put(41, Material.GOLD_BLOCK);
        retmap.put(42, Material.IRON_BLOCK);
        retmap.put(43, Material.DOUBLE_STEP);
        retmap.put(44, Material.STEP);
        retmap.put(45, Material.BRICK);
        retmap.put(46, Material.TNT);
        retmap.put(47, Material.BOOKSHELF);
        retmap.put(48, Material.MOSSY_COBBLESTONE);
        retmap.put(49, Material.OBSIDIAN);
        retmap.put(50, Material.TORCH);
        retmap.put(51, Material.FIRE);
        retmap.put(52, Material.MOB_SPAWNER);
        retmap.put(53, Material.WOOD_STAIRS);
        retmap.put(54, Material.CHEST);
        retmap.put(55, Material.REDSTONE_WIRE);
        retmap.put(56, Material.DIAMOND_ORE);
        retmap.put(57, Material.DIAMOND_BLOCK);
        retmap.put(58, Material.WORKBENCH);
        retmap.put(59, Material.CROPS);
        retmap.put(60, Material.SOIL);
        retmap.put(61, Material.FURNACE);
        retmap.put(62, Material.BURNING_FURNACE);
        retmap.put(63, Material.SIGN_POST);
        retmap.put(64, Material.WOODEN_DOOR);
        retmap.put(65, Material.LADDER);
        retmap.put(66, Material.RAILS);
        retmap.put(67, Material.COBBLESTONE_STAIRS);
        retmap.put(68, Material.WALL_SIGN);
        retmap.put(69, Material.LEVER);
        retmap.put(70, Material.STONE_PLATE);
        retmap.put(71, Material.IRON_DOOR_BLOCK);
        retmap.put(72, Material.WOOD_PLATE);
        retmap.put(73, Material.REDSTONE_ORE);
        retmap.put(74, Material.GLOWING_REDSTONE_ORE);
        retmap.put(75, Material.REDSTONE_TORCH_OFF);
        retmap.put(76, Material.REDSTONE_TORCH_ON);
        retmap.put(77, Material.STONE_BUTTON);
        retmap.put(78, Material.SNOW);
        retmap.put(79, Material.ICE);
        retmap.put(80, Material.SNOW_BLOCK);
        retmap.put(81, Material.CACTUS);
        retmap.put(82, Material.CLAY);
        retmap.put(83, Material.SUGAR_CANE_BLOCK);
        retmap.put(84, Material.JUKEBOX);
        retmap.put(85, Material.FENCE);
        retmap.put(86, Material.PUMPKIN);
        retmap.put(87, Material.NETHERRACK);
        retmap.put(88, Material.SOUL_SAND);
        retmap.put(89, Material.GLOWSTONE);
        retmap.put(90, Material.PORTAL);
        retmap.put(91, Material.JACK_O_LANTERN);
        retmap.put(92, Material.CAKE_BLOCK);
        retmap.put(93, Material.DIODE_BLOCK_OFF);
        retmap.put(94, Material.DIODE_BLOCK_ON);
        retmap.put(96, Material.TRAP_DOOR);
        retmap.put(97, Material.MONSTER_EGGS);
        retmap.put(98, Material.SMOOTH_BRICK);
        retmap.put(99, Material.HUGE_MUSHROOM_1);
        retmap.put(100, Material.HUGE_MUSHROOM_2);
        retmap.put(101, Material.IRON_FENCE);
        retmap.put(102, Material.THIN_GLASS);
        retmap.put(103, Material.MELON_BLOCK);
        retmap.put(104, Material.PUMPKIN_STEM);
        retmap.put(105, Material.MELON_STEM);
        retmap.put(106, Material.VINE);
        retmap.put(107, Material.FENCE_GATE);
        retmap.put(108, Material.BRICK_STAIRS);
        retmap.put(109, Material.SMOOTH_STAIRS);
        retmap.put(110, Material.MYCEL);
        retmap.put(111, Material.WATER_LILY);
        retmap.put(112, Material.NETHER_BRICK);
        retmap.put(113, Material.NETHER_FENCE);
        retmap.put(114, Material.NETHER_BRICK_STAIRS);
        retmap.put(115, Material.NETHER_WARTS);
        retmap.put(116, Material.ENCHANTMENT_TABLE);
        retmap.put(117, Material.BREWING_STAND);
        retmap.put(118, Material.CAULDRON);
        retmap.put(119, Material.ENDER_PORTAL);
        retmap.put(120, Material.ENDER_PORTAL_FRAME);
        retmap.put(121, Material.ENDER_STONE);
        retmap.put(122, Material.DRAGON_EGG);
        retmap.put(123, Material.REDSTONE_LAMP_OFF);
        retmap.put(124, Material.REDSTONE_LAMP_ON);
        retmap.put(125, Material.WOOD_DOUBLE_STEP);
        retmap.put(126, Material.WOOD_STEP);
        retmap.put(127, Material.COCOA);
        retmap.put(128, Material.SANDSTONE_STAIRS);
        retmap.put(129, Material.EMERALD_ORE);
        retmap.put(130, Material.ENDER_CHEST);
        retmap.put(131, Material.TRIPWIRE_HOOK);
        retmap.put(132, Material.TRIPWIRE);
        retmap.put(133, Material.EMERALD_BLOCK);
        retmap.put(134, Material.SPRUCE_WOOD_STAIRS);
        retmap.put(135, Material.BIRCH_WOOD_STAIRS);
        retmap.put(136, Material.JUNGLE_WOOD_STAIRS);
        retmap.put(137, Material.COMMAND);
        retmap.put(138, Material.BEACON);
        retmap.put(139, Material.COBBLE_WALL);
        retmap.put(140, Material.FLOWER_POT);
        retmap.put(141, Material.CARROT);
        retmap.put(142, Material.POTATO);
        retmap.put(143, Material.WOOD_BUTTON);
        retmap.put(144, Material.SKULL);
        retmap.put(145, Material.ANVIL);
        retmap.put(146, Material.TRAPPED_CHEST);
        retmap.put(147, Material.GOLD_PLATE);
        retmap.put(148, Material.IRON_PLATE);
        retmap.put(149, Material.REDSTONE_COMPARATOR_OFF);
        retmap.put(150, Material.REDSTONE_COMPARATOR_ON);
        retmap.put(151, Material.DAYLIGHT_DETECTOR);
        retmap.put(152, Material.REDSTONE_BLOCK);
        retmap.put(153, Material.QUARTZ_ORE);
        retmap.put(154, Material.HOPPER);
        retmap.put(155, Material.QUARTZ_BLOCK);
        retmap.put(156, Material.QUARTZ_STAIRS);
        retmap.put(157, Material.ACTIVATOR_RAIL);
        retmap.put(158, Material.DROPPER);
        retmap.put(159, Material.STAINED_CLAY);
        retmap.put(170, Material.HAY_BLOCK);
        retmap.put(171, Material.CARPET);
        retmap.put(172, Material.HARD_CLAY);
        retmap.put(173, Material.COAL_BLOCK);
        retmap.put(256, Material.IRON_SPADE);
        retmap.put(257, Material.IRON_PICKAXE);
        retmap.put(258, Material.IRON_AXE);
        retmap.put(259, Material.FLINT_AND_STEEL);
        retmap.put(260, Material.APPLE);
        retmap.put(261, Material.BOW);
        retmap.put(262, Material.ARROW);
        retmap.put(263, Material.COAL);
        retmap.put(264, Material.DIAMOND);
        retmap.put(265, Material.IRON_INGOT);
        retmap.put(266, Material.GOLD_INGOT);
        retmap.put(267, Material.IRON_SWORD);
        retmap.put(268, Material.WOOD_SWORD);
        retmap.put(269, Material.WOOD_SPADE);
        retmap.put(270, Material.WOOD_PICKAXE);
        retmap.put(271, Material.WOOD_AXE);
        retmap.put(272, Material.STONE_SWORD);
        retmap.put(273, Material.STONE_SPADE);
        retmap.put(274, Material.STONE_PICKAXE);
        retmap.put(275, Material.STONE_AXE);
        retmap.put(276, Material.DIAMOND_SWORD);
        retmap.put(277, Material.DIAMOND_SPADE);
        retmap.put(278, Material.DIAMOND_PICKAXE);
        retmap.put(279, Material.DIAMOND_AXE);
        retmap.put(280, Material.STICK);
        retmap.put(281, Material.BOWL);
        retmap.put(282, Material.MUSHROOM_SOUP);
        retmap.put(283, Material.GOLD_SWORD);
        retmap.put(284, Material.GOLD_SPADE);
        retmap.put(285, Material.GOLD_PICKAXE);
        retmap.put(286, Material.GOLD_AXE);
        retmap.put(287, Material.STRING);
        retmap.put(288, Material.FEATHER);
        retmap.put(289, Material.SULPHUR);
        retmap.put(290, Material.WOOD_HOE);
        retmap.put(291, Material.STONE_HOE);
        retmap.put(292, Material.IRON_HOE);
        retmap.put(293, Material.DIAMOND_HOE);
        retmap.put(294, Material.GOLD_HOE);
        retmap.put(295, Material.SEEDS);
        retmap.put(296, Material.WHEAT);
        retmap.put(297, Material.BREAD);
        retmap.put(298, Material.LEATHER_HELMET);
        retmap.put(299, Material.LEATHER_CHESTPLATE);
        retmap.put(300, Material.LEATHER_LEGGINGS);
        retmap.put(301, Material.LEATHER_BOOTS);
        retmap.put(302, Material.CHAINMAIL_HELMET);
        retmap.put(303, Material.CHAINMAIL_CHESTPLATE);
        retmap.put(304, Material.CHAINMAIL_LEGGINGS);
        retmap.put(305, Material.CHAINMAIL_BOOTS);
        retmap.put(306, Material.IRON_HELMET);
        retmap.put(307, Material.IRON_CHESTPLATE);
        retmap.put(308, Material.IRON_LEGGINGS);
        retmap.put(309, Material.IRON_BOOTS);
        retmap.put(310, Material.DIAMOND_HELMET);
        retmap.put(311, Material.DIAMOND_CHESTPLATE);
        retmap.put(312, Material.DIAMOND_LEGGINGS);
        retmap.put(313, Material.DIAMOND_BOOTS);
        retmap.put(314, Material.GOLD_HELMET);
        retmap.put(315, Material.GOLD_CHESTPLATE);
        retmap.put(316, Material.GOLD_LEGGINGS);
        retmap.put(317, Material.GOLD_BOOTS);
        retmap.put(318, Material.FLINT);
        retmap.put(319, Material.PORK);
        retmap.put(320, Material.GRILLED_PORK);
        retmap.put(321, Material.PAINTING);
        retmap.put(322, Material.GOLDEN_APPLE);
        retmap.put(323, Material.SIGN);
        retmap.put(324, Material.WOOD_DOOR);
        retmap.put(325, Material.BUCKET);
        retmap.put(326, Material.WATER_BUCKET);
        retmap.put(327, Material.LAVA_BUCKET);
        retmap.put(328, Material.MINECART);
        retmap.put(329, Material.SADDLE);
        retmap.put(330, Material.IRON_DOOR);
        retmap.put(331, Material.REDSTONE);
        retmap.put(332, Material.SNOW_BALL);
        retmap.put(333, Material.BOAT);
        retmap.put(334, Material.LEATHER);
        retmap.put(335, Material.MILK_BUCKET);
        retmap.put(336, Material.CLAY_BRICK);
        retmap.put(337, Material.CLAY_BALL);
        retmap.put(338, Material.SUGAR_CANE);
        retmap.put(339, Material.PAPER);
        retmap.put(340, Material.BOOK);
        retmap.put(341, Material.SLIME_BALL);
        retmap.put(342, Material.STORAGE_MINECART);
        retmap.put(343, Material.POWERED_MINECART);
        retmap.put(344, Material.EGG);
        retmap.put(345, Material.COMPASS);
        retmap.put(346, Material.FISHING_ROD);
        retmap.put(347, Material.WATCH);
        retmap.put(348, Material.GLOWSTONE_DUST);
        retmap.put(349, Material.RAW_FISH);
        retmap.put(350, Material.COOKED_FISH);
        retmap.put(351, Material.INK_SACK);
        retmap.put(352, Material.BONE);
        retmap.put(353, Material.SUGAR);
        retmap.put(354, Material.CAKE);
        retmap.put(355, Material.BED);
        retmap.put(356, Material.DIODE);
        retmap.put(357, Material.COOKIE);
        retmap.put(358, Material.MAP);
        retmap.put(359, Material.SHEARS);
        retmap.put(360, Material.MELON);
        retmap.put(361, Material.PUMPKIN_SEEDS);
        retmap.put(362, Material.MELON_SEEDS);
        retmap.put(363, Material.RAW_BEEF);
        retmap.put(364, Material.COOKED_BEEF);
        retmap.put(365, Material.RAW_CHICKEN);
        retmap.put(366, Material.COOKED_CHICKEN);
        retmap.put(367, Material.ROTTEN_FLESH);
        retmap.put(368, Material.ENDER_PEARL);
        retmap.put(369, Material.BLAZE_ROD);
        retmap.put(370, Material.GHAST_TEAR);
        retmap.put(371, Material.GOLD_NUGGET);
        retmap.put(372, Material.NETHER_STALK);
        retmap.put(373, Material.POTION);
        retmap.put(374, Material.GLASS_BOTTLE);
        retmap.put(375, Material.SPIDER_EYE);
        retmap.put(376, Material.FERMENTED_SPIDER_EYE);
        retmap.put(377, Material.BLAZE_POWDER);
        retmap.put(378, Material.MAGMA_CREAM);
        retmap.put(379, Material.BREWING_STAND_ITEM);
        retmap.put(380, Material.CAULDRON_ITEM);
        retmap.put(381, Material.EYE_OF_ENDER);
        retmap.put(382, Material.SPECKLED_MELON);
        retmap.put(383, Material.MONSTER_EGG);
        retmap.put(384, Material.EXP_BOTTLE);
        retmap.put(385, Material.FIREBALL);
        retmap.put(386, Material.BOOK_AND_QUILL);
        retmap.put(387, Material.WRITTEN_BOOK);
        retmap.put(388, Material.EMERALD);
        retmap.put(389, Material.ITEM_FRAME);
        retmap.put(390, Material.FLOWER_POT_ITEM);
        retmap.put(391, Material.CARROT_ITEM);
        retmap.put(392, Material.POTATO_ITEM);
        retmap.put(393, Material.BAKED_POTATO);
        retmap.put(394, Material.POISONOUS_POTATO);
        retmap.put(395, Material.EMPTY_MAP);
        retmap.put(396, Material.GOLDEN_CARROT);
        retmap.put(397, Material.SKULL_ITEM);
        retmap.put(398, Material.CARROT_STICK);
        retmap.put(399, Material.NETHER_STAR);
        retmap.put(400, Material.PUMPKIN_PIE);
        retmap.put(401, Material.FIREWORK);
        retmap.put(402, Material.FIREWORK_CHARGE);
        retmap.put(403, Material.ENCHANTED_BOOK);
        retmap.put(404, Material.REDSTONE_COMPARATOR);
        retmap.put(405, Material.NETHER_BRICK_ITEM);
        retmap.put(406, Material.QUARTZ);
        retmap.put(407, Material.EXPLOSIVE_MINECART);
        retmap.put(408, Material.HOPPER_MINECART);
        retmap.put(417, Material.IRON_BARDING);
        retmap.put(418, Material.GOLD_BARDING);
        retmap.put(419, Material.DIAMOND_BARDING);
        retmap.put(420, Material.LEASH);
        retmap.put(421, Material.NAME_TAG);
        retmap.put(2256, Material.GOLD_RECORD);
        retmap.put(2257, Material.GREEN_RECORD);
        retmap.put(2258, Material.RECORD_3);
        retmap.put(2259, Material.RECORD_4);
        retmap.put(2260, Material.RECORD_5);
        retmap.put(2261, Material.RECORD_6);
        retmap.put(2262, Material.RECORD_7);
        retmap.put(2263, Material.RECORD_8);
        retmap.put(2264, Material.RECORD_9);
        retmap.put(2265, Material.RECORD_10);
        retmap.put(2266, Material.RECORD_11);
        retmap.put(2267, Material.RECORD_12);
        
        return retmap;
    }
}
