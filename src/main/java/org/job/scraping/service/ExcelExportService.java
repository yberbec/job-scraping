package org.job.scraping.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.job.scraping.exception.ExportException;
import org.job.scraping.model.Job;
import org.job.scraping.util.ExportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

@Service
public class ExcelExportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelExportService.class);

    public void saveJobsToFile(Set<Job> jobs) throws IOException {
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

        String userDirectory = ExportConstants.USER_DIR.get();

        String outputPath = Paths.get(
                userDirectory.contains(ExportConstants.APP_NAME.get())
                        ? userDirectory
                        : ExportConstants.APP_NAME.get(),
                ExportConstants.OUTPUT_DIR.get(),
                ExportConstants.FILE_NAME.get()
        ).toString();

        log.info("this is the output path : " + outputPath);
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            wb.write(fileOut);
        }
        catch (IOException ex){
            throw new ExportException(ex.getMessage());
        }
        finally {
            wb.close();
        }
    }

}
