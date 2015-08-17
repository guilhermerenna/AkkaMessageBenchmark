package Artifice.Mailbox;

import java.io.Serializable;

/**
 * Created by Bruno Maciel on 12/06/15.
 */
public class SenderMessage implements Serializable {

    private final String stimulusValues;
    private final long sendingTime;

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