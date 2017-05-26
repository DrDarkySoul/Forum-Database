package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class PostEntity {

    private Integer id;
    private Integer parent;
    private Integer thread;

    private String forum;
    private String author;
    private String created;
    private String path;
    private String message;

    private boolean isEdited;

    public PostEntity(){}

    @JsonCreator
    public PostEntity(
            @JsonProperty("id") Integer id,
            @JsonProperty("parent") Integer parent,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("thread") Integer thread,
            @JsonProperty("isEdited") boolean isEdited,
            @JsonProperty("forum") String forum,
            @JsonProperty("path") String path,
            @JsonProperty("created") String created) {
        this.id = id;
        this.author =author;
        this.message = message;
        this.thread = thread;
        this.isEdited= isEdited;
        this.path= path;
        this.forum=forum;
        this.created=created;

        if(parent == null)
            this.parent = 0;
        else
            this.parent = parent;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getForum() {
        return forum;
    }

    public Integer getId() {
        return id;
    }

    public Integer getParent() {
        return parent;
    }

    public Integer getThread() {
        return thread;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public Boolean getEdited(){
        return isEdited;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("author",author);
        jsonObject.put("message", message);
        jsonObject.put("parent", parent);
        jsonObject.put("isEdited", isEdited);
        jsonObject.put("thread", thread);
        jsonObject.put("forum", forum);
        jsonObject.put("created", created);
        return jsonObject;
    }

}
