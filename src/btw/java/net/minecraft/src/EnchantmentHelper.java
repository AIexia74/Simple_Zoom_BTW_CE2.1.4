package net.minecraft.src;

import btw.BTWMod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentHelper
{
    /** Is the random seed of enchantment effects. */
    private static final Random enchantmentRand = new Random();

    /**
     * Used to calculate the extra armor of enchantments on armors equipped on player.
     */
    private static final EnchantmentModifierDamage enchantmentModifierDamage = new EnchantmentModifierDamage((Empty3)null);

    /**
     * Used to calculate the (magic) extra damage done by enchantments on current equipped item of player.
     */
    private static final EnchantmentModifierLiving enchantmentModifierLiving = new EnchantmentModifierLiving((Empty3)null);

    /**
     * Returns the level of enchantment on the ItemStack passed.
     */
    public static int getEnchantmentLevel(int par0, ItemStack par1ItemStack)
    {
        if (par1ItemStack == null)
        {
            return 0;
        }
        else
        {
            NBTTagList var2 = par1ItemStack.getEnchantmentTagList();

            if (var2 == null)
            {
                return 0;
            }
            else
            {
                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                    short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");

                    if (var4 == par0)
                    {
                        return var5;
                    }
                }

                return 0;
            }
        }
    }

    /**
     * Return the enchantments for the specified stack.
     */
    public static Map getEnchantments(ItemStack par0ItemStack)
    {
        LinkedHashMap var1 = new LinkedHashMap();
        NBTTagList var2 = par0ItemStack.itemID == Item.enchantedBook.itemID ? Item.enchantedBook.func_92110_g(par0ItemStack) : par0ItemStack.getEnchantmentTagList();

        if (var2 != null)
        {
            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");
                var1.put(Integer.valueOf(var4), Integer.valueOf(var5));
            }
        }

        return var1;
    }

    /**
     * Set the enchantments for the specified stack.
     */
    public static void setEnchantments(Map par0Map, ItemStack par1ItemStack)
    {
        NBTTagList var2 = new NBTTagList();
        Iterator var3 = par0Map.keySet().iterator();

        while (var3.hasNext())
        {
            int var4 = ((Integer)var3.next()).intValue();
            NBTTagCompound var5 = new NBTTagCompound();
            var5.setShort("id", (short)var4);
            var5.setShort("lvl", (short)((Integer)par0Map.get(Integer.valueOf(var4))).intValue());
            var2.appendTag(var5);

            if (par1ItemStack.itemID == Item.enchantedBook.itemID)
            {
                Item.enchantedBook.func_92115_a(par1ItemStack, new EnchantmentData(var4, ((Integer)par0Map.get(Integer.valueOf(var4))).intValue()));
            }
        }

        if (var2.tagCount() > 0)
        {
            if (par1ItemStack.itemID != Item.enchantedBook.itemID)
            {
                par1ItemStack.setTagInfo("ench", var2);
            }
        }
        else if (par1ItemStack.hasTagCompound())
        {
            par1ItemStack.getTagCompound().removeTag("ench");
        }
    }

    /**
     * Returns the biggest level of the enchantment on the array of ItemStack passed.
     */
    public static int getMaxEnchantmentLevel(int par0, ItemStack[] par1ArrayOfItemStack)
    {
        if (par1ArrayOfItemStack == null)
        {
            return 0;
        }
        else
        {
            int var2 = 0;
            ItemStack[] var3 = par1ArrayOfItemStack;
            int var4 = par1ArrayOfItemStack.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                ItemStack var6 = var3[var5];
                int var7 = getEnchantmentLevel(par0, var6);

                if (var7 > var2)
                {
                    var2 = var7;
                }
            }

            return var2;
        }
    }

    /**
     * Executes the enchantment modifier on the ItemStack passed.
     */
    private static void applyEnchantmentModifier(IEnchantmentModifier par0IEnchantmentModifier, ItemStack par1ItemStack)
    {
        if (par1ItemStack != null)
        {
            NBTTagList var2 = par1ItemStack.getEnchantmentTagList();

            if (var2 != null)
            {
                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                    short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");

                    if (Enchantment.enchantmentsList[var4] != null)
                    {
                        par0IEnchantmentModifier.calculateModifier(Enchantment.enchantmentsList[var4], var5);
                    }
                }
            }
        }
    }

    /**
     * Executes the enchantment modifier on the array of ItemStack passed.
     */
    private static void applyEnchantmentModifierArray(IEnchantmentModifier par0IEnchantmentModifier, ItemStack[] par1ArrayOfItemStack)
    {
        ItemStack[] var2 = par1ArrayOfItemStack;
        int var3 = par1ArrayOfItemStack.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];
            applyEnchantmentModifier(par0IEnchantmentModifier, var5);
        }
    }

    /**
     * Returns the modifier of protection enchantments on armors equipped on player.
     */
    public static int getEnchantmentModifierDamage(ItemStack[] par0ArrayOfItemStack, DamageSource par1DamageSource)
    {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.source = par1DamageSource;
        applyEnchantmentModifierArray(enchantmentModifierDamage, par0ArrayOfItemStack);

        if (enchantmentModifierDamage.damageModifier > 25)
        {
            enchantmentModifierDamage.damageModifier = 25;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    /**
     * Return the (magic) extra damage of the enchantments on player equipped item.
     */
    public static int getEnchantmentModifierLiving(EntityLiving par0EntityLiving, EntityLiving par1EntityLiving)
    {
        enchantmentModifierLiving.livingModifier = 0;
        enchantmentModifierLiving.entityLiving = par1EntityLiving;
        applyEnchantmentModifier(enchantmentModifierLiving, par0EntityLiving.getHeldItem());
        return enchantmentModifierLiving.livingModifier > 0 ? 1 + enchantmentRand.nextInt(enchantmentModifierLiving.livingModifier) : 0;
    }

    /**
     * Returns the knockback value of enchantments on equipped player item.
     */
    public static int getKnockbackModifier(EntityLiving par0EntityLiving, EntityLiving par1EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.knockback.effectId, par0EntityLiving.getHeldItem());
    }

    public static int getFireAspectModifier(EntityLiving par0EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, par0EntityLiving.getHeldItem());
    }

    /**
     * Returns the 'Water Breathing' modifier of enchantments on player equipped armors.
     */
    public static int getRespiration(EntityLiving par0EntityLiving)
    {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, par0EntityLiving.getLastActiveItems());
    }

    /**
     * Return the extra efficiency of tools based on enchantments on equipped player item.
     */
    public static int getEfficiencyModifier(EntityLiving par0EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, par0EntityLiving.getHeldItem());
    }

    /**
     * Returns the silk touch status of enchantments on current equipped item of player.
     */
    public static boolean getSilkTouchModifier(EntityLiving par0EntityLiving)
    {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, par0EntityLiving.getHeldItem()) > 0;
    }

    /**
     * Returns the fortune enchantment modifier of the current equipped item of player.
     */
    public static int getFortuneModifier(EntityLiving par0EntityLiving)
    {
    	int iEnchantmentLevel = getEnchantmentLevel(Enchantment.fortune.effectId, par0EntityLiving.getHeldItem());
    	
    	if ( par0EntityLiving.isPotionActive(BTWMod.potionFortune ) )
    	{
        	int iPotionLevel = par0EntityLiving.getActivePotionEffect( BTWMod.potionFortune ).getAmplifier() + 1;
        	
        	if ( iPotionLevel > iEnchantmentLevel )
        	{
        		iEnchantmentLevel = iPotionLevel;
        	}        	
    	}
    	
    	return iEnchantmentLevel;
    }

    /**
     * Returns the looting enchantment modifier of the current equipped item of player.
     */
    public static int getLootingModifier(EntityLiving par0EntityLiving)
    {
    	int iEnchantmentLevel = getEnchantmentLevel(Enchantment.looting.effectId, par0EntityLiving.getHeldItem());
    	
    	if ( par0EntityLiving.isPotionActive(BTWMod.potionLooting ) )
    	{
        	int iPotionLevel = par0EntityLiving.getActivePotionEffect( BTWMod.potionLooting ).getAmplifier() + 1;
        	
        	if ( iPotionLevel > iEnchantmentLevel )
        	{
        		iEnchantmentLevel = iPotionLevel;
        	}        	
    	}
    	
    	return iEnchantmentLevel;
    }

    /**
     * Returns the aqua affinity status of enchantments on current equipped item of player.
     */
    public static boolean getAquaAffinityModifier(EntityLiving par0EntityLiving)
    {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, par0EntityLiving.getLastActiveItems()) > 0;
    }

    public static int func_92098_i(EntityLiving par0EntityLiving)
    {
        return getMaxEnchantmentLevel(Enchantment.thorns.effectId, par0EntityLiving.getLastActiveItems());
    }

    public static ItemStack func_92099_a(Enchantment par0Enchantment, EntityLiving par1EntityLiving)
    {
        ItemStack[] var2 = par1EntityLiving.getLastActiveItems();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];

            if (var5 != null && getEnchantmentLevel(par0Enchantment.effectId, var5) > 0)
            {
                return var5;
            }
        }

        return null;
    }

    /**
     * Adds a random enchantment to the specified item. Args: random, itemStack, enchantabilityLevel
     */
    public static ItemStack addRandomEnchantment(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        List var3 = buildEnchantmentList(par0Random, par1ItemStack, par2);
        boolean var4 = par1ItemStack.itemID == Item.book.itemID;

        if (var4)
        {
            par1ItemStack.itemID = Item.enchantedBook.itemID;
        }

        if (var3 != null)
        {
            Iterator var5 = var3.iterator();

            while (var5.hasNext())
            {
                EnchantmentData var6 = (EnchantmentData)var5.next();

                if (var4)
                {
                    Item.enchantedBook.func_92115_a(par1ItemStack, var6);
                }
                else
                {
                    par1ItemStack.addEnchantment(var6.enchantmentobj, var6.enchantmentLevel);
                }
            }
        }

        return par1ItemStack;
    }

    /**
     * Create a list of random EnchantmentData (enchantments) that can be added together to the ItemStack, the 3rd
     * parameter is the total enchantability level.
     */
    public static List buildEnchantmentList(Random par0Random, ItemStack par1ItemStack, int par2)
    {
        Item var3 = par1ItemStack.getItem();
        int var4 = var3.getItemEnchantability();

        if (var4 <= 0)
        {
            return null;
        }
        else
        {
            var4 /= 2;
            var4 = 1 + par0Random.nextInt((var4 >> 1) + 1) + par0Random.nextInt((var4 >> 1) + 1);
            int var5 = var4 + par2;
            float var6 = (par0Random.nextFloat() + par0Random.nextFloat() - 1.0F) * 0.15F;
            int var7 = (int)((float)var5 * (1.0F + var6) + 0.5F);

            if (var7 < 1)
            {
                var7 = 1;
            }

            ArrayList var8 = null;
            Map var9 = mapEnchantmentData(var7, par1ItemStack);

            if (var9 != null && !var9.isEmpty())
            {
                EnchantmentData var10 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, var9.values());

                if (var10 != null)
                {
                    var8 = new ArrayList();
                    var8.add(var10);

                    for (int var11 = var7; par0Random.nextInt(50) <= var11; var11 >>= 1)
                    {
                        Iterator var12 = var9.keySet().iterator();

                        while (var12.hasNext())
                        {
                            Integer var13 = (Integer)var12.next();
                            boolean var14 = true;
                            Iterator var15 = var8.iterator();

                            while (true)
                            {
                                if (var15.hasNext())
                                {
                                    EnchantmentData var16 = (EnchantmentData)var15.next();

                                    if (var16.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[var13.intValue()]))
                                    {
                                        continue;
                                    }

                                    var14 = false;
                                }

                                if (!var14)
                                {
                                    var12.remove();
                                }

                                break;
                            }
                        }

                        if (!var9.isEmpty())
                        {
                            EnchantmentData var17 = (EnchantmentData)WeightedRandom.getRandomItem(par0Random, var9.values());
                            var8.add(var17);
                        }
                    }
                }
            }

            return var8;
        }
    }

    /**
     * Creates a 'Map' of EnchantmentData (enchantments) possible to add on the ItemStack and the enchantability level
     * passed.
     */
    public static Map mapEnchantmentData(int par0, ItemStack par1ItemStack)
    {
        Item var2 = par1ItemStack.getItem();
        HashMap var3 = null;
        boolean var4 = par1ItemStack.itemID == Item.book.itemID;
        Enchantment[] var5 = Enchantment.enchantmentsList;
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7)
        {
            Enchantment var8 = var5[var7];

            if ( var8 != null && (var2.isEnchantmentApplicable(var8) || var4))
            {
            	// FCMOD: Added to prevent certain enchants from vanilla enchanter
            	if ( !var8.canBeAppliedByVanillaEnchanter() )
            	{
            		continue;
            	}
            	// END FCMOD
            	
                for (int var9 = var8.getMinLevel(); var9 <= var8.getMaxLevel(); ++var9)
                {
                    if (par0 >= var8.getMinEnchantability(var9) && par0 <= var8.getMaxEnchantability(var9))
                    {
                        if (var3 == null)
                        {
                            var3 = new HashMap();
                        }

                        var3.put(Integer.valueOf(var8.effectId), new EnchantmentData(var8, var9));
                    }
                }
            }
        }

        return var3;
    }
    
    // FCMOD: Code added
    public static int calcItemStackEnchantability( Random rand, int iTableSlotNum, int iNumBookShelves, ItemStack stack )
    {
    	// Replace vanilla code to double amount of bookshelves required, cap enchant level at 15, and make enchant with no shelves be level 1
    	
        Item item = stack.getItem();
        int iItemEnchantability = item.getItemEnchantability();

        if (iItemEnchantability <= 0)
        {
            return 0;
        }
        else
        {
            int iEnchantmentLevel = 1;
            
            if ( iTableSlotNum != 0 )
            {
                int iMaxEnchantmentLevel = iNumBookShelves >> 1;
                    
                if ( iMaxEnchantmentLevel <= 0 )
                {
                	iMaxEnchantmentLevel = 1;
                }
                else if ( iMaxEnchantmentLevel > 15 )
                {
                	iMaxEnchantmentLevel = 15;
                }
                	
            	if ( iTableSlotNum == 1 )
            	{
            		if ( iMaxEnchantmentLevel > 1 )
            		{
                		iEnchantmentLevel = 2;
                		
                		if ( iMaxEnchantmentLevel > 3 )
                		{
                			iEnchantmentLevel += rand.nextInt( iMaxEnchantmentLevel - 2 );
                		}
            		}
            	}
            	else
            	{
            		iEnchantmentLevel = iMaxEnchantmentLevel;
            	}
            }
            
            return iEnchantmentLevel;
        }
    }
    // END FCMOD
}
