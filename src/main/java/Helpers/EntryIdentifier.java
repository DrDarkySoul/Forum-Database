package Helpers;

public class EntryIdentifier {
    private Integer id;
    private String slug;
    private String flag;

    public EntryIdentifier(String rawIdentifier) {
        try {
            id = Integer.parseInt(rawIdentifier);
            flag = "id";
        } catch (Exception e) {
            slug = rawIdentifier;
            flag = "slug";
        }
    }

    public String getFlag(){
        return flag;
    }

    public String getSlug(){
        return slug;
    }

    public Integer getId() {
        return id;
    }
}
