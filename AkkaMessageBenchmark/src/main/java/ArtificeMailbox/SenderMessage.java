package ArtificeMailbox;

import akka.actor.ActorRef;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public class SenderMessage {

    private ActorRef sender;
    private ActorRef receiver;
    private String stimulusValues;
    private long sendingTime;

    public SenderMessage(ActorRef sender, ActorRef receiver, String stimulusValues, long sendingTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
    }

    public SenderMessage() {
    }

    public ActorRef getSender() {
        return sender;
    }

    public void setSender(ActorRef sender) {
        this.sender = sender;
    }

    public ActorRef getReceiver() {
        return receiver;
    }

    public void setReceiver(ActorRef receiver) {
        this.receiver = receiver;
    }

    public String getStimulusValues() {
        return stimulusValues;
    }

    public void setStimulusValues(String stimulusValues) {
        this.stimulusValues = stimulusValues;
    }

    public long getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(long sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Override
    public String toString() {
        return "SenderMessage{" +
                "sender=" + sender.path() +
                ", receiver=" + receiver.path() +
                ", stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                '}';
    }
}