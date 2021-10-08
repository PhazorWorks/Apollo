package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.CommandHandler;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

public class Restart extends Command {
    public Restart() {
        this.name = "restart";
        this.triggers = new String[]{"restart"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (event.getCommandType() == CommandHandler.CommandOriginType.SLASH) {
            event.deferReply().queue();
        }
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        if (player.getPlayingTrack() == null) {
            switch (event.getCommandType()) {
                case REGULAR -> {
                    event.getMessage().reply("**Nothing is currently playing!**").mentionRepliedUser(true).queue();
                    return;
                }
                case SLASH -> {
                    event.getHook().editOriginal("**Nothing is currently playing!**").queue();
                    return;
                }
            }
        }
        player.seekTo(0L);
        event.getMessage().addReaction(Emoji.SUCCESS.toString()).queue();
    }
}
