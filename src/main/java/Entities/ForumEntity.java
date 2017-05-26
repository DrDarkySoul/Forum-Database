package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class ForumEntity {

    private Integer posts = 0;
    private Integer threads = 0;
    private String title = "";
    private String user, slug;

    public ForumEntity() {}

    @JsonCreator
    public ForumEntity(
            @JsonProperty("title") String title,
            @JsonProperty("user") String user,
            @JsonProperty("slug") String slug,
            @JsonProperty("threads") Integer threads,
            @JsonProperty("posts") Integer posts) {
        this.title = title;
        this.user = user;
        this.slug = slug;
        this.threads = threads;
        this.posts = posts;
    }

    public Integer getPosts() {
        return posts;
    }

    public Integer getThreads() {
        return threads;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("title",title);
        jsonObject.put("user", user);
        jsonObject.put("slug", slug);
        jsonObject.put("posts", posts);
        jsonObject.put("threads", threads);

        return jsonObject;
    }
}
