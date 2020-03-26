import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainClass {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {

        MessagesMapHandler messagesMapHandler = new MessagesMapHandler();
        MessagesTimeHandler messagesTimeHandler = new MessagesTimeHandler();

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            //createTelegramBotsApi();
            try {
                TelegramBot bot = new TelegramBot("1030843207:AAHahVtGQcCLmbe0SsYTRHEF-h4zy5iz-wk");
                ScheduledMessagesTimeUpdaterTask timeUpdaterTask = new ScheduledMessagesTimeUpdaterTask(messagesTimeHandler, messagesMapHandler, bot);
                BlockingQueue queueMessagesToAdd = new ArrayBlockingQueue(500);
                ThreadPoolExecutor tpeAddMessages = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

                BlockingQueue queueMessagesToSend = new ArrayBlockingQueue(500);
                ThreadPoolExecutor tpeSendMessages = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

                new Timer().schedule(timeUpdaterTask, 0, 10000);

                List<String> keywords = new ArrayList<>();
                keywords.add("?");

                keywords.add("please");
                keywords.add("Please");

                keywords.add("who");
                keywords.add("Who");

                keywords.add("what");
                keywords.add("What");

                keywords.add("why");
                keywords.add("Why");

                keywords.add("when");
                keywords.add("When");

                keywords.add("where");
                keywords.add("Where");

                keywords.add("Can");
                keywords.add("can");

                keywords.add("how");
                keywords.add("How");

                keywords.add("tell");
                keywords.add("Tell");

                keywords.add("does");
                keywords.add("Does");

                keywords.add("know");
                keywords.add("Know");

                // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
                //telegramBotsApi.registerBot(new ChannelHandlers());
                /*telegramBotsApi.registerBot(new DirectionsHandlers());
                telegramBotsApi.registerBot(new RaeHandlers());
                telegramBotsApi.registerBot(new WeatherHandlers());
                telegramBotsApi.registerBot(new TransifexHandlers());
                telegramBotsApi.registerBot(new FilesHandlers());
                telegramBotsApi.registerBot(new CommandsHandler(BotConfig.COMMANDS_USER));
                telegramBotsApi.registerBot(new ElektrollArtFanHandler());*/


                //telegramBotsApi.registerBot(new Bot());
                // Create your bot passing the token received from @BotFather

                GetUpdates getUpdates = new GetUpdates().limit(100000).offset(0).timeout(0);

                //long chatId = -1001217508830L;


                // async
                final GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
                bot.execute(getUpdates, new Callback<GetUpdates, GetUpdatesResponse>() {
                    @Override
                    public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                        List<com.pengrad.telegrambot.model.Update> updates = updatesResponse.updates();
                        for (Update u : updates) {
                            if (u.message() != null) {
                                //System.out.println("response message");
                                //System.out.println(u.message().text());
                            }
                            if (u.channelPost() != null) {
                                //System.out.println("response channel post");
                                //System.out.println(u.channelPost().text());
                            }
                        }
                    }

                    @Override
                    public void onFailure(GetUpdates request, IOException e) {

                    }
                });

                bot.setUpdatesListener(new UpdatesListener() {
                    @Override
                    public int process(List<Update> updates) {

                        for (Update u : updates) {
                            if (u.message() != null) {
                                //System.out.println("update process message");
                                //System.out.println(u.message().text());

                                queueMessagesToAdd.add(u.message());
                                AddMessageTask addMessageTask = new AddMessageTask(queueMessagesToAdd, messagesMapHandler, messagesTimeHandler, keywords);
                                tpeAddMessages.execute(addMessageTask);

                                /*if (u.message().replyToMessage() != null) {
                                    System.out.println("\t\t is reply");
                                    // update reply count
                                    int replyToMessageId = u.message().replyToMessage().messageId();
                                    *//*int resCount = notRepliedMessageTask.mapMessageIdVsResponses.get(replyToMessageId);
                                        resCount = resCount + 1;
                                        notRepliedMessageTask.mapMessageIdVsResponses.put(replyToMessageId, resCount);*//*
                                    mapMessageIdVsMessages.remove(replyToMessageId);
                                } else {
                                    System.out.println("\t\t is first message");
                                    // add to map
                                    mapMessageIdVsMessages.put(u.message().messageId(), u.message());
                                }*/


                                Map<String, String> itemValues = new HashMap<>();
                                if (u.message().text() != null) {
                                    itemValues.put("channelmessagetext", u.message().text());
                                    if (u.message().from().firstName() != null) {
                                        itemValues.put("username", u.message().from().firstName());
                                    }
                                    if (u.message().chat().title() != null) {
                                        itemValues.put("title", u.message().chat().title());
                                    }

                                    //itemValues.put("location", u.channelPost().location().toString());
                                    //System.out.println(itemValues);
                                    //PutItem.writeToDynamoDB(itemValues);
                                }

                            }
                            //System.out.println(u.channelPost().text());

                            if (u.channelPost() != null) {
                                Map<String, String> itemValues = new HashMap<>();
                                System.out.println("update process channelpost");
                                itemValues.put("channelmessagetext", u.channelPost().text());
                                itemValues.put("username", u.channelPost().chat().username());
                                itemValues.put("title", u.channelPost().chat().title());
                                //itemValues.put("location", u.channelPost().location().toString());
                                //System.out.println(itemValues);
                                //PutItem.writeToDynamoDB(itemValues);
                            }
                        }
                        // process updates
                        return UpdatesListener.CONFIRMED_UPDATES_ALL;
                    }
                });
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static TelegramBotsApi createTelegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi;
        //if (!BuildVars.useWebHook) {
        // Default (long polling only)
        telegramBotsApi = createLongPollingTelegramBotsApi();
        /*} else if (!BuildVars.pathToCertificatePublicKey.isEmpty()) {
            // Filled a path to a pem file ? looks like you're going for the self signed option then, invoke with store and pem file to supply.
            telegramBotsApi = createSelfSignedTelegramBotsApi();
            telegramBotsApi.registerBot(new WebHookExampleHandlers());
        } else {
            // Non self signed, make sure you've added private/public and if needed intermediate to your cert-store.
            telegramBotsApi = createNoSelfSignedTelegramBotsApi();
            telegramBotsApi.registerBot(new WebHookExampleHandlers());
        }*/
        return telegramBotsApi;
    }

    /**
     * @return TelegramBotsApi to register the bots.
     * @brief Creates a Telegram Bots Api to use Long Polling (getUpdates) bots.
     */
    private static TelegramBotsApi createLongPollingTelegramBotsApi() {
        return new TelegramBotsApi();
    }

}
