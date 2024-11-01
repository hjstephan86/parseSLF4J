package com.parseSLF4J.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class Parser {

	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
		String plogs = "plogs-2024-11-16.txt";
		String hlogs = "hlogs-2024-11-16.txt";
		if (args.length == 2) {
			plogs = args[0];
			hlogs = args[1];
		}

		LinkedHashMap<String, IP> ips = new LinkedHashMap<String, IP>();

		System.out.println("Parse " + plogs);
		boolean logFilesOverlap = parseLogfile(ips, plogs);
		System.out.println("Log files overlap: " + logFilesOverlap);
		System.out.println();

		System.out.println("Parse " + hlogs);
		logFilesOverlap = parseLogfile(ips, hlogs);
		System.out.println("Log files overlap: " + logFilesOverlap);

		System.out.println();
		printIPs(ips);
	}

	private static boolean parseLogfile(LinkedHashMap<String, IP> ips, String logfilename) {
		boolean logFilesOverlap = false;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(logfilename));
			String line = reader.readLine();
			while (line != null) {
				String keyRemoteAdress = ": Remote address";
				if (line.contains(keyRemoteAdress)) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
					OffsetDateTime dateTime = OffsetDateTime.parse(line.substring(0, 29), formatter);

					String ip = line.substring(line.indexOf(keyRemoteAdress) + keyRemoteAdress.length() + 1,
							line.indexOf(" requested"));

					if (ips.get(ip) == null) {
						ips.put(ip, new IP(dateTime, ip));
					} else {
						IP ipObj = ips.get(ip);
						if (!ipObj.addDateTime(dateTime)) {
							logFilesOverlap = true;
						}
					}
				}
				// Read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logFilesOverlap;
	}

	private static void printIPs(LinkedHashMap<String, IP> ips)
			throws MalformedURLException, IOException, InterruptedException {
		ArrayList<IP> ipsList = new ArrayList<IP>(ips.values());

		Comparator<IP> byFirstDateTime = Comparator.comparing(IP::getFirstDateTime);
		Collections.sort(ipsList, byFirstDateTime);
		System.out.println("Log results from " + ipsList.get(0).getFirstDateTime().toString() + " to "
				+ ipsList.get(ipsList.size() - 1).getDateTimes().getLast().toString());

		Comparator<IP> byOccurrence = Comparator.comparing(IP::getOccurrence);
		Collections.sort(ipsList, byOccurrence.reversed());
		for (int i = 0; i < ipsList.size(); i++) {
			boolean requestSuccess = false;
			while (!requestSuccess) {
				try {
					System.out.println(ipsList.get(i) + " from " + getLocation(ipsList.get(i).getIpString()));
					requestSuccess = true;
				} catch (RuntimeException re) {
					TimeUnit.MILLISECONDS.sleep(500);
				}
			}
		}
	}

	private static String getLocation(String ip) throws MalformedURLException, IOException {
		String urlString = "http://ip-api.com/json/" + ip;

		/// Send GET request
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		// Read response
		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			throw new RuntimeException("Failed with HTTP error code: " + responseCode);
		}

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		}

		// Parse JSON response
		JSONObject json = new JSONObject(response.toString());
		String country = json.getString("country");
		String city = json.getString("city");

		return country + ", " + city;
	}
}
