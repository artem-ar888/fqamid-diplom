package ru.edu.qamid.utils.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.edu.qamid.utils.data.TestData;
import ru.edu.qamid.utils.data.network.AuthResponse;
import ru.edu.qamid.utils.data.network.NewsItem;
import ru.edu.qamid.utils.data.network.NewsListResponse;

public final class ApiHelper {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String BASE_URL = TestData.BASE_URL;
    public static final String LOGIN_URL = BASE_URL + "authentication/login";
    public static final String NEWS_URL = BASE_URL + "news";
    private static final int MAX_PAGES_TO_DELETE_IN_ONE_RUN = 50;

    private final OkHttpClient client;
    private final Gson gson;

    private String accessToken;
    private String refreshToken;

    public ApiHelper() {
        // Создаем логгер
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        // Уровень BODY покажет всё: URL, Заголовки (включая токен!) и Тело запроса/ответа
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson(); // Инициализация конвертера
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    /* =========================================== */
    /* 1. Авторизация                              */
    /* =========================================== */

    public void login(String username, String password) throws IOException {
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("login", username);
        credentials.put("password", password);

        RequestBody body = RequestBody.create(gson.toJson(credentials), JSON);

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);
            parseAuthTokens(response.body().string());
        }
    }

    private void parseAuthTokens(String json) {
        AuthResponse tokens = gson.fromJson(json, AuthResponse.class);
        this.accessToken = tokens.accessToken;
        this.refreshToken = tokens.refreshToken;
    }

    /* =========================================== */
    /* 2. Новости                                  */
    /* =========================================== */

    /**
     * Создает новую новость через POST-запрос.
     */
    public NewsItem createNews(
            int newsCategoryId,
            String title,
            String description,
            long publishDate,
            boolean publishEnabled
    ) throws IOException {
        ensureAuth();

        Map<String, Object> newData = new HashMap<>();

        // Заполняем только те поля, которые требуются бэкендом
        newData.put("newsCategoryId", newsCategoryId);
        newData.put("title", title);
        newData.put("description", description);
        newData.put("publishDate", publishDate);
        newData.put("publishEnabled", publishEnabled);

        RequestBody body = RequestBody.create(gson.toJson(newData), JSON);

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(NEWS_URL)).newBuilder().build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", accessToken)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);

            // Сервер возвращает созданный объект со всеми системными полями (id, creatorId и т.д.)
            return gson.fromJson(response.body().string(), NewsItem.class);
        }
    }

    /**
     * Получает данные страницы новостей вместе с метаданными пагинации.
     */
    public NewsListResponse getFullNewsPageResponse(int pageNumber) throws IOException {
        ensureAuth();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(NEWS_URL)).newBuilder()
                .addQueryParameter("pages", String.valueOf(pageNumber))
                .addQueryParameter("publishDate", "false")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", accessToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);

            // Парсим полную обертку
            return gson.fromJson(response.body().string(), NewsListResponse.class);
        }
    }

    /**
     * Упрощенная версия получения страницы.
     * Возвращает ТОЛЬКО список новостей, скрывая объект NewsListResponse.
     */
    public List<NewsItem> getNewsPage(int pageNumber) throws IOException {
        NewsListResponse fullResponse = getFullNewsPageResponse(pageNumber);

        return (fullResponse.elements != null) ? fullResponse.elements : new ArrayList<>();
    }

    /**
     * Получает ТОЛЬКО общее количество страниц со страницы 0.
     */
    public int getTotalPagesCount() throws IOException {
        ensureAuth();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(NEWS_URL)).newBuilder()
                .addQueryParameter("pages", "0")
                .addQueryParameter("publishDate", "false")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", accessToken)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);

            String rawJson = response.body().string();

            JsonObject jsonObject = gson.fromJson(rawJson, JsonObject.class);

            if (!jsonObject.has("pages") || jsonObject.get("pages").isJsonNull()) {
                throw new IOException("Field 'pages' not found in server response.");
            }

            return jsonObject.get("pages").getAsInt();
        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse pages count from JSON.", e);
        }
    }

    public List<NewsItem> getAllNews() throws IOException {
        ensureAuth();

        List<NewsItem> allCollectedNews = new ArrayList<>();

        // 1. Получаем данные нулевой страницы, чтобы узнать общее количество
        NewsListResponse firstPageMeta = getFullNewsPageResponse(0);

        if (firstPageMeta.elements != null) {
            allCollectedNews.addAll(firstPageMeta.elements); // Сразу забираем новости 0-й страницы
        }

        int totalPagesCount = firstPageMeta.pages;
        System.out.println("[API Helper] Total pages reported by server: " + totalPagesCount);

        // Защита от пустого ответа или некорректного поля 'pages'
        if (totalPagesCount <= 1) {
            return allCollectedNews;
        }

        // 2. Собираем ОСТАВШИЕСЯ страницы (от 1 до totalPagesCount(не включая))
        for (int currentPage = 1; currentPage < totalPagesCount; currentPage++) {
            try {
                System.out.println("[API Helper] Collecting data from page " + currentPage + "...");
                NewsListResponse pageData = getFullNewsPageResponse(currentPage);

                if (pageData.elements != null && !pageData.elements.isEmpty()) {
                    allCollectedNews.addAll(pageData.elements);
                }
            } catch (IOException e) {
                System.err.println("[API Helper] Failed to fetch page " + currentPage + ". Aggregation interrupted.");
                throw e;
            }
        }

        System.out.println("[API Helper] SUCCESS: All news collected. Total size: " + allCollectedNews.size());
        return allCollectedNews;
    }

    /**
     * Получает ID всех новостей, опубликованных после указанного момента.
     * Сбор останавливается:
     * 1) Как только найдена новость старше порога (пагинация по убыванию).
     * 2) ИЛИ когда достигнут лимит страниц, заявленный сервером.
     * 3) ИЛИ когда сервер вернул пустой список.
     *
     * @param thresholdUnixTimestamp Unix-timestamp (в секундах), старше которого новости не нужны.
     * @return Список ID новостей для удаления.
     */
    public List<Integer> getNewsIdsNewerThan(long thresholdUnixTimestamp) throws IOException {
        ensureAuth();

        List<Integer> newsIdsToDelete = new ArrayList<>();
        int currentPage = 0;

        // Лимит страниц узнаем из первой страницы
        int totalPagesLimit = 0;
        boolean limitInitialized = false;

        while (true) {
            // ЖЁСТКАЯ ПРОВЕРКА: не запрашиваем страницу, которой по данным сервера не существует
            if (limitInitialized && currentPage >= totalPagesLimit) {
                System.out.println("[API Helper] Reached server-reported total pages limit (" + totalPagesLimit + "). Stopping.");
                break;
            }

            System.out.println("[API Helper] Fetching page " + currentPage + "...");
            NewsListResponse pageData = getFullNewsPageResponse(currentPage);

            // Инициализируем лимит из ответа (он всегда есть как int)
            if (!limitInitialized) {
                totalPagesLimit = pageData.pages;
                limitInitialized = true;
                System.out.println("[API Helper] Server reports total pages: " + totalPagesLimit);
            }

            // Пустой список — дальше идти некуда
            if (pageData.elements == null || pageData.elements.isEmpty()) {
                System.out.println("[API Helper] Empty response on page " + currentPage + ". Stopping.");
                break;
            }

            boolean reachedThreshold = false;

            for (NewsItem item : pageData.elements) {
                if (item.publishDate > thresholdUnixTimestamp) {
                    newsIdsToDelete.add(item.id);
                } else {
                    // Нашли новость старше порога.
                    // Так как пагинация по убыванию, дальше все будут ещё старше.
                    reachedThreshold = true;
                    break;
                }
            }

            if (reachedThreshold) {
                System.out.println("[API Helper] Threshold reached on page " + currentPage + ". Stopping early.");
                break;
            }

            currentPage++;
        }

        System.out.println("[API Helper] Collected " + newsIdsToDelete.size()
                + " news IDs newer than timestamp " + thresholdUnixTimestamp);
        return newsIdsToDelete;
    }

    /**
     * Редактирует новость по ID методом PUT.
     * Если новости нет — она будет создана (Upsert).
     */
    public NewsItem editNews(
            int newsId,
            String title,
            String description,
            int creatorId,
            long createDate,
            int newsCategoryId,
            long publishDate,
            boolean publishEnabled
    ) throws IOException {
        ensureAuth();

        // Собираем полный объект в Map
        Map<String, Object> updateData = new HashMap<>();

        updateData.put("id", newsId);
        updateData.put("title", title);
        updateData.put("description", description);
        updateData.put("creatorId", creatorId);
        updateData.put("createDate", createDate);
        updateData.put("newsCategoryId", newsCategoryId);
        updateData.put("publishDate", publishDate);
        updateData.put("publishEnabled", publishEnabled);

        RequestBody body = RequestBody.create(gson.toJson(updateData), JSON);

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(NEWS_URL)).newBuilder().build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", accessToken)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);

            // Парсим ответ сервера (созданный или обновленный объект)
            return gson.fromJson(response.body().string(), NewsItem.class);
        }
    }

    /**
     * Удаление новости по ID.
     */
    public void deleteNews(int newsId) throws IOException {
        ensureAuth();

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(NEWS_URL + "/" + newsId)).newBuilder().build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", accessToken)
                .delete() // Используем HTTP DELETE
                .build();

        try (Response response = client.newCall(request).execute()) {
            handleErrors(response);
        }
    }

    /**
     * Пытается удалить новость через deleteNews.
     * Если сервер вернул 404 / ERR_NOT_FOUND (новость уже удалена) — считаем это нормой,
     * логируем и идём дальше. Любые другие ошибки пробрасываются.
     */
    public void deleteNewsSafely(int newsId) throws IOException {
        try {
            deleteNews(newsId);
        } catch (IOException e) {
            String msg = e.getMessage();
            if (msg != null && (
                    msg.contains("404") ||
                            msg.contains("ERR_NOT_FOUND") ||
                            msg.contains("не существует") ||
                            msg.contains("not found")
            )) {
                System.out.println("ID " + newsId + " already deleted or not found (404). Skipping.");
            } else {
                // Любая другая ошибка — реальная проблема, тест должен упасть
                throw e;
            }
        }
    }

    /**
     * Удаляет новости, оставляя только последние N страниц.
     * Возвращает true, если было удалено хотя бы одно сообщение, иначе false.
     */
    public boolean purgeLeavingLastPages(int pagesToKeep) throws Exception {
        ensureAuth();

        if (pagesToKeep <= 0) {
            System.out.println("[API Helper] pagesToKeep <= 0. Aborting purge.");
            return false;
        }

        int totalPages;
        try {
            totalPages = getTotalPagesCount();
        } catch (IOException e) {
            System.err.println("[API Helper] Failed to fetch total pages count. Aborting purge.");
            throw e;
        }

        System.out.println("[API Helper] Total pages: " + totalPages);

        if (totalPages <= pagesToKeep) {
            System.out.println("[API Helper] Nothing to delete. Total pages (" + totalPages +
                    ") <= pagesToKeep (" + pagesToKeep + ").");
            return false;
        }

        int pagesToDeleteCount = totalPages - pagesToKeep;

        if (pagesToDeleteCount > MAX_PAGES_TO_DELETE_IN_ONE_RUN) {
            String msg = "[API Helper] WARNING: Need to delete " + pagesToDeleteCount +
                    " pages, but MAX_PAGES_TO_DELETE_IN_ONE_RUN is " + MAX_PAGES_TO_DELETE_IN_ONE_RUN +
                    ". Deleting only " + MAX_PAGES_TO_DELETE_IN_ONE_RUN + " pages to avoid long test runs.";
            System.out.println(msg);
            pagesToDeleteCount = MAX_PAGES_TO_DELETE_IN_ONE_RUN;
        } else {
            System.out.println("[API Helper] Will delete first " + pagesToDeleteCount + " pages.");
        }

        long startTime = System.currentTimeMillis();
        int deletedCount = 0;

        for (int page = 0; page < pagesToDeleteCount; page++) {
            NewsListResponse pageData;
            try {
                pageData = getFullNewsPageResponse(page);
            } catch (IOException e) {
                System.err.println("[API Helper] Failed to fetch page " + page + ". Stopping purge.");
                throw e;
            }

            if (pageData.elements == null || pageData.elements.isEmpty()) {
                continue;
            }

            for (NewsItem news : pageData.elements) {
                try {
                    deleteNews(news.id);
                    deletedCount++;

                    // Прогресс: точка каждые 8 удалений (ровно одна страница)
                    if (deletedCount % 8 == 0) {
                        System.out.print(".");
                    }
                } catch (Exception ignored) {
                    // Ошибки удаления просто пропускаем
                }
            }
        }

        long durationMs = System.currentTimeMillis() - startTime;
        System.out.println("\n[API Helper] PURGE FINISHED. Deleted " + deletedCount +
                " items in " + durationMs + " ms.");

        return deletedCount > 0;
    }

    /* ===== Вспомогательные методы ===== */

    private void ensureAuth() throws IOException {
        // Если токена нет или он пустой - пытаемся войти сами
        if (accessToken == null || accessToken.isEmpty()) {
            System.out.println("[API Helper] Token is missing. Attempting auto-login...");

            // Вызываем логин с жестко прописанными данными из TestData
            login(TestData.LOGIN, TestData.PASSWORD);

            System.out.println("[API Helper] Auto-login successful.");
        }
    }

    private void handleErrors(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String errorMsg = "API Error [" + response.code() + "] ";
            ResponseBody body = response.body();
            if (body != null) {
                errorMsg += body.string();
            }
            throw new IOException(errorMsg);
        }
    }
}