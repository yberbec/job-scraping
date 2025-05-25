package org.job.scraping.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.job.scraping.model.Job;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelExportService {

    public void saveJobsToFile(List<Job> jobs) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Jobs");
        int rowIdx = 0;
        Row header = sheet.createRow(rowIdx++);
        String[] cols = {"Title", "Company", "Location", "Posted", "Link"};
        for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

        for (Job job : jobs) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(job.getTitle());
            row.createCell(1).setCellValue(job.getCompany());
            row.createCell(2).setCellValue(job.getLocation());
            row.createCell(3).setCellValue(Optional.ofNullable(job.getDetectedExtensions()).orElse(new Job.DetectedExtensions()).getPostedAt());
            row.createCell(4).setCellValue(job.getApplyOptions().stream().map(Job.ApplyOptions::getApplyLink).findFirst().orElse(StringUtils.EMPTY));

        }

        try (FileOutputStream fileOut = new FileOutputStream("output/jobs.xlsx")) {
            wb.write(fileOut);
        } finally {
            wb.close();
        }
    }

}
