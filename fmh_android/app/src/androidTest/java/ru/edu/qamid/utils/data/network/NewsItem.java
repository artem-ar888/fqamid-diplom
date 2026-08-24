package ru.edu.qamid.utils.data.network;

public class NewsItem {
    public int id;
    public int newsCategoryId;
    public String title;
    public String description;
    public int creatorId;
    public long createDate;


    public long publishDate;
    public boolean publishEnabled;
    public String creatorName;
    public boolean isOpen;

    // --- Конструкторы ---

    // Пустой конструктор нужен для десериализации (парсинга) ответа от сервера
    public NewsItem() { }

    // Удобный конструктор для создания новых новостей (без системных ID)
    public NewsItem(int newsCategoryId, String title, String description, long pubDate, boolean enabled) {
        this.newsCategoryId = newsCategoryId;
        this.title = title;
        this.description = description;
        this.publishDate = pubDate;
        this.publishEnabled = enabled;
    }

    // --- toString для удобной отладки в консоли тестов ---
    @Override
    public String toString() {
        return "NewsItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + (description != null ? description.substring(0, Math.min(20, description.length())) : "") + "...\'" +
                '}';
    }
}