package dev.gigafyde.apollo.commands;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import  dev.gigafyde.apollo.Main;
import dev.gigafyde.apollo.commands.basic.Ping;
import dev.gigafyde.apollo.commands.music.*;
import dev.gigafyde.apollo.commands.secret.Eval;
import dev.gigafyde.apollo.core.Client;
import dev.gigafyde.apollo.core.command.CommandRegistry;

public class CommandList extends CommandRegistry {
    public CommandList(Client client) {
        CommandRegistry registry = client.getCommandRegistry();
        //basic commands
        registry.addCommand(new Ping());

        //music commands
        registry.addCommand(new Play());
        registry.addCommand(new Queue());
        registry.addCommand(new Pause());
        registry.addCommand(new Loop());
        registry.addCommand(new Resume());
        registry.addCommand(new Skip());
        registry.addCommand(new Shuffle());
        registry.addCommand(new Restart());
        registry.addCommand(new Connect());
        registry.addCommand(new Disconnect());
        registry.addCommand(new Volume());
        registry.addCommand(new Grab());
        registry.addCommand(new NowPlaying());
        registry.addCommand(new Seek());
        registry.addCommand(new Rewind());
        registry.addCommand(new Remove());
        registry.addCommand(new Clear());
        registry.addCommand(new Jump());
        if (Main.LYRICS_WEB_SERVER != null) registry.addCommand(new Lyrics());

        //secret commands
        registry.addCommand(new Eval());
    }
}
