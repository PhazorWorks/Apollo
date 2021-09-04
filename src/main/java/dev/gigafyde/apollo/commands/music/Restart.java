package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

public class Restart extends Command {
    public Restart() {
        this.name = "restart";
        this.triggers = new String[]{"restart"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        player.seekTo(0L);
        event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }
}
