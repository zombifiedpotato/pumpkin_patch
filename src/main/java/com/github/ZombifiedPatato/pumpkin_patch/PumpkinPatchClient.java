package com.github.ZombifiedPatato.pumpkin_patch;

import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import com.github.ZombifiedPatato.pumpkin_patch.entity.client.FairyPowderRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import com.github.ZombifiedPatato.pumpkin_patch.entity.client.DwarvenDynamiteRenderer;
import com.github.ZombifiedPatato.pumpkin_patch.networking.EntitySpawnPacket;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PumpkinPatchClient implements ClientModInitializer {

    public static final Identifier SPAWN_PACKET_ID = new Identifier(PumpkinPatch.MOD_ID, "spawn_packet");

    @Override
    public void onInitializeClient(){
        EntityRendererRegistry.register(ModEntities.FAIRY_POWDER, FairyPowderRenderer::new);
        EntityRendererRegistry.register(ModEntities.DWARVEN_DYNAMITE, DwarvenDynamiteRenderer::new);
        receiveEntityPacket();

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((((atlasTexture, registry) ->
                registry.register(new Identifier(PumpkinPatch.MOD_ID, "particle/pink_smoke")))));
        ParticleFactoryRegistry.getInstance().register(ModParticles.PINK_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
    }

    public void receiveEntityPacket() {
        System.out.println("running receiveEntityPacket");
        ClientPlayNetworking.registerGlobalReceiver(SPAWN_PACKET_ID, ((client, handler, byteBuf, responseSender) -> {
            System.out.println("Running for receiver for receiveEntityPacket");
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            client.execute(() -> {
                if (client.world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(client.world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(entityId);
                e.setUuid(uuid);
                client.world.addEntity(entityId, e);
            });
        }));
    }

}
