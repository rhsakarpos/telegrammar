import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UpdateMessageTimeTask implements Runnable {
    private BlockingQueue m_q;
    private MessagesTimeHandler m_MessagesTimeHandler;
    private MessagesMapHandler m_MessagesMapHandler;
    private long m_MilliSeconds;
    //private BlockingQueue m_QueueMessagesToSend = new ArrayBlockingQueue(500);
    //private ThreadPoolExecutor m_TPESendMessages = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private ThreadPoolExecutor m_TPESendMessages;
    private TelegramBot m_bot;
    private BlockingQueue m_QueueMessagesToSend;

    public UpdateMessageTimeTask(BlockingQueue queue, MessagesTimeHandler mth, MessagesMapHandler messagesMapHandler, long n, TelegramBot bot,
                                 ThreadPoolExecutor tpeSendMessages, BlockingQueue smq) {
        this.m_q = queue;
        this.m_MessagesTimeHandler = mth;
        this.m_MilliSeconds = 10 * n * 1000;
        this.m_MessagesMapHandler = messagesMapHandler;
        this.m_bot = bot;
        this.m_TPESendMessages = tpeSendMessages;
        this.m_QueueMessagesToSend = smq;
        SendMessageTask sendMessageTask = new SendMessageTask(m_QueueMessagesToSend, m_bot);
        m_TPESendMessages.execute(sendMessageTask);
    }

    public void run() {
        try {
            Integer messageId = (Integer) m_q.take();
            //System.out.println("Executing UpdateMessageTimeTask " + messageId + " in thread " + Thread.currentThread().getName() + " milliseconds= " + m_MilliSeconds);

            // update the time by 'n' seconds
            long currentTS = m_MessagesTimeHandler.getFromMap(messageId);
            long sysTime = System.currentTimeMillis();

            if (Math.abs(sysTime - currentTS) > 30000) {
                Message m = m_MessagesMapHandler.getFromMap(messageId);
                if (m != null) {
                    if (m.text() != null) {
                        System.out.println("\n\t\t" + m.text());
                        m_QueueMessagesToSend.add("@admin, Please respond to question '" + m.text() + "' asked by " + m.from().firstName());
                    }
                    // remove this since admins have been informed
                    m_MessagesMapHandler.removeFromMap(m.messageId());
                }

            } else {
                long updatedTS = currentTS + m_MilliSeconds;
                m_MessagesTimeHandler.addToMap(messageId, updatedTS);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
