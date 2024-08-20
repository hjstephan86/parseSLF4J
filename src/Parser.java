import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

public class Parser {

	public static void main(String[] args) {
		LinkedHashSet<String> ips = new LinkedHashSet<String>();
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\Steph\\OneDrive\\Desktop\\SLF4J-20240802.txt"));
			String line = reader.readLine();
			while (line != null) {
				String keyRemoteAdress = ": Remote address";
				if (line.contains(keyRemoteAdress)) {
					String ip = line.substring(line.indexOf(keyRemoteAdress) + keyRemoteAdress.length() + 1,
							line.indexOf(" requested"));

					if (!ips.contains(ip)) {
						ips.add(ip);
						// System.out.println(ip);
					}
				}
				// Read next line
				line = reader.readLine();
			}
			reader.close();

			// Sort ips
			ArrayList<IP> ipsList = new ArrayList<IP>(ips.size());
			for (String ip : ips) {
				ipsList.add(new IP(ip));
			}

			Collections.sort(ipsList);
			for (int i = 0; i < ipsList.size(); i++) {
				System.out.println(ipsList.get(i));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
