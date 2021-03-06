package net.fabricmc.biomes;

import static net.minecraft.entity.EntityType.COW;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.biomes.blocks.ExampleBlock;
import net.fabricmc.biomes.blocks.ModBlocks;
import net.fabricmc.biomes.dimensions.VoidChunkGenerator;
import net.fabricmc.biomes.features.BMDefaultBiomeFeatures;
import net.fabricmc.biomes.features.BMFeatures;
import net.fabricmc.biomes.features.StoneSpiralFeature;
import net.fabricmc.biomes.fluids.AcidFluid;
import net.fabricmc.biomes.structures.MyPiece;
import net.fabricmc.biomes.structures.Structure1;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.WallBlock;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;


@SuppressWarnings("deprecation")
public class BiomesMod implements ModInitializer {
	/* Constants */
	public static final String MOD_ID = "biomes";

	/* Blocks */
	public static final ItemGroup EXAMPLE_BUILDING_BLOCKS = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "example_building_blocks"), () ->
	new ItemStack(ModBlocks.ANDESITE_BRICKS));

	public static final Block EXAMPLE_BLOCK = new ExampleBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static final Block EXAMPLE_BLOCK_WALL = new WallBlock(FabricBlockSettings.copyOf(EXAMPLE_BLOCK));

	/* More Blocks Handled by ModBlocks class */
	public static final Item ANDESITE_BRICKS = register(ModBlocks.ANDESITE_BRICKS, BiomesMod.EXAMPLE_BUILDING_BLOCKS);
	public static final Item ANDESITE_BRICKS_STAIRS = register(ModBlocks.ANDESITE_BRICKS_STAIRS, BiomesMod.EXAMPLE_BUILDING_BLOCKS);
	public static final Item ANDESITE_BRICKS_SLAB = register(ModBlocks.ANDESITE_BRICKS_SLAB, BiomesMod.EXAMPLE_BUILDING_BLOCKS);
	public static final Item ANDESITE_BRICKS_WALL = register(ModBlocks.ANDESITE_BRICKS_WALL, BiomesMod.EXAMPLE_BUILDING_BLOCKS);

	public static Item register(Block block, ItemGroup group) {
		return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, new Item.Settings().maxCount(64).group(group)));
	}

	/* Dimensions */
	private static final RegistryKey<DimensionOptions> DIMENSIONS_KEY = RegistryKey.of(
		Registry.DIMENSION_KEY,
		new Identifier("biomes", "void")
	);

	private static RegistryKey<World> WORLD_KEY = RegistryKey.of(
		Registry.WORLD_KEY,
		DIMENSIONS_KEY.getValue()
	);

	@SuppressWarnings({"unused"})
	private static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY = RegistryKey.of(
		Registry.DIMENSION_TYPE_KEY,
		new Identifier("biomes", "void")
	);
	
	/* Structures */
	public static final StructurePieceType MY_PIECE = MyPiece::new;
	private static final StructureFeature<DefaultFeatureConfig> MY_STRUCTURE = new Structure1(DefaultFeatureConfig.CODEC);
	private static final ConfiguredStructureFeature<?, ?> MY_CONFIGURED = MY_STRUCTURE.configure(DefaultFeatureConfig.DEFAULT);

	/* Biomes */
	private static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> CUSTOM_SURFACE_BUILDER_0 = SurfaceBuilder.DEFAULT
	.withConfig(new TernarySurfaceConfig(
		Blocks.BASALT.getDefaultState(), 
		Blocks.SOUL_SAND.getDefaultState(), 
		Blocks.GRAVEL.getDefaultState()));

	private static final Biome CUSTOMLAND = createCustomLand();

	private static Biome createCustomLand() {

		SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addMonsters(spawnSettings, 95, 5, 100);
		DefaultBiomeFeatures.addFarmAnimals(spawnSettings);

		GenerationSettings.Builder generatorSettings = new GenerationSettings.Builder();
		generatorSettings.surfaceBuilder(CUSTOM_SURFACE_BUILDER_0);
		DefaultBiomeFeatures.addDungeons(generatorSettings);
		DefaultBiomeFeatures.addMineables(generatorSettings);
		DefaultBiomeFeatures.addLandCarvers(generatorSettings);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(generatorSettings);
		BMDefaultBiomeFeatures.addLargeLake(generatorSettings); //add feature to custom biome

		return (new Biome.Builder())
		.precipitation(Biome.Precipitation.RAIN)
		.category(Biome.Category.NONE) //change to nether, end, or whatever to be specific
		.depth(0.125F)
		.scale(0.05F)
		.temperature(0.8F)
		.downfall(0.4F)
		.effects((new BiomeEffects.Builder())
			.waterColor(0xb434eb)
			.waterFogColor(0x050533)
			.fogColor(0xffc0c0)
			.skyColor(0xffa977)
			.build())
		.spawnSettings(spawnSettings.build())
		.generationSettings(generatorSettings.build())
		.build();
	}

	public static final RegistryKey<Biome> CUSTOMLAND_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("biomes", "customland"));

	//Nether Noise points
	public static final Biome.MixedNoisePoint NOISE_POINT = new Biome.MixedNoisePoint(0.0F, 0.0F, 0.35F, 0.35F, 0.2F);
	
	/* Fluids */
	public static FlowableFluid STILL_ACID;
	public static FlowableFluid FLOWING_ACID;
	public static Item ACID_BUCKET;

	public static Block ACID;

	private static final Feature<DefaultFeatureConfig> STONE_SPIRAL = new StoneSpiralFeature(DefaultFeatureConfig.CODEC);

	public static final ConfiguredFeature<?, ?> STONE_SPIRAL_CONFIGURED = STONE_SPIRAL.configure(FeatureConfig.DEFAULT)
	.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(100)));

	@Override
	public void onInitialize() {
		/* Blocks */
		Registry.register(Registry.BLOCK, new Identifier(BiomesMod.MOD_ID, "example_block2"), EXAMPLE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier(BiomesMod.MOD_ID, "example_block2"), new BlockItem(EXAMPLE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		
		Registry.register(Registry.BLOCK, new Identifier(BiomesMod.MOD_ID, "blackstone_wall"), EXAMPLE_BLOCK_WALL);
		Registry.register(Registry.ITEM, new Identifier(BiomesMod.MOD_ID, "blackstone_wall"), new BlockItem(EXAMPLE_BLOCK_WALL, new FabricItemSettings().group(ItemGroup.MISC)));
		new ModBlocks();

		/* Fluids */
		STILL_ACID = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "acid"), new AcidFluid.Still());
		FLOWING_ACID = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "flowing_acid"), new AcidFluid.Flowing());
		ACID_BUCKET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "acid_bucket"), new BucketItem(STILL_ACID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));

		ACID = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "acid") , new FluidBlock(STILL_ACID, FabricBlockSettings.copy(Blocks.WATER)){});

		Registry.register(Registry.FEATURE, new Identifier("biomes", "stone_spiral"), STONE_SPIRAL);

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("biomes", "stone_spiral"), STONE_SPIRAL_CONFIGURED);

		/* Wide Lake Feature */
		BMWorldGenRegistries.registerFeatures();

		RegistryKey<ConfiguredFeature<?, ?>> widewaterLake = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, 
		new Identifier("biomes", "wide_water_lake"));

		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
		GenerationStep.Feature.RAW_GENERATION,
		widewaterLake
		);

		/* Dimensions */
		Registry.register(Registry.CHUNK_GENERATOR, new Identifier("biomes", "void"), VoidChunkGenerator.CODEC);

		WORLD_KEY = RegistryKey.of(Registry.WORLD_KEY, new Identifier("biomes", "void"));

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerWorld overworld = server.getWorld(World.OVERWORLD);
			ServerWorld world = server.getWorld(WORLD_KEY);

			if (world == null) throw new AssertionError("Void world doesn't exist.");

			Entity entity = COW.create(overworld);

			if (!entity.world.getRegistryKey().equals(World.OVERWORLD)) throw new AssertionError("Entity starting void world isn't in overworld.");

			TeleportTarget target = new TeleportTarget(Vec3d.ZERO, new Vec3d(1, 1, 1), 45f, 60f);

			Entity teleported = FabricDimensions.teleport(entity, world, target);

			if (teleported == null) throw new AssertionError("Entity didn't teleport.");

			if (!teleported.world.getRegistryKey().equals(WORLD_KEY)) throw new AssertionError("Target void world cannot be reached.");

			if (!teleported.getPos().equals(target.position)) throw new AssertionError("Target position cannot be reached.");
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
			dispatcher.register(literal("fabric_dimension_test").executes(BiomesMod.this::swapTargeted))
		);

		/* Structures */
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier("biomes", "my_piece"), MY_PIECE);
		FabricStructureBuilder.create(new Identifier("biomes", "my_structure"), MY_STRUCTURE)
		.step(net.minecraft.world.gen.GenerationStep.Feature.SURFACE_STRUCTURES)
		.defaultConfig(32, 8, 12345)
		.adjustsSurface()
		.register();

		RegistryKey<ConfiguredStructureFeature<?, ?>> myConfigured = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, 
		new Identifier("biomes", "my_structure"));
		BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, myConfigured.getValue(), MY_CONFIGURED);

		BiomeModifications.addStructure(BiomeSelectors.all(), myConfigured);

		/* Biomes */
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, new Identifier("biomes", "basalt"), CUSTOM_SURFACE_BUILDER_0);
		Registry.register(BuiltinRegistries.BIOME, CUSTOMLAND_KEY.getValue(), CUSTOMLAND);

		NetherBiomes.addNetherBiome(CUSTOMLAND_KEY, NOISE_POINT);

		OverworldBiomes.addContinentalBiome(CUSTOMLAND_KEY, OverworldClimate.TEMPERATE, 2D);
		OverworldBiomes.addContinentalBiome(CUSTOMLAND_KEY, OverworldClimate.COOL, 2D);
	}

	/* Dimensions */
	private int swapTargeted(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		ServerWorld serverWorld = player.getServerWorld();
		ServerWorld modWorld = getModWorld(context);

		if (serverWorld != modWorld) {
			TeleportTarget target = new TeleportTarget(new Vec3d(0.5, 101, 0.5), Vec3d.ZERO, 0, 0);
			FabricDimensions.teleport(player, modWorld, target);

			if (player.world != modWorld) {
				throw new CommandException(new LiteralText("Teleportation Failed."));
			}

			modWorld.setBlockState(new BlockPos(0, 100, 0), Blocks.DIAMOND_BLOCK.getDefaultState());
			modWorld.setBlockState(new BlockPos(0, 101, 0), Blocks.TORCH.getDefaultState());
		} else {
			TeleportTarget target = new TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO,
			(float) Math.random() * 360 - 180, (float) Math.random() * 360 - 180);
			FabricDimensions.teleport(player, getWorld(context, World.OVERWORLD), target);
		}

		return 1;
	}

	private ServerWorld getModWorld(CommandContext<ServerCommandSource> context) {
		return getWorld(context, WORLD_KEY);
	}

	private ServerWorld getWorld(CommandContext<ServerCommandSource> context, RegistryKey<World> dimensionRegistryKey) {
		return context.getSource().getMinecraftServer().getWorld(dimensionRegistryKey);
	}


	// Wide Lake Feature
	public static class BMWorldGenRegistries {
		public static void registerFeatures() {
			BMFeatures.init();
		}
	}

}
