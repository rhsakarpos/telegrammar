import com.pengrad.telegrambot.model.Message;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class AddMessageTask implements Runnable {
    private BlockingQueue m_q;
    private MessagesMapHandler m_MessagesMapHandler;
    private MessagesTimeHandler m_MessagesTimeHandler;
    private static List<String> m_Keywords;

    public AddMessageTask(BlockingQueue queue, MessagesMapHandler mh, MessagesTimeHandler mth, List<String> keywords) {
        this.m_q = queue;
        this.m_MessagesMapHandler = mh;
        this.m_MessagesTimeHandler = mth;
        this.m_Keywords = keywords;
    }

    private static boolean doesMessageContainAnyKeyword(String message) {
        boolean found = false;
        for (String s : m_Keywords) {
            if (message.contains(s)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void run() {
        try {
            Message message = (Message) m_q.take();
            //System.out.println("Executing AddMessageTask " + message.messageId() + " in thread " + Thread.currentThread().getName());

            if (message.replyToMessage() != null) {
                //System.out.println("\t\t is reply");
                int replyToMessageId = message.replyToMessage().messageId();
                m_MessagesMapHandler.removeFromMap(replyToMessageId);
                m_MessagesTimeHandler.removeFromMap(replyToMessageId);
            } else {
                //System.out.println("\t\t is first message");
                // add to map
                if (message.text() != null && doesMessageContainAnyKeyword(message.text())) {
                    m_MessagesMapHandler.addToMap(message);
                    m_MessagesTimeHandler.addToMap(message.messageId(), System.currentTimeMillis());
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
