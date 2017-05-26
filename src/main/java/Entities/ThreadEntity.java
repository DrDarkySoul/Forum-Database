package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class ThreadEntity {

    private Integer id;
    private Integer votes = 0;

    private String title = "";
    private String forum = "";
    private String message = "";
    private String created = "";

    private String slug;
    private String author;

    public ThreadEntity() {}

    @JsonCreator
    public ThreadEntity(
            @JsonProperty("id") Integer id,
            @JsonProperty("title") String title,
            @JsonProperty("author") String author,
            @JsonProperty("slug") String slug,
            @JsonProperty("message") String message,
            @JsonProperty("forum") String forum,
            @JsonProperty("votes") Integer votes,
            @JsonProperty("created") String created) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.slug = slug;
        this.created = created;

        if(votes == null)
            this.votes = 0;
        else
            this.votes = votes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public int getId() {
        return id;
    }

    public int getVotes() {
        return votes;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public String getMessage() {
        return message;
    }

    public boolean isEmpty() {
        return this.message == null || this.title == null;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("author", author);
        jsonObject.put("created", created);
        jsonObject.put("forum", forum);
        jsonObject.put("id", id);
        jsonObject.put("message", message);
        jsonObject.put("slug", slug);
        jsonObject.put("title", title);

        if (!votes.equals(0))
            jsonObject.put("votes", votes);

        return jsonObject;
    }

    public String getJSONString() {
        return this.getJSON().toString();
    }
}
