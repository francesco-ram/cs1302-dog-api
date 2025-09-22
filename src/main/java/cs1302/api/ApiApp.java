package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This app reads a dog breed, uses The Dog API for the breed and image, then uses the OpenLibrary
 * API for books, then displays the books title then author.
 */
public class ApiApp extends Application {

    private TextField breedInput;
    private Button searchButton;
    private ImageView dogImageView;
    private ListView<String> bookListView;
    private Label statusLabel;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        Label title = new Label("DogBook");
        title.setFont(Font.font(24));
        Label breedLabel = new Label("Breed:");
        breedInput = new TextField();
        breedInput.setPromptText("e.g. Labrador");
        HBox.setHgrow(breedInput, Priority.ALWAYS);
        searchButton = new Button("Search");

        HBox topBar = new HBox(10, title, breedLabel, breedInput, searchButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        dogImageView = new ImageView();
        dogImageView.setPreserveRatio(true);
        dogImageView.setFitWidth(300);
        StackPane imagePane = new StackPane(dogImageView);
        imagePane.setPadding(new Insets(10));
        imagePane.setMaxWidth(300);

        bookListView = new ListView<>();
        VBox listPane = new VBox(new Label("Books:"), bookListView);
        listPane.setPadding(new Insets(10));
        listPane.setSpacing(5);

        SplitPane centerPane = new SplitPane(imagePane, listPane);
        centerPane.setDividerPositions(0.3);

        statusLabel = new Label("Enter a breed and click Search");
        statusLabel.setPadding(new Insets(5));
        HBox statusBar = new HBox(statusLabel);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerPane);
        root.setBottom(statusBar);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("DogBook");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaxWidth(1280);
        stage.setMaxHeight(720);
        stage.show();

        searchButton.setOnAction(e -> doSearch());
    }

    /**
     * This method executes the two API chain. Which happens when you search a breed, it goes to
     * fetchImageUrl. Then, searches Open Library for books. Lastly, the method updates the UI on
     * the JavaFX thread.
     */
    private void doSearch() {
        String breed = breedInput.getText().trim();
        if (breed.isEmpty()) {
            statusLabel.setText("Please enter a breed.");
            return;
        }
        searchButton.setDisable(true);
        statusLabel.setText("Searching for \"" + breed + "\"…");
        bookListView.getItems().clear();
        dogImageView.setImage(null);

        CompletableFuture
            .supplyAsync(() -> DogApi.searchBreed(breed))
            .thenAccept(optBreeds -> {
                if (optBreeds.isEmpty() || optBreeds.get().length == 0) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Breed not found or rate-limited.");
                        searchButton.setDisable(false);
                    });
                } else {
                    var b = optBreeds.get()[0];
                    CompletableFuture
                        .supplyAsync(() -> DogApi.fetchImageUrl(b.id))
                        .thenAccept(optUrl -> Platform.runLater(() ->
                            optUrl.ifPresent(url -> dogImageView.setImage(new Image(url)))
                        ));
                    Platform.runLater(() ->
                        statusLabel.setText("Searching books for \"" + b.name + "\"…")
                    );
                    searchBooks(b.name);
                }
            });
    }

    /**
     * Queries Open Library for {@code query}, removes any duplicate results,
     * and updates the bookListView.
     *
     * @param query the breed name to search
     */
    private void searchBooks(String query) {
        CompletableFuture
            .supplyAsync(() -> OpenLibrarySearchApi.search(query))
            .thenAccept(optBooks -> Platform.runLater(() -> {
                Set<String> entries = new LinkedHashSet<>();
                optBooks.ifPresent(result -> {
                    for (var doc : result.docs) {
                        String authors = (doc.authorName != null && doc.authorName.length > 0)
                            ? String.join(", ", doc.authorName)
                            : "Unknown author";
                        entries.add(doc.title + " — by " + authors);
                    }
                });
                ObservableList<String> list = FXCollections.observableArrayList(entries);
                bookListView.setItems(list);
                statusLabel.setText(
                    list.isEmpty()
                      ? "No books found for \"" + query + "\""
                      : "Search complete for \"" + query + "\""
                );
                searchButton.setDisable(false);
            }));
    }

}

