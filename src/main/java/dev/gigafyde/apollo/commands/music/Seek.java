package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

import java.util.concurrent.TimeUnit;

public class Seek extends Command {
    public Seek() {
        this.name = "seek";
        this.triggers = new String[]{"seek"};
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        AudioTrack playingTrack = player.getPlayingTrack();
        String argument = event.getArgument();
        if (argument.isEmpty()) {
            event.getMessage().reply("Please provide a new position in minutes.").mentionRepliedUser(true).queue();
            return;
        }
        if (playingTrack == null) {
            event.getMessage().reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
            return;
        }
        try {
            int seekNumber = Integer.parseInt(argument);
            long maxSeekLength = playingTrack.getDuration();
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getTrackPosition();
            if (amountToSeek + currentTime >= maxSeekLength) {
                event.getMessage().reply("**Input is larger then song duration!**").mentionRepliedUser(true).queue();
                return;
            }
            long newTime = currentTime + amountToSeek;
            player.seekTo(newTime);
            if (event.getArgument().startsWith("-")) {
                long amountRewound = -amountToSeek;
                if (amountToSeek < 61000)
                    event.getMessage().reply(String.format("** Rewound %d seconds**", TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound)))).queue();
                else
                    event.getMessage().reply(String.format("** Rewound %d min, %d seconds**", TimeUnit.MILLISECONDS.toMinutes(amountRewound), TimeUnit.MILLISECONDS.toSeconds(amountRewound) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountRewound)))).queue();
            } else {
                if (amountToSeek < 61000)
                    event.getMessage().reply(String.format("** %d seconds skipped**", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
                else
                    event.getMessage().reply(String.format("** %d min, %d seconds skipped**", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek)))).queue();
            }
        } catch (NumberFormatException exception) {
            event.getMessage().reply("Failed to parse number from input").queue();
        }
    }

    protected void executeSlash(SlashEvent event) {

    }

    protected void executeContext(messageCommandEvent event) {

    }
}
