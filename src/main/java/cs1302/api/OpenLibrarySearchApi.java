package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * This class for querying the Open Library Search API.
 */
public class OpenLibrarySearchApi {

    /** Models a single book document in the API response. */
    public static class OpenLibraryDoc {
        String title;
        @SerializedName("author_name")
        String[] authorName;
    }

    /** Models the root search result object. */
    public static class OpenLibraryResult {
        int numFound;
        OpenLibraryDoc[] docs;
    }

    private static final String ENDPOINT = "https://openlibrary.org/search.json";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Searches Open Library for the given query.
     *
     * @param q the search query
     * @return an Optional containing the result, or empty on error
     */
    public static Optional<OpenLibraryResult> search(String q) {
        try {
            String url = ENDPOINT + "?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
            HttpRequest req = HttpRequest.newBuilder()
                                     .uri(URI.create(url))
                                     .build();
            HttpResponse<String> res = HTTP_CLIENT.send(req, BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                GSON.fromJson(res.body(), OpenLibraryResult.class)
            );
        } catch (IOException | InterruptedException ex) {
            return Optional.empty();
        }
    }

}

