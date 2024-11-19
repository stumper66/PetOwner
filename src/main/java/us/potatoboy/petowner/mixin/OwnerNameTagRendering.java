package us.potatoboy.petowner.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import us.potatoboy.petowner.client.PetOwnerClient;
import us.potatoboy.petowner.client.config.PetOwnerConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mixin(EntityRenderDispatcher.class)
public abstract class OwnerNameTagRendering {

	@Shadow
	public Entity targetedEntity;

	@Shadow
	public abstract double getSquaredDistanceToCamera(Entity entity);

	@Shadow
	public abstract Quaternionf getRotation();

	@Shadow @Final
	public TextRenderer textRenderer;

	@Inject(method = "render", at = @At("TAIL"))
	private void render(Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		//PetOwnerClient.LOGGER.info("EntityRenderDispatcher#render entity: {}", entity.getType().getName());
		PetOwnerClient.useEntity = entity;
		PetOwnerClient.tickDelta = tickDelta;
		//If HUD is hidden

//		if (MinecraftClient.getInstance().options.hudHidden) return;
//		//If the player is riding the entity
//		if (entity.hasPassenger(MinecraftClient.getInstance().player)) return;
//		//If the key is bound and owner is disabled
//		if (!PetOwnerClient.keyBinding.isUnbound() && !PetOwnerClient.enabled) return;
//		//If the entity is not targeted
////		if (targetedEntity != entity && !PetOwnerConfig.alwaysShow) {
////			PetOwnerClient.LOGGER.info("return 1");
////			return;
////		}
//
//		List<UUID> ownerIds = PetOwnerClient.getOwnerIds(entity);
//		if (ownerIds.isEmpty()) return;
//
//		for (int i = 0; i < ownerIds.size(); i++) {
//			UUID ownerId = ownerIds.get(i);
//			if (ownerId == null) return;
//
//			Optional<String> usernameString = PetOwnerClient.getNameFromId(ownerId);
//			if (usernameString.isPresent())
//				PetOwnerClient.LOGGER.info("owner: {}", usernameString.get());
//			else
//				PetOwnerClient.LOGGER.info("owner was empty");
//
//			Text text = Text.translatable("text.petowner.message.owner", usernameString.isPresent() ?
//					Text.literal(usernameString.get()).formatted(Formatting.WHITE) : Text.translatable("text.petowner.message.error").formatted(Formatting.RED)).formatted(Formatting.DARK_AQUA);
//			if (FabricLoader.getInstance().isDevelopmentEnvironment() && usernameString.isEmpty()) {
//				PetOwnerClient.LOGGER.error("If you're trying to figure out why the mod doesn't work, it's cause you're in a dev env");
//			}
//
//			double d = getSquaredDistanceToCamera(entity);
//			if (d <= 4096.0D) {
//				Vec3d vec3d = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw(tickDelta));
//				if (vec3d != null) {
//					int newY = 10 + (10 * i);
//					matrices.push();
//					matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
//					matrices.multiply(getRotation());
//					matrices.scale(0.025F, -0.025F, 0.025F);
//					Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//					float newX = (float) (-textRenderer.getWidth(text) / 2);
//
//					float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
//					int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;
//
//					//PetOwnerClient.LOGGER.info("drawing at {}, {}, x: {}, y: {}, i: {}", newX, newY, x, y, i);
//					textRenderer.draw(text, newX, (float) newY, 553648127, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, light);
//					textRenderer.draw(text, newX, (float) newY, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
//
//					matrices.pop();
//				}
//			}
//		}
	}

}
