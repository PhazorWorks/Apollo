package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Constants;
import dev.gigafyde.apollo.utils.SongUtils;
import java.util.Objects;
import net.dv8tion.jda.api.entities.AudioChannel;
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
        AudioChannel vc = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
        AudioChannel selfVC = Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel();
        if (vc == selfVC) {
            event.sendError("**I am already connected to this voice channel!**");
            return;
        }
        if (event.getSelfMember().getVoiceState().inAudioChannel()) {
            if (!SongUtils.botAloneInVC(event)) return;
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(Objects.requireNonNull(vc));
            event.getClient().getMusicManager().getScheduler(Objects.requireNonNull(event.getGuild())).setBoundChannel(event.getChannel());
            event.send("Connected to`" + vc.getName() + "`.");
        } catch (InsufficientPermissionException ignored) {
            event.sendError(Constants.unableToJoinVC);
        }
    }

}
