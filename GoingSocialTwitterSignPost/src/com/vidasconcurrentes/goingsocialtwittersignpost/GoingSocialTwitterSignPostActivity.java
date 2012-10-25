/**
 * @author Meta
 * http://blog.vidasconcurrentes.com/android/integrando-twitter-en-aplicaciones-android-con-signpost-y-twitter4j/
 */
package com.vidasconcurrentes.goingsocialtwittersignpost;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GoingSocialTwitterSignPostActivity extends Activity {

	private static final String CONSUMER_KEY = "";	// Hay que poner el Consumer Key proporcionado
	private static final String CONSUMER_SECRET = "";	// Hay que poner el Consumer Secret proporcionado
	private static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	private static final String CALLBACK_URL = "app://goingsocial";
	
	private TextView codigoRespuesta;
	private Button botonAutorizar;
	private TextView campoTweet;
	private Button botonEnviarTweet;
	
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	private AccessToken accessToken;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		codigoRespuesta = (TextView) findViewById(R.id.codigoRespuesta);
		botonAutorizar = (Button) findViewById(R.id.botonAutorizar);
		campoTweet = (TextView) findViewById(R.id.tweet);
		botonEnviarTweet = (Button) findViewById(R.id.botonEnviarTweet);
		
		botonAutorizar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autoriza();
			}
		});
		
		botonEnviarTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enviaTweet();
			}
		});
	}
	
	private void autoriza() {
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			httpOauthprovider = new DefaultOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
			String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, CALLBACK_URL);
	
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			codigoRespuesta.setText(e.getMessage());
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
			try {
				httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
				accessToken = new AccessToken(httpOauthConsumer.getToken(), httpOauthConsumer.getTokenSecret());
				
				botonAutorizar.setEnabled(false);
			} catch (Exception e) {
				codigoRespuesta.setText(e.getMessage());
			}
		}
	}
	
	private void enviaTweet() {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			twitter.setOAuthAccessToken(accessToken);
	
			// send the tweet
			twitter.updateStatus(campoTweet.getText().toString());
	
			// feedback for the user...
			codigoRespuesta.setText("Tweet enviado!");
		} catch(Exception e) {
			codigoRespuesta.setText(e.getMessage());
		}
	}
}