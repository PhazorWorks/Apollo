package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.Emoji;
import dev.gigafyde.apollo.utils.SongUtils;

import java.util.Objects;

public class Volume extends Command {
    private CommandEvent event;
    private AudioPlayer player;

    public Volume() {
        this.name = "volume";
        this.triggers = new String[]{"volume", "vol"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> {
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                player = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).player;
                if (event.getArgument().isEmpty()) {
                    event.send(Emoji.VOLUME + " Current volume is: " + getVolume() + "%");
                    return;
                }
                setVolume(event.getArgument());
            }
            case SLASH -> {
                event.deferReply().queue();
                if (!SongUtils.passedVoiceChannelChecks(event)) return;
                player = event.getClient().getMusicManager().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).player;
                if (event.getOptions().size() == 0) {
                    event.send(Emoji.VOLUME + " Current volume is: " + getVolume() + "%");
                    return;
                }
                setVolume(Objects.requireNonNull(event.getOption("input")).getAsString());
            }
        }
    }

    private float getVolume() {
        return player.getVolume();
    }

    private void setVolume(String input) {
        try {
            if (!Objects.requireNonNull(event.getSelfMember().getVoiceState()).inAudioChannel()) {
                event.sendError(Constants.botNotInVC);
                return;
            }
            if (!SongUtils.userConnectedToBotVC(event)) return;
            int volume = Integer.parseInt(input.replaceAll("[^0-9.]+", ""));
            if (volume > 300) {
                volume = 300;
                event.send("Requested volume exceeds 300, limiting to 300%");
            }
            player.setVolume(volume);
            event.send(Emoji.VOLUME + "  Volume set to: " + volume + "%.");
        } catch (NumberFormatException ignored) {
            event.sendError(Constants.invalidInt);
        }
    }
}
