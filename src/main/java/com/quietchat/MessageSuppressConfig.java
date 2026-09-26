package com.quietchat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MessageSuppressConfig.GROUP)
public interface MessageSuppressConfig extends Config
{
	String GROUP = "messagesuppress";

	@ConfigItem(
		keyName = "exactMessages",
		name = "Exact messages",
		description = "One full message per line. A line is removed only when the whole message matches.",
		position = 0
	)
	default String exactMessages()
	{
		return "Your puppy is very hungry.";
	}

	@ConfigItem(
		keyName = "ignoreCase",
		name = "Ignore case",
		description = "Match regardless of capitalization.",
		position = 1
	)
	default boolean ignoreCase()
	{
		return true;
	}

	@ConfigItem(
		keyName = "gameMessagesOnly",
		name = "Game messages only",
		description = "Leave player chat alone, even if someone types the same sentence.",
		position = 2
	)
	default boolean gameMessagesOnly()
	{
		return true;
	}
}
