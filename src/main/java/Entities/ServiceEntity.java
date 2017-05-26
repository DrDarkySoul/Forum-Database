package Entities;

import org.json.JSONObject;

public class ServiceEntity {

    private Integer forum = 0;
    private Integer thread = 0;
    private Integer post = 0;
    private Integer user = 0;

    public ServiceEntity() {}

    public ServiceEntity(Integer forum,
                         Integer thread,
                         Integer post,
                         Integer user) {
        this.forum = forum;
        this.thread = thread;
        this.post = post;
        this.user = user;
    }

    public Integer getForum() {
        return forum;
    }

    public Integer getThread() {
        return thread;
    }

    public Integer getPost() {
        return post;
    }

    public Integer getUser() {
        return user;
    }

    public void setForum(Integer forum) {
        this.forum = forum;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("forum", forum);
        jsonObject.put("thread", thread);
        jsonObject.put("post", post);
        jsonObject.put("user", user);

        return jsonObject;
    }

    public String getJSONString() {
        return this.getJSON().toString();
    }
}
