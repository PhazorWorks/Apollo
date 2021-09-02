package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Disconnect extends Command {
    public Disconnect() {
        this.name = "disconnect";
        this.triggers = new String[]{"disconnect", "dc", "fuckoff", "bye"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (!event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().isConnected()) {
            event.getTrigger().reply("**Not connected**").mentionRepliedUser(true).queue();
            return;
        }
        event.getClient().getMusicManager().disconnect(event.getGuild());
        event.getClient().getLavalink().getLink(event.getGuild()).destroy();
        event.getTrigger().reply("**Disconnected!**").mentionRepliedUser(false).queue();
    }
}
