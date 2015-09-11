package Artifice.Mailbox;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public final class SenderMessage implements Serializable {

    private final ActorRef sender;
    private final String stimulusValues;
    private final long sendingTime;

    public SenderMessage(ActorRef sender, String stimulusValues, long sendingTime) {
        this.sender = sender;
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
    }

    public ActorRef getSender() {
        return sender;
    }

    public String getStimulusValues() {
        return stimulusValues;
    }

    public long getSendingTime() {
        return sendingTime;
    }

    @Override
    public String toString() {
        return "SenderMessage{sender=" + sender.toString() +
                ", stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                '}';
    }
}