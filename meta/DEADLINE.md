# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> My DogBook app lets users enter a dog breed, automatically fetches a representative image from The
> Dog API, and then looks up related books on Open Library. The breed search endpoint drives the
> image lookup (by breed ID), and the returned breed name drives the Open Library search.

> https://github.com/francescoram/cs1302-api-app

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### API 1 - The Dog API

```
https://api.thedogapi.com/v1/breeds/search?api_key
=live_jKYOtfxAmhp83zCL493zNvgcjIt3eMd9YEvt8tjDtMDw6bdLuUyqC7QqAyF0pgl9&q=bulldog
```

```
https://api.thedogapi.com/v1/images/search?breed_id=10&api_key
=live_jKYOtfxAmhp83zCL493zNvgcjIt3eMd9YEvt8tjDtMDw6bdLuUyqC7QqAyF0pgl9
```
> Requires the API key in the path - resources/config.properties.
> The rate is limited to 1000 requests per day, on HTTP 429 the app shows "Breed not found or rate
> limited".

### API 2 - OpenLibrarySearch API

```
https://openlibrary.org/search.json?q=bulldog
```

> Public, no auth required.

## Part 2: New

> I learned to chain two RESTful JSON APIs in JavaFX using `CompletableFuture` and
> `Platform.runLater` to keep the UI responsive while performing multiple asynchronous HTTP calls
> I also implemented made sure to get rid of dupes of JSON results and formatted them as
> by the title and author.

## Part 3: Retrospect

> If I were to restart, I would add simple tests for the API wrapper classes to try and catch errors
> earlier. Next, I would show a pop-up dialog for rate limit issues instead of only a status
> message. Lastly, I would limit book results to keep the list manageable and fast.
