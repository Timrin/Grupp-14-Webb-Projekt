package connectSpotify;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import spotify.Envelope;
import spotify.Item;
import spotify.Track;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class SearchTrack {


    private static final String ACCESS_CODE = "Bearer ";
    private HttpResponse httpResponse = null;
    private HttpClient httpclient = null;
    private StatusLine status = null;
    private HttpEntity httpEntity = null;
    private InputStream dataStream = null;
    private Reader reader = null;
    private Gson gson = null;
    private List<Item> items = null;
    private Track tracks = null;
    private String weatherType;

    //Create Get request using accesscode, get data and use Reader object to later parse into Track object
    public void connectToSpotify() {
        String url = "https://api.spotify.com/v1/search";
        try {
            httpclient = HttpClients.custom().build();
            HttpUriRequest request = RequestBuilder.get()
                    .setUri(url)
                    //  .setHeader(HttpHeaders.AUTHORIZATION, SPOTIFY_JULIA)
                    .addHeader(HttpHeaders.AUTHORIZATION, ACCESS_CODE)
                    .addParameter("q", "cardigan")      //FIXME: value ska vara weatherType
                    .addParameter("type", "track")
                    .addParameter("limit", "2")
                    .build();
            httpResponse = httpclient.execute(request);
            status = httpResponse.getStatusLine();
            System.out.println(status);

            if (status.getStatusCode() == 200) {
                httpEntity = httpResponse.getEntity();
                dataStream = httpEntity.getContent();

                reader = new InputStreamReader(dataStream);
                gsonParser(reader);
            }

        } catch (IOException | JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
    }

    //Parse reader to Track object using Gson
    public void gsonParser(Reader reader) {

        gson = new Gson();
        Envelope envelope = gson.fromJson(reader, Envelope.class);
        tracks = envelope.getTracks();
        items = tracks.getItems();
        ArrayList<String> playlist = new ArrayList<>();

        for (Item item : items) {
            String trackUri = item.getUri();
            playlist.add(trackUri);
            System.out.println(trackUri);
        }
    }

}
