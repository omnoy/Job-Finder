package resumeBuilder.chatgpt;

import com.plexpt.chatgpt.ChatGPT;
import org.springframework.beans.factory.annotation.Value;

public class ChatGptSingletone {
	private static ChatGptSingletone instance;
	private ChatGPT chatGpt;
	private String apiKey;
    
	private ChatGptSingletone() {
		this.chatGpt = ChatGPT.builder()
				.apiKey(this.apiKey)
				.build()
				.init();
	}
	
    public static ChatGptSingletone getInstance() {
        if (instance == null) {
        	instance = new ChatGptSingletone();
        }
        return instance;
    }
    
    public ChatGPT getChat() {
    	return this.chatGpt;
    }

    @Value("${chatgpt.apikey}")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}