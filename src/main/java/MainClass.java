import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import updateshandlers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class MainClass {
    private static final String LOGTAG = "MAIN";

    public static void main(String[] args) {

        try {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            //createTelegramBotsApi();
            try {
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
                TelegramBot bot = new TelegramBot("1030843207:AAHahVtGQcCLmbe0SsYTRHEF-h4zy5iz-wk");
                GetUpdates getUpdates = new GetUpdates().limit(1000).offset(0).timeout(0);
                // async
                final GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
                bot.execute(getUpdates, new Callback<GetUpdates, GetUpdatesResponse>() {
                    @Override
                    public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                        List<com.pengrad.telegrambot.model.Update> updates = updatesResponse.updates();
                        for (Update u : updates) {
                            if (u.message() != null) {
                                System.out.println(u.message().text());
                            }
                            System.out.println(u.channelPost().text());
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
                                System.out.println(u.message().text());
                            }
                            //System.out.println(u.channelPost().text());
                            Map<String, String> itemValues = new HashMap<>();
                            itemValues.put("channelmessagetext", u.channelPost().text());
                            itemValues.put("username", u.channelPost().chat().username());
                            itemValues.put("title", u.channelPost().chat().title());
                            //itemValues.put("location", u.channelPost().location().toString());
                            System.out.println(itemValues);
                            PutItem.writeToDynamoDB(itemValues);

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
