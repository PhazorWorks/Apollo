package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;

public class Rewind extends Command {
    public Rewind() {
        this.name = "rewind";
        this.triggers = new String[]{"rewind"};
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        String argument = event.getArgument();
        AudioTrack track = player.getPlayingTrack();
        if (track == null) {
            event.getMessage().reply("**Nothing is currently playing.**").mentionRepliedUser(true).queue();
            return;
        }
        if (argument.isEmpty()) {
            event.getMessage().reply("Please provide a new position in seconds.").mentionRepliedUser(true).queue();
            return;
        }
        try {
            int seekNumber = Integer.parseInt(argument);
            long maxLength = track.getDuration();
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getTrackPosition();
            long newTime = currentTime - amountToSeek;
            //if (newTime < 0) newTime = 0;
            player.seekTo(newTime);
            long newCurrentTime = track.getPosition();
            System.out.printf("seekNumber: %s\nmaxLength: %s\nAmountToSeek: %s\ncurrentTime: %s \nnewTime: %s \nnewCurrentTime: %s", seekNumber, maxLength, amountToSeek, currentTime, newTime, newCurrentTime);
            event.getMessage().reply("Triggered").mentionRepliedUser(false).queue();
        } catch (NumberFormatException exception) {
            event.getMessage().reply("Failed to parse number from input").queue();
        }
    }

    protected void executeSlash(SlashEvent event) {

    }
}
