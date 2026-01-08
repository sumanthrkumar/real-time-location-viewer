# Real-Time Fleet Tracker

## Description
The Real-Time Fleet Tracker is a distributed system designed to ingest, process, and visualize real-time location data from numerous devices. This project showcases a robust architecture built around message queues (Apache Kafka) for scalable data handling, backend services for data ingestion and analytics, and a dynamic web frontend for live map visualization.

The system efficiently receives incoming device location updates, performs essential processing such as calculating the total distance traveled, and then pushes these processed updates to connected web clients for immediate display on an interactive map. A dedicated Python-based simulator generates realistic location data, enabling comprehensive testing and demonstration of the system's capabilities under various loads.

## Features
*   **Real-time Location Tracking**: Visualize the live movement of multiple devices on an interactive map.
*   **Scalable Data Ingestion**: A dedicated Java-based ingestion service receives high volumes of location updates via a RESTful HTTP POST endpoint.
*   **Asynchronous Messaging with Kafka**: Leverages Apache Kafka as a high-throughput, fault-tolerant message broker for handling the stream of location data.
*   **Location Data Processing**: An analytics service, likely built with Java, consumes raw location data from Kafka, calculating and enriching it with metrics such as total distance traveled per device.
*   **WebSocket Communication**: Employs WebSockets, using STOMP over SockJS, to push real-time location updates from the analytics service to connected frontend clients.
*   **Interactive Map Visualization**: The web-based frontend, served by the analytics service, uses Leaflet.js and OpenStreetMap to dynamically display device positions and their recent movement paths.
*   **Dynamic Device Simulation**: A Python-based simulator generates realistic, random-walk location data for a configurable number of devices, ideal for load testing and demonstrations.
*   **Modern Web Technologies**: The frontend is built using HTML, CSS, and JavaScript, integrating popular libraries like Leaflet.js, SockJS, and Stomp.js for a responsive user experience.
*   **Java Backend**: The core services (ingestion and analytics) are developed in Java, indicating a robust and performant backend architecture.

## Installation

This project consists of several interconnected services. While specific build configuration files (e.g., `pom.xml`, `build.gradle`) for the Java services are not explicitly provided, the general installation process involves setting up Kafka, building and running the Java services, and preparing the Python simulator.

### Prerequisites

*   Java Development Kit (JDK) 17 or newer
*   Apache Maven or Gradle (for building Java services - assumed)
*   Python 3.8 or newer
*   `pip` (Python package installer)
*   Docker and Docker Compose (highly recommended for running Kafka)

### Setup Steps

1.  **Clone the Repository**:
    ```bash
    git clone https://your-repository-url/real-time-fleet-tracker.git
    cd real-time-fleet-tracker
    ```

2.  **Set up Apache Kafka**:
    Kafka is central to the messaging architecture. A typical setup uses Docker Compose.
    You might have a `docker-compose.yml` resembling this structure:
    ```yaml
    version: '3'
    services:
      zookeeper:
        image: confluentinc/cp-zookeeper:7.0.1
        hostname: zookeeper
        ports:
          - "2181:2181"
        environment:
          ZOOKEEPER_CLIENT_PORT: 2181
          ZOOKEEPER_TICK_TIME: 2000

      kafka:
        image: confluentinc/cp-kafka:7.0.1
        hostname: kafka
        ports:
          - "9092:9092"
        environment:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
          KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
          KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
          KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
          KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
        depends_on:
          - zookeeper
    ```
    Start Kafka and Zookeeper:
    ```bash
    docker-compose up -d zookeeper kafka
    ```

3.  **Build and Run Java Services**:
    The `ingestion-service` and `analytics-service` are Java applications. Navigate into each service's directory and perform the build and run commands.
    *Note*: You will need the appropriate `pom.xml` (Maven) or `build.gradle` (Gradle) files in these directories, which are not provided in the snippet.

    *Example for `ingestion-service` (assuming Spring Boot and Maven)*:
    ```bash
    # Navigate to the ingestion-service directory
    cd ingestion-service
    mvn clean package
    java -jar target/ingestion-service-0.0.1-SNAPSHOT.jar # Adjust JAR name as needed
    cd ..
    ```

    *Example for `analytics-service` (assuming Spring Boot and Maven)*:
    ```bash
    # Navigate to the analytics-service directory
    cd analytics-service
    mvn clean package
    java -jar target/analytics-service-0.0.1-SNAPSHOT.jar # Adjust JAR name as needed
    cd ..
    ```
    Ensure that both Java services are configured to connect to your Kafka instance (e.g., `kafka:29092` if running in Docker, or `localhost:9092` if Kafka is local). The `analytics-service` will serve the `index.html` frontend, typically on port `8080`.

4.  **Install Python Dependencies for Simulator**:
    Navigate to the `test-simulator` directory:
    ```bash
    cd test-simulator
    pip install requests
    # If a requirements.txt file existed, you would use: pip install -r requirements.txt
    cd ..
    ```

## Usage

Once all components are installed and running, you can interact with the Real-Time Fleet Tracker.

1.  **Start all Services**:
    Ensure your Kafka instance, the `ingestion-service`, and the `analytics-service` are all running and accessible.

2.  **Open the Real-Time Fleet Tracker UI**:
    Open your web browser and navigate to the address where the `analytics-service` is hosted. This is typically `http://localhost:8080` (or the specific port configured for your analytics service). You should see an interactive map, initially centered on Seattle.

3.  **Run the Device Simulator**:
    In a new terminal window, navigate to the `test-simulator` directory and execute the simulator script:
    ```bash
    python main.py
    ```
    This will start sending simulated location updates for `NUM_DEVICES` (default 1000) to the `ingestion-service`. You will see console output showing `Response = 200` for successful posts.

4.  **Observe Real-Time Updates**:
    Switch back to your web browser. As the simulator sends data, new markers will appear on the map, representing the simulated devices. Their positions will update in real-time, and blue polyline trails will visualize their recent movement history. Clicking on a marker will display its unique ID and the calculated total distance traveled.

5.  **Stop Simulation**:
    To halt the generation of simulated data, simply press `Ctrl+C` in the terminal where the `python main.py` script is running. The markers on the map will stop updating, but their last known positions and paths will remain visible until the `analytics-service` is restarted or the page is refreshed.