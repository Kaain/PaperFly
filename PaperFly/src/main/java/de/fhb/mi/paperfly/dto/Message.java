package de.fhb.mi.paperfly.dto;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a mapped message.
 *
 * @author Michael Koppen <michael.koppen@googlemail.com>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Message {

    private String username;
    private MessageType type;
    private Date sendTime;
    private String body;

    public Message(String username, MessageType type, Date sendTime, String body) {
        this.username = username;
        this.type = type;
        this.sendTime = sendTime;
        this.body = body;
    }
}
