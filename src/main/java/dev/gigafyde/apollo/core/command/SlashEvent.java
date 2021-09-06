package dev.gigafyde.apollo.core.command;

import dev.gigafyde.apollo.core.Client;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashEvent {
    private final Client client;
    private final SlashCommandEvent event;

    public SlashEvent(Client client, SlashCommandEvent slashCommandEvent) {
        this.client = client;
        this.event = slashCommandEvent;
    }

    public SlashCommandEvent getSlashCommandEvent() {
        return event;
    }

    public Client getClient() {
        return client;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public User getAuthor() {
        return event.getUser();
    }

// TODO add these methods after testing
//    public ReplyAction reply(Message message) {
//        return event.reply(message);
//    }
//    public ReplyAction reply(String message) {
//        return event.reply(message);
//    }
//    Not yet tested code, seems like it should work though.

}
