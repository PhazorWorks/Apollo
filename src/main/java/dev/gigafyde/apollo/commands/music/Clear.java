package dev.gigafyde.apollo.commands.music;

import dev.gigafyde.apollo.core.TrackScheduler;
import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Clear extends Command {
    public Clear() {
        this.name = "clear";
        this.triggers = new String[]{"clear", "cls"};
    }

    protected void execute(CommandEvent event) {
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        TrackScheduler scheduler = event.getClient().getMusicManager().getScheduler(event.getGuild());
        if (scheduler == null) {
            VoiceChannel vc = event.getMember().getVoiceState().getChannel();
            if (vc == null) {
                event.getMessage().reply("**Please join a voice channel first!**").mentionRepliedUser(true).queue();
                return;
            }
            try {
                scheduler = event.getClient().getMusicManager().addScheduler(vc, false);
            } catch (InsufficientPermissionException ignored) {
                event.getMessage().reply("**Cannot join VC**").mentionRepliedUser(true).queue();
                return;
            }
        }
        scheduler.getQueue().clear();
        event.getMessage().reply("**Queue cleared**").mentionRepliedUser(false).queue();
    }

    protected void executeSlash(SlashEvent event) {

    }

    protected void executeContext(messageCommandEvent event) {

    }
}
