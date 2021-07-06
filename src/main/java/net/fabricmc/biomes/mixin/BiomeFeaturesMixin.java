package net.fabricmc.biomes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.biomes.features.BMConfiguredFeatures;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

@Mixin(DefaultBiomeFeatures.class)
public class BiomeFeaturesMixin {
  @Inject(method = "addPlainsFeatures(Lnet/minecraft/world/biome/GenerationSettings$Builder;)V", at = @At("TAIL"))
  private static void addPlainsFeatures(GenerationSettings.Builder builder, CallbackInfo ci) {
    builder.feature(GenerationStep.Feature.RAW_GENERATION,
    BMConfiguredFeatures.WIDE_WATER_LAKE);
  }
  
}
