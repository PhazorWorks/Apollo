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

import java.util.Objects;

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
        Member member = event.getGuild().retrieveMember(event.getAuthor()).complete();
        VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (vc == Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel()) {
            sendError("**Already connected to **`" + vc.getName() + "`");
            return;
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(vc);
            send("**Connected to **`" + vc.getName() + "`");
        } catch (InsufficientPermissionException ignored) {
            sendError("**Failed to connect to the desired voice channel.**");
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
            case REGULAR -> message.reply(content).queue();
            case SLASH -> hook.editOriginal(content).queue();
        }
    }


}
