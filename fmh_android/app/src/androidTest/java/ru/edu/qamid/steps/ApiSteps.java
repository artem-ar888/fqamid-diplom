package ru.edu.qamid.steps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.kotlin.Allure;
import ru.edu.qamid.utils.data.DataGenerator;
import ru.edu.qamid.utils.data.TestData;
import ru.edu.qamid.utils.data.network.NewsItem;
import ru.edu.qamid.utils.helpers.ApiHelper;

public class ApiSteps {

    private final ApiHelper apiClient;

    // Каждый новый экземпляр ApiSteps будет иметь свой пустой список.
    private final List<Integer> createdNewsIds = new ArrayList<>();

    // Конструктор принимает готовый инстанс клиента из теста
    public ApiSteps(ApiHelper apiClient) {
        this.apiClient = apiClient;
        // Поле createdNewsIds создастся само с пустым массивом автоматически
    }

    /**
     * Удаляет все новости из БД сервера через API.
     */
    public void purgeAllNews() throws Exception {
        Allure.step("Подготавливаем данные: удаляем все новости из БД сервера через API");
        List<NewsItem> allExisting = apiClient.getAllNews();

        for (NewsItem news : allExisting) {
            try {
                apiClient.deleteNews(news.id);
            } catch (Exception ignored) {
                // Игнорируем ошибки удаления (например, если уже удалено)
            }
        }
    }

    /**
     * Универсальный шаг для начала всех тестов.
     * Удаляет старые новости (оставляя 20 страниц), и если что-то было удалено —
     * создаёт 8 новостей-заглушек для страницы 0.
     */
    public void clearAndFillPageZeroWithStubs() throws Exception {
        // Allure.step("Подготавливаем данные: очищаем новости до 20‑й страницы и при необходимости создаём 8 заглушек для страницы 0");
        // 1. Удаляем новости до 20-й страницы (если это возможно)
        boolean wasDeleted = apiClient.purgeLeavingLastPages(20);

        // 2. Создаём заглушки только если реально происходило удаление
        if (wasDeleted) {
            createBatchOfEightFutureStubs();
        }
    }


    /**
     * Универсальный шаг для начала теста.
     * Удаляет все новости за последние 10 дней и создаёт 8 новостей-заглушек для страницы 0.
     */
    public void clearLast10DaysAndFillPageZeroWithStubs() throws IOException {
        Allure.step("Подготавливаем данные: удаляем новости за последние 10 дней и создаём 8 заглушек для страницы 0");
        // 1. Считаем порог: 10 дней назад в Unix-секундах
        long tenDaysAgo = DataGenerator.generateRelativeUnixTimestampSeconds(-10 * TestData.DAY);

        // 2. Получаем ID всех новостей за последние 10 дней
        List<Integer> idsToDelete = apiClient.getNewsIdsNewerThan(tenDaysAgo);

        if (idsToDelete.isEmpty()) {
            System.out.println("[STEPS] No news to delete (older than 10 days).");
        } else {
            System.out.println("[STEPS] Deleting " + idsToDelete.size() + " news items...");
            for (int id : idsToDelete) {
                // Удаляем безопасно, т.к. ID в БД могут дублироваться
                apiClient.deleteNewsSafely(id);
            }
        }

        // 3. Создаём 8 заглушек для нулевой страницы
        createBatchOfEightFutureStubs();
    }

    /**
     * Создает 8 заглушек новостей со случайными данными и ОГРОМНОЙ датой публикации.
     * Эти новости должны гарантировано оказаться на странице 0.
     */
    public void createBatchOfEightFutureStubs() throws IOException {
        // Allure.step("Подготавливаем данные: создаём пакет из 8 новостей‑заглушек с будущей датой публикации");
        System.out.println("[STEPS] Creating batch of 8 future stub news...");
        // Генерируем "огромную" дату публикации
        long publishDate = (DataGenerator.generateCurrentUnixTimestamp() * 1000) + 888;
        boolean publishEnabled = true;

        for (int i = 0; i < 8; i++) {
            int newsCategoryId = DataGenerator.randomIntBetween(1, 8);
            String title = DataGenerator.generateWithTimestamp("Zero_page_", "_ar");
            String description = DataGenerator.randomAlphanumeric(16);

            NewsItem created = apiClient.createNews(
                    newsCategoryId,
                    title,
                    description,
                    publishDate,
                    publishEnabled
            );

            if (created == null || created.id <= 0) {
                throw new IllegalStateException("Failed to create stub #" + i);
            }
        }

        System.out.println("[STEPS] Batch of 8 FUTURE stubs seeded successfully.");
    }

    /**
     * Создает новость с начальными данными и подменяет title/description на новые.
     * Возвращает объект NewsItem с обновленными полями title/description,
     * но оригинальным ID и датой создания от сервера.
     * Можно использовать для создания уже опубликованной новости,
     * чтобы в будущем отредактировать её через API.
     */
    public NewsItem createNewsAndSwapContent(
            String initialTitle,
            String initialDescription,
            String newTitle,
            String newDescription
    ) throws IOException {
        Allure.step("Подготавливаем данные: создаём новость и подготавливаем контент для последующего редактирования: initialTitle=" +
                initialTitle + ", newTitle=" + newTitle);

        int categoryId = DataGenerator.randomIntBetween(1, 8);
        long publishDate = DataGenerator.generateRelativeUnixTimestampSeconds(-5 * TestData.MINUTE); // 5 минут назад
        boolean publishEnabled = true;

        System.out.println("[STEPS] Creating news stub for edit flow...");

        // Вызываем существующий API клиент
        NewsItem created = apiClient.createNews(
                categoryId,
                initialTitle,
                initialDescription,
                publishDate,
                publishEnabled
        );

        // Если сервер вернул null или пустой объект - прерываем выполнение теста исключением
        if (created == null || created.id <= 0) {
            throw new IllegalStateException("Failed to create news item via API. Check server logs.");
        }

        createdNewsIds.add(created.id);

        // === МАНИПУЛЯЦИЯ ОБЪЕКТОМ В ПАМЯТИ JAVA ===
        // Мы НЕ шлем PUT запрос! Мы просто меняем поля Java-объекта перед возвратом.
        created.title = newTitle;
        created.description = newDescription;

        // Поля id, creatorId, createDate остаются оригинальными от сервера.
        return created;
    }

    /**
     * Редактирует новость, используя данные из переданного объекта.
     * Берет ID из объекта, а остальные поля подставляет актуальные.
     * Передаваемый объект уже должен иметь желаемые поля для редактирования.
     */
    public NewsItem editExistingNewsFromObject(NewsItem source) throws IOException {
        Allure.step("Подготавливаем данные: редактируем новость через API: id=" +
                source.id + ", title=" + source.title);

        if (source == null || source.id <= 0) {
            throw new IllegalArgumentException("Source news object is invalid or missing ID.");
        }

        System.out.println("[STEPS] Editing news via existing object. ID: " + source.id);

        // Вызываем наш существующий API клиент для PUT запроса
        NewsItem updated = apiClient.editNews(
                source.id,
                source.title,
                source.description,
                source.creatorId,
                source.createDate,
                source.newsCategoryId,
                source.publishDate,
                source.publishEnabled
        );
        createdNewsIds.add(source.id); // PUT-метод может и создавать новость, кладём id на всякий случай
        return updated;
    }

    /**
     * Создает новость с указанным заголовком и датой публикации.
     * Остальные поля заполняются случайными данными для уникальности.
     *
     * @param title       Точный заголовок новости.
     * @param description Точное описние новости.
     * @param publishDate Unix Timestamp даты публикации.
     */
    public NewsItem createNewsWithTitleDescriptionAndDate(
            String title,
            String description,
            long publishDate
    ) throws IOException {
        Allure.step("Подготавливаем данные: создаём новость с заданными заголовком, описанием и датой публикации: title=" +
                title + ", date=" + publishDate);

        int newsCategoryId = DataGenerator.randomIntBetween(1, 8);
        boolean publishEnabled = true;

        System.out.println("[STEPS] Creating specific news. Title: " + title + ", Description: " + description + ", Date: " + publishDate);

        NewsItem created = apiClient.createNews(
                newsCategoryId,
                title,
                description,
                publishDate,
                publishEnabled
        );

        if (created != null && created.id > 0) {
            createdNewsIds.add(created.id); // Сохраняем ID для удаления после теста
        }

        return created;
    }

    /**
     * Удаляет новость, используя данные из переданного объекта (ID).
     */
    public void deleteExistingNewsFromObject(NewsItem source) throws IOException {
        Allure.step("Очищаем тестовые данные: удаляем новость через API по ID из объекта: id=" +
                source.id);

        if (source == null || source.id <= 0) {
            throw new IllegalArgumentException("Source news object is invalid or missing ID.");
        }

        System.out.println("[STEPS] Deleting news via existing object. ID: " + source.id);

        apiClient.deleteNewsSafely(source.id);
    }

    /**
     * Создает пачку новостей с последовательно уходящим в прошлое временем публикации.
     * Категории идут по циклу: 1, 2, ..., 8, 1, 2, ...
     * Используется для тестов пагинации и сортировки.
     *
     * @param count Количество создаваемых новостей.
     */
    public void seedDatabaseWithTimeSeriesNews(
            int count,
            String titlePrefix,
            String descriptionPrefix
    ) throws IOException {
        Allure.step("Подготавливаем данные: создаём новостей: " + count +
                ", с последовательными датами публикации: prefix=" + titlePrefix);

        if (count <= 0) return;
        System.out.println("[STEPS] Seeding DB with " + count + " timestamped news items...");

        final int CATEGORY_COUNT = 8;
        long baseTimestamp = DataGenerator.generateCurrentUnixTimestamp();

        for (int i = 0; i < count; i++) {
            // Циклическая категория: 1..8, потом опять 1..
            int categoryId = (i % CATEGORY_COUNT) + 1;
            String title = titlePrefix + (i + 1);
            String description = descriptionPrefix + (i + 1);

            // Логика смещения времени: первая -5 мин, остальные через сутки назад
            long publishDateOffset = (i == 0)
                    ? (5 * TestData.MINUTE)
                    : ((long) i * TestData.DAY);

            long publishDate = baseTimestamp - publishDateOffset;
//            long publishDate = DataGenerator.generateRelativeUnixTimestampSeconds(-i * TestData.MINUTE);
            boolean publishEnabled = true;

            NewsItem created = apiClient.createNews(
                    categoryId,
                    title,
                    description,
                    publishDate,
                    publishEnabled
            );

            if (created != null && created.id > 0) {
                createdNewsIds.add(created.id);
            } else {
                throw new IllegalStateException("Failed to create seeded news #" + i);
            }
        }

        System.out.println("[STEPS] Database seeded successfully.");
    }

    /**
     * Удаляет все новости, дата публикации которых СТАРШЕ или РАВНА указанной дате.
     * Используется для зачистки тестовых данных старше указанной Unix Timestamp.
     *
     * @param thresholdUnixTimestamp Пороговая дата в формате Unix Timestamp (секунды).
     *                               Все новости с publishDate <= этой даты будут удалены.
     */
    public void purgeNewsOlderThan(long thresholdUnixTimestamp) throws IOException {
        Allure.step("Очищаем тестовые данные: удаляем все новости, дата публикации которых не новее указанного timestamp: " +
                thresholdUnixTimestamp);

        System.out.println("[CLEANUP] Purging news older than timestamp: " + thresholdUnixTimestamp);

        List<NewsItem> allNews = apiClient.getAllNews();

        int deletedCount = 0;
        for (NewsItem news : allNews) {
            if (news.publishDate != 0 && news.publishDate <= thresholdUnixTimestamp) {
                try {
                    apiClient.deleteNews(news.id);
                    deletedCount++;
                } catch (IOException e) {
                    System.err.println("Failed to delete old news ID: " + news.id + ". Error: " + e.getMessage());
                }
            }
        }

        System.out.println("[CLEANUP] Cleanup finished. Deleted items: " + deletedCount);
    }

    /**
     * Очистка всех созданных за время теста новостей.
     */
    public void cleanupCreatedNews() {
        Allure.step("Очищаем тестовые данные: удаляем все новости, созданные в рамках этого теста через API");
        if (createdNewsIds.isEmpty()) return;

        System.out.println("[TEARDOWN] Deleting " + createdNewsIds.size() + " test news items...");

        // Проходимся по копии списка на случай ошибок сети
        List<Integer> idsToDelete = new ArrayList<>(createdNewsIds);

        for (Integer id : idsToDelete) {
            try {
                apiClient.deleteNewsSafely(id);
            } catch (IOException e) {
                System.err.println("Cleanup failed for ID " + id + ": " + e.getMessage());
            }
        }

        createdNewsIds.clear();
    }
}