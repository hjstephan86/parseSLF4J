import requests
import time
from collections import Counter

def fetch_location(ip):
    """Fetch location data for a given IP address."""
    url = f"http://ip-api.com/json/{ip}"
    while True:
        try:
            response = requests.get(url, timeout=5)
            data = response.json()
            if data.get("status") == "fail":
                return f"Unknown location (Error: {data.get('message')})"
            return f"{data.get('city')}, {data.get('country')}"
        except (requests.RequestException, ValueError):
            time.sleep(0.5)

def main():
    # Read IPs from ips.txt
    try:
        with open("ips.txt", "r") as file:
            ips = [line.strip() for line in file if line.strip()]
    except FileNotFoundError:
        print("Error: 'ips.txt' file not found.")
        return

    # Count occurrences of each unique IP
    ip_counts = Counter(ips)

    # Fetch location once per unique IP and print with count
    for ip, count in ip_counts.items():
        location = fetch_location(ip)
        print(f"{ip} from {location} ({count} times)")

if __name__ == "__main__":
    main()
