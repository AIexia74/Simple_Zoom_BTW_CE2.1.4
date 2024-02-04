package btw.block.tileentity.dispenser;

import btw.item.BTWItems;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class BTWDispenserBehaviorManager {
    public static void initDispenserBehaviors() {
        BlockDispenser.dispenseBehaviorRegistry.putObject(BTWItems.broadheadArrow, new BroadheadArrowDispenserBehavior());
        BlockDispenser.dispenseBehaviorRegistry.putObject(BTWItems.rottenArrow, new RottedArrowDispenserBehavior());
        BlockDispenser.dispenseBehaviorRegistry.putObject(BTWItems.soulUrn, new SoulUrnDispenserBehavior());
        BlockDispenser.dispenseBehaviorRegistry.putObject(BTWItems.dynamite, new DynamiteDispenserBehavior());

        BlockDispenser.dispenseBehaviorRegistry.putObject(BTWItems.netherSludge, new MortarApplicationDispenserBehavior());
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.clay, new MortarApplicationDispenserBehavior());
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.slimeBall, new MortarApplicationDispenserBehavior());

        // need this on the standalone server due to init order problems
        if (MinecraftServer.getServer() != null) {
            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.potion, new DispenserBehaviorPotion());

            DispenserBehaviorFilledBucket filledBucketBehavior = new DispenserBehaviorFilledBucket();

            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketLava, filledBucketBehavior);
            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketWater, filledBucketBehavior);

            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.bucketEmpty, new DispenserBehaviorEmptyBucket());
        }
    }
}
