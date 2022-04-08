package dev.gigafyde.apollo.commands.basic;

/*
 Created by GigaFyde
  https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.command.Command;
import dev.gigafyde.apollo.core.command.CommandEvent;
import dev.gigafyde.apollo.utils.Emoji;
import net.dv8tion.jda.api.EmbedBuilder;

public class Ping extends Command {
    public Ping() {
        this.name = "ping";
        this.triggers = new String[]{"ping"};
    }

    public void execute(CommandEvent event) {
        switch (event.getCommandType()) {
            case MESSAGE -> {
                long currentTime = System.currentTimeMillis();
                event.getMessage().reply("Pinging...").mentionRepliedUser(false).queue(message -> message.editMessageEmbeds(new EmbedBuilder().setDescription(Emoji.HEARTBEAT + " " + event.getJDA().getGatewayPing() + " ms\n\n" + Emoji.PINGPONG + " " + (System.currentTimeMillis() - currentTime) + " ms").build()).override(true).queue());
            }
            case SLASH -> {
                long currentTime = System.currentTimeMillis();
                event.deferReply().complete();
                String embed = Emoji.HEARTBEAT + " %s ms" + "\n" + Emoji.PINGPONG + " %s ms";
                event.getHook().editOriginal(String.format(embed, event.getJDA().getGatewayPing(), (System.currentTimeMillis() - currentTime))).queue();
            }
        }
    }
}
