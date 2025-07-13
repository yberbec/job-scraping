package org.job.scraping.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.job.scraping.model.Job;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelExportServiceTest {

    private ExcelExportService excelExportService;

    @BeforeEach
    void setUp() {
        excelExportService = new ExcelExportService();
    }

    @Test
    void saveJobsToFile_createsValidExcelFile() throws Exception {
        // Given
        Job job = new Job();
        job.setTitle("Java Developer");
        job.setCompany("Company");
        job.setLocation("Remote");

        Job.DetectedExtensions extensions = new Job.DetectedExtensions();
        extensions.setPostedAt("1 day ago");
        job.setDetectedExtensions(extensions);

        Job.ApplyOptions option = new Job.ApplyOptions();
        option.setApplyLink("https://jobLink.com/jobs/java-dev");
        job.setApplyOptions(List.of(option));

        Set<Job> jobs = Set.of(job);
        System.out.println("Current working dir: " + System.getProperty("user.dir"));

        excelExportService.saveJobsToFile(jobs);

        File file = new File("output/jobs.xlsx");
        assertThat(file).exists();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Jobs");
            assertThat(sheet).isNotNull();

            Row headerRow = sheet.getRow(0);
            assertThat(headerRow).isNotNull();
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Title");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("Company");

            Row dataRow = sheet.getRow(1);
            assertThat(dataRow).isNotNull();
            assertThat(dataRow.getCell(0).getStringCellValue()).isEqualTo("Java Developer");
            assertThat(dataRow.getCell(1).getStringCellValue()).isEqualTo("Company");
            assertThat(dataRow.getCell(2).getStringCellValue()).isEqualTo("Remote");
            assertThat(dataRow.getCell(3).getStringCellValue()).isEqualTo("1 day ago");
            assertThat(dataRow.getCell(4).getStringCellValue()).isEqualTo("https://jobLink.com/jobs/java-dev");
        }
    }

    @AfterEach
    void cleanUp() {
        File file = new File("output/jobs.xlsx");
        if (file.exists()) {
            file.delete();
        }
    }
}