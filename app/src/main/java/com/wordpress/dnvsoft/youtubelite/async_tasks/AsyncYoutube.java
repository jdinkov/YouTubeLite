package com.wordpress.dnvsoft.youtubelite.async_tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

abstract class AsyncYoutube extends AsyncTask<Void, String, YouTubeResult> {

    String accountEmail;
    protected YouTubeResult result;
    protected YouTube youtube;
    private TaskCompleted callback;
    private static WeakReference<Context> context;

    private static final int REQUEST_AUTHORIZATION = 1234;

    AsyncYoutube(Context c, TaskCompleted callback) {
        if (c == null) {
            cancel(true);
            return;
        }

        context = new WeakReference<>(c);
        this.callback = callback;
        result = new YouTubeResult();

        HttpRequestInitializer initializer;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getAppContext(),
                Lists.newArrayList(
                        YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_FORCE_SSL))
                .setBackOff(new ExponentialBackOff());

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getAppContext());
        if (account != null) {
            accountEmail = account.getEmail();
            credential.setSelectedAccountName(accountEmail);
            initializer = credential;
        } else {
            initializer = new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest hr) {
                    hr.getHeaders().set("X-Android-Package", getAppContext().getPackageName());
                    hr.getHeaders().set("X-Android-Cert", "AF53644C5D325AB0084CD3262D63B8C2DB3C0934");
                }
            };
        }

        youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), initializer)
                .setApplicationName(getAppContext().getString(R.string.app_name))
                .build();
    }

    static Context getAppContext() {
        return context.get();
    }

    @Override
    protected YouTubeResult doInBackground(Void... params) {
        try {
            result = DoItInBackground();
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult((Activity) getAppContext(), e.getIntent(), REQUEST_AUTHORIZATION, null);
        } catch (GoogleJsonResponseException exception) {
            if (exception.getDetails().getErrors().get(0).getMessage().equals("The requester is not allowed to access the requested subscriptions.")) {
                publishProgress("");
            } else if (exception.getDetails().getErrors().get(0).getMessage().equals("The request uses the <code>mine</code> parameter but is not properly authorized.")) {
                publishProgress("Unauthorized");
            } else if (exception.getDetails().getErrors().get(0).getMessage().equals("The video identified by the <code><a href=\"/youtube/v3/docs/commentThreads/list#videoId\">videoId</a></code> parameter has disabled comments.")) {
                publishProgress("Comments are disabled.");
            } else if (exception.getDetails().getErrors().get(0).getMessage().equals("The <code>comment</code> resource that is being inserted must specify a value for the <code>snippet.textOriginal</code> property. Comments cannot be empty.")) {
                publishProgress("Comments cannot be empty.");
            } else {
                String message = exception.getStatusMessage() + "\n" + exception.getDetails().getMessage();
                publishProgress(message);
            }
        } catch (UnknownHostException exception) {
            String message = "No network connection available.";
            publishProgress(message);
        } catch (IOException e) {
            String message = e.getClass().getSimpleName();
            publishProgress(message);
        }
        return result;
    }

    abstract YouTubeResult DoItInBackground() throws IOException;

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (!values[0].equals("")) {
            Toast.makeText(getAppContext(), values[0], Toast.LENGTH_SHORT).show();
        }
        result.setCanceled(true);
    }

    @Override
    protected void onPostExecute(YouTubeResult result) {
        callback.onTaskComplete(result);
    }
}
