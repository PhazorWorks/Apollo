package dev.gigafyde.apollo.core.command;

import dev.gigafyde.apollo.core.Client;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.commands.MessageContextCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class messageCommandEvent {
    private final Client client;
    private final MessageContextCommandEvent event;

    public messageCommandEvent(Client client, MessageContextCommandEvent messageCommandEvent) {
        this.client = client;
        this.event = messageCommandEvent;
    }

    public MessageContextCommandEvent getMessageCommandEvent() {
        return event;
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public Client getClient() {
        return client;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public VoiceChannel getVoiceChannel() {
        return event.getVoiceChannel();
    }

    public User getUser() {
        return event.getUser();
    }

    public User getAuthor() {
        return event.getUser();
    }

    public Member getMember() {
        return event.getMember();
    }

    // TODO add these methods after testing
    public ReplyAction reply(Message message) {
        return event.reply(message);
    }

    public ReplyAction reply(String message) {
        return event.reply(message);
    }
//    Not yet tested code, seems like it should work though.

}
