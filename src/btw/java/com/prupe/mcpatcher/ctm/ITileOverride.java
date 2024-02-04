package com.prupe.mcpatcher.ctm;

import com.prupe.mcpatcher.mal.block.BlockStateMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;

import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
interface ITileOverride extends Comparable<ITileOverride> {
    boolean isDisabled();

    void registerIcons();

    List<BlockStateMatcher> getMatchingBlocks();

    Set<String> getMatchingTiles();

    int getRenderPass();

    int getWeight();

    Icon getTileWorld(RenderBlockState renderBlockState, Icon origIcon);

    Icon getTileHeld(RenderBlockState renderBlockState, Icon origIcon);
}