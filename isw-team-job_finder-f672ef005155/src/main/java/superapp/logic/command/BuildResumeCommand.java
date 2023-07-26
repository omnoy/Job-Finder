package superapp.logic.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;

import superapp.data.MiniAppCommandEntity;
import superapp.data.SuperAppObjectEntity;
import superapp.data.SuperAppObjectIdEntity;

public class BuildResumeCommand extends Command {

	private static String CONTEXT_MESSAGE = "As a professional resume builder, you receive a JSON file containing user data."
			+ " Your task is to generate a nicely formatted HTML resume for the user. The resume should fit"
			+ " within a single page and exclude any null fields. Field names of null fields should not be displayed"
			+ " in the output HTML."
			+ " Please make sure to create an aesthetically pleasing CSS code that complements the HTML resume."
			+ " The CSS should be specifically tailored for a resume, ensuring that the overall design is "
			+ " professional and visually appealing. Consider using appropriate fonts, colors, , padding, spacing, margin, and "
			+ " layout techniques to present the resume in an attractive manner."
			+ " Return only the HTML code, without any other text!.";
	private static String API_KEY = "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";
	private ChatGPT chat;
	private ObjectMapper jackson;
	private Map<String, Object> commandAttributes;
	private SuperAppObjectIdEntity userId;

	public BuildResumeCommand(MiniAppCommandEntity command) {
		this.chat = ChatGPT.builder().apiKey(API_KEY).build().init();
		this.commandAttributes = command.getCommandAttributes();
		this.jackson = new ObjectMapper();
		this.userId = command.getTargetObject();
	}

	@Override
	public List<SuperAppObjectEntity> execute() {
		try {
			String commandAttributesJson = this.jackson.writeValueAsString(this.commandAttributes);
			Message context = Message.ofSystem(CONTEXT_MESSAGE);
			Message message = Message.of(commandAttributesJson);

			Message res = this.getCompletion(context, message);

			HTMLResume htmlResume = new HTMLResume(res.getContent());

			SuperAppObjectEntity toSave = this.commandResources.getDatabaseObjectCrud().findById(this.userId)
					.orElseThrow();

			toSave.getObjectDetails().put("status", "ready");
			toSave.getObjectDetails().put("resume", htmlResume);

			this.commandResources.getDatabaseObjectCrud().save(toSave);

			return null;
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private Message getCompletion(Message context, Message message) {
		try {
			ChatCompletion chatCompletion = ChatCompletion.builder().model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
					.messages(Arrays.asList(context, message)).maxTokens(3000).temperature(0).build();
			ChatCompletionResponse response = this.chat.chatCompletion(chatCompletion);
			Message res = response.getChoices().get(0).getMessage();

			return res;
		} catch (Exception e) {
			throw new RuntimeException("Could not parse resume to JSON");
		}
	}

}