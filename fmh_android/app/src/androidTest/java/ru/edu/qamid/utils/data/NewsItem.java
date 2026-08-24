package ru.edu.qamid.utils.data;

public class NewsItem {
    private String titleText;
    private String descriptionText;
    private String publishDateText;
    private String newsCategory;
    private int newsCategoryId;
    private String newsRuCategory;

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getPublishDateText() {
        return publishDateText;
    }

    public void setPublishDateText(String publishDateText) {
        this.publishDateText = publishDateText;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String  newsCategory) {
        this.newsCategory = newsCategory;
        updateCategoryFields(newsCategory);
    }

    public int getNewsCategoryId() {
        return newsCategoryId;
    }

    public String getNewsRuCategory() {
        return newsRuCategory;
    }

    private void updateCategoryFields(String categoryName) {
        switch (categoryName) {
            case "Advertisement":
                this.newsCategoryId = 1;
                this.newsRuCategory = "Объявление";
                break;
            case "Birthday":
                this.newsCategoryId = 2;
                this.newsRuCategory = "День рождения";
                break;
            case "Salary":
                this.newsCategoryId = 3;
                this.newsRuCategory = "Зарплата";
                break;
            case "Union":
                this.newsCategoryId = 4;
                this.newsRuCategory = "Профсоюз";
                break;
            case "Holiday":
                this.newsCategoryId = 5;
                this.newsRuCategory = "Праздник";
                break;
            case "Massage":
                this.newsCategoryId = 6;
                this.newsRuCategory = "Массаж";
                break;
            case "Gratitude":
                this.newsCategoryId = 7;
                this.newsRuCategory = "Благодарность";
                break;
            case "Help":
                this.newsCategoryId = 8;
                this.newsRuCategory = "Нужна помощь";
                break;
            default:
                this.newsCategoryId = 0;
                this.newsRuCategory = "Неизвестно";
        }
    }
}
