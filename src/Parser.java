import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Parser {

	public static void main(String[] args) {
		LinkedHashMap<String, IP> ips = new LinkedHashMap<String, IP>();
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("/home/stephan/Downloads/bible-app-logs-2024-10-22-07-53.txt"));
			String line = reader.readLine();
			while (line != null) {
				String keyRemoteAdress = ": Remote address";
				if (line.contains(keyRemoteAdress)) {
					String ip = line.substring(line.indexOf(keyRemoteAdress) + keyRemoteAdress.length() + 1,
							line.indexOf(" requested"));

					if (ips.get(ip) == null) {
						ips.put(ip, new IP(ip));
					} else {
						IP ipObj = ips.get(ip);
						int occurrence = ipObj.getOccurrence();
						ipObj.setOccurrence(occurrence + 1);
					}
				}
				// Read next line
				line = reader.readLine();
			}
			reader.close();

			ArrayList<IP> ipsList = new ArrayList<IP>(ips.values());
			Collections.sort(ipsList);
			for (int i = 0; i < ipsList.size(); i++) {
				System.out.println(ipsList.get(i));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
