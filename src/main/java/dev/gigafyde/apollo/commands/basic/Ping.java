package dev.gigafyde.apollo.commands.basic;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.core.command.SlashEvent;
import dev.gigafyde.apollo.core.command.messageCommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;

public class Ping extends Command {
    public Ping() {
        this.name = "ping";
        this.triggers = new String[]{"ping"};
    }

    public void execute(CommandEvent event) {
        long currentTime = System.currentTimeMillis();
        event.getMessage().reply("Pinging...").mentionRepliedUser(false).queue(message -> message.editMessageEmbeds(new EmbedBuilder().setDescription(Emoji.HEARTBEAT + " " + event.getJDA().getGatewayPing() + " ms\n\n" + Emoji.PINGPONG + " " + (System.currentTimeMillis() - currentTime) + " ms").build()).override(true).queue());
    }

    @Override
    protected void executeSlash(SlashEvent event) {

    }

    @Override
    protected void executeContext(messageCommandEvent event) {

    }
}
