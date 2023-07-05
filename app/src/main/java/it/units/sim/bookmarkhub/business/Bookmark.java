package it.units.sim.bookmarkhub.business;

public class Bookmark {
    private final String userId;
    private final String name;
    private final String url;
    private final String category;

    public Bookmark(String userId, String name, String url, String category) {
        this.userId = userId;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

}
