# 🧠 Job Scraping API (Spring Boot + SerpApi)

A Spring Boot application that scrapes job listings from Google Jobs using the SerpApi engine. It exposes a REST API to retrieve filtered job data based on title, location, number of pages, and posting date.

---

## 🔧 Features

- Fetch jobs by title and country
- Filter by posting date (`hours`, `days`, etc.)
- Paginate results using SerpApi's `next_page_token`
- Automatically export results to Excel
- Built with FeignClient, Jackson, and Spring Boot REST

---

## 🧪 API Usage

### Endpoint

GET /api/jobs/google-jobs

### Sample Request
```
http://localhost:8080
http://localhost:8080/api/jobs/google-jobs?jobTitle=java+backend+developer+jobs&country=Canada&pages=1&posted_at=days
```

### Query Parameters

| Param      | Type   | Description                               | Required |
|------------|--------|-------------------------------------------|----------|
| `jobTitle` | String | Title of the job you're searching for     | ✅ Yes   |
| `country`  | String | Country to target job results from        | ✅ Yes   |
| `pages`    | Int    | Number of pages to fetch (each ~10 jobs)  | ✅ Yes   |
| `posted_at`| String | Keyword to filter by post time (`hours`, `days`, etc.) | ✅ Yes   |

---

## 📁 Output

- The API returns a list of job listings as JSON
- Also saves all fetched jobs into an `.xlsx` Excel file automatically in the root directory

---

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Web + Feign Client**
- **SerpApi**
- **Jackson for JSON**
- **Apache POI for Excel Export**

---

## 🧰 Requirements

- Java 17+
- Maven
- SerpApi API Key (add to your `application.properties`)

```properties
serpapi.api.key=YOUR_API_KEY
```
---
## 🏁 Getting Started
```
# 1. Clone the repo
git clone https://github.com/yberbec/job-scraping.git

# 2. Navigate into the project
cd job-scraping-app

# 3. Run the app
mvn spring-boot:run
```
## 📦 Project Structure
```src/
└── main/
└── java/
└── job/
└── scraping/
├── api/
│   └── GoogleJobsApi.java          # Feign client to call SerpApi
├── common/
│   └── JobScheduler.java           # (Optional) Background job runner
├── controller/
│   ├── JobController.java          # REST endpoint controller
│   └── GlobalExceptionHandler.java # Handles application-wide errors
├── exception/
│   ├── ExportException.java
│   ├── ExternalApiException.java
│   ├── InvalidJobRequestException.java
│   └── JobParsingException.java    # Custom error types
├── model/
│   └── Job.java                    # POJO model for job listings
├── service/
├── util/
│   └── ExportConstants.java
├── ExcelExportService.java     # Exports jobs to Excel
└── GoogleJobsRestClient.java   # Core logic: fetch + parse jobs
```

## 📬 Contact
Made by Yahya Berbeche
📧 [yberbeche@gmail.com]
💻 GitHub: yberbec


