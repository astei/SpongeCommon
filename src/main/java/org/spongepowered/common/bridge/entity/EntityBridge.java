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
package org.spongepowered.common.bridge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.event.cause.entity.dismount.DismountType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Transform;
import org.spongepowered.common.event.tracking.phase.tick.EntityTickContext;
import org.spongepowered.math.vector.Vector3d;

public interface EntityBridge {

    boolean bridge$isConstructing();

    void bridge$fireConstructors();

    boolean bridge$removePassengers(DismountType type);

    void bridge$setImplVelocity(Vector3d velocity);

    @Nullable Text bridge$getDisplayNameText();

    void bridge$setDisplayName(@Nullable Text displayName);

    @Nullable BlockPos bridge$getLastCollidedBlockPos();

    void bridge$setLocationAndAngles(Transform transform);

    void bridge$setFireImmuneTicks(int ticks);


    default void bridge$clearWrappedCaptureList() {

    }

    /**
     * @author gabizou - July 26th, 2018
     * @reason Due to vanilla logic, a block is removed *after* the held item is set,
     * so, when the block event gets cancelled, we don't have a chance to cancel the
     * enderman pickup. Specifically applies to Enderman so far, may have other uses
     * in the future.
     *
     * @param phaseContext The context, for whatever reason in the future
     */
    default void bridge$onCancelledBlockChange(EntityTickContext phaseContext) {

    }
}
