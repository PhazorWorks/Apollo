package dev.gigafyde.apollo.commands.music;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.utils.SongUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.Objects;

public class Connect extends Command {
    public Connect() {
        this.name = "connect";
        this.triggers = new String[]{"connect", "summon", "join"};
        this.guildOnly = true;
    }

    public void execute(CommandEvent event) {
        Member member = event.getGuild().retrieveMember(event.getAuthor()).complete();
        VoiceChannel vc = Objects.requireNonNull(member.getVoiceState()).getChannel();
        if (!SongUtils.passedVoiceChannelChecks(event)) return;
        if (vc == Objects.requireNonNull(event.getSelfMember().getVoiceState()).getChannel()) {
            event.getMessage().reply("**Already connected to **`" + vc.getName() + "`").mentionRepliedUser(true).queue();
            return;
        }
        try {
            event.getClient().getMusicManager().moveVoiceChannel(vc);
            event.getMessage().reply("**Connected to **`" + vc.getName() + "`").mentionRepliedUser(false).queue();
        } catch (InsufficientPermissionException ignored) {
            event.getMessage().reply("**Failed to connect to the desired voice channel.**").mentionRepliedUser(true).queue();
        }
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }
}
