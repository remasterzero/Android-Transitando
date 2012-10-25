package com.vagabonds.transitandosocialtwitter;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static final String CONSUMER_KEY = "ZEl1TwnrfhxaHwFn7o4WA";
	private static final String CONSUMER_SECRET = "1P6tPR82p6Q6gJPQkbBmfbmAXm630oJWCZdRVJHo4";
	private static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	private static final String CALLBACK_URL = "app://testtwitter";
	
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
        setContentView(R.layout.activity_main);
        
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
    
    @SuppressWarnings("unused")
	private void enviaTweet() {
    	try {
    		Twitter twitter = new TwitterFactory().getInstance();
    		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    		twitter.setOAuthAccessToken(accessToken);

    		twitter.updateStatus(campoTweet.getText().toString());

    		codigoRespuesta.setText("Tweet enviado!");
    	} catch(Exception e) {
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
