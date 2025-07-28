package TOTs.OOTDay;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OotDayApplicationTests {


	@Autowired
	OpenAiChatModel model;

	@Test
	void contextLoads() {
		UserMessage userMessage = new UserMessage("오늘의 날씨를 알려줘");
		Prompt prompt = new Prompt(userMessage);

		ChatResponse response = model.call(prompt);
		System.out.println(response.getResult().getOutput().getText());
	}

}
