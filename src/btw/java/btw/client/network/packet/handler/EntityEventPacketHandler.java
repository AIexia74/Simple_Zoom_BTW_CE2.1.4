package btw.client.network.packet.handler;

import btw.entity.mob.CowEntity;
import btw.entity.mob.SquidEntity;
import btw.network.packet.handler.CustomPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.WorldClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class EntityEventPacketHandler implements CustomPacketHandler {
    public static final int SET_ATTACK_TARGET_EVENT_ID = 0;
    public static final int SQUID_TENTACLE_ATTACK_EVENT_ID = 1;
    public static final int COW_KICK_ATTACK_EVENT_ID = 2;

    @Override
    public void handleCustomPacket(Packet250CustomPayload packet) throws IOException {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        int iEntityID = dataStream.readInt();

        Entity entity = world.getEntityByID(iEntityID);

        if (entity != null) {
            int iEventType = dataStream.readByte();

            if (iEventType == SET_ATTACK_TARGET_EVENT_ID) {
                if (entity instanceof EntityCreature) {
                    EntityCreature attackingCreature = (EntityCreature) entity;

                    int iTargetEntityID = dataStream.readInt();

                    if (iTargetEntityID >= 0) {
                        Entity targetEntity = world.getEntityByID(iTargetEntityID);

                        attackingCreature.setTarget(targetEntity);
                    } else {
                        attackingCreature.setTarget(null);
                    }
                }
            } else if (iEventType == SQUID_TENTACLE_ATTACK_EVENT_ID) {
                if (entity instanceof SquidEntity) {
                    SquidEntity attackingSquid = (SquidEntity) entity;

                    double dTargetXPos = ((double) (dataStream.readInt())) / 32D;
                    double dTargetYPos = ((double) (dataStream.readInt())) / 32D;
                    double dTargetZPos = ((double) (dataStream.readInt())) / 32D;

                    attackingSquid.onClientNotifiedOfTentacleAttack(dTargetXPos, dTargetYPos, dTargetZPos);
                }
            } else if (iEventType == COW_KICK_ATTACK_EVENT_ID) {
                if (entity instanceof CowEntity) {
                    CowEntity attackingCow = (CowEntity) entity;

                    attackingCow.onClientNotifiedOfKickAttack();
                }
            }
        }
    }
    }
