package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class VoteEntity {

    private Integer threadId;
    private Integer voice;
    private String slug;
    private String nickname;

    public VoteEntity() {}

    @JsonCreator
    public VoteEntity(
            @JsonProperty("slug") String slug,
            @JsonProperty("threadId") Integer threadId,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") Integer voice) {
        this.slug = slug;
        this.threadId = threadId;
        this.nickname = nickname;
        this.voice = voice;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public Integer getVoice() {
        return voice;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSlug() {
        return slug;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("threadId", threadId);
        jsonObject.put("nickname", nickname);
        jsonObject.put("slug", slug);
        jsonObject.put("voice", voice);

        return jsonObject;
    }
}
