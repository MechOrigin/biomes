package net.fabricmc.biomes.structures;

import java.util.List;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Deprecated
public class MyGenerator {

  private static final Identifier IGLOO_TOP = new Identifier("igloo/top");
  //private static final Identifier SKULL_1 = new Identifier("fossil/skull_1");

  public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces) {
    pieces.add(new MyPiece(manager, pos, IGLOO_TOP, rotation));
  }

  }
