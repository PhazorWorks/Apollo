package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Connect extends Command {

    private Message message;
    private InteractionHook hook;
    private CommandEvent event;

    public Connect() {
        this.name = "connect";
        this.triggers = new String[]{"connect", "summon", "join"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case REGULAR -> {
                message = event.getMessage();
                connect();
            }
            case SLASH -> {
                hook = event.getHook();
                event.deferReply().queue();
                connect();
            }
        }
    }

    protected void connect() {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        VoiceChannel vc = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        VoiceChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        if (vc == selfVC) {
            sendError("**I am already connected to this voice channel!**");
            return;
        }
        if (event.getSelfMember().getVoiceState().inVoiceChannel()) {
            List<Member> members = selfVC.getMembers().stream().filter(member -> !member.getUser().isBot()).collect(Collectors.toList());
            if (members.size() >= 2) {
                sendError("**I am already being used in a different voice channel!**");
                return;
            }
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(vc);
            send("Connected to `" + vc.getName() + "`.");
        } catch (InsufficientPermissionException ignored) {
            sendError("**I am unable to join this voice channel!**");
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
