package dev.gigafyde.apollo.core.command;

/*
 Created by GigaFyde
 https://github.com/GigaFyde
 */

import dev.gigafyde.apollo.core.Client;
import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.NewsChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.context.MessageContextInteraction;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("ConstantConditions")
public class CommandEvent implements SlashCommandInteraction, MessageContextInteraction {
    private final Command command;
    private final Client client;
    private final net.dv8tion.jda.api.interactions.commands.Command.Type type;
    private Message trigger = null;
    private String argument = "";
    private SlashCommandInteraction slashCommandEvent;
    private MessageContextInteraction messageContextCommandEvent;

    public CommandEvent(Command command, Client client, net.dv8tion.jda.api.interactions.commands.Command.Type type, Message trigger, String argument, SlashCommandInteraction slashCommandEvent, MessageContextInteraction messageCommandEvent) {
        this.type = type;
        this.command = command;
        this.client = client;
        switch (type) {
            case MESSAGE -> {
                this.argument = argument;
                this.trigger = trigger;
            }
            case SLASH -> {
                this.slashCommandEvent = slashCommandEvent;
                slashCommandEvent.deferReply().queue();
            }
            case USER -> this.messageContextCommandEvent = messageCommandEvent;
        }
    }

    public net.dv8tion.jda.api.interactions.commands.Command.Type getCommandType() {
        return type;
    }

    public Client getClient() {
        return client;
    }

    public Message getMessage() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) return trigger;
        return null;
    }

    public User getAuthor() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case USER -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public Member getMember() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getMember();
            }
            case SLASH -> {
                return slashCommandEvent.getMember();
            }
            case USER -> {
                return messageContextCommandEvent.getMember();
            }
        }
        return null;
    }

    public SelfUser getSelfUser() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getJDA().getSelfUser();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA().getSelfUser();
            }
            case USER -> {
                return messageContextCommandEvent.getJDA().getSelfUser();
            }
        }
        return null;
    }

    public Member getSelfMember() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getGuild().getSelfMember();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild().getSelfMember();
            }
            case USER -> {
                return messageContextCommandEvent.getGuild().getSelfMember();
            }
        }
        return null;
    }

    public String getArgument() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) {
            return argument;
        }
        return "";
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
            case MESSAGE -> {
                return trigger.getGuild();
            }
            case SLASH -> {
                return slashCommandEvent.getGuild();
            }
            case USER -> {
                return messageContextCommandEvent.getGuild();
            }
        }
        return null;
    }


    public @NotNull JDA getJDA() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getJDA();
            }
            case SLASH -> {
                return slashCommandEvent.getJDA();
            }
            case USER -> {
                return messageContextCommandEvent.getJDA();
            }
        }
        return null;
    }

    public @NotNull TextChannel getTextChannel() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getTextChannel();
            }
            case SLASH -> {
                return slashCommandEvent.getTextChannel();
            }
            case USER -> {
                return messageContextCommandEvent.getTextChannel();
            }
        }
        return null;
    }

    public @NotNull ReplyCallbackAction reply(@NotNull Message content) {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.reply(content);
            }
            case USER -> {
                return messageContextCommandEvent.reply(content);
            }
        }
        return null;
    }


    @NotNull
    @Override
    public String getName() {
        return command.name;
    }

    @NotNull
    @Override
    public MessageChannel getChannel() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getChannel();
            }
            case SLASH -> {
                return slashCommandEvent.getChannel();
            }
            case USER -> {
                return messageContextCommandEvent.getChannel();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public InteractionHook getHook() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.getHook();
            }
            case USER -> {
                return messageContextCommandEvent.getHook();
            }
        }
        return null;
    }

    @Override
    public boolean isAcknowledged() {
        return false;
    }

    @NotNull
    @Override
    public ReplyCallbackAction deferReply() {
        switch (type) {
            case SLASH -> {
                return slashCommandEvent.deferReply();
            }
            case USER -> {
                return messageContextCommandEvent.deferReply();
            }
        }
        return null;
    }


    @Override
    public long getCommandIdLong() {
        return 0;
    }

    @NotNull
    public ChannelType getChannelType() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getChannelType();
            }
            case SLASH -> {
                return slashCommandEvent.getChannelType();
            }
            case USER -> {
                return messageContextCommandEvent.getChannelType();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public User getUser() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getAuthor();
            }
            case SLASH -> {
                return slashCommandEvent.getUser();
            }
            case USER -> {
                return messageContextCommandEvent.getUser();
            }
        }
        return null;
    }

    public List<Message.Attachment> getAttachments() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE) {
            return trigger.getAttachments();
        }
        return null;
    }

    public boolean isFromGuild() {
        switch (type) {
            case MESSAGE -> {
                return trigger.isFromGuild();
            }
            case SLASH -> {
                return slashCommandEvent.isFromGuild();
            }
            case USER -> {
                return messageContextCommandEvent.isFromGuild();
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Message getTarget() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.USER) {
            return messageContextCommandEvent.getTarget();
        }
        return null;
    }

    //@Override
    public long getTargetIdLong() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.USER) {
            return messageContextCommandEvent.getTarget().getIdLong();
        }
        return 0;
    }

    @Nullable
    @Override
    public String getSubcommandName() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
            return slashCommandEvent.getSubcommandName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getSubcommandGroup() {
        if (type == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH) {
            return slashCommandEvent.getSubcommandGroup();
        }
        return null;
    }

    @Override
    public @NotNull List<OptionMapping> getOptions() {
        return slashCommandEvent.getOptions();
    }

    public OptionMapping getOption(@Nonnull String name) {
        return slashCommandEvent.getOption(name);
    }


    @Override
    public long getIdLong() {
        switch (type) {
            case MESSAGE -> {
                return trigger.getIdLong();
            }
            case SLASH -> {
                return slashCommandEvent.getIdLong();
            }
            case USER -> {
                return messageContextCommandEvent.getIdLong();
            }
        }
        return 0;
    }

    public void sendError(String error) {
        switch (type) {
            case MESSAGE -> trigger.reply(error).mentionRepliedUser(true).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(error).queue();
        }
    }

    public void send(String content) {
        switch (type) {
            case MESSAGE -> trigger.reply(content).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(content).queue();
        }
    }

    public void sendFile(InputStream inputStream, String name) {
        switch (type) {
            case MESSAGE -> trigger.reply(inputStream, name).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginal(inputStream, name).queue();
        }
    }

    public void sendEmbed(EmbedBuilder embed) {
        switch (type) {
            case MESSAGE -> trigger.replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
            case SLASH -> slashCommandEvent.getHook().editOriginalEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public Locale getUserLocale() {
        return null;
    }

    @NotNull
    @Override
    public GuildMessageChannel getGuildChannel() {
        return SlashCommandInteraction.super.getGuildChannel();
    }

    @NotNull
    @Override
    public ContextTarget getTargetType() {
        return MessageContextInteraction.super.getTargetType();
    }

    @NotNull
    @Override
    public ReplyCallbackAction deferReply(boolean ephemeral) {
        return SlashCommandInteraction.super.deferReply(ephemeral);
    }

    @NotNull
    @Override
    public ReplyCallbackAction reply(@NotNull String content) {
        return SlashCommandInteraction.super.reply(content);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return SlashCommandInteraction.super.replyEmbeds(embeds);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyEmbeds(@NotNull MessageEmbed embed, @NotNull MessageEmbed... embeds) {
        return SlashCommandInteraction.super.replyEmbeds(embed, embeds);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyFormat(@NotNull String format, @NotNull Object... args) {
        return SlashCommandInteraction.super.replyFormat(format, args);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyFile(@NotNull InputStream data, @NotNull String name, @NotNull AttachmentOption... options) {
        return SlashCommandInteraction.super.replyFile(data, name, options);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyFile(@NotNull File file, @NotNull AttachmentOption... options) {
        return SlashCommandInteraction.super.replyFile(file, options);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyFile(@NotNull File file, @NotNull String name, @NotNull AttachmentOption... options) {
        return SlashCommandInteraction.super.replyFile(file, name, options);
    }

    @NotNull
    @Override
    public ReplyCallbackAction replyFile(@NotNull byte[] data, @NotNull String name, @NotNull AttachmentOption... options) {
        return SlashCommandInteraction.super.replyFile(data, name, options);
    }

    @NotNull
    @Override
    public String getCommandPath() {
        return SlashCommandInteraction.super.getCommandPath();
    }

    @NotNull
    @Override
    public String getCommandString() {
        return SlashCommandInteraction.super.getCommandString();
    }

    @NotNull
    @Override
    public String getCommandId() {
        return SlashCommandInteraction.super.getCommandId();
    }

    @Override
    public boolean isGuildCommand() {
        return false;
    }

    @Override
    public boolean isGlobalCommand() {
        return SlashCommandInteraction.super.isGlobalCommand();
    }

    @NotNull
    @Override
    public List<OptionMapping> getOptionsByName(@NotNull String name) {
        return SlashCommandInteraction.super.getOptionsByName(name);
    }

    @NotNull
    @Override
    public List<OptionMapping> getOptionsByType(@NotNull OptionType type) {
        return SlashCommandInteraction.super.getOptionsByType(type);
    }

    @Nullable
    @Override
    public <T> T getOption(@NotNull String name, @NotNull Function<? super OptionMapping, ? extends T> resolver) {
        return SlashCommandInteraction.super.getOption(name, resolver);
    }

    @Override
    public <T> T getOption(@NotNull String name, @Nullable T fallback, @NotNull Function<? super OptionMapping, ? extends T> resolver) {
        return SlashCommandInteraction.super.getOption(name, fallback, resolver);
    }

    @Override
    public <T> T getOption(@NotNull String name, @Nullable Supplier<? extends T> fallback, @NotNull Function<? super OptionMapping, ? extends T> resolver) {
        return SlashCommandInteraction.super.getOption(name, fallback, resolver);
    }

    @NotNull
    @Override
    public InteractionType getType() {
        return SlashCommandInteraction.super.getType();
    }

    @NotNull
    @Override
    public MessageChannel getMessageChannel() {
        return SlashCommandInteraction.super.getMessageChannel();
    }

    @NotNull
    @Override
    public NewsChannel getNewsChannel() {
        return SlashCommandInteraction.super.getNewsChannel();
    }

    @NotNull
    @Override
    public VoiceChannel getVoiceChannel() {
        return SlashCommandInteraction.super.getVoiceChannel();
    }

    @NotNull
    @Override
    public PrivateChannel getPrivateChannel() {
        return SlashCommandInteraction.super.getPrivateChannel();
    }

    @NotNull
    @Override
    public ThreadChannel getThreadChannel() {
        return SlashCommandInteraction.super.getThreadChannel();
    }

    @NotNull
    @Override
    public Locale getGuildLocale() {
        return SlashCommandInteraction.super.getGuildLocale();
    }

    @NotNull
    @Override
    public String getId() {
        return SlashCommandInteraction.super.getId();
    }

    @NotNull
    @Override
    public OffsetDateTime getTimeCreated() {
        return SlashCommandInteraction.super.getTimeCreated();
    }

    @NotNull
    @Override
    public ModalCallbackAction replyModal(@NotNull Modal modal) {
        return null;
    }
}
