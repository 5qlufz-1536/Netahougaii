package com.saluf.netahougaii;

import com.saluf.netahougaii.command.RegisterCommand;
import com.saluf.netahougaii.main.Netahougaii;
import net.fabricmc.api.ClientModInitializer;

public class NetahougaiiMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Netahougaii.registerNetahougaii();
        RegisterCommand.registerCommands();
    }
}