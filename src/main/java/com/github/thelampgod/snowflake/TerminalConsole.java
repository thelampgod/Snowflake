package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

import java.io.IOException;


public class TerminalConsole extends SimpleTerminalConsole {
    private final Snowflake snowflake;
    public TerminalConsole(Snowflake snowflake) {
        this.snowflake = snowflake;
    }



    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected void runCommand(String command) {
        try {
            for (SocketClient client : snowflake.getServer().connectedClients) {
                if (!client.isReceiver()) return;

                client.getConnection().sendPacket(new PlainMessagePacket(command));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void shutdown() {

    }
}
