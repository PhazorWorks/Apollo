package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.Client;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.commands.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CommandEvent implements SlashCommandInteraction {
    private Client client;
    private Message trigger = null;
    private String argument = "";
    private CommandType type;
    private SlashCommandEvent slashCommandEvent;
//    private MessageContextCommandEvent messageContextCommandEvent;

    public enum CommandType {
        REGULAR,
        SLASH,
        CONTEXT,
        UNDEFINED
    }

//    public CommandEvent(Client client, Message trigger, String argument, SlashCommandEvent slashCommandEvent , MessageContextCommandEvent messageCommandEvent) {
    public CommandEvent(Client client, Message trigger, String argument, SlashCommandEvent slashCommandEvent ) {
        this.client = client;
        if (trigger != null) {
            this.type = CommandType.REGULAR;
        }
        if (slashCommandEvent != null) {
            this.type = CommandType.SLASH;
            this.slashCommandEvent = slashCommandEvent;
        }
//        if (messageCommandEvent != null) {
//            this.type = CommandType.CONTEXT;
//            this.messageContextCommandEvent = messageCommandEvent;
//        }
        if (this.type == CommandType.REGULAR) {
            this.argument = argument;
            this.trigger = trigger;
        }
    }

    public CommandType getCommandType() {
        return type;
    }

    public Client getClient() {
        return client;
    }

    public Message getTrigger() {
        return trigger;
    }

    public Message getMessage() {
        return trigger;
    }

    public User getAuthor() {
        switch (type) {
            case REGULAR -> { return trigger.getAuthor(); }
            case SLASH -> { return slashCommandEvent.getUser();}
//            case CONTEXT -> { return messageContextCommandEvent.getUser();}
        }
        return null;
    }

    public Member getMember() {
        return trigger.getMember();
    }

    public String getAuthorId() {
        return trigger.getAuthor().getId();
    }

    public Long getAuthorIdLong() {
        return trigger.getAuthor().getIdLong();
    }

    public SelfUser getSelfUser() {
        return trigger.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        Guild guild = trigger.getGuild();
        return guild.getSelfMember();
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public int getTypeRaw() {
        return 0;
    }

    @NotNull
    @Override
    public String getToken() {
        return null;
    }

    public Guild getGuild() {
        switch (type) {
            case REGULAR -> { return trigger.getGuild(); }
            case SLASH -> { return slashCommandEvent.getGuild();}
//            case CONTEXT -> { return messageContextCommandEvent.getGuild();}
        }
        return null;
    }


    public JDA getJDA() {
        switch (type) {
            case REGULAR -> {}
            case SLASH -> {}
            case CONTEXT -> {}
        }
        return trigger.getJDA();
    }

    public TextChannel getTextChannel() {
        switch (type) {
            case REGULAR -> {return trigger.getTextChannel();}
            case SLASH -> {return slashCommandEvent.getTextChannel();}
//            case CONTEXT -> {return messageContextCommandEvent.getTextChannel();}
        }
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    @NotNull
    @Override
    public MessageChannel getChannel() {
        switch (type) {
            case REGULAR -> {return trigger.getChannel();}
            case SLASH -> {return slashCommandEvent.getChannel();}
//            case CONTEXT -> {return messageContextCommandEvent.getChannel();}
        }
        return null;
    }

    @NotNull
    @Override
    public InteractionHook getHook() {
        if (type == CommandType.SLASH) {
            return slashCommandEvent.getHook();
        }
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @NotNull
    @Override
    public ReplyAction deferReply() {
        if (type == CommandType.SLASH) {
            return deferReply();
        }
        return null;
    }

    @Override
    public long getCommandIdLong() {
        return 0;
    }

    public String getChannelId() {
        return trigger.getChannel().getId();
    }

    public Long getChannelIdLong() {
        return trigger.getChannel().getIdLong();
    }

    public String getChannelName() {
        return trigger.getChannel().getName();
    }

    @NotNull
    public ChannelType getChannelType() {
        return trigger.getChannelType();
    }

    @NotNull
    @Override
    public User getUser() {
        switch (type) {
            case REGULAR -> {return trigger.getAuthor();}
            case SLASH -> {return slashCommandEvent.getUser();}
//            case CONTEXT -> {return messageContextCommandEvent.getUser();}
        }
        return null;
    }

    public String getMessageId() {
        return trigger.getId();
    }

    public Long getMessageIdLong() {
        return trigger.getIdLong();
    }

    public MessageType getMessageType() {
        return trigger.getType();
    }

    public boolean isWebhookMessage() {
        return trigger.isWebhookMessage();
    }

    public boolean isFromBot() {
        return trigger.getAuthor().isBot();
    }

    public List<Message.Attachment> getAttachments() {
        return trigger.getAttachments();
    }

    public boolean isFromGuild() {
        return trigger.isFromType(ChannelType.TEXT);
    }

    @NotNull
//    @Override
    public Message getTargetMessage() {
        return null;
    }

//    @Override
    public long getTargetIdLong() {
        return 0;
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        return null;
    }

    @Override
    public List<OptionMapping> getOptions() {
        slashCommandEvent.getOption("");

        return slashCommandEvent.getOptions();
    }

    public OptionMapping getOption(@Nonnull String name) {
        return slashCommandEvent.getOption(name);
    }


    @Override
    public long getIdLong() {
        return 0;
    }
}
