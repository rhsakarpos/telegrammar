import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.concurrent.BlockingQueue;

public class SendMessageTask implements Runnable {
    private BlockingQueue m_q;
    private TelegramBot m_bot;

    public SendMessageTask(BlockingQueue queue, TelegramBot bot) {
        this.m_q = queue;
        this.m_bot = bot;
    }

    public void run() {
        try {
            String toSend = (String) m_q.take();
            long chatId = -475095459L;

            SendMessage request = new SendMessage(chatId, toSend)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);
            //.replyToMessageId(1)
            //.replyMarkup(new ForceReply());

            // sync
            SendResponse sendResponse = m_bot.execute(request);

            boolean ok = sendResponse.isOk();
            Message message = sendResponse.message();
            System.out.println(sendResponse.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
