package Artifice.Mailbox.messageQueue;

import Artifice.Mailbox.MyUnboundedMessageQueueSemantics;
import Artifice.Mailbox.StampedSenderMessage;
import akka.actor.ActorRef;
import akka.dispatch.Envelope;
import akka.dispatch.MessageQueue;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class BackendGroupedMessageQueue implements MessageQueue,
        MyUnboundedMessageQueueSemantics {

    private Comparator<Envelope> envelopeComparator = new Comparator<Envelope>() {
        public int compare(Envelope o1, Envelope o2) {
            return 1;
        }
    };

    private final Queue<Envelope> queue =
            new PriorityBlockingQueue<Envelope>(10000000, envelopeComparator);

    public void enqueue(ActorRef receiver, Envelope handle) {
            queue.offer(handle);
    }

    public Envelope dequeue() {
        return queue.poll();
    }

    public int numberOfMessages() {
        return queue.size();
    }

    public boolean hasMessages() {
        return !queue.isEmpty();
    }

    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        for (Envelope handle : queue) {
            deadLetters.enqueue(owner, handle);
        }
    }
}