// FCMOD

package btw.entity;

import btw.network.packet.BTWPacketManager;
import btw.entity.util.CanvasArt;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.*;

public class CanvasEntity extends Entity
	implements EntityWithCustomPacket
{
    private int tickCounter1;

    /** the direction the painting faces */
    public int direction;
    public int canvasPosX;
    public int canvasPosY;
    public int canvasPosZ;
    public CanvasArt art;

    public CanvasEntity(World par1World)
    {
        super(par1World);
        tickCounter1 = 0;
        direction = 0;
        yOffset = 0.0F;
        setSize(0.5F, 0.5F);
    }

    public CanvasEntity(World par1World, int i, int j, int k, int iFacing )
    {
        this(par1World);
        canvasPosX = i;
        canvasPosY = j;
        canvasPosZ = k;
        ArrayList arraylist = new ArrayList();
        CanvasArt aenumart[] = CanvasArt.values();
        int iNameLength = aenumart.length;

        for (int iTemp = 0; iTemp < iNameLength; iTemp++)
        {
            CanvasArt enumart = aenumart[iTemp];
            art = enumart;
            func_412_b(iFacing);

            if (onValidSurface())
            {
                arraylist.add(enumart);
            }
        }

        if (arraylist.size() > 0)
        {
            art = (CanvasArt)arraylist.get(rand.nextInt(arraylist.size()));
        }

        func_412_b(iFacing);
    }

    /*
     * Constructor using art ordinal specificall for use by the client
     */
    public CanvasEntity(World par1World, int i, int j, int k, int iFacing, int iArtOrdinal )
    {
        this(par1World);
        canvasPosX = i;
        canvasPosY = j;
        canvasPosZ = k;
        CanvasArt aenumart[] = CanvasArt.values();

        art = aenumart[iArtOrdinal];

        func_412_b(iFacing);
    }
    
    public CanvasEntity(World par1World, int par2, int par3, int par4, int par5, String par6Str)
    {
        this(par1World);
        canvasPosX = par2;
        canvasPosY = par3;
        canvasPosZ = par4;
        CanvasArt aenumart[] = CanvasArt.values();
        int i = aenumart.length;
        int j = 0;

        do
        {
            if (j >= i)
            {
                break;
            }

            CanvasArt enumart = aenumart[j];

            if (enumart.title.equals(par6Str))
            {
                art = enumart;
                break;
            }

            j++;
        }
        while (true);

        func_412_b(par5);
    }

    protected void entityInit()
    {
    }

    public void func_412_b(int iFacing)
    {
        direction = iFacing;
        prevRotationYaw = rotationYaw = iFacing * 90;
        float f = art.sizeX;
        float f1 = art.sizeY;
        float f2 = art.sizeX;

        if (iFacing == 0 || iFacing == 2)
        {
            f2 = 0.5F;
        }
        else
        {
            f = 0.5F;
        }

        f /= 32F;
        f1 /= 32F;
        f2 /= 32F;
        float f3 = (float) canvasPosX + 0.5F;
        float f4 = (float) canvasPosY + 0.5F;
        float f5 = (float) canvasPosZ + 0.5F;
        float f6 = 0.5625F;

        if (iFacing == 0)
        {
            f5 -= f6;
            f3 -= computeBlockOffset(art.sizeX);
        }
        else if (iFacing == 1)
        {
            f3 -= f6;
            f5 += computeBlockOffset(art.sizeX);
        }
        else if (iFacing == 2)
        {
            f5 += f6;
            f3 += computeBlockOffset(art.sizeX);
        }
        else if (iFacing == 3)
        {
            f3 += f6;
            f5 -= computeBlockOffset(art.sizeX);
        }
        
        f4 += computeBlockOffset(art.sizeY);
        
        setPosition(f3, f4, f5);
        
        float f7 = -0.00625F;
        boundingBox.setBounds(f3 - f - f7, f4 - f1 - f7, f5 - f2 - f7, f3 + f + f7, f4 + f1 + f7, f5 + f2 + f7);
    }

    private float computeBlockOffset(int iEdgeSize)
    {
    	if ( iEdgeSize % 32 == 0 )
    	{
    		return 0.5F;
    	}
    	
    	return 0.0F;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (tickCounter1++ == 100 && !worldObj.isRemote)
        {
            tickCounter1 = 0;

            if (!isDead && !onValidSurface())
            {
                setDead();
                worldObj.spawnEntityInWorld(EntityList.createEntityOfType(EntityItem.class, worldObj, posX, posY, posZ, new ItemStack(BTWItems.canvas)));
            }
        }
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        if (worldObj.getCollidingBoundingBoxes(this, boundingBox).size() > 0)
        {
            return false;
        }

        int i = art.sizeX / 16;
        int j = art.sizeY / 16;
        int k = canvasPosX;
        int l = canvasPosY;
        int i1 = canvasPosZ;

        if (direction == 0)
        {
            k = MathHelper.floor_double(posX - (double)((float) art.sizeX / 32F));
        }

        if (direction == 1)
        {
            i1 = MathHelper.floor_double(posZ - (double)((float) art.sizeX / 32F));
        }

        if (direction == 2)
        {
            k = MathHelper.floor_double(posX - (double)((float) art.sizeX / 32F));
        }

        if (direction == 3)
        {
            i1 = MathHelper.floor_double(posZ - (double)((float) art.sizeX / 32F));
        }

        l = MathHelper.floor_double(posY - (double)((float) art.sizeY / 32F));

        for (int j1 = 0; j1 < i; j1++)
        {
            for (int k1 = 0; k1 < j; k1++)
            {
                Material material;

                if (direction == 0 || direction == 2)
                {
                    material = worldObj.getBlockMaterial(k + j1, l + k1, canvasPosZ);
                }
                else
                {
                    material = worldObj.getBlockMaterial(canvasPosX, l + k1, i1 + j1);
                }

                if (!material.isSolid())
                {
                    return false;
                }
            }
        }

        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);

        for (int l1 = 0; l1 < list.size(); l1++)
        {
            if (list.get(l1) instanceof EntityPainting || list.get(l1) instanceof CanvasEntity)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (!isDead && !worldObj.isRemote)
        {
            setDead();
            setBeenAttacked();
            worldObj.spawnEntityInWorld(EntityList.createEntityOfType(EntityItem.class, worldObj, posX, posY, posZ, new ItemStack(BTWItems.canvas)));
        }

        return true;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("Dir", (byte)direction);
        par1NBTTagCompound.setString("Motive", art.title);
        par1NBTTagCompound.setInteger("TileX", canvasPosX);
        par1NBTTagCompound.setInteger("TileY", canvasPosY);
        par1NBTTagCompound.setInteger("TileZ", canvasPosZ);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        direction = par1NBTTagCompound.getByte("Dir");
        canvasPosX = par1NBTTagCompound.getInteger("TileX");
        canvasPosY = par1NBTTagCompound.getInteger("TileY");
        canvasPosZ = par1NBTTagCompound.getInteger("TileZ");
        String s = par1NBTTagCompound.getString("Motive");
        CanvasArt aenumart[] = CanvasArt.values();
        int i = aenumart.length;

        for (int j = 0; j < i; j++)
        {
        	CanvasArt enumart = aenumart[j];

            if (enumart.title.equals(s))
            {
                art = enumart;
            }
        }

        if (art == null)
        {
            art = CanvasArt.Icarus;
        }

        func_412_b(direction);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        if (!worldObj.isRemote && !isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)
        {
            setDead();
            worldObj.spawnEntityInWorld(EntityList.createEntityOfType(EntityItem.class, worldObj, posX, posY, posZ, new ItemStack(BTWItems.canvas)));
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double par1, double par3, double par5)
    {
        if (!worldObj.isRemote && !isDead && par1 * par1 + par3 * par3 + par5 * par5 > 0.0D)
        {
            setDead();
            worldObj.spawnEntityInWorld(EntityList.createEntityOfType(EntityItem.class, worldObj, posX, posY, posZ, new ItemStack(BTWItems.canvas)));
        }
    }
    
    /*
    @Override
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
    	// empty override to prevent network entity teleport packets from fucking up position and bounding box
    }
    */
    
    //************* FCIEntityPacketHandler ************//

    @Override
    public Packet getSpawnPacketForThisEntity()
    {    	
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream( byteStream );
        
        try
        {
	        dataStream.writeInt( BTWPacketManager.CANVAS_SPAWN_PACKET_ID);
	        dataStream.writeInt( entityId );
	        
	        dataStream.writeInt(canvasPosX);
	        dataStream.writeInt(canvasPosY);
	        dataStream.writeInt(canvasPosZ);
	        dataStream.writeInt( direction );
	        dataStream.writeInt(art.ordinal());
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }        
	        
    	return new Packet250CustomPayload(BTWPacketManager.SPAWN_CUSTOM_ENTITY_PACKET_CHANNEL, byteStream.toByteArray() );
    }
    
    @Override
    public int getTrackerViewDistance()
    {
    	return 160;
    }
    
    @Override
    public int getTrackerUpdateFrequency()
    {
    	return 0x7fffffff;
    }    
    
    @Override
    public boolean getTrackMotion()
    {
    	return false;
    }
    
    @Override
    public boolean shouldServerTreatAsOversized()
    {
    	return true;
    }
    
    @Override
    protected boolean shouldSetPositionOnLoad()
    {
    	return false;
    }
}
