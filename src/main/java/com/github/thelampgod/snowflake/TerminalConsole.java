package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

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
        if (command.equals("stop") || command.equals("shutdown")) {
            new Thread(snowflake::shutdown).start();
            return;
        }
        for (ConnectionPair pair : snowflake.getServer().getConnections()) {
            pair.getReceiver().sendPacket(new PlainMessagePacket(command));
        }
    }

    @Override
    protected void shutdown() {

    }
}
