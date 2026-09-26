package com.quietchat;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MessageSuppressPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MessageSuppressPlugin.class);
		RuneLite.main(args);
	}
}
