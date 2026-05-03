package backend.academy.linktracker.scrapper.adapter.out.db.orm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatEntity {

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    public ChatEntity(Long chatId) {
        this.chatId = chatId;
    }
}
