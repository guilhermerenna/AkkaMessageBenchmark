package Artifice.Mailbox;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public final class ReceiverMessage implements Serializable {

    private final ActorRef sender;
    private final ActorRef receiver;
    private final String stimulusValues;
    private final long sendingTime;
    private final long receivingTime;
    private final long processingTime;

    public ReceiverMessage(ActorRef sender, ActorRef receiver, String stimulusValues, long sendingTime, long receivingTime, long processingTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
        this.receivingTime = receivingTime;
        this.processingTime = processingTime;
    }

    public ActorRef getSender() {
        return sender;
    }

    public ActorRef getReceiver() {
        return receiver;
    }

    public String getStimulusValues() {
        return stimulusValues;
    }

    public long getSendingTime() {
        return sendingTime;
    }

    public long getReceivingTime() {
        return receivingTime;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    @Override
    public String toString() {
        return "ReceiverMessage{" +
                "sender=" + sender.path() +
                ", receiver=" + receiver.path() +
                ", stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                ", receivingTime=" + receivingTime +
                ", processingTime=" + processingTime +
                '}';
    }
}