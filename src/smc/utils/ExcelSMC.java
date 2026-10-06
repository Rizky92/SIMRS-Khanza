package smc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.OldFormulaRecord;
import org.apache.poi.hssf.record.OldLabelRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public final class ExcelSMC {
    private ExcelSMC() {
    }

    public static Workbook openExcel(File file) throws IOException {
        try {
            return WorkbookFactory.create(file, null, true);
        } catch (OldExcelFormatException e) {
            return openLegacyExcel(file);
        }
    }

    private static Workbook openLegacyExcel(File file) throws IOException {
        try (InputStream eis = FileMagic.prepareToCheckMagic(new FileInputStream(file))) {
            if (FileMagic.OLE2 != FileMagic.valueOf(eis)) {
                return readBiffRecords(eis);
            }
        }

        try (POIFSFileSystem fs = new POIFSFileSystem(file, true)) {
            DirectoryNode root = fs.getRoot();
            String name = root.hasEntry("Workbook") ? "Workbook" : "Book";

            try (InputStream is = root.createDocumentInputStream(name)) {
                return readBiffRecords(is);
            }
        }
    }

    private static Workbook readBiffRecords(InputStream inputStream) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CodepageRecord codepage = null;
        Cell formula = null;
        RecordInputStream row = new RecordInputStream(inputStream);

        while (row.hasNextRecord()) {
            int sid = row.getNextSid();
            row.nextRecord();

            switch (sid) {
                case BOFRecord.biff2_sid:
                case BOFRecord.biff3_sid:
                case BOFRecord.biff4_sid:
                case BOFRecord.biff5_sid:
                    if (0 < sheet.getPhysicalNumberOfRows()) {
                        return workbook;
                    }
                    break;

                case CodepageRecord.sid:
                    codepage = new CodepageRecord(row);
                    break;

                case OldLabelRecord.biff2_sid:
                case OldLabelRecord.biff345_sid: {
                    OldLabelRecord record = new OldLabelRecord(row);
                    record.setCodePage(codepage);
                    readCell(sheet, record.getRow(), record.getColumn()).setCellValue(record.getValue());
                    break;
                }

                case NumberRecord.sid: {
                    NumberRecord record = new NumberRecord(row);
                    readCell(sheet, record.getRow(), record.getColumn()).setCellValue(record.getValue());
                    break;
                }

                case RKRecord.sid: {
                    RKRecord record = new RKRecord(row);
                    readCell(sheet, record.getRow(), record.getColumn()).setCellValue(record.getRKNumber());
                    break;
                }

                case OldFormulaRecord.biff2_sid:
                case OldFormulaRecord.biff3_sid:
                case OldFormulaRecord.biff4_sid: {
                    OldFormulaRecord record = new OldFormulaRecord(row);
                    formula = readCell(sheet, record.getRow(), record.getColumn());
                    if (CellType.NUMERIC.getCode() == record.getCachedResultType()) {
                        formula.setCellValue(record.getValue());
                        formula = null;
                    }
                    break;
                }

                case OldStringRecord.biff2_sid:
                case OldStringRecord.biff345_sid: {
                    OldStringRecord record = new OldStringRecord(row);
                    record.setCodePage(codepage);
                    if (null != formula) {
                        formula.setCellValue(record.getString());
                        formula = null;
                    }
                    break;
                }

                default:
                    break;
            }

            if (0 < row.remaining()) {
                row.readRemainder();
            }
        }
        if (0 == sheet.getPhysicalNumberOfRows()) {
            throw new OldExcelFormatException("Tidak ada data yang bisa dibaca dari berkas Excel versi lama");
        }

        return workbook;
    }

    private static Cell readCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = null == sheet.getRow(rowIndex) ? sheet.createRow(rowIndex) : sheet.getRow(rowIndex);

        return null == row.getCell(colIndex) ? row.createCell(colIndex) : row.getCell(colIndex);
    }
}
