package main;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class AllUtil {
	public static Random random;
	
	public static Image getResImage(String url) {
		try {
			return Toolkit.getDefaultToolkit().getImage(Main.getMain().getClass().getClassLoader().getResource(url));			
		} catch(Exception e) {
			//return Toolkit.getDefaultToolkit().getImage(url);
			e.printStackTrace();
		}
		return null;
	}
	
	public static URL getResURL(String url) {
		return Main.getMain().getClass().getClassLoader().getResource(url);
	}
	
	public static int getRand(int min, int max) {
		if( random == null )
			random = new Random();
		
		return (random.nextInt() % (max - min)) + min;
	}
	
	static final String[] browsers = { "google-chrome", "firefox", "opera", "epiphany"
										, "konqueror", "conkeror",
										"midori", "kazehakase", "mozilla" };
	static final String errMsg = "Error attempting to launch web browser";

	public static void openURL(String url) {
		try { // attempt to use Desktop library from JDK 1.6+
			Class<?> d = Class.forName("java.awt.Desktop");
			d.getDeclaredMethod("browse", new Class[] { java.net.URI.class })
			.invoke(d.getDeclaredMethod("getDesktop").invoke(null), new Object[] { 
			java.net.URI.create(url) });
			// above code mimicks: java.awt.Desktop.getDesktop().browse()
		} catch (Exception ignore) { // library not available or failed
			String osName = System.getProperty("os.name");
			try {
				if (osName.startsWith("Mac OS")) {
					Class.forName("com.apple.eio.FileManager")
					.getDeclaredMethod("openURL", new Class[] { String.class })
					.invoke(null, new Object[] { url });
				} else if (osName.startsWith("Windows"))
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
				else { // assume Unix or Linux
					String browser = null;
					for (String b : browsers)
					if (browser == null
					&& Runtime.getRuntime().exec(
					new String[] { "which", b }).getInputStream().read() != -1)
					Runtime.getRuntime().exec(new String[] { browser = b, url });
					if (browser == null)
						throw new Exception(Arrays.toString(browsers));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
