package net.fabricmc.biomes.features;

import static net.fabricmc.biomes.features.WorldGenRegistrationHelper.createConfiguredFeature;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class BYGConfiguredFeatures {
    public static final ConfiguredFeature<?, ?> WIDE_WATER_LAKE = createConfiguredFeature("wide_water_lake",
    BYGFeatures.WIDE_LAKE.configure(new SimpleBlockProviderConfig(new SimpleBlockStateProvider(Blocks.WATER.getDefaultState())))
    .decorate(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(100))));

}
