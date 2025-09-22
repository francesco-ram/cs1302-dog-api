package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

/**
 * This class is made for querying The Dog API. First off is to search breeds by name. Then,
 * Fetching a representative image URL by breed ID.
 */
public class DogApi {

    private static final String BREED_SEARCH =
        "https://api.thedogapi.com/v1/breeds/search";
    private static final String IMAGE_SEARCH =
        "https://api.thedogapi.com/v1/images/search";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
    private static final Gson GSON = new GsonBuilder().create();

    /** The Dog API key, loaded from resources/config.properties. */
    private static final String API_KEY;

    static {
        String key = "";
        try (FileInputStream fis =
                 new FileInputStream("resources/config.properties")) {
            Properties props = new Properties();
            props.load(fis);
            key = props.getProperty("thedogapi.apikey", "");
        } catch (IOException e) {
            System.err.println("Could not load API key: " + e);
        }
        API_KEY = key;
    }

    /** Models a breed entry from the /breeds/search endpoint. */
    public static class Breed {
        int id;
        String name;
        @SerializedName("breed_group")
        String breedGroup;
    }

    /** Models an image result from the /images/search endpoint. */
    private static class ImageResult {
        String url;
    }

    /**
     * Searches for breeds matching the query string.
     *
     * @param q the breed name
     * @return an Optional of Breed[] or empty on error
     */
    public static Optional<Breed[]> searchBreed(String q) {
        try {
            String url = String.format("%s?api_key=%s&q=%s",
                BREED_SEARCH,
                URLEncoder.encode(API_KEY, StandardCharsets.UTF_8),
                URLEncoder.encode(q, StandardCharsets.UTF_8));
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> res =
                HTTP_CLIENT.send(req, BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                GSON.fromJson(res.body(), Breed[].class)
            );
        } catch (IOException | InterruptedException ex) {
            return Optional.empty();
        }
    }

    /**
     * Fetches a representative image URL for the given breed ID.
     *
     * @param breedId the numeric ID of a breed
     * @return an Optional containing the image URL, or empty on error
     */
    public static Optional<String> fetchImageUrl(int breedId) {
        try {
            String url = String.format("%s?breed_id=%d&api_key=%s",
                IMAGE_SEARCH,
                breedId,
                URLEncoder.encode(API_KEY, StandardCharsets.UTF_8));
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> res =
                HTTP_CLIENT.send(req, BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Optional.empty();
            }
            ImageResult[] arr = GSON.fromJson(res.body(), ImageResult[].class);
            return (arr.length > 0 && arr[0].url != null)
                ? Optional.of(arr[0].url)
                : Optional.empty();
        } catch (IOException | InterruptedException ex) {
            return Optional.empty();
        }
    }

}

