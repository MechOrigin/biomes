package net.fabricmc.biomes.features;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.fabricmc.biomes.BiomesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;


public class WideLake extends Feature<SimpleBlockProviderConfig> {

    protected static final Set<Material> unacceptableSolidMaterials = ImmutableSet.of(
    Material.BAMBOO,
    Material.LEAVES, 
    Material.COBWEB, 
    Material.CACTUS, 
    Material.REPAIR_STATION, 
    Material.GOURD, 
    Material.CAKE, 
    Material.EGG, 
    Material.BARRIER, 
    Material.CAKE
    );

    protected long noiseSeed;
    protected OctaveSimplexNoiseSampler noiseGen;

    public void setSeed(long seed) {
        ChunkRandom sharedRandomSeed = new ChunkRandom(seed);
        if (this.noiseSeed != seed || this.noiseGen == null) {
            this.noiseGen = new OctaveSimplexNoiseSampler(sharedRandomSeed, ImmutableList.of(0));
        }

        this.noiseSeed = seed;
    }

    public WideLake(Codec<SimpleBlockProviderConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkSettings, Random random, BlockPos position,
            SimpleBlockProviderConfig config) {

        setSeed(world.getSeed());
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(position.down(2)); // goes down 2 blocks

        // creates the lakes
        boolean containedFlag;
        Material material;
        BlockState blockState;
        for (int x = -2; x < 18; ++x) {
            for (int z = -2; z < 18; ++z) {

                int xTemp = x - 10;
                int zTemp = z - 10;
                //circle shape
                if (xTemp * xTemp + zTemp * zTemp < 64) {

                    double samplePerlin1 = (this.noiseGen.sample(
                        position.getX() + x * 0.05D,
                        position.getZ() + z * 0.05D,
                        true) + 1) * 3.0D;
                    
                    for (int y = 0; y > -samplePerlin1; --y) {

                        mutable.set(position).move(x, y, z);

                        //check if the spot is solid around, nothing solid above
                        containedFlag = checkIfValidSpot(world, mutable, samplePerlin1);

                        // is spot within the mask and contained
                        if (containedFlag) {

                            //check below
                            //set the fluid block
                            BlockState configState = config.getBlockProvider().getBlockState(random, mutable);

                            world.setBlockState(mutable, configState, 3);
                            if (configState == Blocks.WATER.getDefaultState())
                            world.getFluidTickScheduler().schedule(mutable, Fluids.WATER, 0);
                            else if (configState == Blocks.LAVA.getDefaultState())
                            world.getFluidTickScheduler().schedule(mutable, Fluids.LAVA, 0);

                            //remove floating plants from hovering
                            //check above
                            blockState = world.getBlockState(mutable.move(Direction.UP));
                            material = blockState.getMaterial();

                            if (material == Material.PLANT && blockState.getBlock() != Blocks.LILY_PAD) {
                                world.setBlockState(mutable, Blocks.AIR.getDefaultState(), 2);

                                // recursively move up and break floating sugar cane
                                while (mutable.getY() < world.getHeight() && world.getBlockState(mutable.move(Direction.UP)) == Blocks.SUGAR_CANE.getDefaultState()) {
                                    world.setBlockState(mutable, Blocks.AIR.getDefaultState(), 2);
                                }
                            }
                            if (material == Material.REPLACEABLE_PLANT && blockState.getBlock() != Blocks.VINE) {
                                world.setBlockState(mutable, Blocks.AIR.getDefaultState(), 2);
                                world.setBlockState(mutable.up(), Blocks.AIR.getDefaultState(), 2);
                            }

                                    // recursively move down and replace water with acid
                                    while (mutable.getY() < world.getHeight() && world.getBlockState(mutable.move(Direction.DOWN)) == Blocks.WATER.getDefaultState()) {
                                        world.setBlockState(mutable, BiomesMod.ACID.getDefaultState(), 2);
                                        world.getFluidTickScheduler().schedule(mutable, BiomesMod.STILL_ACID, 0);
                                    }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean checkIfValidSpot(WorldAccess world, BlockPos.Mutable blockpos$Mutable, double noise) {
        Material material;
        BlockState blockState;

        //cannot be under ledge
        BlockPos.Mutable temp = new BlockPos.Mutable().set(blockpos$Mutable);
        blockState = world.getBlockState(temp.up());
        while (!blockState.getFluidState().isEmpty() && temp.getY() < 255) {
            temp.move(Direction.UP);
        }
        if (!blockState.isAir() && blockState.getFluidState().isEmpty())
        return false;

        //must be solid below and will return false is unacceptable solid material found
        blockState = world.getBlockState(blockpos$Mutable.down());
        material = blockState.getMaterial();
        if ((!material.isSolid() || unacceptableSolidMaterials.contains(material) ||
            BlockTags.PLANKS.contains(blockState.getBlock())) &&
            blockState.getFluidState().isEmpty() &&
            blockState.getFluidState() != Fluids.WATER.getStill(false)) {
                return false;
        }

        //place water on tip
        if ((noise < 2D && world.getBlockState(blockpos$Mutable.up()).isAir())) {
            int open = 0;
            for (Direction direction : Direction.Type.HORIZONTAL) {
                Material material2 = world.getBlockState(blockpos$Mutable.offset(direction)).getMaterial();
                if (unacceptableSolidMaterials.contains(material2)) return false;
                if (world.getBlockState(blockpos$Mutable.offset(direction)).isAir()) open++;
            }
            if (open == 1) return true;
        }

        //must be solid all around and will return false if unacceptable solid material
        for (int x2 = -1; x2 < 2; x2++) {
            for (int z2 = -1; z2 < 2; z2++) {
                blockState = world.getBlockState(blockpos$Mutable.add(x2, 0, z2));
                material = blockState.getMaterial();

                if ((!material.isSolid() || unacceptableSolidMaterials.contains(material) ||
                BlockTags.PLANKS.contains(blockState.getBlock())) &&
                blockState.getFluidState().isEmpty() &&
                blockState.getFluidState() != Fluids.WATER.getStill(false)) {
                    return false;
            }
            }
        }

        return true;
    }

}