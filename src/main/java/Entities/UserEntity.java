package Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class UserEntity {

    private String nickname = "";
    private String fullname = "";
    private String about = "";
    private String email = "";

    public UserEntity() {}

    @JsonCreator
    public UserEntity(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("fullname") String fullname,
            @JsonProperty("about") String about,
            @JsonProperty("email") String email) {
        this.nickname = nickname;
        this.fullname = fullname;
        this.about = about;
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public JSONObject getJSON() {
        final JSONObject jsonObject = new JSONObject();

        jsonObject.put("nickname", nickname);
        jsonObject.put("fullname", fullname);
        jsonObject.put("about", about);
        jsonObject.put("email", email);

        return jsonObject;
    }

    public String getJSONString() {
        return this.getJSON().toString();
    }
}
