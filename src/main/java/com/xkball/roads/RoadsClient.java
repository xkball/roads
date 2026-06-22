package com.xkball.roads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import com.xkball.roads.block.collidetest.TriangleCollection;
import com.xkball.roads.client.LocalPlayerCollideMomentumDebugEntry;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Mod(value = Roads.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Roads.MODID, value = Dist.CLIENT)
public class RoadsClient {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Identifier LOCAL_PLAYER_COLLIDE_MOMENTUM = Identifier.fromNamespaceAndPath(Roads.MODID, "local_player_collide_momentum");

    public RoadsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Roads.LOGGER.info("HELLO FROM CLIENT SETUP");
        Roads.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("roads")
                .then(Commands.literal("export_triangles")
                        .then(Commands.argument("block", BlockStateArgument.block(event.getBuildContext()))
                                .executes(context -> exportTriangles(context.getSource(), BlockStateArgument.getBlock(context, "block").getState())))));
    }

    @SubscribeEvent
    static void onRegisterDebugEntries(RegisterDebugEntriesEvent event) {
        event.register(LOCAL_PLAYER_COLLIDE_MOMENTUM, new LocalPlayerCollideMomentumDebugEntry());
        event.includeInProfile(LOCAL_PLAYER_COLLIDE_MOMENTUM, DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.IN_OVERLAY);
    }

    private static int exportTriangles(CommandSourceStack source, BlockState state) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        TriangleCollection collection = collectTriangles(state);
        Path path = Path.of(id.getNamespace() + "_" + id.getPath().replace('/', '_') + ".json").toAbsolutePath().normalize();
        try (Writer writer = Files.newBufferedWriter(path)) {
            var json = TriangleCollection.CODEC.encodeStart(JsonOps.INSTANCE, collection)
                    .getOrThrow(message -> new IllegalStateException(message));
            GSON.toJson(json, writer);
            source.sendSuccess(() -> Component.literal("Exported triangle collection to " + path), false);
            return collection.triangles().size();
        } catch (IOException | RuntimeException e) {
            Roads.LOGGER.error("Failed to export triangle collection", e);
            source.sendFailure(Component.literal("Failed to export triangle collection: " + e.getMessage()));
            return 0;
        }
    }

    private static TriangleCollection collectTriangles(BlockState state) {
        var model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(BlockAndTintGetter.EMPTY, BlockPos.ZERO, state, RandomSource.create(42L), parts);
        List<TriangleCollection.Triangle> triangles = new ArrayList<>();
        for (BlockStateModelPart part : parts) {
            addQuads(triangles, part.getQuads(null));
            for (Direction direction : Direction.values()) {
                addQuads(triangles, part.getQuads(direction));
            }
        }
        return new TriangleCollection(triangles);
    }

    private static void addQuads(List<TriangleCollection.Triangle> triangles, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            Vector3f v0 = copy(quad.position0());
            Vector3f v1 = copy(quad.position1());
            Vector3f v2 = copy(quad.position2());
            Vector3f v3 = copy(quad.position3());
            triangles.add(new TriangleCollection.Triangle(v0, v1, v2));
            triangles.add(new TriangleCollection.Triangle(v0, v2, v3));
        }
    }

    private static Vector3f copy(Vector3fc vector) {
        return new Vector3f(vector.x(), vector.y(), vector.z());
    }
}
