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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


/**
 * ImgflipApi makes use of a large series of common meme templates and takes them and combines
 * them with JokeApi to create new memes as an image.
 */

public class ImgflipApi {
    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static final String ENDPOINT = "https://api.imgflip.com";
    private static final String USER = "g_user_109888274588286589205";
    private static final String PASSWORD = "Secretpassword";

    /**
     * Represents an Imgflip API result.
     */
    private static class ImgflipResult {
        Data data;
    }

    /**
     * Represents the data with the image link contained in the Imgflilp Result.
     */
    private static class Data {
        String url;
        ArrayList<Template> memes;
    }

    /**
     * Represnts the templates inside of the memes array.
     */
    private static class Template {
        String id;
        String name;
        @SerializedName("box_count")
        int boxCount;
    }

    /*
    public static void main(String[] arg) {
        String joke1 = "To prove he was right, the flat-earther walked to the end of the Earth";
        String joke2 = "He eventually came around";
        String template_id = "222403160";

        createImg(joke1, joke2, template_id);
        ArrayList<Template> templates = getTemplates();
        for (Template template: templates) {
            System.out.println(GSON.toJson(template));
        }

    }
    */

    /**
     * Calls the ImgflipApi to get the list of top 100 meme templates.
     *
     * @return Returns an arraylist of meme templates containing their name and id.
     */

    public static ArrayList<Template> getTemplates() {
        ArrayList<Template> templates = new ArrayList<Template>(10);
        try {
            String url = String.format("%s/get_memes", ENDPOINT);
            String json = fetchString(url);

            ImgflipResult result = GSON.fromJson(json, ImgflipResult.class);
            for (Template template: result.data.memes) {
                if (template.boxCount == 2) {
                    templates.add(template);
                }
            }
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return templates;
    }

    /**
     * Takes in the user input and previous JokeAPI information along with the user-chosen template
     * to generate a meme as a jpg.
     *
     * @param joke1 the first half/setup of the joke
     * @param joke2 the punchline of the joke
     * @param template_id the meme template
     */
    public static void createImg(String joke1, String joke2, String template_id) {

        joke1 = URLEncoder.encode(joke1, StandardCharsets.UTF_8);
        joke2 = URLEncoder.encode(joke2, StandardCharsets.UTF_8);

        try {
            String url = String.format("%s/caption_image?template_id=%s&username=%s&password=%s" +
                "&text0=%s&text1=%s", ENDPOINT, template_id, USER, PASSWORD, joke1, joke2
            );
            System.out.println(url);
            String json = fetchString(url);

            ImgflipResult result = GSON.fromJson(json, ImgflipResult.class);
            System.out.println(GSON.toJson(result));

            System.out.println(result.data.url);
        } catch (IllegalArgumentException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
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
