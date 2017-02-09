package com.deucks.partyplayr;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Class for Communicating with Partyplayr
 */
public class PartyPlayrService {

    //Declare Variables
    private static final String PARTY_TOKEN_STRING = "_token";
    private static final String PARTY_PLAYR_URL = "http://partyplayr.raajit.com";
    private static final String TOKEN_URL = PARTY_PLAYR_URL + "/token";
    private static final String PARTY_CREATE_URL = PARTY_PLAYR_URL + "/create";
    private static final String PARTY_UPDATE_HOST = PARTY_PLAYR_URL + "/updatehost";
    private static final String ADD_USER_URL = PARTY_PLAYR_URL + "/adduser";
    private static final String PARTY_DETAILS_URL = PARTY_PLAYR_URL + "/getpartydetails";
    private static final String PARTY_MUSIC_LIST_URL = PARTY_PLAYR_URL + "/list?url=";
    private static final String PARTY_MUSIC_PEOPLE_URL = PARTY_PLAYR_URL + "/people";
    private static final String PARTY_MUSIC_ADD_URL = PARTY_PLAYR_URL + "/addmusic";
    private static final String PARTY_MUSIC_END_URL = PARTY_PLAYR_URL + "/nextvideo";
    private static final String PARTY_DISLIKE_SONG_URL = PARTY_PLAYR_URL + "/dislike";

    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&order=relevance&maxResults=5&videoCategoryId=10&key=" + UniversalConstants.YOUTUBE_SEARCH_KEY + "&q=";
    private AsyncHttpClient mClient;


    private PreferenceManager mPrefManager;
    private ProgressDialog mRingProgressDialog;
    private Context mContext;

    CookieStore cookieStore;
    BasicHttpContext httpContext;


    /**
     * Contructor for partyplayr service
     * @param context
     */
    public PartyPlayrService(Context context) {
        mContext = context;
        mPrefManager = new PreferenceManager(mContext);
        mClient = new AsyncHttpClient();

        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * Method for creating a party
     * @param partyName
     * @return
     */
    public boolean createParty(String partyName) {
        //Check whether the token is bad/good, update the token
        Log.d("update token", partyName);
        if (updateToken()) {
            String partyUrl = createTheParty(partyName);
            Log.d("party url", partyUrl);
            //Create party, add user as the host of that party
            if(addToParty(partyUrl))
            {   Log.d("add to party", partyUrl);
                //return status of request
                return updatePartyHostId(partyUrl);
            }
        }
        return false;
    }

    /**
     * Method for updating who is the part host
     * @param partyUrl
     * @return
     */
    private boolean updatePartyHostId(String partyUrl)
    {
        // Creating HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpPost httpPost = new HttpPost(PARTY_UPDATE_HOST);
        httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

        // Building post parameters
        // key and value pair
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
        nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
        nameValuePair.add(new BasicNameValuePair("party_fb_id", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_ID)));
        nameValuePair.add(new BasicNameValuePair("party_url", partyUrl));

        //Url Encoding the POST parameters
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println(responseBody);
            if (response.getStatusLine().getStatusCode() == 200) {
                //If everything worked, return status
                return true;
            }

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return false;
    }

    /**
     * method for creating the party
     * @param name
     * @return
     */
    public String createTheParty(String name)
    {
        // Creating HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpPost httpPost = new HttpPost(PARTY_CREATE_URL);
        httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

        // Building post parameters
        // key and value pair
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
        nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
        nameValuePair.add(new BasicNameValuePair("name", name));
        nameValuePair.add(new BasicNameValuePair("app", "true"));

        //Url Encoding the POST parameters
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            //Log.d("Http Response status:", response.toString());
            String responseBody = EntityUtils.toString(response.getEntity());
            Log.d("Create party", responseBody);
            if (response.getStatusLine().getStatusCode() == 200) {
                //If everything went well, return the response
                return responseBody;
            }

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return UniversalConstants.EMPTY;
    }

    /**
     * Method for joining a party
     * @param partyInput
     * @return
     */
    public boolean joinParty(String partyInput) {
        //Check the token status, then add the user to a party
        if (updateToken()) {
            if (addToParty(partyInput)) {
                //return status
                return true;
            }
        }
        return false;
    }

    /**
     * Method for disliking a song
     * @return
     */
    public boolean dislikeCurrentSong(String musicId, String playrUrl) {
        //Check token status
        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(PARTY_DISLIKE_SONG_URL);
            httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

            // Building post parameters
            // key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
            nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
            nameValuePair.add(new BasicNameValuePair("party_music_id", musicId));
            nameValuePair.add(new BasicNameValuePair("player_url", playrUrl));


            //Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }

            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost, httpContext);
                System.out.println(response);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //If everything went well, return status
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println(responseBody);
                    return true;

                }

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Method for updating when a song ends
     * @param partyUrl
     * @param partyMusicId
     * @return
     */
    public boolean endSongToQue(String partyUrl, String partyMusicId) {
        //Check token status
        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(PARTY_MUSIC_END_URL);
            httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

            // Building post parameters
            // key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
            nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
            nameValuePair.add(new BasicNameValuePair("video_id", partyMusicId));
            nameValuePair.add(new BasicNameValuePair("player_url", partyUrl));


            //Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }

            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost, httpContext);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //If everything went well, return status
                    return true;

                }

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Method for adding a new song to partyplayr que
     * @param name
     * @param url
     * @param playerUrl
     * @param userId
     * @return
     */
    public boolean addSongToQue(String name, String url, String playerUrl, String userId)
    {
        //Check if token is valid
        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(PARTY_MUSIC_ADD_URL);
            httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

            // Building post parameters
            // key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
            nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
            nameValuePair.add(new BasicNameValuePair("musicTitle", name));
            nameValuePair.add(new BasicNameValuePair("musicUrl", url));
            nameValuePair.add(new BasicNameValuePair("player_url", playerUrl));
            nameValuePair.add(new BasicNameValuePair("player_id", userId));

            //Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }

            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost, httpContext);

                if (response.getStatusLine().getStatusCode() == 200) {
                    //If everything went well, return the status
                   return true;

                }
                // writing response to log

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();

            }
        }
        return false;
    }

    /**
     * Method for searching youtube, returns a model class with songs
     * @param searchName
     * @param youtubeSearchModel
     * @return
     */
    public List<YoutubeSearchModel> youtubeSearch(String searchName, List<YoutubeSearchModel> youtubeSearchModel)
    {
        final String ITEMS_SEARCH_NODE = "items";
        final String ITEMS_SEARCH_NODE_ID = "id";
        final String ITEMS_SEARCH_NODE_SNIPPET = "snippet";
        final String ITEMS_SEARCH_NODE_TITLE = "title";
        final String ITEMS_SEARCH_NODE_VIDEOID = "videoId";

        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpGet httpPost = new HttpGet(YOUTUBE_SEARCH_URL + searchName);

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            if (response.getStatusLine().getStatusCode() == 200) {
                //If everything went well, then parse the json
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject jObject = null;
                youtubeSearchModel = new ArrayList<>();
                try {
                    jObject = new JSONObject(responseBody);
                    JSONArray items = jObject.getJSONArray(ITEMS_SEARCH_NODE);
                    for (int i = 0; i < items.length(); i++) {

                        //Get individual item
                        JSONObject item = items.getJSONObject(i);
                        JSONObject videoIdObject = new JSONObject(item.getString(ITEMS_SEARCH_NODE_ID));
                        JSONObject videoSnippetObject = new JSONObject(item.getString(ITEMS_SEARCH_NODE_SNIPPET));

                        //Add to youtube model class
                        youtubeSearchModel.add(
                                new YoutubeSearchModel(
                                        videoSnippetObject.getString(ITEMS_SEARCH_NODE_TITLE),
                                        videoIdObject.getString(ITEMS_SEARCH_NODE_VIDEOID),
                                        UniversalConstants.EMPTY
                                )
                        );

                        Log.d("Http Response:", videoIdObject.getString(ITEMS_SEARCH_NODE_VIDEOID));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Return the youtube model class
                return  youtubeSearchModel;
            }

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return null;
    }

    /**
     * Gets List of people at the party
     * @param id
     * @param partyPeopleModel
     * @return
     */
    public List<PartyPeopleModel> partyPeople(String id, List<PartyPeopleModel> partyPeopleModel) {
        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(PARTY_MUSIC_PEOPLE_URL);
            httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

            // Building post parameters
            // key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
            nameValuePair.add(new BasicNameValuePair("party_id", id));


            //Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }

            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost, httpContext);

                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    Log.d("Http Response status:", responseBody.toString());
                    JSONArray jObject = new JSONArray("[" + responseBody + "]");
                    JSONArray jsonPartyData = jObject.getJSONArray(0);
                    partyPeopleModel = new ArrayList<>();
                    for (int i = 0; i < jsonPartyData.length(); i++)
                    {
                        Bitmap imageDownloadStore = null;
                        JSONObject item = jsonPartyData.getJSONObject(i);

                        //Download the music image
                        try {
                            InputStream in = new java.net.URL(item.getString("user_profile_url")).openStream();
                            imageDownloadStore = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }

                        //Add to list
                        partyPeopleModel.add(
                                new PartyPeopleModel(
                                        item.getString("user_id"),
                                        item.getString("user_firstName"),
                                        item.getString("user_lastName"),
                                        item.getString("user_name"),
                                        item.getString("user_profile_url"),
                                        item.getString("user_fb_id"),
                                        imageDownloadStore
                                )
                        );
                    }


                    return partyPeopleModel;

                }
                // writing response to log

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Method for getting the party music
     * @param id
     * @param partyMusicModel
     * @return
     */
    public List<PartyMusicModel> partyMusic(String id, List<PartyMusicModel> partyMusicModel)
    {
        final String TOKEN_NAME = "X-CSRF-TOKEN";
        final String ALBUM_ART_NAME = "music_albumart";
        final String ARTIST_NAME = "music_artist";
        //Check update token, update is needed
        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpGet httpGet = new HttpGet(PARTY_MUSIC_LIST_URL + id);
            httpGet.setHeader(TOKEN_NAME, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));


            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpGet, httpContext);
                Log.d("Http Response status:", PARTY_MUSIC_LIST_URL + id);

                if (response.getStatusLine().getStatusCode() == 200) {
                    //If Completed successfully then parse through response and return the PartyMusic Object
                    String responseBody = EntityUtils.toString(response.getEntity());
                    partyMusicModel = new ArrayList<>();
                    Log.d("Http Response status:", responseBody.toString());
                    JSONArray jObject = new JSONArray("[" + responseBody + "]");
                    JSONArray jsonPartyData = jObject.getJSONArray(0);
                    for (int i = 0; i < jsonPartyData.length(); i++)
                    {
                        Bitmap imageDownloadStore = null;
                        JSONObject item = jsonPartyData.getJSONObject(i);

                        //Try downloading the image
                        try {
                            InputStream in = new java.net.URL(item.getString(ALBUM_ART_NAME)).openStream();
                            imageDownloadStore = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }

                        Log.d("Http Response status:", item.getString("music_artist"));


                        partyMusicModel.add(
                            new PartyMusicModel(
                                item.getString("party_music_id"),
                                item.getString("party_id"),
                                item.getString("music_id"),
                                item.getString("music_played"),
                                item.getString("user_id"),
                                item.getString("music_name"),
                                item.getString("music_url"),
                                item.getString("music_artist"),
                                item.getString("music_albumart"),
                                imageDownloadStore
                            )
                        );
                    }


                    return partyMusicModel;

                }
                // writing response to log

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Method for getting the partyDetails model
     * @param partyModel
     * @return
     */
    public PartyDetailModel partyDetails(PartyDetailModel partyModel) {

        final String PARTY_ID_STRING = "party_id";
        final String PARTY_NAME_STRING = "party_name";
        final String PARTY_URL_STRING = "party_url";
        final String PARTY_USR_ID_STRING = "user_id";
        final String PARTY_FB_ID_STRING = "party_fb_id";

        if (updateToken()) {
            // Creating HTTP client
            HttpClient httpClient = new DefaultHttpClient();
            // Creating HTTP Post
            HttpPost httpPost = new HttpPost(PARTY_DETAILS_URL);
            httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

            // Building post parameters
            // key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
            nameValuePair.add(new BasicNameValuePair(PARTY_TOKEN_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
            nameValuePair.add(new BasicNameValuePair(PARTY_FB_ID_STRING, mPrefManager.getGeneralString(UniversalConstants.PREF_FB_ID)));


            //Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }

            // Making HTTP Request
            try {
                HttpResponse response = httpClient.execute(httpPost, httpContext);


                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JSONArray jObject = new JSONArray("[" + responseBody + "]");
                    Log.d("Http Response status:", responseBody.toString());
                    JSONArray jsonPartyData = jObject.getJSONArray(0);
                    JSONObject item = jsonPartyData.getJSONObject(0);

                    partyModel = new PartyDetailModel();
                    partyModel.setmPartyId(item.getString(PARTY_ID_STRING));
                    partyModel.setmPartyName(item.getString(PARTY_NAME_STRING));
                    partyModel.setmPartyUrl(item.getString(PARTY_URL_STRING));
                    partyModel.setmPartyHostId(item.getString(PARTY_USR_ID_STRING));

                    return partyModel;

                }
                // writing response to log

            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * Method for adding user to a party
     * @param partyInput
     * @return
     */
    public boolean addToParty(String partyInput) {

        final

        // Creating HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpPost httpPost = new HttpPost(ADD_USER_URL);
        httpPost.setHeader("X-CSRF-TOKEN", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN));

        // Building post parameters
        // key and value pair
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(7);
        nameValuePair.add(new BasicNameValuePair("_token", mPrefManager.getGeneralString(UniversalConstants.PREF_PP_TOKEN)));
        nameValuePair.add(new BasicNameValuePair("party_id", partyInput));
        nameValuePair.add(new BasicNameValuePair("party_fb_firstName", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_FIRST_NAME)));
        nameValuePair.add(new BasicNameValuePair("party_fb_lastName", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_LAST_NAME)));
        nameValuePair.add(new BasicNameValuePair("party_fb_name", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_USER_NAME)));
        nameValuePair.add(new BasicNameValuePair("party_fb_id", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_ID)));
        nameValuePair.add(new BasicNameValuePair("party_fb_profile_url", mPrefManager.getGeneralString(UniversalConstants.PREF_FB_PROFILE)));


        //Url Encoding the POST parameters
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            Log.d("Http Response status:", response.toString());
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                Log.d("add to party", responseBody);
                if (responseBody.equals("error"))
                    return false;
                return true;
            }
            // writing response to log

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return false;
    }

    /**
     * Method for updating the PartyPlayr Token
     * @return
     */
    public boolean updateToken() {

        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpGet httpPost = new HttpGet(TOKEN_URL);

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            if (response.getStatusLine().getStatusCode() == 200) {
                //Set the token, return status
                mPrefManager.setGeneralString(UniversalConstants.PREF_PP_TOKEN, EntityUtils.toString(response.getEntity()));
                return true;
            }
            // writing response to log

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();

        }
        return false;
    }


}
