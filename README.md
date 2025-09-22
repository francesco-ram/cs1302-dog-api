# CS1302 Dog Explorer (JavaFX + TheDogAPI + Open Library)

A Java 17 / JavaFX desktop app that **searches dog breeds** using TheDogAPI and shows a **grid of images** for the selected breed, while also performing an **Open Library** search to list relevant books. This satisfies the CS1302 API requirement by composing two independent, public JSON APIs into one cohesive UI.

---

## ✨ Features

- **Breed search (TheDogAPI)**  
  Type a breed (e.g., `poodle`, `husky`) and fetch matches via `/v1/breeds/search`.  
  Selecting a result shows a gallery of dog images via `/v1/images/search`.

- **Related books (Open Library)**  
  Uses `https://openlibrary.org/search.json` to look up books related to your search term (breed or free text), showing a simple list with title and author.

- **Responsive UI**  
  Built with JavaFX; network calls are handled off the JavaFX Application Thread so the UI stays responsive, with a status label to report progress/errors.

---

## 🧩 How it works

**Packages & classes**

- `cs1302.api.ApiDriver` — program entry point (launches JavaFX `Application`).  
- `cs1302.api.ApiApp` — the JavaFX `Application`; builds the UI, handles events, and composes the two APIs.  
- `cs1302.api.DogApi` — small HTTP client for TheDogAPI (`/v1/breeds/search`, `/v1/images/search`), JSON parsed with Gson.  
- `cs1302.api.OpenLibrarySearchApi` — simple HTTP client for Open Library search JSON.

**High-level flow**

1. User enters a **breed** or query and clicks **Search**.  
2. App calls **TheDogAPI** `breeds/search` → populates a list of matching breeds.  
3. On selection, app calls **TheDogAPI** `images/search` for that breed → displays images.  
4. In parallel (or on demand), app queries **Open Library** to list **books** related to the search term.

---

## 🔌 API configuration

TheDogAPI recommends using an API key (even for public endpoints) to avoid strict rate limits.

- Config file path: `resources/config.properties`  

