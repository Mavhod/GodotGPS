GodotGPS
========

This is the Google Play Service module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Leaderboard only

How to use
----------
Drop the "googleplayservice" directory inside the "modules" directory on the Godot source.

In android/AndroidManifestChunk.xml modify:
```
<meta-data android:name="com.google.android.gms.games.APP_ID"
  android:value="\ 012345678901" /> 
```
Replace your APP_ID value, it must begin with "\ ".

Yes there is uncomfortable because each apps have a unique value, I haven't found better solution yet.

```
  <meta-data android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />
```
If your other module had this meta-data (such as Admob module) so delete this.

Recompile

In your project:

file engine.cfg add
```
  [android]
    modules="org/godotengine/godot/GodotGPS"
``` 
If you use multiple modules add with comma (without space) such as
```
  [android]
    modules="org/godotengine/godot/GodotAdMob,org/godotengine/godot/GodotGPS"
```
Export->Target->Android

	Options:
		Custom Package:
			- place your apk from build
		Permissions on:
			- Access Network State
			- Internet

API Reference
-------------

The following methods are available:
```
  void init()
  void signIn()
  void signOut()
	
  int getStatus()
    return:
    0 = not connect
    1 = connecting
    2 = connected
  
  void lbSubmit(String id, int score)
    id = Leaderboard ID
    score = score value
  
  void lbShow(String id)
    id = Leaderboard ID
```
License
-------------
MIT license
