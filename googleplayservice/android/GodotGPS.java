
package com.android.godot;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.os.Bundle;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.app.Activity;

public class GodotGPS extends Godot.SingletonBase
{
	private static final int	REQUEST_RESOLVE_ERROR	= 1001;
	private static final int	REQUEST_LEADERBOARD		= 1002;

	private Activity		activity			= null;
	private GoogleApiClient	client			= null;
	private boolean		isResolvingError	= false;
	private String			lbId				= null;
	private int			lbScore			= 0;
	
	public void init()
	{
		Log.d("godot", "GooglePlayService: init");
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				client	= new GoogleApiClient.Builder(activity)
					.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
					{
						@Override public void onConnected(Bundle bundle)
						{
							Log.d("godot", "GooglePlayService: onConnected");
						}
						
						@Override public void onConnectionSuspended(int i)
						{
							Log.d("godot", "GooglePlayService: onConnectionSuspended");
							client.connect();
						}
					})
					.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
					{
						@Override public void onConnectionFailed(ConnectionResult result)
						{
							if(isResolvingError)
							{
								Log.d("godot", "GooglePlayService: onConnectionFailed->" + result.toString());
								return;
							}
							else if (result.hasResolution())
							{
								try
								{
									isResolvingError	= true;
									result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
								}
								catch (SendIntentException e)
								{
									// There was an error with the resolution intent. Try again.
									Log.d("godot", "GooglePlayService: onConnectionFailed, try again");
									client.connect();
								}
							}
							else
							{
								// Show dialog using GooglePlayServicesUtil.getErrorDialog()
								//showErrorDialog(result.getErrorCode());
								Log.d("godot", "GooglePlayService: onConnectionFailed->" + result.toString());
								isResolvingError	= true;
								GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0).show();
							}
						}
					})
					.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
					.addApi(Games.API).addScope(Games.SCOPE_GAMES)
					.build();
				isResolvingError	= false;
				client.connect();
			}
		});
	}
	
	private void disconnect()
	{
		Plus.AccountApi.clearDefaultAccount(client);
		client.disconnect();
	}
	
	@Override protected void onMainActivityResult(int requestCode, int responseCode, Intent intent)
	{
		switch(requestCode)
		{
		case REQUEST_RESOLVE_ERROR:
			if (responseCode != Activity.RESULT_OK)
			{
				Log.d("godot", "GooglePlayService: onMainActivityResult, REQUEST_RESOLVE_ERROR = " + responseCode);
			}
			isResolvingError	= true;
			if (!client.isConnecting() && !client.isConnected())
			{
				client.connect();
			}
			break;
		case REQUEST_LEADERBOARD:
			Log.d("godot", "GooglePlayService: onMainActivityResult, REQUEST_LEADERBOARD = " + responseCode);
			if(responseCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED)
			{
				disconnect();
			}
			break;
		}
	}
	
	public void signin()
	{
		Log.d("godot", "GooglePlayService: signin");
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (!client.isConnecting())
				{
					isResolvingError	= false;
					client.connect();
				}
			}
		});
	}
	
	public void signout()
	{
		Log.d("godot", "GooglePlayService: signout");
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (client.isConnected())
				{
					disconnect();
				}
			}
		});
	}
	
	public int getStatus()
	{
		if(client.isConnecting())	return 1;
		if(client.isConnected())	return 2;
		return 0;
	}
	
	/*public void revoke()
	{
		Log.d("godot", "GooglePlayService: revoke");
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (client.isConnected())
				{
					Plus.AccountApi.clearDefaultAccount(client);
					Plus.AccountApi.revokeAccessAndDisconnect(client)
						.setResultCallback(new ResultCallback<Status>()
						{
							@Override public void onResult(Status arg0)
							{
								Log.d("godot", "GooglePlayService: revoked");
								//client.connect();
							}
						});
				}
			}
		});
	}*/
	
	/*public void printInfo()
	{
		Log.d("godot", "GooglePlayService: printInfo");
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				try
				{
					if (Plus.PeopleApi.getCurrentPerson(client) != null)
					{
						Person	person	= Plus.PeopleApi.getCurrentPerson(client);
						Log.d("godot", "GooglePlayService: name = " + person.getDisplayName());
						Log.d("godot", "GooglePlayService: photo = " + person.getImage().getUrl());
						Log.d("godot", "GooglePlayService: g+ profile = " + person.getUrl());
						//Log.d("godot", "GooglePlayService: email = " + Plus.AccountApi.getAccountName(client));
					}
					else
					{
						Log.d("godot", "GooglePlayService: info is null");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}*/
	
	public void lbSubmit(String id, int score)
	{
		Log.d("godot", "GooglePlayService: lbSubmit");
		lbId		= id;
		lbScore	= score;
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (client.isConnected())
				{
					Games.Leaderboards.submitScore(client, lbId, lbScore);
				}
			}
		});
	}
	
	public void lbShow(String id)
	{
		Log.d("godot", "GooglePlayService: lbShow");
		lbId		= id;
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (client.isConnected())
				{
					activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
						client, lbId), REQUEST_LEADERBOARD);
				}
			}
		});
	}

	static public Godot.SingletonBase initialize(Activity p_activity)
	{
		return new GodotGPS(p_activity);
	}
	
	public GodotGPS(Activity p_activity)
	{
		registerClass("GooglePlayService", new String[]
		{
			"init", "signin", "signout", "getStatus", /*"revoke", *//*"printInfo", */"lbSubmit", "lbShow"
		});
		activity	= p_activity;
	}
}

