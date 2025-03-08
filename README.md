# Media Enhancement API

A Spring Boot application for enhancing images and videos with a priority queue system for processing requests.

## Features

- Upload images and videos for enhancement
- Priority-based processing queue (LOW, MEDIUM, HIGH)
- Simulated processing delay (5 seconds per file)
- Non-blocking API with asynchronous processing
- Check status of enhancement requests
- Cancel pending requests
- View all requests for a specific user

## Technical Details

- Java 17
- Spring Boot 3.2.3
- RESTful API
- Asynchronous processing with priority queue
- Multipart file upload support
- Proper handling of Spring's @Async limitations

## Architecture

The application uses a non-blocking architecture:

1. **Controller Layer**: Accepts requests and returns immediate responses with request IDs
2. **Service Layer**: Manages the priority queue and processes requests asynchronously
3. **Async Processing**: Uses Spring's @Async capabilities with a dedicated AsyncProcessor to avoid self-invocation issues
4. **Priority Queue**: Processes higher priority requests first, then by submission time (FIFO)

## API Endpoints

### Submit Enhancement Request

```
POST /api/enhancement
Content-Type: multipart/form-data

Form fields:
- userId: String (required)
- file: File (required)
- mediaType: String (IMAGE or VIDEO, required)
- priority: String (LOW, MEDIUM, HIGH, optional, default: MEDIUM)
```

Response: Request ID (UUID) with 202 Accepted status

### Get Enhancement Result

```
GET /api/enhancement/{requestId}
```

Response: Enhancement result or status (QUEUED, PROCESSING, COMPLETED, FAILED)

### Get User Requests

```
GET /api/enhancement/user/{userId}
```

Response: List of all requests for the user

### Cancel Request

```
DELETE /api/enhancement/{requestId}
```

Response: 204 No Content

## Configuration

The application can be configured using the following properties in `application.properties`:

```
server.port=8080
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
media.enhancement.processing.threads=1
```

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/media-enhancement-api-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

## Implementation Details

### Asynchronous Processing

The application uses Spring's `@Async` capabilities to process media enhancement requests in the background:

- Requests are added to a `PriorityBlockingQueue` when submitted
- A dedicated background thread processes requests one at a time
- The API endpoints return immediately, not waiting for processing to complete
- Users can check the status of their requests using the provided UUID

### Priority Queue Implementation

The priority queue ensures that:

1. Higher priority requests (HIGH > MEDIUM > LOW) are processed first
2. Requests with the same priority are processed in FIFO order (first-come, first-served)
3. The queue is thread-safe using Java's `PriorityBlockingQueue`

### Handling @Async Limitations

Spring's `@Async` has two limitations:
- It must be applied to public methods only
- Self-invocation (calling the async method from within the same class) doesn't work

To solve this, the application uses:
- A dedicated `AsyncProcessor` class with an `@Async` method
- The `@PostConstruct` method calls the async method through this processor
- This avoids the self-invocation problem while maintaining clean code

### Simulated Processing

Currently, the application simulates a 5-second processing time per file. In a real implementation, this would be replaced with actual enhancement algorithms.

## Testing with Curl/Postman

### Submit a request:

```bash
curl -X POST \
  http://localhost:8080/api/enhancement \
  -H 'Content-Type: multipart/form-data' \
  -F 'userId=user123' \
  -F 'mediaType=IMAGE' \
  -F 'priority=HIGH' \
  -F 'file=@/path/to/your/image.jpg'
```

### Check status:

```bash
curl -X GET http://localhost:8080/api/enhancement/{requestId}
```

Replace `{requestId}` with the UUID returned from your submission. 