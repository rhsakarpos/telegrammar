import com.pengrad.telegrambot.model.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessagesTimeHandler {
    private Map<Integer, Long> m_MapMessageIdVsTimestamp = new ConcurrentHashMap<>();


    public void addToMap(Integer messageId, long timeStamp) {
        m_MapMessageIdVsTimestamp.put(messageId, timeStamp);
    }

    public Long getFromMap(Integer id) {
        Long ts = m_MapMessageIdVsTimestamp.get(id);
        return ts;
    }

    public void removeFromMap(Integer id) {
        m_MapMessageIdVsTimestamp.remove(id);
    }

    public Map<Integer, Long> getM_MapMessageIdVsTimestamp() {
        return m_MapMessageIdVsTimestamp;
    }
}
