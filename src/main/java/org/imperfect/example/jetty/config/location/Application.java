package org.imperfect.example.jetty.config.location;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Application implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(Application.class, args);
		
		CountDownLatch closeLatch = context.getBean(CountDownLatch.class);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				context.getBean(Server.class).stop();
			} catch(Exception ex) {
				LOGGER.warn("Failed to stop Jetty!", ex);
			}
			closeLatch.countDown();
		}));
		closeLatch.await();
	}
	
	@Override
	public void run(String... args) throws Exception {
		try (CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build()) {
			HttpGet request = new HttpGet("http://127.0.0.1:8081/greet");
			request.setProtocolVersion(HttpVersion.HTTP_1_0);
			
			HttpResponse response = client.execute(request);
			Header[] headers = response.getAllHeaders();
			for(Header header : headers) {
				LOGGER.info("{}: {}", header.getName(), header.getValue());
			}
		}
	}
}
