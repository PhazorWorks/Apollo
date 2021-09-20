package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;

public class Disconnect extends Command {
    public Disconnect() {
        this.name = "disconnect";
        this.triggers = new String[]{"disconnect", "dc", "fuckoff", "bye"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (!event.getClient().getLavalink().getLink(event.getGuild()).getPlayer().isConnected()) {
            event.getMessage().reply("**Not connected**").mentionRepliedUser(true).queue();
            return;
        }
        event.getClient().getMusicManager().disconnect(event.getGuild());
        event.getClient().getLavalink().getLink(event.getGuild()).destroy();
        event.getMessage().reply("**Disconnected!**").mentionRepliedUser(false).queue();
    }

    protected void executeSlash(SlashEvent event) {

    }

    protected void executeContext(messageCommandEvent event) {

    }
}
