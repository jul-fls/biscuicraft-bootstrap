package fr.biscuicraftbootstrap;

import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ClasspathConstructor;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.openlauncherlib.util.explorer.ExploredDirectory;
import fr.theshark34.openlauncherlib.util.explorer.Explorer;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import static fr.theshark34.swinger.Swinger.*;
import java.io.File;

public class BiscuicraftBootstrap {
	private static SplashScreen splash;
	private static SColoredBar bar;
	private static Thread barThread;
	private static final File BC_B_DIR = new File(GameDirGenerator.createGameDir("Biscuicraft"), "launcher");
	private static CrashReporter BC_B_REPORTER =  new CrashReporter("Biscuicraft", BC_B_DIR);
	public static void main(String[] args) {
		setSystemLookNFeel();
		setResourcePath("/fr/biscuicraftbootstrap/resources/");
		showSplash();
		try {
			Update();
		} catch (Exception e) {
			if (barThread != null)
				barThread.interrupt();
				e.printStackTrace();
				BC_B_REPORTER.catchError(e, "Impossible de mettre à jour le launcher !");
		}
		try
		{
			launch();
		}catch (LaunchException e)
		{
			BC_B_REPORTER.catchError(e, "Impossible de lancer le launcher !");
		}
	}
	
	private static void showSplash() {
		splash = new SplashScreen("Biscuicraft", getResource("splash.png"));
		splash.setLayout(null);
		bar = new SColoredBar(getTransparentWhite(100), getTransparentWhite(175));
		bar.setBounds(0,450, 256, 20);
		splash.add(bar);
		splash.setBackground(TRANSPARENT);
		splash.setVisible(true);
		splash.setIconImage(Swinger.getResource("rounded_icon.png"));
	}
	
	private static void Update() throws Exception {
		SUpdate su = new SUpdate("url du serveur S-Update",(BC_B_DIR));
		barThread = new Thread() {
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
					bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000));
				}
			}
		};
		barThread.start();
		su.start();
		barThread.interrupt();
	}
	
	private static void launch() throws LaunchException {
		ClasspathConstructor constructor = new ClasspathConstructor();
		ExploredDirectory gameDir = Explorer.dir(BC_B_DIR);
		constructor.add(gameDir.sub("Libs").allRecursive().files().match("^(.*\\.((jar)$))*$"));
		constructor.add(gameDir.get("Biscuicraft.jar"));
		ExternalLaunchProfile profile = new ExternalLaunchProfile("fr.biscuicraft.launcher.LauncherFrame", constructor.make());
		ExternalLauncher launcher = new ExternalLauncher(profile);
		Process p = launcher.launch();
		splash.setVisible(false);
		try {
			p.waitFor();
		} catch (InterruptedException ignored){
		}
		System.exit(0);
	}
}
.
