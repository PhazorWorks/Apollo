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
import net.dv8tion.jda.api.events.interaction.commands.MessageContextCommandEvent;
import net.dv8tion.jda.api.events.interaction.commands.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.interactions.MessageCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.interactions.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CommandEvent implements SlashCommandInteraction, MessageCommandInteraction {
    private Client client = null;
    private Message trigger = null;
    private String argument = "";
    private CommandType type;
    private SlashCommandEvent slashCommandEvent;
    private MessageContextCommandEvent messageContextCommandEvent;

    public enum CommandType {
        REGULAR,
        SLASH,
        CONTEXT,
        UNDEFINED
    }

    public CommandEvent(Client client, Message trigger, String argument, SlashCommandEvent slashCommandEvent, MessageContextCommandEvent messageCommandEvent) {
        if (trigger != null) {
            this.type = CommandType.REGULAR;
        }
        if (slashCommandEvent != null) {
            this.type = CommandType.SLASH;
        }
        if (messageCommandEvent != null) {
            this.type = CommandType.CONTEXT;
        }
        if (this.type == CommandType.REGULAR) {
            this.argument = argument;
            this.client = client;
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
        return trigger.getAuthor();
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
        String argument = "";
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
        return trigger.getGuild();
    }

    public String getGuildName() {
        return trigger.getGuild().getName();
    }

    public String getGuildId() {
        return trigger.getGuild().getId();
    }

    public Long getGuildIdLong() {
        return trigger.getGuild().getIdLong();
    }

    public JDA getJDA() {
        return trigger.getJDA();
    }

    public TextChannel getTextChannel() {
        return trigger.getTextChannel();
    }

    @NotNull
    @Override
    public String getName() {
        return null;
    }

    public MessageChannel getChannel() {
        return trigger.getChannel();
    }

    @NotNull
    @Override
    public InteractionHook getHook() {
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @NotNull
    @Override
    public ReplyAction deferReply() {
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

    public ChannelType getChannelType() {
        return trigger.getChannelType();
    }

    @NotNull
    @Override
    public User getUser() {
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
    @Override
    public Message getTargetMessage() {
        return null;
    }

    @Override
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
