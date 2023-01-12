package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;

import java.util.Objects;

public class Disconnect extends Command {

    private CommandEvent event;

    public Disconnect() {
        this.name = "disconnect";
        this.triggers = new String[]{"disconnect", "dc", "fuckoff", "bye", "leave"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> disconnect();
            case SLASH -> {
                event.deferReply().queue();
                disconnect();
            }
        }
    }

    protected void disconnect() {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (!Objects.requireNonNull(event.getSelfMember().getVoiceState()).inAudioChannel()) {
            event.sendError("**I am not connected to a voice channel!**");
            return;
        }
        if (!SongUtils.userConnectedToBotVC(event)) return;
        if (!SongUtils.botAloneInVC(event)) return;
        Objects.requireNonNull(event.getGuild()).getAudioManager().closeAudioConnection();
        event.send("I have disconnected from this voice channel.");
    }
}
