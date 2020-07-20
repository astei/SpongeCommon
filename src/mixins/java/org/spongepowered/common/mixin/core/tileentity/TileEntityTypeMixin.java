/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.bridge.ResourceKeyBridge;
import org.spongepowered.common.bridge.TrackableBridge;
import org.spongepowered.common.bridge.tileentity.TileEntityTypeBridge;
import org.spongepowered.common.config.SpongeConfig;
import org.spongepowered.common.config.category.BlockEntityTrackerCategory;
import org.spongepowered.common.config.category.BlockEntityTrackerModCategory;
import org.spongepowered.common.config.type.TrackerConfig;
import org.spongepowered.plugin.PluginContainer;

@Mixin(TileEntityType.class)
public abstract class TileEntityTypeMixin implements ResourceKeyBridge, TileEntityTypeBridge {

    private ResourceKey impl$key;
    private boolean impl$canTick;

    @Redirect(method = "register",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/registry/Registry;register(Lnet/minecraft/util/registry/Registry;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private static Object impl$setKeyAndInitializeTrackerState(final Registry<Object> registry, final String key, final Object tileEntityType) {
        final PluginContainer plugin = SpongeCommon.getActivePlugin();

        Registry.register(registry, key, tileEntityType);

        final ResourceKeyBridge resourceKeyBridge = (ResourceKeyBridge) tileEntityType;
        resourceKeyBridge.bridge$setKey(ResourceKey.of(plugin, key));

        final TrackableBridge trackableBridge = (TrackableBridge) tileEntityType;

        final SpongeConfig<TrackerConfig> trackerConfigAdapter = SpongeCommon.getTrackerConfigAdapter();
        final BlockEntityTrackerCategory blockEntityTracker = trackerConfigAdapter.getConfig().getBlockEntityTracker();

        BlockEntityTrackerModCategory modCapturing = blockEntityTracker.getModMappings().get(plugin.getMetadata().getId());

        if (modCapturing == null) {
            modCapturing = new BlockEntityTrackerModCategory();
            blockEntityTracker.getModMappings().put(plugin.getMetadata().getId(), modCapturing);
        }

        if (!modCapturing.isEnabled()) {
            trackableBridge.bridge$setAllowsBlockBulkCaptures(false);
            trackableBridge.bridge$setAllowsBlockEventCreation(false);
            trackableBridge.bridge$setAllowsEntityBulkCaptures(false);
            trackableBridge.bridge$setAllowsEntityEventCreation(false);
            modCapturing.getBlockBulkCaptureMap().computeIfAbsent(key, k -> false);
            modCapturing.getEntityBulkCaptureMap().computeIfAbsent(key, k -> false);
            modCapturing.getBlockEventCreationMap().computeIfAbsent(key, k -> false);
            modCapturing.getEntityEventCreationMap().computeIfAbsent(key, k -> false);
        } else {
            trackableBridge.bridge$setAllowsBlockBulkCaptures(modCapturing.getBlockBulkCaptureMap().computeIfAbsent(key, k -> true));
            trackableBridge.bridge$setAllowsBlockEventCreation(modCapturing.getBlockEventCreationMap().computeIfAbsent(key, k -> true));
            trackableBridge.bridge$setAllowsEntityBulkCaptures(modCapturing.getEntityBulkCaptureMap().computeIfAbsent(key, k -> true));
            trackableBridge.bridge$setAllowsEntityEventCreation(modCapturing.getEntityEventCreationMap().computeIfAbsent(key, k -> true));
        }

        if (blockEntityTracker.autoPopulateData()) {
            trackerConfigAdapter.save();
        }

        return tileEntityType;
    }

    @Override
    public ResourceKey bridge$getKey() {
        return this.impl$key;
    }

    @Override
    public void bridge$setKey(final ResourceKey key) {
        this.impl$key = key;
    }

    @Override
    public boolean bridge$canTick() {
        return this.impl$canTick;
    }

    @Override
    public boolean bridge$setCanTick(final boolean canTick) {
        return this.impl$canTick = canTick;
    }
}
