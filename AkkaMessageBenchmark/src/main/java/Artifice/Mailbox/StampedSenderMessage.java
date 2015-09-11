package Artifice.Mailbox;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public final class StampedSenderMessage implements Serializable {

    private final ActorRef sender;
    private final String stimulusValues;
    private final long sendingTime;
    private final long receivingTime;

    public StampedSenderMessage(ActorRef sender, String stimulusValues, long sendingTime, long receivingTime) {
        this.sender = sender;
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
        this.receivingTime = receivingTime;
    }

    public ActorRef getSender() {
        return this.sender;
    }

    public String getStimulusValues() {
        return this.stimulusValues;
    }

    public long getSendingTime() {
        return this.sendingTime;
    }

    public long getReceivingTime() {
        return this.receivingTime;
    }

    @Override
    public String toString() {
        return "SenderMessage{sender="+sender.toString()+
                ", stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                '}';
    }
}