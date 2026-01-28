package se.jensen.alexandra.springboot2.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * En klass som representerar tabellen "comments" i databasen.
 * Innehåller all information om en kommentar.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Användaren som skrev kommentaren
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Inlägget som kommentaren hör till
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    // Kommentar som denna kommentar svarar på (null om huvudkommentar)
    @ManyToOne
    @JoinColumn(name = "parentComment_id")
    private Comment parentComment;

    // Lista med svar på denna kommentar
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
    private List<Comment> replies = new ArrayList<>();

    // Själva textinnehållet i kommentaren
    @Column(nullable = false, length = 1000)
    private String content;

    // Tidpunkt när den skapades
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Tidpunkt när den senast uppdaterades
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Användare som har gillat kommentaren
    @ManyToMany
    @JoinTable(
            name = "comment_likes",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedBy = new HashSet<>();

    public Comment() {
    }

    public Comment(Long id, User user, Post post, Comment parentComment,
                   List<Comment> replies, String content, LocalDateTime createdAt,
                   LocalDateTime updatedAt, Set<User> likedBy) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.parentComment = parentComment;
        this.replies = replies;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedBy = likedBy;
    }

    //Getter/Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<User> likedBy) {
        this.likedBy = likedBy;
    }
}
