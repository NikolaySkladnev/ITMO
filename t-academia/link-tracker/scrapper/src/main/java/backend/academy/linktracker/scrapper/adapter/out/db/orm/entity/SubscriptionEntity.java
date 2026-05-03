package backend.academy.linktracker.scrapper.adapter.out.db.orm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_subscriptions_chat_link",
                        columnNames = {"chat_id", "link_id"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "link_id", nullable = false)
    private LinkEntity link;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscription_tags",
            joinColumns = @JoinColumn(name = "subscription_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @OrderBy("name asc")
    private Set<TagEntity> tags = new LinkedHashSet<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public SubscriptionEntity(ChatEntity chat, LinkEntity link, Set<TagEntity> tags) {
        this.chat = chat;
        this.link = link;
        this.tags = tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }
}
