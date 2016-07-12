package com.huayun.spring.jetty.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * 发送邮件
 * 
 * 
 */
public class MailUtil {
	private static Map<String, Object> proMap = null;
	private static JavaMailSenderImpl instance = null;

	static {
		proMap = new HashMap<String, Object>();
		proMap.put("resource.loader", "class");
		proMap.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

	}

	public static JavaMailSender getInstance(String serverHost, String mailFrom, String mailPassword, boolean ssl, String serverPort) {
		synchronized (JavaMailSenderImpl.class) {
			if (null == instance) {
				instance = new JavaMailSenderImpl();
			}
			instance.setHost(serverHost);
			instance.setUsername(mailFrom);
			instance.setPassword(mailPassword);
			Properties properties = new Properties();
			properties.setProperty("mail.smtp.auth", ssl + "");

			// 使用gmail发送邮件是必须设置如下参数的 主要是port不一样
			if (serverHost.indexOf("smtp.gmail.com") >= 0) {
				properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				properties.setProperty("mail.smtp.socketFactory.fallback", ssl + "");
				properties.setProperty("mail.smtp.port", serverPort);
				properties.setProperty("mail.smtp.socketFactory.port", serverPort);
			}
			instance.setJavaMailProperties(properties);

		}

		return instance;
	}

	

	public static void sendEmail(String serverHost, String serverPort, boolean ssl, final String mailFrom, String mailPassword, final String[] mailTo, final Map<String, Object> mailData,
			final String title, final String templateFile, final String[] files) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			// 注意MimeMessagePreparator接口只有这一个回调函数
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "GBK");
				// 这是一个生成Mime邮件简单工具，如果不使用GBK这个，中文会出现乱码
				// 如果您使用的都是英文，那么可以使用MimeMessageHelper message = new
				// MimeMessageHelper(mimeMessage);
				message.setTo(mailTo);// 设置接收方的email地址
				message.setSubject(title);// 设置邮件主题
				message.setFrom(mailFrom);// 设置发送方地址
				String text = templateFile;
				// 从模板中加载要发送的内容，vmfile就是模板文件的名字
				// 注意模板中有中文要加GBK，model中存放的是要替换模板中字段的值
				message.setText(text, true);
				// 将发送的内容赋值给MimeMessageHelper,后面的true表示内容解析成html
				// 如果您不想解析文本内容，可以使用false或者不添加这项
				if (files != null) {
					FileSystemResource file;
					for (String s : files)// 添加附件
					{
						file = new FileSystemResource(new File(s));// 读取附件
						message.addAttachment(s, file);// 向email中添加附件
					}
				}
			}
		};

		MailUtil.getInstance(serverHost, mailFrom, mailPassword, ssl, serverPort).send(preparator);// 发送邮件
	}


	public static void main(String[] args) {
		Map<String, Object> mailData = new HashMap<String, Object>();
		mailData.put("userName", "童昊");
		mailData.put("emailAddress", "描述信息");

		// String emailHost = "smtp.chinacloud.com.cn";
		// String mailFrom = "tonghao@chinacloud.com.cn";
		// String mailPassword = "8**";
		// boolean ssl = true;
		// String serverPort = "25";
		// String[] mailTo = new String[] { "tonghao@chinacloud.com.cn" };
		// String title = "告警信息666";
		// String templateFile = "mail/mail.vm";

		String emailHost = "123.125.50.132";
		String mailFrom = "sss_test2014@163.com";
		String mailPassword = "test2014";
		
		// 
		// String mailFrom = "zjtesthuayun@163.com";
		// String mailPassword = "zj19901225";
		
		boolean ssl = false;
		String serverPort = "25";
		String[] mailTo = new String[] { "zhongjun@chinacloud.com.cn" };
		String title = "告警信息666";
		String templateFile = "mail/mail-content.vm";

		MailUtil.sendEmail(emailHost, serverPort, ssl, mailFrom, mailPassword, mailTo, mailData, title, templateFile, null);
	}
}
