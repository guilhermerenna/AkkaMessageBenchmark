package Artifice.Mailbox;

import akka.actor.ActorRef;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public class ReceiverMessage {

    private final ActorRef sender;
    private final ActorRef receiver;
    private final String stimulusValues;
    private final long sendingTime;
    private final long receivingTime;

    public ReceiverMessage(ActorRef sender, ActorRef receiver, String stimulusValues, long sendingTime, long receivingTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
        this.receivingTime = receivingTime;
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

    @Override
    public String toString() {
        return "ReceiverMessage{" +
                "sender=" + sender.path() +
                ", receiver=" + receiver.path() +
                ", stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                ", receivingTime=" + receivingTime +
                '}';
    }
}