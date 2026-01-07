import time
import random
import uuid
import os
import requests
from datetime import datetime
from models import LocationUpdate
import concurrent.futures


# URL Set in docker, but default to localhost
URL = os.getenv("TARGET_URL", "http://localhost:8080/api/locations")

# Simulate with 10 devices for now
NUM_DEVICES = 10

# Manual delay between posting events for testing 
DELAY = 1.0 

# List of cities to simulate data in
CITIES = [
    {"name": "Seattle", "lat": 47.6062, "lon": -122.3321},
    {"name": "New York", "lat": 40.7128, "lon": -74.0060},
    {"name": "London",  "lat": 51.5074, "lon": -0.1278},
    {"name": "Tokyo",   "lat": 35.6762, "lon": 139.6503},
]


def simulate_device(city_name, start_lat, start_lon):
    device_id = str(uuid.uuid4())
    print(f"Device {device_id} started in {city_name}")
    
    # Start everyone near given coordinates, but slightly spread out
    lat = start_lat + random.uniform(-0.05, 0.05)
    lon = start_lon + random.uniform(-0.05, 0.05)

    while True:
        try:
            lat += random.uniform(-0.0005, 0.0005)
            lon += random.uniform(-0.0005, 0.0005)

            locationUpdate = LocationUpdate(device_id = device_id, 
                                            latitude = lat, 
                                            longitude = lon, 
                                            timestamp = datetime.now()).to_dict()
            
            response = requests.post(URL, json=locationUpdate)
            print(f"Response = {response.status_code}")


            time.sleep(DELAY)
        except Exception as e:
            print(f"Error: {e}")
            time.sleep(DELAY)

def run_load_test():

    with concurrent.futures.ThreadPoolExecutor(max_workers=NUM_DEVICES) as executor:

        # Submit the 'simulate_device' task NUM_DEVICES times
        futures = []

        for _ in range(NUM_DEVICES):
            city = random.choice(CITIES)

            # Create thread for random city to simulate random location updates
            future = executor.submit(
                simulate_device, 
                city["name"], 
                city["lat"], 
                city["lon"]
            )
            futures.append(future)
        
        # Keep main thread alive while workers work
        concurrent.futures.wait(futures)

if __name__ == "__main__":
    run_load_test()