# Auto Chat Suppression

RuneLite plugin that automatically removes game chat lines when the full message matches a configured list. Built for Old School RuneScape.

Matching is exact, after color tags are stripped. Case folding is on. Player chat is ignored unless that option is turned off.

The default list contains one game line:

```
Your puppy is very hungry.
```

## Plugin Hub

Public source: https://github.com/KyBau/auto-chat-suppression

Approval is a pull request to [runelite/plugin-hub](https://github.com/runelite/plugin-hub). After merge, the client downloads it from the plug icon. Search **Auto Chat Suppression**.

## Run from source

Requires JDK 21. https://adoptium.net/temurin/releases/?version=21

Windows: `gradlew.bat run`

macOS and Linux: `chmod +x gradlew && ./gradlew run`

Enable **Auto Chat Suppression** in that client. The normal RuneLite launcher does not load sideloaded jars.

Jagex accounts: add `--insecure-write-credentials` to the launcher client arguments, log in once, then run the command above. Delete `.runelite/credentials.properties` afterward. Do not share that file.
