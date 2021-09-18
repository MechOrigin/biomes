package net.fabricmc.biomes.blocks;

import net.fabricmc.biomes.utils.BlockRegistrationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class ModBlocks extends BlockRegistrationUtil {

    public static final Block ANDESITE_BRICKS = registerBlockCopy("andesite_bricks", Blocks.POLISHED_ANDESITE);
    public static final Block ANDESITE_BRICKS_STAIRS = registerStairs(ANDESITE_BRICKS);
    public static final Block ANDESITE_BRICKS_SLAB = registerSlab(ANDESITE_BRICKS);
    public static final Block ANDESITE_BRICKS_WALL = registerWall(ANDESITE_BRICKS);
    
}
