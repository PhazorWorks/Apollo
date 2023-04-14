package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Connect extends Command {

    private CommandEvent event;

    public Connect() {
        this.name = "connect";
        this.triggers = new String[]{"connect", "summon", "join"};
        this.guildOnly = true;
    }

    protected void execute(CommandEvent event) {
        this.event = event;
        switch (event.getCommandType()) {
            case MESSAGE -> connect();
            case SLASH -> {
                event.deferReply().queue();
                connect();
            }
        }
    }

    protected void connect() {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        VoiceChannel vc = event.getGuild().getVoiceChannelById(event.getMember().getVoiceState().getChannel().getIdLong());
        AudioChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        if (vc == selfVC) {
            event.sendError("**I am already connected to this voice channel!**");
            return;
        }
        if (event.getSelfMember().getVoiceState().inAudioChannel()) {
            if (!SongUtils.botAloneInVC(event)) return;
        }
        try {
            event.getGuild().getAudioManager().openAudioConnection(vc);
            event.sendMessage("Connected to`" + vc.getName() + "`.");
        } catch (InsufficientPermissionException ignored) {
            event.sendError(Constants.unableToJoinVC);
        }
    }

}
