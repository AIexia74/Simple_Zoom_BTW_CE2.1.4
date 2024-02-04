package btw.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/**
 * Extend this class to enable directional functionality like logs.
 * Metadata 0-3 available for different types.
 * 4-7 and 8-11 used for orientation of those types.
 */
public class PillarBlock extends Block {
	public String[] topTextures;
	public String[] sideTextures;

	protected PillarBlock(int id, Material material, String[] topTextures, String[] sideTextures) {
		super(id, material);
        this.topTextures = topTextures;
        this.sideTextures = sideTextures;
	}
	
	public boolean canRotateOnTurntable(IBlockAccess access, int x, int y, int z) {
		return (access.getBlockMetadata(x,y,z) & 12) != 0;
	}

    public int rotateMetadataAroundJAxis(int meta, boolean var2) {
        int directionMeta = meta & 12;

        if (directionMeta != 0) {
            if (directionMeta == 4) {
                directionMeta = 8;
            }
            else if (directionMeta == 8) {
                directionMeta = 4;
            }

            meta = meta & -13 | directionMeta;
        }

        return meta;
    }
	 
    
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        int type = meta & 3;
        byte directionMeta = 0;

        switch (side) {
            case 0:
            case 1:
                directionMeta = 0;
                break;
            case 2:
            case 3:
                directionMeta = 8;
                break;
            case 4:
            case 5:
                directionMeta = 4;
        }

        return type | directionMeta;
    }

    
    public int damageDropped(int meta) {
        return meta & 3;
    }

    
    public static int limitToValidMetadata(int meta) {
        return meta & 3;
    }
    
    //CLIENT ONLY
	public Icon[] topIcons;
	public Icon[] sideIcons;
	
	public Icon getIcon(int side, int meta) {
        int directionMeta = meta & 12;
        int type = meta & 3;
        
        if (directionMeta == 0 && (side == 0 || side == 1) ||
        		directionMeta == 4 && (side == 4 || side == 5) ||
            	directionMeta == 8 && (side == 2 || side == 3)) {
        	return topIcons[type];
        }
        else {
        	return sideIcons[type];
        }
    }
	
	public void registerIcons(IconRegister register) {
		topIcons = new Icon[topTextures.length];
		sideIcons = new Icon[topTextures.length];
		
		for (int i = 0; i < topTextures.length; i++) {
			topIcons[i] = register.registerIcon(topTextures[i]);
			sideIcons[i] = register.registerIcon(sideTextures[i]);
		}
	}
	
	public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
        int meta = render.blockAccess.getBlockMetadata(x, y, z);
        int directionMeta = meta & 12;

        if (directionMeta == 4) {
			render.setUVRotateTop(1);
			render.setUVRotateBottom(1);
			render.setUVRotateWest(1);
			render.setUVRotateEast(1);
        }
        else if (directionMeta == 8) {
			render.setUVRotateNorth(1);
			render.setUVRotateSouth(1);
        }
        
		render.setRenderBounds(0D,0D,0D,1D,1D,1D);
		render.renderStandardBlock(this, x, y, z);
		render.clearUVRotation();
		return true;
	}
}