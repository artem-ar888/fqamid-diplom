package ru.edu.qamid.utils.data.network;

import java.util.List;

// Обертка для ответа со списком новостей
public class NewsListResponse {
    public List<NewsItem> elements;
    public int pages; // Для пагинации
}