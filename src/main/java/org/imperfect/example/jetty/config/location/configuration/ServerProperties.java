package org.imperfect.example.jetty.config.location.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class ServerProperties {
	
	private final String serverName;
	private final int serverPort;
	
	public ServerProperties(
			@Value("${server.name}") String serverName,
			@Value("${server.port}") int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public int getServerPort() {
		return serverPort;
	}
}
