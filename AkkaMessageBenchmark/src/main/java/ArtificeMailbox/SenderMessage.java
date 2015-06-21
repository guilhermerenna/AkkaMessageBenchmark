package ArtificeMailbox;

import akka.actor.ActorRef;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public class SenderMessage {

    private String stimulusValues;
    private long sendingTime;

    public SenderMessage(String stimulusValues, long sendingTime) {
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
    }

    public String getStimulusValues() {
        return stimulusValues;
    }

    public long getSendingTime() {
        return sendingTime;
    }

    @Override
    public String toString() {
        return "SenderMessage{stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                '}';
    }
}