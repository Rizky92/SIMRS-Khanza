package smc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.OldExcelFormatException;
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
    private static final int SID_BOF_BIFF2 = 0x0009, SID_BOF_BIFF3 = 0x0209, SID_BOF_BIFF4 = 0x0409, SID_BOF_BIFF5 = 0x0809;

    private ExcelSMC() {
    }

    public static Workbook bukaBukuKerja(File berkas) throws IOException {
        try {
            return WorkbookFactory.create(berkas, null, true);
        } catch (OldExcelFormatException e) {
            return bukaExcelLama(berkas);
        }
    }

    private static Workbook bukaExcelLama(File berkas) throws IOException {
        try (InputStream masukan = FileMagic.prepareToCheckMagic(new FileInputStream(berkas))) {
            if (FileMagic.OLE2 != FileMagic.valueOf(masukan)) {
                return bacaRekamanBiff(masukan);
            }
        }
        try (POIFSFileSystem sistem = new POIFSFileSystem(berkas, true)) {
            DirectoryNode akar = sistem.getRoot();
            String nama = akar.hasEntry("Workbook") ? "Workbook" : "Book";
            try (InputStream masukan = akar.createDocumentInputStream(nama)) {
                return bacaRekamanBiff(masukan);
            }
        }
    }

    private static Workbook bacaRekamanBiff(InputStream masukan) throws IOException {
        Workbook buku = new HSSFWorkbook();
        Sheet lembar = buku.createSheet();
        CodepageRecord codepage = null;
        Cell rumus = null;
        RecordInputStream rekaman = new RecordInputStream(masukan);
        while (rekaman.hasNextRecord()) {
            int sid = rekaman.getNextSid();
            rekaman.nextRecord();
            switch (sid) {
                case SID_BOF_BIFF2:
                case SID_BOF_BIFF3:
                case SID_BOF_BIFF4:
                case SID_BOF_BIFF5:
                    if (0 < lembar.getPhysicalNumberOfRows()) {
                        return buku;
                    }
                    break;
                case CodepageRecord.sid:
                    codepage = new CodepageRecord(rekaman);
                    break;
                case OldLabelRecord.biff2_sid:
                case OldLabelRecord.biff345_sid: {
                    OldLabelRecord label = new OldLabelRecord(rekaman);
                    label.setCodePage(codepage);
                    sel(lembar, label.getRow(), label.getColumn()).setCellValue(label.getValue());
                    break;
                }
                case NumberRecord.sid: {
                    NumberRecord angka = new NumberRecord(rekaman);
                    sel(lembar, angka.getRow(), angka.getColumn()).setCellValue(angka.getValue());
                    break;
                }
                case RKRecord.sid: {
                    RKRecord rk = new RKRecord(rekaman);
                    sel(lembar, rk.getRow(), rk.getColumn()).setCellValue(rk.getRKNumber());
                    break;
                }
                case OldFormulaRecord.biff2_sid:
                case OldFormulaRecord.biff3_sid:
                case OldFormulaRecord.biff4_sid: {
                    OldFormulaRecord formula = new OldFormulaRecord(rekaman);
                    rumus = sel(lembar, formula.getRow(), formula.getColumn());
                    if (CellType.NUMERIC.getCode() == formula.getCachedResultType()) {
                        rumus.setCellValue(formula.getValue());
                        rumus = null;
                    }
                    break;
                }
                case OldStringRecord.biff2_sid:
                case OldStringRecord.biff345_sid: {
                    OldStringRecord teks = new OldStringRecord(rekaman);
                    teks.setCodePage(codepage);
                    if (null != rumus) {
                        rumus.setCellValue(teks.getString());
                        rumus = null;
                    }
                    break;
                }
                default:
                    break;
            }
            if (0 < rekaman.remaining()) {
                rekaman.readRemainder();
            }
        }
        if (0 == lembar.getPhysicalNumberOfRows()) {
            throw new OldExcelFormatException("Tidak ada data yang bisa dibaca dari berkas Excel versi lama");
        }
        return buku;
    }

    private static Cell sel(Sheet lembar, int baris, int kolom) {
        Row row = null == lembar.getRow(baris) ? lembar.createRow(baris) : lembar.getRow(baris);
        return null == row.getCell(kolom) ? row.createCell(kolom) : row.getCell(kolom);
    }
}
