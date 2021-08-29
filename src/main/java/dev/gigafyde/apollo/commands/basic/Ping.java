package dev.gigafyde.apollo.commands.basic;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Ping extends Command {
    public Ping() {
        this.name = "ping";
        this.triggers = new String[]{"ping"};
    }

    public void execute(CommandEvent event) {
        long currentTime = System.currentTimeMillis();
        event.getTrigger().reply("Pinging...").mentionRepliedUser(false).queue(message -> message.editMessage(new EmbedBuilder().setDescription(Emoji.HEARTBEAT + " " + event.getJDA().getGatewayPing() + " ms\n\n" + Emoji.PINGPONG + " " + (System.currentTimeMillis() - currentTime) + " ms").build()).override(true).queue());
    }
}
