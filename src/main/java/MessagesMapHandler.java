import com.pengrad.telegrambot.model.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessagesMapHandler {
    private Map<Integer, Message> m_MapMessageIdVsMessages = new ConcurrentHashMap<>();


    public void addToMap(Message m) {
        m_MapMessageIdVsMessages.put(m.messageId(), m);
    }

    public Message getFromMap(Integer id) {
        Message m = m_MapMessageIdVsMessages.get(id);
        return m;
    }

    public void removeFromMap(Integer id) {
        m_MapMessageIdVsMessages.remove(id);
    }
}
