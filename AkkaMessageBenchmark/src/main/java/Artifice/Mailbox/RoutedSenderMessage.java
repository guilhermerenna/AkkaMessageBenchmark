package Artifice.Mailbox;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public final class RoutedSenderMessage implements Serializable {

    private final SenderMessage sm;

    public RoutedSenderMessage(SenderMessage sm) {
        this.sm = sm;
    }

    public SenderMessage getSenderMessage() {
        return sm;
    }

    @Override
    public String toString() {
        return "SenderMessage{sender=" + sm.getSender().toString() +
                ", stimulusValues='" + sm.getStimulusValues() + '\'' +
                ", sendingTime=" + sm.getSendingTime() +
                '}';
    }
}