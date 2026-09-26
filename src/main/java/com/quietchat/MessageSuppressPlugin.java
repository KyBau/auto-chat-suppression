package com.quietchat;

import com.google.inject.Provides;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

/**
 * Drops chat lines whose full text is on an exact-match list.
 *
 * <p>The game asks plugins whether a line may be shown via the
 * {@code chatFilterCheck} script callback, before the line is inserted.
 * Setting {@code intStack[size - 3]} to {@code 0} hides it. That is the
 * same contract the built-in Chat Filter plugin uses.
 */
@Slf4j
@PluginDescriptor(
	name = "Auto Chat Suppression",
	description = "Automatic exact-match suppression of game chat messages for Old School RuneScape.",
	tags = {"chat", "filter"}
)
public class MessageSuppressPlugin extends Plugin
{
	/**
	 * Types the built-in Chat Filter treats as game chat rather than a player speaking.
	 */
	private static final Set<ChatMessageType> GAME_TYPES = EnumSet.of(
		ChatMessageType.GAMEMESSAGE,
		ChatMessageType.ENGINE,
		ChatMessageType.FRIENDSCHATNOTIFICATION,
		ChatMessageType.ITEM_EXAMINE,
		ChatMessageType.NPC_EXAMINE,
		ChatMessageType.OBJECT_EXAMINE,
		ChatMessageType.SPAM,
		ChatMessageType.CLAN_MESSAGE,
		ChatMessageType.CLAN_GUEST_MESSAGE,
		ChatMessageType.CLAN_GIM_MESSAGE,
		ChatMessageType.NPC_SAY
	);

	@Inject
	private Client client;

	@Inject
	private MessageSuppressConfig config;

	private Set<String> exact = Set.of();
	private boolean ignoreCase;

	@Override
	protected void startUp()
	{
		rebuild();
	}

	@Override
	protected void shutDown()
	{
		exact = Set.of();
	}

	@Provides
	MessageSuppressConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MessageSuppressConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!MessageSuppressConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		rebuild();
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!"chatFilterCheck".equals(event.getEventName()))
		{
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();
		Object[] objectStack = client.getObjectStack();
		int objectStackSize = client.getObjectStackSize();

		if (intStackSize < 3 || objectStackSize < 1 || exact.isEmpty())
		{
			return;
		}

		ChatMessageType chatMessageType = ChatMessageType.of(intStack[intStackSize - 2]);
		if (chatMessageType == null)
		{
			return;
		}

		if (config.gameMessagesOnly() && !GAME_TYPES.contains(chatMessageType))
		{
			return;
		}

		Object raw = objectStack[objectStackSize - 1];
		if (!(raw instanceof String))
		{
			return;
		}

		if (!exact.contains(normalize((String) raw, ignoreCase)))
		{
			return;
		}

		// 0 tells the client not to add the line. Do not write 1 on a miss:
		// another plugin may already have blocked it.
		intStack[intStackSize - 3] = 0;
		log.debug("Suppressed exact message");
	}

	private void rebuild()
	{
		boolean folded = config.ignoreCase();
		Set<String> next = new HashSet<>();
		String raw = config.exactMessages();
		if (raw != null)
		{
			for (String line : raw.split("\\R"))
			{
				String normalized = normalize(line, folded);
				if (!normalized.isEmpty())
				{
					next.add(normalized);
				}
			}
		}

		ignoreCase = folded;
		exact = Collections.unmodifiableSet(next);
	}

	/**
	 * Strip Jagex color and image tags, then compare the whole remaining line.
	 * A substring is not a match.
	 */
	static String normalize(String message, boolean foldCase)
	{
		if (message == null)
		{
			return "";
		}

		String stripped = Text.removeTags(message).replace('\u00A0', ' ').trim();
		return foldCase ? stripped.toLowerCase(Locale.ENGLISH) : stripped;
	}
}
