
package groupa.deadlywheels.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;

import groupa.deadlywheels.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class LoadAppDeadlyWheel extends Activity {

	TextView textView1;

	static String SERVER_IP;
	static final int SERVER_PORT = 10002;

	Handler handler = new Handler();
	ServerSocket serverSocket;
	
	MediaPlayer mpl;
	
	// ---get the local IPv4 address---
	public String getLocalIpv4Address() {
		try {
			for (Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface
					.getNetworkInterfaces(); networkInterfaceEnum
					.hasMoreElements();) {
				NetworkInterface networkInterface = networkInterfaceEnum
						.nextElement();
				for (Enumeration<InetAddress> ipAddressEnum = networkInterface
						.getInetAddresses(); ipAddressEnum.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) ipAddressEnum
							.nextElement();

					// ---check that it is not a loopback address and
					// it is IPv4---
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("IPAddress", ex.toString());
		}
		return null;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// *************************************************
		// Performs first principal inherited class (Upper class - Extends )
		super.onCreate(savedInstanceState);

		// getting rib of the status bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.appload_layout);
		textView1 = (TextView) findViewById(R.id.textView1);
		
		mpl = MediaPlayer.create(LoadAppDeadlyWheel.this, R.raw.tech_background_cut);
		mpl.setLooping(true);
		mpl.start();

	}

	public class ServerThread implements Runnable {
		public void run() {
			try {
				if (SERVER_IP != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							textView1.setText(textView1.getText()
									+ "Server listening on IP: " + SERVER_IP
									+ "\n");
						}
					});

					// ---create an instance of the server socket---
					serverSocket = new ServerSocket(SERVER_PORT);

					while (true) {
						// ---wait for incoming clients---
						Socket client = serverSocket.accept();

						// ---the above code is a blocking call;
						// i.e. it will block until a client connects---

						// ---client has connected---
						handler.post(new Runnable() {
							@Override
							public void run() {
								textView1.setText(textView1.getText()
										+ "Connected to client." + "\n");
							}
						});

						try {
							// ---get an InputStream object to read from the
							// socket---
							BufferedReader br = new BufferedReader(
									new InputStreamReader(
											client.getInputStream()));

							OutputStream outputStream = client
									.getOutputStream();

							// ---read all incoming data terminated with a \n
							// char---
							String line = null;
							while ((line = br.readLine()) != null) {
								final String strReceived = line;

								// ---send whatever you received back to the
								// client---
								String s = line + "\n";
								outputStream.write(s.getBytes());

								handler.post(new Runnable() {
									@Override
									public void run() {
										textView1.setText(textView1.getText()
												+ strReceived + "\n");
									}
								});
							}

							// ---client has disconnected from the server---
							handler.post(new Runnable() {
								@Override
								public void run() {
									textView1.setText(textView1.getText()
											+ "Client disconnected." + "\n");
								}
							});

						} catch (Exception e) {
							final String error = e.getLocalizedMessage();
							handler.post(new Runnable() {
								@Override
								public void run() {
									textView1.setText(textView1.getText()
											+ error);
								}
							});
						}
					}
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							textView1.setText(textView1.getText()
									+ "No internet connection on device."
									+ "\n");
						}
					});
				}
			} catch (Exception e) {
				final String error = e.getLocalizedMessage();
				handler.post(new Runnable() {
					@Override
					public void run() {
						textView1.setText(textView1.getText() + error + "\n");
					}
				});
			}

			handler.post(new Runnable() {
				@Override
				public void run() {
					textView1.setText(textView1.getText() + "\n"
							+ "Server exited" + "\n");
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// ---get the IP address of itself---
		SERVER_IP = getLocalIpv4Address();

		// ---start the server---
		Thread serverThread = new Thread(new ServerThread());
		serverThread.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 */

	/**
	 * Called when the user clicks on the image (for now) later it will be
	 * called after a certain time interval
	 */
	public void gotoLogin(View view) {

			// *************************************************
			// Creating Object to send the intent to start the Screen
			// properties
			Intent intent = new Intent(this, Login.class);
			// *************************************************
			// Sending the Operating System the intent to start Activity
			startActivity(intent);
	}
}