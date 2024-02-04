// FCMOD

package btw.block.stepsound;

import net.minecraft.src.StepSound;

public class SquishStepSound extends StepSound
{
	public SquishStepSound(String s, float f, float f1)
    {
        super( s, f, f1 );
    }
    
    @Override
    public String getStepSound()
    {
        return "mob.slime.attack";
    }    

    @Override
    public String getBreakSound()
    {
        return "mob.slime.small";
    }
}
