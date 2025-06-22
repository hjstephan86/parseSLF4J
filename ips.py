import requests
import time

def fetch_location(ip):
    """Fetch location data for a given IP address."""
    url = f"http://ip-api.com/json/{ip}"
    while True:
        try:
            response = requests.get(url, timeout=5)  # 5-second timeout
            data = response.json()
            if data.get("status") == "fail":
                return f"Error: {data.get('message')} (IP: {ip})"
            return f"{data.get('country')}, {data.get('city')}"
        except (requests.RequestException, ValueError) as e:
            # print(f"Request failed for IP: {ip}. Retrying in 500ms. Error: {e}")
            time.sleep(0.5)  # 500-millisecond delay before retrying

def main():
    # Read IPs from ips.txt
    try:
        with open("ips.txt", "r") as file:
            ips = [line.strip() for line in file if line.strip()]
    except FileNotFoundError:
        print("Error: 'ips.txt' file not found.")
        return

    # Process each IP
    for ip in ips:
        result = fetch_location(ip)
        print(ip + " from " + result)

if __name__ == "__main__":
    main()

