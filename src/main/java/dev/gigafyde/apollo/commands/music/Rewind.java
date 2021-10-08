package dev.gigafyde.apollo.commands.music;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.TimeUnit;

public class Rewind extends Command {
    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Rewind() {
        this.name = "rewind";
        this.triggers = new String[]{"rewind"};
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        LavalinkPlayer player = event.getClient().getLavalink().getLink(event.getGuild()).getPlayer();
        AudioTrack track = player.getPlayingTrack();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        switch (event.getCommandType()) {
            case REGULAR -> {
                String args = event.getArgument();
                message = event.getMessage();
                if (args.isEmpty()) {
                    sendError("**Please provide a new position in seconds!**");
                    return;
                }
                if (track == null) {
                    sendError("**Nothing is currently playing!**");
                    return;
                }
                rewindSong(args, player);
            }
            case SLASH -> {
                hook = event.getHook();
                if (track == null) {
                    sendError("**Nothing is currently playing!**");
                    return;
                }
                rewindSong(event.getOption("amount").getAsString(), player);
            }
        }
    }

    private void rewindSong(String args, LavalinkPlayer player) {
        try {
            int seekNumber = Integer.parseInt(args);
            long amountToSeek = seekNumber * 1000L;
            long currentTime = player.getTrackPosition();
            long newTime = currentTime - amountToSeek;
            player.seekTo(newTime);
            if (amountToSeek <= 59000)
                send(String.format("Rewound %d seconds.", TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));
            else
                send(String.format("Rewound %d min, %d seconds.", TimeUnit.MILLISECONDS.toMinutes(amountToSeek), TimeUnit.MILLISECONDS.toSeconds(amountToSeek) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(amountToSeek))));

        } catch (NumberFormatException exception) {
            sendError("**Failed to parse number from input!**");
        }
    }

    protected void sendError(String error) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> hook.editOriginal(error).queue();
        }
    }

    protected void send(String content) {
        switch (event.getCommandType()) {
            case REGULAR -> message.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }
}
