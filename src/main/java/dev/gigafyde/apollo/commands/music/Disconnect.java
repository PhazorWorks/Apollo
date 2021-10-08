package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Objects;

public class Disconnect extends Command {

    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Disconnect() {
        this.name = "disconnect";
        this.triggers = new String[]{"disconnect", "dc", "fuckoff", "bye", "leave"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                disconnect();
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                disconnect();
            }
        }
    }

    protected void disconnect() {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        VoiceChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        if (!Objects.requireNonNull(event.getSelfMember().getVoiceState()).inVoiceChannel()) {
            sendError("**I am not connected to a voice channel!**");
            return;
        }
        if (vc != selfVC) {
            sendError("**I am connected elsewhere, please join my voice channel if you wish to disconnect me!**");
            return;
        }
        event.getClient().getMusicManager().disconnect(event.getGuild());
        event.getClient().getLavalink().getLink(event.getGuild()).destroy();
        send("I have disconnected from this voice channel.");
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
