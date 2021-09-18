package net.fabricmc.biomes.utils;

import net.fabricmc.biomes.BiomesMod;
import net.fabricmc.biomes.blocks.StairsBase;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class BlockRegistrationUtil {

    public static Block registerBlockCopy(String id, Block base) {
        return register(id, new Block(FabricBlockSettings.copy(base)));
    }

    public static Block registerBlockCopy(String id, Block base, MapColor color) {
        return register(id, new Block(FabricBlockSettings.copyOf(base).materialColor(color)));
    }

    public static Block registerWall(String id, Block base) {
        return register(id + "_wall", new WallBlock(FabricBlockSettings.copy(base)));
    }

    public static Block registerWall(Block base) {
        return registerWall(Registry.BLOCK.getId(base).getPath(), base);
    }

    public static Block registerSlab(String id, Block base) {
        return register(id + "_slab", new SlabBlock(FabricBlockSettings.copy(base)));
    }

    public static Block registerSlab(Block base) {
        return registerSlab(Registry.BLOCK.getId(base).getPath(), base);
    }

    public static Block registerStairs(String id, Block base) {
        return register(id + "_stairs", new StairsBase(base.getDefaultState(), FabricBlockSettings.copy(base)));
    }

    public static Block registerStairs(Block base) {
        return registerStairs(Registry.BLOCK.getId(base).getPath(), base);
    }


    //Registry
    public static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(BiomesMod.MOD_ID, id), block);
    }
    
}
