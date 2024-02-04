// FCMOD

package btw.block;

import net.minecraft.src.World;

public interface MechanicalBlock
{
	public boolean canOutputMechanicalPower();
	
	public boolean canInputMechanicalPower();
	
	public boolean isInputtingMechanicalPower(World world, int i, int j, int k);
	
	public boolean isOutputtingMechanicalPower(World world, int i, int j, int k);
	
	public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing);
	
	public void overpower(World world, int i, int j, int k);
}