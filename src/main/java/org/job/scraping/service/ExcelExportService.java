package org.job.scraping.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.job.scraping.model.Job;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {
    public void exportJobs(List<Job> jobs, HttpServletResponse resp) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Jobs");
        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        String[] cols = {"Title","Company","Location","Summary","Posted","URL"};
        for(int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);

        for(Job job: jobs){
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(job.getTitle());
            row.createCell(1).setCellValue(job.getCompany());
            row.createCell(2).setCellValue(job.getLocation());
            row.createCell(3).setCellValue(job.getSummary());
            row.createCell(4).setCellValue(job.getPostedDate());
            row.createCell(5).setCellValue(job.getUrl());
        }

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition","attachment; filename=jobs.xlsx");
        wb.write(resp.getOutputStream());
        wb.close();  // :contentReference[oaicite:7]{index=7}
        saveJobsToFile(jobs,"src/main/resources/jobs.xlsx");
    }

    public void saveJobsToFile(List<Job> jobs, String filePath) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Jobs");
        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        String[] cols = {"Title", "Company", "Location", "Summary", "Posted", "URL"};
        for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

        for (Job job : jobs) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(job.getTitle());
            row.createCell(1).setCellValue(job.getCompany());
            row.createCell(2).setCellValue(job.getLocation());
            row.createCell(3).setCellValue(job.getSummary());
            row.createCell(4).setCellValue(job.getPostedDate());
            row.createCell(5).setCellValue(job.getUrl());
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            wb.write(fileOut);
        } finally {
            wb.close();
        }
    }

}
