package DAOs;

import Entities.PostEntity;
import Entities.ThreadEntity;
import Entities.VoteEntity;
import Helpers.EntryIdentifier;
import Helpers.DateFix;
import Mappers.PostMapper;
import Mappers.ThreadMapper;
import Mappers.VoteMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThreadDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ThreadDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void insertPost(PostEntity post, String time, KeyHolder keyHolder){
        jdbcTemplate.update(connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO post (parent,author,message,isEdited,forum,thread,path,created) " +
                            "VALUES (?,?,?,?,?,?,?,?::timestamptz)", new String[]{"id", "created"});
            preparedStatement.setInt(1, post.getParent());
            preparedStatement.setString(2, post.getAuthor());
            preparedStatement.setString(3, post.getMessage());
            preparedStatement.setBoolean(4, post.getEdited());
            preparedStatement.setString(5, post.getForum());
            preparedStatement.setInt(6, post.getThread());
            preparedStatement.setString(7, post.getPath());
            preparedStatement.setString(8, time);
            return preparedStatement;
        }, keyHolder);
    }

    public ResponseEntity<String> createPosts(ArrayList<PostEntity> postEntityArrayList, String slug_or_id) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        final ThreadEntity objThread;
        if (threadIdentifier.getFlag().equals("id"))
            try {
                objThread = this.getThreadEntityById(threadIdentifier.getId());
                if (objThread == null)
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
        else
            try {
                objThread = this.getThreadEntityBySlug(threadIdentifier.getSlug());
                if (objThread == null)
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
        String createdTimeWithTS = "";
        String createdTime = "";
        final JSONArray result = new JSONArray();
        for (PostEntity post : postEntityArrayList) {
            post.setForum(objThread.getForum());
            post.setThread(objThread.getId());
            if (post.getParent() != 0)
                try {
                    final List<PostEntity> posts = jdbcTemplate.query(
                            "SELECT * FROM post WHERE id=? AND thread=?",
                            new Object[]{post.getParent(), objThread.getId()}, new PostMapper());
                    if (posts.isEmpty())
                        return new ResponseEntity<>("", HttpStatus.CONFLICT);
                } catch (Exception e) {
                    return new ResponseEntity<>("", HttpStatus.CONFLICT);
                }
            final ResponseEntity responseEntity = new UserDAO(jdbcTemplate).get(post.getAuthor());
            if (responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            if (post.getParent() == 0) {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM post WHERE parent = 0 AND thread = ?",
                        new Object[]{post.getThread()}, Integer.class);
                if(count < 0)
                    count = 0;

                final String path = String.format("%1$06x", count);
                post.setPath(path);

                if (createdTimeWithTS.equals(""))
                    jdbcTemplate.update(connection -> {
                        final PreparedStatement preparedStatement = connection.prepareStatement(
                                "INSERT INTO post (parent,author,message,isEdited,forum,thread,path) " +
                                        "VALUES (?,?,?,?,?,?,?)", new String[]{"id", "created"});
                        preparedStatement.setInt    (1, post.getParent());
                        preparedStatement.setString (2, post.getAuthor());
                        preparedStatement.setString (3, post.getMessage());
                        preparedStatement.setBoolean(4, post.getEdited());
                        preparedStatement.setString (5, post.getForum());
                        preparedStatement.setInt    (6, post.getThread());
                        preparedStatement.setString (7, post.getPath());
                        return preparedStatement;
                    }, keyHolder);
                else
                    insertPost(post, createdTime, keyHolder);
                post.setId((int) keyHolder.getKeys().get("id"));
            } else {
                final String prevPath = jdbcTemplate.queryForObject(
                        "SELECT path FROM post WHERE id = ?;",
                        new Object[]{post.getParent()}, String.class);
                if (createdTimeWithTS.equals(""))
                    jdbcTemplate.update(connection -> {
                        final PreparedStatement preparedStatement = connection.prepareStatement(
                                "INSERT INTO post (parent,author,message,isEdited,forum,thread) " +
                                        "VALUES (?,?,?,?,?,?)", new String[]{"id", "created"});
                        preparedStatement.setInt(1, post.getParent());
                        preparedStatement.setString(2, post.getAuthor());
                        preparedStatement.setString(3, post.getMessage());
                        preparedStatement.setBoolean(4, post.getEdited());
                        preparedStatement.setString(5, post.getForum());
                        preparedStatement.setInt(6, post.getThread());
                        return preparedStatement;}, keyHolder);
                else
                    insertPost(post, createdTime, keyHolder);
                final Integer id = (Integer)keyHolder.getKeys().get("id");

                if(id <  0) post.setId(0);
                else post.setId(id);

                final String path = prevPath + '.' + String.format("%1$06x", post.getId());
                post.setPath(path);
                jdbcTemplate.update("UPDATE post SET path = ? WHERE id = ?", path, id);
            }
            if (createdTimeWithTS.equals("")) {
                createdTimeWithTS = DateFix.transformWithAppend0300(
                        keyHolder.getKeys().get("created").toString());
                createdTime = keyHolder.getKeys().get("created").toString();
            }
            post.setCreated(createdTimeWithTS);
            result.put(post.getJSON());
        }
        jdbcTemplate.update(
                "UPDATE forum SET posts = posts + " +
                        postEntityArrayList.size()
                        + " WHERE LOWER(slug) = LOWER(?)",
                objThread.getForum());
        return new ResponseEntity<>(result.toString(), HttpStatus.CREATED);
    }

    public ResponseEntity<String> vote(VoteEntity objVote, String slug_or_id) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        final ThreadEntity result;

        if (new UserDAO(jdbcTemplate).getUserEntity(objVote.getNickname()) == null)
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        if (this.getThreadEntity(slug_or_id) == null)
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        if (threadIdentifier.getFlag().equals("id")) {
            objVote.setThreadId(threadIdentifier.getId());
            final List<VoteEntity> objVoteList = jdbcTemplate.query(
                    "SELECT * FROM vote WHERE(id, LOWER(nickname))=(?,LOWER(?))",
                    new Object[]{objVote.getThreadId(), objVote.getNickname()}, new VoteMapper());
            if (objVoteList.isEmpty()) {
                if (objVote.getVoice() == 1)
                    jdbcTemplate.update("UPDATE thread SET votes=votes+1 WHERE id=?", objVote.getThreadId());
                else
                    jdbcTemplate.update("UPDATE thread SET votes=votes-1 WHERE id=?", objVote.getThreadId());

                result = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE id =?",
                        new Object[]{objVote.getThreadId()}, new ThreadMapper());
                jdbcTemplate.update("INSERT INTO vote (id,nickname,voice,slug) VALUES(?,?,?,?)",
                        objVote.getThreadId(), objVote.getNickname(), objVote.getVoice(), result.getSlug());
            } else {
                jdbcTemplate.update("UPDATE vote SET voice=? WHERE id=?",
                        objVote.getVoice(), objVote.getThreadId());
                if ((objVote.getVoice() == -1) && (objVoteList.get(0).getVoice() == 1))
                    jdbcTemplate.update("UPDATE thread SET votes=votes-2 WHERE id=?", objVote.getThreadId());
                if ((objVote.getVoice() == 1) && (objVoteList.get(0).getVoice() == -1))
                    jdbcTemplate.update("UPDATE thread SET votes=votes+2 WHERE id=?", objVote.getThreadId());

                result = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE id =?",
                        new Object[]{objVote.getThreadId()}, new ThreadMapper());
            }
            result.setCreated(DateFix.transformWithAppend00(result.getCreated()));
            return new ResponseEntity<>(result.getJSON().toString(), HttpStatus.OK);
        } else {
            objVote.setSlug(threadIdentifier.getSlug());
            final List<VoteEntity> objVoteList = jdbcTemplate.query(
                    "SELECT * FROM vote WHERE (LOWER(slug),LOWER(nickname))=(LOWER(?),LOWER(?))",
                    new Object[]{objVote.getSlug(), objVote.getNickname()}, new VoteMapper());
            if (objVoteList.isEmpty()) {
                jdbcTemplate.update("INSERT INTO vote (slug,nickname,voice) VALUES(?,?,?)",
                        objVote.getSlug(), objVote.getNickname(), objVote.getVoice());
                if (objVote.getVoice() == 1)
                    jdbcTemplate.update("UPDATE thread SET votes=votes+1 WHERE LOWER(slug)=LOWER(?)",
                            threadIdentifier.getSlug());
                else
                    jdbcTemplate.update("UPDATE thread SET votes=votes-1 WHERE LOWER(slug)=LOWER(?)",
                            threadIdentifier.getSlug());
                result = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE LOWER(slug) =LOWER(?)",
                        new Object[]{objVote.getSlug()}, new ThreadMapper());
                jdbcTemplate.update("UPDATE vote SET id=? WHERE LOWER(slug)=LOWER(?)",
                        result.getId(), result.getSlug());
            } else {
                jdbcTemplate.update("UPDATE vote SET voice=? WHERE LOWER(slug)=LOWER(?)",
                        objVote.getVoice(), objVote.getSlug());
                if ((objVote.getVoice() == -1) && (objVoteList.get(0).getVoice() == 1))
                    jdbcTemplate.update("UPDATE thread SET votes=votes-2 WHERE LOWER(slug)=LOWER(?)",
                            threadIdentifier.getSlug());
                if ((objVote.getVoice() == 1) && (objVoteList.get(0).getVoice() == -1))
                    jdbcTemplate.update("UPDATE thread SET votes=votes+2 WHERE LOWER(slug)=LOWER(?)",
                            threadIdentifier.getSlug());
                result = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE LOWER(slug) =LOWER(?)",
                        new Object[]{objVote.getSlug()}, new ThreadMapper());
            }
            result.setCreated(DateFix.transformWithAppend00(result.getCreated()));
            return new ResponseEntity<>(result.getJSON().toString(), HttpStatus.OK);
        }
    }

    public ResponseEntity<String> getThreadDetails(String slug_or_id) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        try {
            if (threadIdentifier.getFlag().equals("id")) {
                final ThreadEntity result = this.getThreadEntityById(threadIdentifier.getId());
                assert result != null;
                result.setCreated(DateFix.transformWithAppend00(result.getCreated()));
                return new ResponseEntity<>(result.getJSONString(), HttpStatus.OK);
            }
            else {
                final ThreadEntity result = this.getThreadEntityBySlug(threadIdentifier.getSlug());
                result.setCreated(DateFix.transformWithAppend00(result.getCreated()));
                return new ResponseEntity<>(result.getJSON().toString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> getThreadPosts(String slug_or_id, Integer limit,
                                                 String sort, Boolean desc, Integer marker) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        final ThreadEntity threadEntity;

        final StringBuilder query = new StringBuilder(
                "SELECT * FROM post WHERE thread = ");
        if (threadIdentifier.getFlag().equals("id"))
            threadEntity = this.getThreadEntityById(threadIdentifier.getId());
        else
            threadEntity = this.getThreadEntityBySlug(threadIdentifier.getSlug());

        if (threadEntity != null) {
            query.append(threadEntity.getId());
            List<PostEntity> posts = null;
            if (sort == null) sort = "flat";
            switch (sort) {
                case "flat": {
                    query.append(" ORDER BY created");
                    if (desc != null && desc) query.append(" DESC");query.append(" , id");
                    if (desc != null && desc) query.append(" DESC");query.append(" LIMIT ").append(limit.toString());
                    query.append(" OFFSET ").append(marker.toString());
                    break;
                }
                case "tree": {
                    query.append(" ORDER BY LEFT(path,6)");
                    if (desc != null && desc) {
                        query.append(" DESC");
                        query.append(", path DESC");
                    }
                    if (desc != null && !desc) {
                        query.append(", path ASC");
                    }
                    query.append(" LIMIT ").append(limit.toString());
                    query.append(" OFFSET ").append(marker.toString());
                    break;
                }
                case "parent_tree": {
                    if (limit != null) {
                        if (desc != null && !desc) {
                            final Integer maxID = new PostDAO(jdbcTemplate).getCountOfMainPosts(threadEntity.getId());
                            if ((maxID - limit - marker) < 0) {
                                if(marker < 0) marker = 0;
                                query.append(" AND path >= '").append(String.format("%1$06x", marker)).append("'");
                            } else
                                query.append(" AND path >= '").append(String.format("%1$06x", marker)).append("'")
                                        .append(" AND path < '").append(String.format("%1$06x", marker + limit)).append("'");
                        } else {
                            final Integer maxIds = new PostDAO(jdbcTemplate).getCountOfMainPosts(threadEntity.getId());
                            if ((maxIds - limit - marker) < 0) {
                                Integer high = maxIds - marker;
                                if(high < 0) high = 0;
                                query.append(" AND path >= ").append("'0'").append(" AND path < '").append(String.format("%1$06x", high)).append("'");
                            } else {
                                Integer top = maxIds - marker;
                                Integer bottom = maxIds - limit - marker;
                                if(top < 0) top = 0;
                                if(bottom < 0) bottom = 0;
                                query.append(" AND path >= '").append(String.format("%1$06x", bottom)).append("'")
                                        .append(" AND path < '").append(String.format("%1$06x", top)).append("'");
                            }
                        }
                    }
                    query.append(" ORDER BY LEFT(path,6)");
                    if (desc != null && desc) {
                        query.append(" DESC");
                        query.append(", path DESC");
                    }
                    if (desc != null && !desc) query.append(", path ASC");
                    break;
                }
            }
            try {
                posts = jdbcTemplate.query(query.toString(), new PostMapper());
            } catch (Exception ignored) {}

            final JSONObject result = new JSONObject();
            if (posts != null && posts.isEmpty()) result.put("marker", marker.toString());
            else {
                Integer sum;
                if(limit == null) sum = marker;
                else sum = limit + marker;
                result.put("marker", sum.toString());
            }

            final JSONArray resultArray = new JSONArray();
            if (posts != null)
                for (PostEntity postEntity : posts) {
                    postEntity.setCreated(DateFix.transformWithAppend0300(postEntity.getCreated()));
                    resultArray.put(postEntity.getJSON());
                }
            result.put("posts", resultArray);
            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    ThreadEntity getThreadEntityBySlug(String slug) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM thread WHERE LOWER(slug) = LOWER(?)", new Object[]{slug}, new ThreadMapper());
        } catch (Exception e) {
            return null;
        }
    }

    private ThreadEntity getThreadEntityById(Integer id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM thread WHERE id=?", new Object[]{id}, new ThreadMapper());
        } catch (Exception e) {
            return null;
        }
    }

    ThreadEntity getThreadEntity(String slug_or_id) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        final ThreadEntity threadEntity;
        try {
            if (threadIdentifier.getFlag().equals("id"))
                threadEntity = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE id=?",
                        new Object[]{threadIdentifier.getId()}, new ThreadMapper());
            else
                threadEntity = jdbcTemplate.queryForObject("SELECT * FROM thread WHERE LOWER(slug)=LOWER(?)",
                        new Object[]{threadIdentifier.getSlug()}, new ThreadMapper());
        } catch (Exception e) {
            return null;
        }
        return threadEntity;
    }

    public ResponseEntity<String> updateThread(ThreadEntity newData, String slug_or_id) {
        final EntryIdentifier threadIdentifier = new EntryIdentifier(slug_or_id);
        if (this.getThreadDetails(slug_or_id).getStatusCode().equals(HttpStatus.OK)) {
            if (newData.getMessage() != null && newData.getTitle() != null)
                try {
                    if (threadIdentifier.getFlag().equals("id"))
                        jdbcTemplate.update("UPDATE thread SET message=?, title=? WHERE id=?",
                                newData.getMessage(), newData.getTitle(), threadIdentifier.getId());
                    else
                        jdbcTemplate.update("UPDATE thread SET message=?, title=? WHERE LOWER(slug)=LOWER(?)",
                                newData.getMessage(), newData.getTitle(), threadIdentifier.getSlug());
                } catch (Exception e) {
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
                }
            else if (newData.getMessage() != null && newData.getTitle() == null)
                try {
                    if (threadIdentifier.getFlag().equals("id"))
                        jdbcTemplate.update("UPDATE thread SET message=? WHERE id=?",
                                newData.getMessage(), threadIdentifier.getId());
                    else
                        jdbcTemplate.update("UPDATE thread SET message=? WHERE LOWER(slug)=LOWER(?)",
                                newData.getMessage(), threadIdentifier.getSlug());
                } catch (Exception e) {
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
                }
            else if (newData.getMessage() == null && newData.getTitle() != null) {
                try {
                    if (threadIdentifier.getFlag().equals("id"))
                        jdbcTemplate.update("UPDATE thread SET  title=? WHERE id=?",
                                newData.getTitle(), threadIdentifier.getId());
                    else
                        jdbcTemplate.update("UPDATE thread SET title=? WHERE LOWER(slug)=LOWER(?)",
                                newData.getTitle(), threadIdentifier.getSlug());
                } catch (Exception e) {
                    return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
                }
            }
            final ThreadEntity objThread = this.getThreadEntity(slug_or_id);
            if (objThread != null) {
                newData = objThread;
                newData.setCreated(DateFix.transformWithAppend00(newData.getCreated()));
            }
            return new ResponseEntity<>(newData.getJSONString(), HttpStatus.OK);
        } else
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
    }
}
