package us.potatoboy.petowner.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.potatoboy.petowner.client.PetOwnerClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(EntityRenderer.class)
public abstract class EntityRenderMixin {
    @Final
    @Shadow
    protected EntityRenderDispatcher dispatcher;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow
    public abstract void renderLabelIfPresent(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Inject(method = "render", at = @At("HEAD"))
    private void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Entity entity = PetOwnerClient.useEntity;

        //Entity entity = PetOwnerClient.useEntity;
        if (entity == null) {
            PetOwnerClient.LOGGER.info("entity was null");
            return;
        }

        //If HUD is hidden
        if (MinecraftClient.getInstance().options.hudHidden) return;
        //If the player is riding the entity
        if (entity.hasPassenger(MinecraftClient.getInstance().player)) return;
        //If the key is bound and owner is disabled
        if (!PetOwnerClient.keyBinding.isUnbound() && !PetOwnerClient.enabled) return;
        //If the entity is not targeted
        //if (dispatcher.targetedEntity != PetOwnerClient.useEntity && !PetOwnerConfig.alwaysShow) return;
//        if (!PetOwnerConfig.alwaysShow) {
//            PetOwnerClient.LOGGER.info("returning due to alwaysShow");
//            return;
//        }

        List<UUID> ownerIds = PetOwnerClient.getOwnerIds(entity);
        if (ownerIds.isEmpty()) return;

        for (int i = 0; i < ownerIds.size(); i++) {
            UUID ownerId = ownerIds.get(i);
            if (ownerId == null) return;

            Optional<String> usernameString = PetOwnerClient.getNameFromId(ownerId);

            Text text = Text.translatable("text.petowner.message.owner", usernameString.isPresent() ?
                    Text.literal(usernameString.get()).formatted(Formatting.WHITE) : Text.translatable("text.petowner.message.error").formatted(Formatting.RED)).formatted(Formatting.DARK_AQUA);
            if (FabricLoader.getInstance().isDevelopmentEnvironment() && usernameString.isEmpty()) {
                PetOwnerClient.LOGGER.error("If you're trying to figure out why the mod doesn't work, it's cause you're in a dev env");
            }

            state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw(PetOwnerClient.tickDelta));
            // this functions the exact same:
//            renderLabelIfPresent(
//                    state,
//                    text,
//                    matrices,
//                    vertexConsumers,
//                    light
//            );

            double d = this.dispatcher.getSquaredDistanceToCamera(entity);
            if (d <= 4096.0D) {
                Vec3d vec3d = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw(PetOwnerClient.tickDelta));
                if (vec3d != null) {
                    int y = 10 + (10 * i);
                    matrices.push();
                    matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
                    matrices.multiply(this.dispatcher.getRotation());
                    matrices.scale(0.025F, -0.025F, 0.025F);
                    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                    TextRenderer textRenderer = this.getTextRenderer();
                    float x = (float) (-textRenderer.getWidth(text) / 2);

                    float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
                    int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

                    PetOwnerClient.LOGGER.info("drawing at {}, {}, i: {}, tickDelta: {}", x, y, i, PetOwnerClient.tickDelta);
                    textRenderer.draw(text, x, (float) y, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, light);
                    textRenderer.draw(text, x, (float) y, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

                    matrices.pop();
                }
            }
            else
                PetOwnerClient.LOGGER.info("tickDelta was: {}", PetOwnerClient.tickDelta);
        }

        //PetOwnerClient.useEntity = null;
    }
}
