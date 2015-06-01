package imin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPAddressSucceededException;
import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;

public class MainClass {
	public static void main(String[] args) throws Exception {
		
		HelperClass hc = new HelperClass();
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.HOUR_OF_DAY, 9);
		c.set(Calendar.MINUTE, 31);
		c.set(Calendar.SECOND, 00);
		
		
		if (!valid(c)) {
			new Ui(HelperClass.getMetaContent());
			return;
		}
		
		Timer timer = new Timer();
		timer.schedule(hc, c.getTime());

	}

	private static boolean valid(Calendar base) {
		
		Calendar current = Calendar.getInstance();
		
		return current.getTimeInMillis() <= base.getTimeInMillis();
	}
}

class HelperClass extends TimerTask {
	
	static String root = "C:/20150323/";

	public void run() {
		
		try {
			StringBuilder sb = new StringBuilder();

			String[] a = getMetaContent();
			
//			isAllInClass(sb);
			
			if (isAllInClass(sb)) {
//			System.out.println(sb);

				// sendMail
				

				smtpsend(a, sb.toString());

			} else {
//
				new Ui(a);
//				smtpsend(a, ui.getContent());
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String[] getMetaContent() throws Exception {
		Properties p = new Properties();
		FileReader fr = new FileReader(root + "mail.properties");
		p.load(fr);
		
		
		String[] a = { "-d", "-S", "-A", "-M", "smtp.gmail.com", "-U",
				p.getProperty("u"), "-P", p.getProperty("p"), "-s", "출석결과", p.getProperty("m") };
		
		fr.close();
	
		return a;
	}

	private boolean isAllInClass(StringBuilder sb) throws Exception {

		FileReader fr = new FileReader(root + "classipmember.properties");
		
		int e = 0;

		Properties properties = new Properties();
		properties.load(fr);

		Enumeration<Object> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			Process p = Runtime.getRuntime().exec("ping -n 1 " + key);
			p.waitFor();
		}

		Process p = Runtime.getRuntime().exec("arp -a");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String str;
		while ((str = br.readLine()) != null) {
			String ip = "";

			CharSequence inputStr = str;
			String patternStr = "(\\d+\\.){3}\\d+";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(inputStr);

			if (matcher.find()) {

				ip = str.substring(matcher.start(),
						str.indexOf(" ", matcher.start()));// this will give you
															// index

			}

			if (properties.containsKey(ip)) {
				sb.append(properties.get(ip) + " 출석\n");
				e++;
			}
		}
		
//		System.out.println(sb);

		fr.close();
		br.close();

		return (properties.size() > e) ? false : true;
	}
	
	/**
	 * Example of how to extend the SMTPTransport class. This example
	 * illustrates how to issue the XACT command before the SMTPTransport issues
	 * the DATA command.
	 *
	 * public static class SMTPExtension extends SMTPTransport { public
	 * SMTPExtension(Session session, URLName url) { super(session, url); // to
	 * check that we're being used
	 * System.out.println("SMTPExtension: constructed"); }
	 * 
	 * protected synchronized OutputStream data() throws MessagingException { if
	 * (supportsExtension("XACCOUNTING")) issueCommand("XACT", 250); return
	 * super.data(); } }
	 */

	public static void smtpsend(String[] argv, String text) {
		String to, subject = null, from = null, cc = null, bcc = null, url = null;
		String mailhost = null;
		String mailer = "smtpsend";
		String file = null;
		String protocol = null, host = null, user = null, password = null;
		String record = null; // name of folder in which to record mail
		boolean debug = false;
		boolean verbose = false;
		boolean auth = false;
		String prot = "smtp";
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int optind;

		/*
		 * Process command line arguments.
		 */
		for (optind = 0; optind < argv.length; optind++) {
			if (argv[optind].equals("-T")) {
				protocol = argv[++optind];
			} else if (argv[optind].equals("-H")) {
				host = argv[++optind];
			} else if (argv[optind].equals("-U")) {
				user = argv[++optind];
			} else if (argv[optind].equals("-P")) {
				password = argv[++optind];
			} else if (argv[optind].equals("-M")) {
				mailhost = argv[++optind];
			} else if (argv[optind].equals("-f")) {
				record = argv[++optind];
			} else if (argv[optind].equals("-a")) {
				file = argv[++optind];
			} else if (argv[optind].equals("-s")) {
				subject = argv[++optind];
			} else if (argv[optind].equals("-o")) { // originator
				from = argv[++optind];
			} else if (argv[optind].equals("-c")) {
				cc = argv[++optind];
			} else if (argv[optind].equals("-b")) {
				bcc = argv[++optind];
			} else if (argv[optind].equals("-L")) {
				url = argv[++optind];
			} else if (argv[optind].equals("-d")) {
				debug = true;
			} else if (argv[optind].equals("-v")) {
				verbose = true;
			} else if (argv[optind].equals("-A")) {
				auth = true;
			} else if (argv[optind].equals("-S")) {
				prot = "smtps";
			} else if (argv[optind].equals("--")) {
				optind++;
				break;
			} else if (argv[optind].startsWith("-")) {
				System.out
						.println("Usage: smtpsend [[-L store-url] | [-T prot] [-H host] [-U user] [-P passwd]]");
				System.out
						.println("\t[-s subject] [-o from-address] [-c cc-addresses] [-b bcc-addresses]");
				System.out
						.println("\t[-f record-mailbox] [-M transport-host] [-d] [-a attach-file]");
				System.out.println("\t[-v] [-A] [-S] [address]");
				System.exit(1);
			} else {
				break;
			}
		}

		try {
			/*
			 * Prompt for To and Subject, if not specified.
			 */
			if (optind < argv.length) {
				to = argv[optind];
				System.out.println("To: " + to);
			} else {
				System.out.print("To: ");
				System.out.flush();
				to = in.readLine();
			}
			if (subject == null) {
				System.out.print("Subject?: ");
				System.out.flush();
				subject = in.readLine();
			} else {
				System.out.println("Subject!: " + subject);
			}

			/*
			 * Initialize the JavaMail Session.
			 */
			Properties props = System.getProperties();
			if (mailhost != null)
				props.put("mail." + prot + ".host", mailhost);
			if (auth)
				props.put("mail." + prot + ".auth", "true");

			/*
			 * Create a Provider representing our extended SMTP transport and
			 * set the property to use our provider.
			 * 
			 * Provider p = new Provider(Provider.Type.TRANSPORT, prot,
			 * "smtpsend$SMTPExtension", "JavaMail demo", "no version");
			 * props.put("mail." + prot + ".class", "smtpsend$SMTPExtension");
			 */

			// Get a Session object
			Session session = Session.getInstance(props, null);
			if (debug)
				session.setDebug(true);

			/*
			 * Register our extended SMTP transport.
			 * 
			 * session.addProvider(p);
			 */

			/*
			 * Construct the message and send it.
			 */
			Message msg = new MimeMessage(session);
			if (from != null)
				msg.setFrom(new InternetAddress(from));
			else
				msg.setFrom();

			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to, false));
			if (cc != null)
				msg.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(cc, false));
			if (bcc != null)
				msg.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(bcc, false));

			msg.setSubject(subject);

			// String text = collect(in);

			if (file != null) {
				// Attach the specified file.
				// We need a multipart message to hold the attachment.
				MimeBodyPart mbp1 = new MimeBodyPart();
				mbp1.setText(text);
				MimeBodyPart mbp2 = new MimeBodyPart();
				mbp2.attachFile(file);
				MimeMultipart mp = new MimeMultipart();
				mp.addBodyPart(mbp1);
				mp.addBodyPart(mbp2);
				msg.setContent(mp);
			} else {
				// If the desired charset is known, you can use
				// setText(text, charset)
				msg.setText(text);
			}

			msg.setHeader("X-Mailer", mailer);
			msg.setSentDate(new Date());

			// send the thing off
			/*
			 * The simple way to send a message is this:
			 * 
			 * Transport.send(msg);
			 * 
			 * But we're going to use some SMTP-specific features for
			 * demonstration purposes so we need to manage the Transport object
			 * explicitly.
			 */
			SMTPTransport t = (SMTPTransport) session.getTransport(prot);
			try {
				if (auth)
					t.connect(mailhost, user, password);
				else
					t.connect();
				t.sendMessage(msg, msg.getAllRecipients());
			} finally {
				if (verbose)
					System.out
							.println("Response: " + t.getLastServerResponse());
				t.close();
			}

			System.out.println("\nMail was sent successfully.");

			/*
			 * Save a copy of the message, if requested.
			 */
			if (record != null) {
				// Get a Store object
				Store store = null;
				if (url != null) {
					URLName urln = new URLName(url);
					store = session.getStore(urln);
					store.connect();
				} else {
					if (protocol != null)
						store = session.getStore(protocol);
					else
						store = session.getStore();

					// Connect
					if (host != null || user != null || password != null)
						store.connect(host, user, password);
					else
						store.connect();
				}

				// Get record Folder. Create if it does not exist.
				Folder folder = store.getFolder(record);
				if (folder == null) {
					System.err.println("Can't get record folder.");
					System.exit(1);
				}
				if (!folder.exists())
					folder.create(Folder.HOLDS_MESSAGES);

				Message[] msgs = new Message[1];
				msgs[0] = msg;
				folder.appendMessages(msgs);

				System.out.println("Mail was recorded successfully.");
			}

		} catch (Exception e) {
			/*
			 * Handle SMTP-specific exceptions.
			 */
			if (e instanceof SendFailedException) {
				MessagingException sfe = (MessagingException) e;
				if (sfe instanceof SMTPSendFailedException) {
					SMTPSendFailedException ssfe = (SMTPSendFailedException) sfe;
					System.out.println("SMTP SEND FAILED:");
					if (verbose)
						System.out.println(ssfe.toString());
					System.out.println("  Command: " + ssfe.getCommand());
					System.out.println("  RetCode: " + ssfe.getReturnCode());
					System.out.println("  Response: " + ssfe.getMessage());
				} else {
					if (verbose)
						System.out.println("Send failed: " + sfe.toString());
				}
				Exception ne;
				while ((ne = sfe.getNextException()) != null
						&& ne instanceof MessagingException) {
					sfe = (MessagingException) ne;
					if (sfe instanceof SMTPAddressFailedException) {
						SMTPAddressFailedException ssfe = (SMTPAddressFailedException) sfe;
						System.out.println("ADDRESS FAILED:");
						if (verbose)
							System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out
								.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					} else if (sfe instanceof SMTPAddressSucceededException) {
						System.out.println("ADDRESS SUCCEEDED:");
						SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException) sfe;
						if (verbose)
							System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out
								.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					}
				}
			} else {
				System.out.println("Got Exception: " + e);
				if (verbose)
					e.printStackTrace();
			}
		}
	}

	/**
	 * Read the body of the message until EOF.
	 */
	public String collect(BufferedReader in) throws IOException {
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = in.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

}
