package net.zombified_patato.pumpkin_patch.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.zombified_patato.pumpkin_patch.block.ModBlocks;
import net.zombified_patato.pumpkin_patch.entity.custom.DwarvenDynamiteEntity;

public class DwarvenDynamiteRenderer extends EntityRenderer<DwarvenDynamiteEntity> {

    public DwarvenDynamiteRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(DwarvenDynamiteEntity dwarvenDynamiteEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        matrixStack.translate(0.0, 0.5, 0.0);
        int j = dwarvenDynamiteEntity.getFuse();
        if ((float)j - g + 1.0f < 10.0f) {
            float h = 1.0f - ((float)j - g + 1.0f) / 10.0f;
            h = MathHelper.clamp(h, 0.0f, 1.0f);
            h *= h;
            h *= h;
            float k = 1.0f + h * 0.3f;
            matrixStack.scale(k, k, k);
        }
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0f));
        matrixStack.translate(-0.5, -0.5, 0.5);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        int i = j / 5 % 2 == 0 ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV;
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(ModBlocks.DWARVEN_DYNAMITE_BLOCK.getDefaultState(), matrixStack, vertexConsumerProvider, light, i);
        matrixStack.pop();
        super.render(dwarvenDynamiteEntity, f, g, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(DwarvenDynamiteEntity entity) {
        return null;
    }

}
