package Artifice.Mailbox;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public class StampedSenderMessage {

    private final String stimulusValues;
    private final long sendingTime;
    private final long receivingTime;

    public StampedSenderMessage(String stimulusValues, long sendingTime, long receivingTime) {
        this.stimulusValues = stimulusValues;
        this.sendingTime = sendingTime;
        this.receivingTime = receivingTime;
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
        return "SenderMessage{stimulusValues='" + stimulusValues + '\'' +
                ", sendingTime=" + sendingTime +
                '}';
    }
}