package com.snorbitzz.astralskies.mixin;

import com.snorbitzz.astralskies.Constants;
import com.snorbitzz.astralskies.registry.ModMenuTypes;
import com.snorbitzz.astralskies.screen.AstralPowersScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        
        Constants.LOG.info("This line is printed by an example mod common mixin!");
        Constants.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());

        // Register the Astral Powers screen for the menu type
        MenuScreens.register(ModMenuTypes.ASTRAL_POWERS, AstralPowersScreen::new);
        Constants.LOG.info("Astral Skies screens registered.");
    }
}