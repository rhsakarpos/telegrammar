import com.pengrad.telegrambot.TelegramBot;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ScheduledMessagesTimeUpdaterTask extends TimerTask {

    private static int n = 0;
    private MessagesTimeHandler m_mth;
    private MessagesMapHandler m_mmh;
    BlockingQueue m_QueueMessagesToUpdateTime = new ArrayBlockingQueue(500);
    ThreadPoolExecutor m_TPEUpdateMessageTimes = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private BlockingQueue m_QueueMessagesToSend = new ArrayBlockingQueue(500);
    private ThreadPoolExecutor m_TPESendMessages = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private TelegramBot m_bot;

    public ScheduledMessagesTimeUpdaterTask(MessagesTimeHandler mth, MessagesMapHandler mmh, TelegramBot bot) {

        m_mth = mth;
        m_mmh = mmh;
        m_bot = bot;
    }

    @Override
    public void run() {
        //System.out.println("\t\t ScheduledMessagesTimeUpdaterTask");
        n++;

        for (Map.Entry<Integer, Long> entry : m_mth.getM_MapMessageIdVsTimestamp().entrySet()) {
            if (m_mth.getM_MapMessageIdVsTimestamp().containsKey(entry.getKey())) {
                m_QueueMessagesToUpdateTime.add(entry.getKey());
                UpdateMessageTimeTask updateMessageTimeTask = new UpdateMessageTimeTask(m_QueueMessagesToUpdateTime, m_mth, m_mmh, n,
                        m_bot, m_TPESendMessages, m_QueueMessagesToSend);
                m_TPEUpdateMessageTimes.execute(updateMessageTimeTask);
            }
        }
    }
}

