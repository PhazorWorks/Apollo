package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;

public class Disconnect extends Command {
    public Disconnect() {
        this.name = "disconnect";
        this.triggers = new String[]{"disconnect", "dc", "fuckoff"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!event.getGuild().getAudioManager().isConnected()) {
            event.getChannel().sendMessage("**Not connected**").queue();
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
        event.getClient().getMusicManager().disconnect(event.getGuild());
        event.getChannel().sendMessage("**Disconnected!**").queue();
    }
}
