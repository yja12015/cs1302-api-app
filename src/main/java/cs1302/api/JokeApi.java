package cs1302.api;

import java.io.IOException;
import java.lang.System;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * JokeApi uses the api to generate jokes to be sent to the {@link ImgflipApi}.
 *
 */

public class JokeApi {
    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static final String ENDPOINT = "https://v2.jokeapi.dev/";

    private static final Map<String, String> DICTIONARY = new HashMap<String, String>() {{
            put("English", "en");
            put("Czech", "cs");
            put("German","de");
            put("Spanish","es");
            put("French","fr");
            put("Portuguese","pt");
        }};

    /**
     * Represents a JokeApi Result.
     */
    static class JokeApiResult {
        int amount;
        Joke[] jokes;
    }

    /**
     * Represents each joke in the jokes list.
     */
    static class Joke {
        String setup;
        String delivery;
    }

    /*
    public static void main(String[] args) {
        String[] category = new String[]{"Any"};
        getJoke(category, "en", "");
    }
    */

    /**
     * getJoke uses several optional paramaters to find two halves of a joke to be made into a meme.
     *
     * @param category list of the categories that can be added on or Any
     * @param language language chosen for the joke to be in
     * @param search a queried word that can be used to search for specific joke
     *
     * @return JokeApiResult is returned
     */
    public static JokeApiResult getJoke(String[] category, String language, String search) {
        String blacklistFlags = "&blacklistFlags=nsfw,religious,political,racist,sexist,explicit";


        if (category[0] == "Any") {
            category = new String[]{"Any"};
        }

        String Category = category[0];
        for (int i = 1;i < category.length;i++) {
            Category += "," + category[i];
        }

        String Search = search;

        if (search != "") {
            Search = "&contains=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
        }

        String Language = "lang=" + DICTIONARY.get(language);


        try {
            String url = String.format("%s/joke/%s?%s%s&type=twopart%s&amount=10", ENDPOINT,
                Category, Language, blacklistFlags, Search);

            System.out.println(url);
            String json = fetchString(url);

            JokeApiResult result = GSON.fromJson(json, JokeApiResult.class);
            //System.out.println(GSON.toJson(result));

            return result;
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Returns the response body string data from a URI.
     * @param uri location of desired content
     * @return response body string
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the HTTP client's {@code send} method is
     *    interrupted
     */
    private static String fetchString(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .build();
        HttpResponse<String> response = HTTP_CLIENT
            .send(request, BodyHandlers.ofString());
        final int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new IOException("response status code not 200:" + statusCode);
        } // if
        return response.body().trim();
    } // fetchString

}
