package org.imperfect.example.jetty.config.location.configuration;

import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.rewrite.handler.RedirectRegexRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
public class ServerConfiguration {
	
	@Bean
	Server server(HttpConfiguration httpConfiguration, Handler rewriteHandler) throws Exception {
		Server server = new Server();
		
		ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
		connector.setPort(8081);
		
		server.addConnector(connector);
		server.setHandler(rewriteHandler);
		server.start();
		
		return server;
	}
	
	@Bean
	HttpConfiguration httpConfiguration(HttpConfiguration.Customizer locationConfiguration) {
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSendDateHeader(true);
		httpConfig.setSendServerVersion(false);
		httpConfig.addCustomizer(locationConfiguration);
		
		return httpConfig;
	}
	
	@Bean
	HttpConfiguration.Customizer locationConfiguration(ServerProperties properties) {
		String serverName = properties.getServerName();
		int serverPort = properties.getServerPort();
		return (connector, channelConfig, request) -> {
			String host = serverName == null ? request.getServerName() : serverName;
			int port = HttpScheme.normalizePort(request.getScheme(), serverPort == 0 ?
					request.getServerPort() : serverPort);
			
			if(serverName != null || serverPort > 0)
				request.setHttpURI(HttpURI.build(request.getHttpURI()).authority(host, port));
			
			HttpFields original = request.getHttpFields();
			HttpFields.Mutable httpFields = HttpFields.build(original.size() + 1);
			httpFields.add(new HostPortHttpField(host, port));
			httpFields.add(request.getHttpFields());
			request.setHttpFields(httpFields);
		};
	}
	
	@Bean
	RewriteHandler rewriteHandler() {
		RewriteHandler rewriteHandler = new RewriteHandler();
		RedirectRegexRule redirectRule = new RedirectRegexRule("/greet", "/greet/");
		redirectRule.setStatusCode(HttpStatus.MOVED_PERMANENTLY_301);
		rewriteHandler.addRule(redirectRule);
		return rewriteHandler;
	}
	
	@Bean
	CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}
}
