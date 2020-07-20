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
package org.spongepowered.common.mixin.core.world;

import com.google.gson.JsonElement;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.dimension.DimensionType;
import org.spongepowered.api.world.dimension.DimensionTypes;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.teleport.PortalAgentType;
import org.spongepowered.api.world.teleport.PortalAgentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.bridge.world.storage.WorldInfoBridge;
import org.spongepowered.common.bridge.world.WorldSettingsBridge;
import org.spongepowered.common.world.dimension.SpongeDimensionType;

import javax.annotation.Nullable;

@Mixin(WorldSettings.class)
public abstract class WorldSettingsMixin implements WorldSettingsBridge {

    @Shadow private boolean commandsAllowed;
    @Shadow private boolean bonusChestEnabled;

    @Nullable private ResourceKey key;
    @Nullable private SpongeDimensionType dimensionType = (SpongeDimensionType) DimensionTypes.OVERWORLD.get();
    @Nullable private Difficulty difficulty = Difficulties.NORMAL.get();
    @Nullable private SerializationBehavior serializationBehavior = SerializationBehaviors.AUTOMATIC.get();
    private boolean isEnabled = true;
    private boolean loadOnStartup = true;
    @Nullable private Boolean keepSpawnLoaded = null;
    private boolean generateSpawnOnLoad = false;
    private boolean pvpEnabled = true;
    @Nullable private PortalAgentType portalAgentType;
    private boolean seedRandomized = false;
    @Nullable private DataContainer generatorSettings;

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At(value = "RETURN"))
    private void impl$reAssignValuesFromIncomingInfo(final WorldInfo info, final CallbackInfo ci) {
        final WorldProperties properties = (WorldProperties) info;
        if (((WorldInfoBridge) info).bridge$isValid()) {
            this.dimensionType = (SpongeDimensionType) properties.getDimensionType();
            this.dimensionType = (SpongeDimensionType) properties.getDimensionType();
            this.difficulty = properties.getDifficulty();
            this.serializationBehavior = properties.getSerializationBehavior();
            this.isEnabled = properties.isEnabled();
            this.loadOnStartup = properties.doesLoadOnStartup();
            this.keepSpawnLoaded = properties.doesKeepSpawnLoaded();
            this.generateSpawnOnLoad = properties.doesGenerateSpawnOnLoad();
            this.pvpEnabled = properties.isPVPEnabled();
            this.bonusChestEnabled = properties.doesGenerateBonusChest();
            this.generatorSettings = properties.getGeneratorSettings();
        }
    }

    @Override
    public boolean bridge$isSeedRandomized() {
        return this.seedRandomized;
    }

    @Override
    public void bridge$setRandomSeed(final boolean state) {
        this.seedRandomized = state;
    }

    @Inject(method = "setGeneratorOptions", at = @At(value = "RETURN"))
    private void onSetGeneratorOptions(final JsonElement element, final CallbackInfoReturnable<WorldSettings> cir) {
        // TODO 1.14 - JsonElement -> DataContainer
    }

    @Override
    public ResourceKey bridge$getKey() {
        return this.key;
    }

    @Override
    public SpongeDimensionType bridge$getLogicType() {
        return this.dimensionType;
    }

    @Override
    public Difficulty bridge$getDifficulty() {
        return this.difficulty;
    }

    @Override
    public PortalAgentType bridge$getPortalAgentType() {
        if (this.portalAgentType == null) {
            this.portalAgentType = PortalAgentTypes.DEFAULT.get();
        }
        return this.portalAgentType;
    }

    @Override
    public void bridge$setPortalAgentType(final PortalAgentType type) {
        this.portalAgentType = type;
    }

    @Override
    public DataContainer bridge$getGeneratorSettings() {
        return this.generatorSettings;
    }

    @Override
    public SerializationBehavior bridge$getSerializationBehavior() {
        return this.serializationBehavior;
    }

    @Override
    public boolean bridge$isEnabled() {
        return this.isEnabled;
    }

    @Override
    public boolean bridge$loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public boolean bridge$doesKeepSpawnLoaded() {
        if (this.keepSpawnLoaded == null) {
            this.keepSpawnLoaded = this.dimensionType == DimensionTypes.OVERWORLD.get();
        }
        return this.keepSpawnLoaded;
    }

    @Override
    public boolean bridge$generateSpawnOnLoad() {
        return this.generateSpawnOnLoad;
    }

    @Override
    public boolean bridge$isPVPEnabled() {
        return this.pvpEnabled;
    }

    @Override
    public void bridge$setKey(final ResourceKey key) {
        this.key = key;
    }

    @Override
    public void bridge$setDimensionType(final DimensionType dimensionType) {
        this.dimensionType = (SpongeDimensionType) dimensionType;
    }

    @Override
    public void bridge$setDifficulty(final Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void bridge$setSerializationBehavior(final SerializationBehavior behavior) {
        this.serializationBehavior = behavior;
    }

    @Override
    public void bridge$setGeneratorSettings(final DataContainer generatorSettings) {
        // TODO DataContainer -> JsonElement
    }

    @Override
    public void bridge$setEnabled(final boolean state) {
        this.isEnabled = state;
    }

    @Override
    public void bridge$setLoadOnStartup(final boolean state) {
        this.loadOnStartup = state;
    }

    @Override
    public void bridge$setKeepSpawnLoaded(@Nullable final Boolean state) {
        this.keepSpawnLoaded = state;
    }

    @Override
    public void bridge$setGenerateSpawnOnLoad(final boolean state) {
        this.generateSpawnOnLoad = state;
    }

    @Override
    public void bridge$setPVPEnabled(final boolean state) {
        this.pvpEnabled = state;
    }

    @Override
    public void bridge$setCommandsEnabled(final boolean state) {
        this.commandsAllowed = state;
    }

    @Override
    public void bridge$setGenerateBonusChest(final boolean state) {
        this.bonusChestEnabled = state;
    }

    @Override
    public Boolean bridge$internalKeepSpawnLoaded() {
        return this.keepSpawnLoaded;
    }

    @Override
    public void bridge$populateInfo(final WorldInfo info) {
        final WorldArchetype this$ = (WorldArchetype) (Object) this;
        final WorldInfoBridge infoBridge = (WorldInfoBridge) info;

        // TODO 1.14 - Add all the property setters
        infoBridge.bridge$setEnabled(this$.isEnabled());
        infoBridge.bridge$setLogicType(this$.getDimensionType());
        infoBridge.bridge$setLoadOnStartup(this$.doesLoadOnStartup());
        infoBridge.bridge$setGenerateSpawnOnLoad(this$.doesGenerateSpawnOnLoad());
        infoBridge.bridge$setKeepSpawnLoaded(this$.doesKeepSpawnLoaded());
        infoBridge.bridge$setGenerateBonusChest(this$.doesGenerateBonusChest());
        infoBridge.bridge$setSerializationBehavior(this$.getSerializationBehavior());
        infoBridge.bridge$forceSetDifficulty((net.minecraft.world.Difficulty) (Object) this$.getDifficulty());
    }
}
