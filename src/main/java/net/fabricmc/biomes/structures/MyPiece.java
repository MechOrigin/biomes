package net.fabricmc.biomes.structures;

import java.util.Random;
import net.fabricmc.biomes.BiomesMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

@Deprecated
public class MyPiece extends SimpleStructurePiece {

  public final BlockRotation rotation;
  public final Identifier template;

  public MyPiece(StructureManager structureManager, NbtCompound compoundTag) {
    super(BiomesMod.MY_PIECE, compoundTag);
    this.template = new Identifier(compoundTag.getString("Template"));
    this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
    this.initializeStructureData(structureManager);
  }

  public MyPiece(StructureManager structureManager, BlockPos pos, Identifier template, BlockRotation rotation) {
    super(BiomesMod.MY_PIECE, 0);
    this.pos = pos;
    this.rotation = rotation;
    this.template = template;

    this.initializeStructureData(structureManager);
  }

  private void initializeStructureData(StructureManager structureManager) {
    Structure structure = structureManager.getStructureOrBlank(this.template);
    StructurePlacementData placementData = (new StructurePlacementData())
      .setRotation(this.rotation)
      .setMirror(BlockMirror.NONE)
      .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    this.setStructureData(structure, this.pos, placementData);
  }

  protected void toNbt(NbtCompound tag) {
    super.toNbt(tag);
    tag.putString("Template", this.template.toString());
    tag.putString("Rot", this.rotation.name());
  }

  @Override
  protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
  }

  }
