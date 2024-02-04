package net.minecraft.src;

public class StepSound
{
    public final String stepSoundName;
    public final float stepSoundVolume;
    public final float stepSoundPitch;

    public StepSound(String par1Str, float par2, float par3)
    {
        this.stepSoundName = par1Str;
        this.stepSoundVolume = par2;
        this.stepSoundPitch = par3;
    }

    public float getVolume()
    {
        return this.stepSoundVolume;
    }
    
    public float getStepVolume() {
    	return this.getVolume();
    }
    
    public float getPlaceVolume() {
    	return this.getVolume();
    }
    
    public float getBreakVolume() {
    	return this.getVolume();
    }

    public float getPitch()
    {
        return this.stepSoundPitch;
    }
    
    public float getStepPitch() {
    	return this.getPitch();
    }
    
    public float getPlacePitch() {
    	return this.getPitch();
    }
    
    public float getBreakPitch() {
    	return this.getPitch();
    }

    /**
     * Used when a block breaks, EXA: Player break, Shep eating grass, etc..
     */
    public String getBreakSound()
    {
        return "dig." + this.stepSoundName;
    }

    /**
     * Used when a entity walks over, or otherwise interacts with the block.
     */
    public String getStepSound()
    {
        return "step." + this.stepSoundName;
    }

    /**
     * Used when a player places a block.
     */
    public String getPlaceSound()
    {
        return this.getBreakSound();
    }
}
